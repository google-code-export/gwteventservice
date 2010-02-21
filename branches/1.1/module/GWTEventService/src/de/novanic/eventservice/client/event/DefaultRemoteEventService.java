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
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.command.*;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;

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
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.event.RemoteEventConnector} for the connection
     * between client side and server side
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
        if(addListenerLocal(aDomain, aRemoteListener)) {
            activate(aDomain, aCallback);
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
        if(addListenerLocal(aDomain, aRemoteListener, anEventFilter)) {
            activate(aDomain, anEventFilter, aCallback);
        }
    }

    /**
     * Adds a listener for a domain.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @return true, when it is a new / unregistered domain for the client, otherwise false
     */
    private boolean addListenerLocal(Domain aDomain, RemoteEventListener aRemoteListener) {
        List<RemoteEventListener> theListeners = myDomainListenerMapping.get(aDomain);
        final boolean isNewDomain = theListeners == null;
        if(theListeners == null) {
            theListeners = new ArrayList<RemoteEventListener>();
            myDomainListenerMapping.put(aDomain, theListeners);
            theListeners.add(aRemoteListener);
        } else {
            theListeners.add(aRemoteListener);
        }
        return isNewDomain;
    }

    /**
     * Adds a listener for a domain. The EventFilter is applied to the domain to filter events before the
     * RemoteEventListener recognizes the event.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @return true, when it is a new / unregistered domain for the client, otherwise false
     */
    private boolean addListenerLocal(Domain aDomain, RemoteEventListener aRemoteListener, EventFilter anEventFilter) {
        List<RemoteEventListener> theListeners = myDomainListenerMapping.get(aDomain);
        final boolean isNewDomain = theListeners == null;
        if(isNewDomain) {
            theListeners = new ArrayList<RemoteEventListener>();
            myDomainListenerMapping.put(aDomain, theListeners);
        } else {
            registerEventFilter(aDomain, anEventFilter);
        }
        theListeners.add(aRemoteListener);
        return isNewDomain;
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to listen for all
     * user/client domain deregistrations and timeouts. The scope for unlisten events to receive is set to
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope#UNLISTEN} by default.
     * To use other scopes see
     * {@link de.novanic.eventservice.client.event.RemoteEventService#addUnlistenListener(de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope, de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener, com.google.gwt.user.client.rpc.AsyncCallback)}.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * to listen for all user/client domain deregistrations and timeouts.
     * @param aCallback callback
     */
    public void addUnlistenListener(UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback) {
        addUnlistenListener(UnlistenEventListener.Scope.UNLISTEN, anUnlistenEventListener, aCallback);
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to listen for all
     * user/client domain deregistrations and timeouts.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * to listen for all user/client domain deregistrations and timeouts.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param aCallback callback
     */
    public void addUnlistenListener(UnlistenEventListener.Scope anUnlistenScope, UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback) {
        addUnlistenListener(anUnlistenScope, anUnlistenEventListener, null, aCallback);
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to listen for all
     * user/client domain deregistrations and timeouts. The custom {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     * will be registered at the server side and transferred to all users/clients which have an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * registered. That {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} can for example contain user information
     * of your specific user-system to recover the user in your user-system on a timeout. The scope for unlisten events to receive is set to
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope#UNLISTEN} by default.
     * To use other scopes see
     * {@link de.novanic.eventservice.client.event.RemoteEventService#addUnlistenListener(de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope, de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener, de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent, com.google.gwt.user.client.rpc.AsyncCallback)}.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * to listen for all user/client domain deregistrations and timeouts.
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can contain custom data
     * @param aCallback callback
     */
    public void addUnlistenListener(final UnlistenEventListener anUnlistenEventListener, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback) {
        addUnlistenListener(UnlistenEventListener.Scope.UNLISTEN, anUnlistenEventListener, anUnlistenEvent, aCallback);
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to listen for all
     * user/client domain deregistrations and timeouts. The custom {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     * will be registered at the server side and transferred to all users/clients which have an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * registered. That {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} can for example contain user information
     * of your specific user-system to recover the user in your user-system on a timeout.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * to listen for all user/client domain deregistrations and timeouts.
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can contain custom data
     * @param aCallback callback
     */
    public void addUnlistenListener(final UnlistenEventListener.Scope anUnlistenScope, UnlistenEventListener anUnlistenEventListener, final UnlistenEvent anUnlistenEvent, final AsyncCallback<Void> aCallback) {
        if(UnlistenEventListener.Scope.LOCAL == anUnlistenScope) {
            addListenerLocal(DomainFactory.UNLISTEN_DOMAIN, anUnlistenEventListener);
            schedule(new RegistrationUnlistenEventCommand(anUnlistenScope, myRemoteEventConnector, anUnlistenEvent, aCallback));
        } else {
            addListener(DomainFactory.UNLISTEN_DOMAIN, anUnlistenEventListener, new VoidAsyncCallback() {
                public void onSuccess(Void aResult) {
                    schedule(new RegistrationUnlistenEventCommand(anUnlistenScope, myRemoteEventConnector, anUnlistenEvent, aCallback));
                }
            });
        }
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
     * Returns all active domains (all domains where the client has listeners registered).
     * @return all active domains
     */
    public Set<Domain> getActiveDomains() {
        return myDomainListenerMapping.keySet();
    }

    /**
     * Returns all registered listeners of a domain.
     * @param aDomain domain
     * @return all registered listeners of the domain
     */
    public List<RemoteEventListener> getRegisteredListeners(Domain aDomain) {
        return myDomainListenerMapping.get(aDomain);
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
     * Removes an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to remove
     * @param aCallback callback
     */
    public void removeUnlistenListener(UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback) {
        removeListener(DomainFactory.UNLISTEN_DOMAIN, anUnlistenEventListener, aCallback);
    }

    /**
     * Stops listening for {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} instances.
     * @param aCallback callback (only called when an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} is registered)
     */
    public void removeUnlistenListeners(AsyncCallback<Void> aCallback) {
        unlisten(DomainFactory.UNLISTEN_DOMAIN, aCallback);
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

    /**
     * Starts the init command and schedules all other commands till the init command is finished. That must be done
     * to avoid double initialized sessions (race condition GWT issue 1846).  
     * @param aClientCommand command to schedule
     * @param <R> Return type of the command callback
     */
    private <R> void schedule(final ClientCommand<R> aClientCommand) {
        if(myClientCommandQueue == null) {
            myClientCommandQueue = new LinkedList<ClientCommand>();
            ClientCommand theInitCommand = new InitEventServiceCommand(myRemoteEventConnector, new InitCommandCallback());
            theInitCommand.execute();
        }
        myClientCommandQueue.add(aClientCommand);
        executeCommands();
    }

    /**
     * Executes the scheduled commands ({@link ClientCommand}. The commands can be scheduled with
     * {@link de.novanic.eventservice.client.event.DefaultRemoteEventService#schedule(de.novanic.eventservice.client.event.command.ClientCommand)}.
     */
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

        /**
        * That method will be called when the listening for events is aborted (unexpected).
        */
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

    /**
     * Callback for the init command.
     */
    private class InitCommandCallback implements AsyncCallback<Void>
    {
        /**
         * Executes the scheduled commands on success.
         * @param aResult no result (void)
         */
        public void onSuccess(Void aResult) {
            finishFirstCall();
        }

        /**
         * Executes the scheduled commands on failure.
         * @param aThrowable throwable caused by a failed server call
         */
        public void onFailure(Throwable aThrowable) {
            finishFirstCall();
        }

        /**
         * Executes the scheduled commands.
         */
        private void finishFirstCall() {
            //Schedule the next command after the callback is finished. The timer is needed, because some browsers doesn't
            //notice the server call cycle, when the next command is executed directly.
            ClientCommandSchedulerFactory.getInstance().getClientCommandScheduler().schedule(new ClientCommand<Void>() {
                public void execute() {
                    isSessionInitialized = true;
                    executeCommands();
                }

                public AsyncCallback<Void> getCommandCallback() { return null; }
            });
        }
    }
}
