/*
 * GWTEventService
 * Copyright (c) 2008, GWTEventService Committers
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.command.*;

import java.util.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The RemoteEventService supports listening to the server via RemoteEventListeners ({@link de.novanic.eventservice.client.event.listener.RemoteEventListener}).
 * It keeps a connection to the server. When an event occurred at the server, the RemoteEventService informs the RemoteEventListeners
 * about the event and starts listening at the server again. When no RemoteEventListeners registered anymore, the
 * RemoteEventService stops listening till new RemoteEventListeners are registered.
 * The listening works with a domain/context scope. See the documentation/manual to get more information about the
 * listening concept.
 *
 * @author sstrohschein
 * <br>Date: 06.06.2008
 * <br>Time: 18:56:46
 */
public final class DefaultRemoteEventService implements RemoteEventService
{
    private RemoteEventConnector myRemoteEventConnector;
    private Queue<ClientCommand> myClientCommandQueue;
    private Map<Domain, List<RemoteEventListener>> myDomainListenerMapping;
    private boolean isSessionInitialized;

    /**
     * Creates a new RemoteEventService.
     */
    DefaultRemoteEventService(RemoteEventConnector aRemoteEventConnector) {
        myRemoteEventConnector = aRemoteEventConnector;
        myDomainListenerMapping = new HashMap<Domain, List<RemoteEventListener>>();
    }

    /**
     * Adds a listener for a domain.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     */
    public void addListener(Domain aDomain, RemoteEventListener aRemoteListener) {
        addListener(aDomain, aRemoteListener, (AsyncCallback<Void>)null);
    }

    /**
     * Adds a listener for a domain.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param aCallback callback (only called when no listener is already registered for the domain)
     */
    public void addListener(Domain aDomain, RemoteEventListener aRemoteListener, AsyncCallback<Void> aCallback) {
        List<RemoteEventListener> theListeners = myDomainListenerMapping.get(aDomain);
        if(theListeners == null) {
            theListeners = new ArrayList<RemoteEventListener>();
            myDomainListenerMapping.put(aDomain, theListeners);
            theListeners.add(aRemoteListener);
            activate(aDomain, aCallback);
        } else {
            theListeners.add(aRemoteListener);
        }
    }

    /**
     * Adds a listener for a domain. The EventFilter is applied to the domain to filter events before the
     * RemoteEventListener recognizes the event.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     */
    public void addListener(Domain aDomain, RemoteEventListener aRemoteListener, EventFilter anEventFilter) {
        addListener(aDomain, aRemoteListener, anEventFilter, null);
    }

    /**
     * Adds a listener for a domain. The EventFilter is applied to the domain to filter events before the
     * RemoteEventListener recognizes the event.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @param aCallback callback (only called when no listener is registered for the domain)
     */
    public void addListener(Domain aDomain, RemoteEventListener aRemoteListener, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        List<RemoteEventListener> theListeners = myDomainListenerMapping.get(aDomain);
        if(theListeners == null) {
            theListeners = new ArrayList<RemoteEventListener>();
            myDomainListenerMapping.put(aDomain, theListeners);
            activate(aDomain, anEventFilter, aCallback);
        } else {
            registerEventFilter(aDomain, anEventFilter);
        }
        theListeners.add(aRemoteListener);
    }

    /**
     * Removes a listener for a domain.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param aDomain domain
     * @param aRemoteListener listener to remove
     */
    public void removeListener(Domain aDomain, RemoteEventListener aRemoteListener) {
        removeListener(aDomain, aRemoteListener, new VoidAsyncCallback());
    }

    /**
     * Removes a listener for a domain.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param aDomain domain
     * @param aRemoteListener listener to remove
     * @param aCallback callback
     */
    public void removeListener(Domain aDomain, RemoteEventListener aRemoteListener, AsyncCallback<Void> aCallback) {
        if(myDomainListenerMapping.containsKey(aDomain)) {
            removeListenerInternal(aDomain, aRemoteListener, aCallback);
        }
    }

    /**
     * Registers the domain for listening and activates the RemoteEventService (starts listening) if it is inactive.
     * @param aDomain domain to register/activate
     * @param aCallback callback
     */
    private void activate(Domain aDomain, AsyncCallback<Void> aCallback) {
        activate(aDomain, null, aCallback);
    }

    /**
     * Registers the domain with the EventFilter for listening and activates the RemoteEventService (starts listening)
     * if it is inactive.
     * @param aDomain domain to register/activate
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @param aCallback callback
     */
    private void activate(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        schedule(new ActivationCommand(myRemoteEventConnector, aDomain, anEventFilter, new ListenerEventNotification(), aCallback));
    }

    /**
     * Registers an EventFilter for a domain. This can be used when a listener is already added and an EventFilter
     * needed later or isn't available when the listener is added.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     */
    public void registerEventFilter(Domain aDomain, EventFilter anEventFilter) {
        registerEventFilter(aDomain, anEventFilter, new VoidAsyncCallback());
    }

    /**
     * Registers an EventFilter for a domain. This can be used when a listener is already added and an EventFilter
     * needed later or isn't available when the listener is added.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @param aCallback callback
     */
    public void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        schedule(new RegistrationEventFilterCommand(myRemoteEventConnector, aDomain, anEventFilter, aCallback));
    }

    /**
     * Deregisters the EventFilter for a domain.
     * @param aDomain domain to remove the EventFilter from
     */
    public void deregisterEventFilter(Domain aDomain) {
        deregisterEventFilter(aDomain, new VoidAsyncCallback());
    }

    /**
     * Deregisters the EventFilter for a domain.
     * @param aDomain domain to remove the EventFilter from
     * @param aCallback callback
     */
    public void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback) {
        schedule(new DeregistrationEventFilterCommand(myRemoteEventConnector, aDomain, aCallback));
    }

    /**
     * Checks if the RemoteEventService is active (listening).
     * @return true when active/listening, otherwise false
     */
    public boolean isActive() {
        return myRemoteEventConnector.isActive();
    }

    /**
     * Removes all RemoteEventListeners and deactivates the RemoteEventService (stop listening).
     */
    public void removeListeners() {
        removeListeners(new VoidAsyncCallback());
    }

    /**
     * Removes all RemoteEventListeners and deactivates the RemoteEventService (stop listening).
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    public void removeListeners(AsyncCallback<Void> aCallback) {
        removeListeners(myDomainListenerMapping.keySet(), aCallback);
    }

    /**
     * Calls unlisten for a set of domains (stop listening for these domains). The RemoteEventListeners for these
     * domains will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomains domains to unlisten
     */
    public void removeListeners(Set<Domain> aDomains) {
        removeListeners(aDomains, new VoidAsyncCallback());
    }

    /**
     * Calls unlisten for a set of domains (stop listening for these domains). The RemoteEventListeners for these
     * domains will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomains domains to unlisten
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    public void removeListeners(Set<Domain> aDomains, AsyncCallback<Void> aCallback) {
        Set<Domain> theDomains = new HashSet<Domain>(aDomains);
        Iterator<Domain> theDomainIterator = theDomains.iterator();
        while(theDomainIterator.hasNext()) {
            Domain theDomain = theDomainIterator.next();
            if(!(unlisten(theDomain, null))) {
                theDomainIterator.remove();
            }
        }
        if(!theDomains.isEmpty()) {
            //removeListeners is called with a set of domains to reduce remote server calls.
            schedule(new DeactivationCommand(myRemoteEventConnector, theDomains, aCallback));
        }
    }

    /**
     * Stops listening for the corresponding domain. The RemoteEventFilters for the domain will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomain domain to unlisten
     */
    public void removeListeners(Domain aDomain) {
        removeListeners(aDomain, new VoidAsyncCallback());
    }

    /**
     * Stops listening for the corresponding domain. The RemoteEventFilters for the domain will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomain domain to unlisten
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    public void removeListeners(Domain aDomain, AsyncCallback<Void> aCallback) {
        unlisten(aDomain, aCallback);
    }

    /**
     * Stops listening for the corresponding domain. The RemoteEventFilters for the domain will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomain domain to unlisten
     * @param aCallback callback (if it is NULL, no call is executed to the server)
     * @return true when listeners registered (remote call needed), otherwise false
     */
    private boolean unlisten(Domain aDomain, AsyncCallback<Void> aCallback) {
        return myDomainListenerMapping.containsKey(aDomain) && removeDomain(aDomain, aCallback);
    }

    /**
     * Removes a listener from a domain. When it is the last listener, the domain will be deregistered for listening,
     * because there aren't any listeners registered for the domain.
     * It deactivates the RemoteEventService if no more domains/listeners are registered.
     * @param aDomain domain to remove the listener from (the domain will be removed when no other listeners are registered to the domain)
     * @param aListener listener to remove
     * @param aCallback callback
     */
    private void removeListenerInternal(Domain aDomain, RemoteEventListener aListener, AsyncCallback<Void> aCallback) {
        if(aListener != null) {
            //remove the listener
            List<RemoteEventListener> theListeners = myDomainListenerMapping.get(aDomain);
            if(theListeners != null) {
                myDomainListenerMapping.put(aDomain, removeOnCopy(theListeners, aListener));
            }
            theListeners = myDomainListenerMapping.get(aDomain);
            if(theListeners == null || theListeners.isEmpty()) {
                removeDomain(aDomain, aCallback);
            }
        }
    }

    /**
     * Removes the domain with all listener registrations to the domain.
     * @param aDomain domain to remove
     * @param aCallback callback (only called when the domain isn't already removed)
     * @return true when the domain is removed, otherwise false (false when the domain was already removed)
     */
    private boolean removeDomain(Domain aDomain, AsyncCallback<Void> aCallback) {
        //remove the domain (all domain registrations)
        boolean isRemoved = (myDomainListenerMapping.remove(aDomain) != null);
        if(isRemoved) {
            if(aCallback != null) {
                schedule(new DeactivationCommand(myRemoteEventConnector, aDomain, aCallback));
            }
            if(myDomainListenerMapping.isEmpty()) {
                schedule(new DeactivationCommand(myRemoteEventConnector));
            }
        }
        return isRemoved;
    }

    /**
     * Removes an entry from a list and avoids {@link ConcurrentModificationException} when an entry is removed while iterating.
     * @param aList list to remove an entry from
     * @param anEntry entry to remove
     * @param <CT> type of the contained objects
     * @return new list instance without the removed entry
     */
    private <CT> List<CT> removeOnCopy(List<CT> aList, CT anEntry) {
        List<CT> theCollectionCopy = new ArrayList<CT>(aList);
        if(theCollectionCopy.remove(anEntry)) {
            return theCollectionCopy;
        }
        return aList;
    }

    private <R> void schedule(final ClientCommand<R> aClientCommand) {
        if(myClientCommandQueue == null) {
            myClientCommandQueue = new LinkedList<ClientCommand>();
            final AsyncCallback<R> theAsyncCallback = aClientCommand.getCommandCallback();
            aClientCommand.setCommandCallback(new FirstAsyncCallback<R>(theAsyncCallback));
            aClientCommand.execute();
        } else {
            myClientCommandQueue.add(aClientCommand);
        }
        executeCommands();
    }

    private void executeCommands() {
        if(isSessionInitialized) {
            ClientCommand theClientCommand;
            while((theClientCommand = myClientCommandQueue.poll()) != null) {
                theClientCommand.execute();
            }
        }
    }

    /**
     * The ListenEventCallback is used to produce the listen cycle. It executes a {@link de.novanic.eventservice.client.command.RemoteListenCommand}
     * and is attached as callback for the listen server call.
     */
    private final class ListenerEventNotification implements EventNotification
    {
        /**
        * All listeners of the according domains will be informed about the incoming events.
        * @param anEvents incoming events
        */
        public void onNotify(List<DomainEvent> anEvents) {
            for(DomainEvent theDomainEvent : anEvents) {
                //all listeners for the domain of the event will be executed
                List<RemoteEventListener> theListeners = myDomainListenerMapping.get(theDomainEvent.getDomain());
                if(theListeners != null) {
                    final Event theEvent = theDomainEvent.getEvent();
                    for(RemoteEventListener theListener: theListeners) {
                        theListener.apply(theEvent);
                    }
                }
            }
        }

        public void onAbort() {
            //if the remote doesn't know the client, all listeners will be removed and the connection gets inactive
            removeListeners();
        }
    }

    /**
     * Empty callback
     */
    private static class VoidAsyncCallback implements AsyncCallback<Void>
    {
        public void onFailure(Throwable aThrowable) {}

        public void onSuccess(Void aResult) {}
    }

    private class FirstAsyncCallback<R> implements AsyncCallback<R>
    {
        private AsyncCallback<R> mySubAsyncCallback;

        public FirstAsyncCallback(AsyncCallback<R> aSubAsyncCallback) {
            mySubAsyncCallback = aSubAsyncCallback;
        }

        public void onSuccess(R aResult) {
            if(mySubAsyncCallback != null) {
                mySubAsyncCallback.onSuccess(aResult);
            }
            finishFirstCall();
        }

        public void onFailure(Throwable aThrowable) {
            if(mySubAsyncCallback != null) {
                mySubAsyncCallback.onFailure(aThrowable);
            }
            finishFirstCall();
        }

        private void finishFirstCall() {
            isSessionInitialized = true;
            executeCommands();
        }
    }
}
