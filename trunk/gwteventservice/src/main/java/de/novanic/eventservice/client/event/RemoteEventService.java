/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * Other licensing for GWTEventService may also be possible on request.
 * Please view the license.txt of the project for more information.
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
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;

import java.util.List;
import java.util.Set;

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
 * <br>Date: 09.08.2008
 * <br>Time: 22:22:14
 */
public interface RemoteEventService
{
    /**
     * Adds a listener for a domain.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     */
    void addListener(Domain aDomain, RemoteEventListener aRemoteListener);

    /**
     * Adds a listener for a domain.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param aCallback callback (only called when no listener is registered for the domain)
     */
    void addListener(Domain aDomain, RemoteEventListener aRemoteListener, AsyncCallback<Void> aCallback);

    /**
     * Adds a listener for a domain. The EventFilter is applied to the domain to filter events before the
     * RemoteEventListener recognizes the event.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     */
    void addListener(Domain aDomain, RemoteEventListener aRemoteListener, EventFilter anEventFilter);

    /**
     * Adds a listener for a domain. The EventFilter is applied to the domain to filter events before the
     * RemoteEventListener recognizes the event.
     * It activates the RemoteEventService if it was inactive.
     * @param aDomain domain
     * @param aRemoteListener new listener
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @param aCallback callback (only called when no listener is registered for the domain)
     */
    void addListener(Domain aDomain, RemoteEventListener aRemoteListener, EventFilter anEventFilter, AsyncCallback<Void> aCallback);

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
    void addUnlistenListener(UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback);

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to listen for all
     * user/client domain deregistrations and timeouts.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}
     * to listen for all user/client domain deregistrations and timeouts.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param aCallback callback
     */
    void addUnlistenListener(UnlistenEventListener.Scope anUnlistenScope, UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback);

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
    void addUnlistenListener(UnlistenEventListener anUnlistenEventListener, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback);

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
    void addUnlistenListener(UnlistenEventListener.Scope anUnlistenScope, UnlistenEventListener anUnlistenEventListener, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback);

    /**
     * Removes a listener for a domain.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param aDomain domain
     * @param aRemoteListener listener to remove
     */
    void removeListener(Domain aDomain, RemoteEventListener aRemoteListener);

    /**
     * Removes a listener for a domain.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param aDomain domain
     * @param aRemoteListener listener to remove
     * @param aCallback callback
     */
    void removeListener(Domain aDomain, RemoteEventListener aRemoteListener, AsyncCallback<Void> aCallback);

    /**
     * Registers an EventFilter for a domain. This can be used when a listener is already added and an EventFilter
     * needed later or isn't available when the listener is added.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     */
    void registerEventFilter(Domain aDomain, EventFilter anEventFilter);

    /**
     * Registers an EventFilter for a domain. This can be used when a listener is already added and an EventFilter
     * needed later or isn't available when the listener is added.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events before RemoteEventListener
     * @param aCallback callback
     */
    void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback);

    /**
     * Deregisters the EventFilter for a domain.
     * @param aDomain domain to remove the EventFilter from
     */
    void deregisterEventFilter(Domain aDomain);

    /**
     * Deregisters the EventFilter for a domain.
     * @param aDomain domain to remove the EventFilter from
     * @param aCallback callback
     */
    void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback);

    /**
     * Checks if the RemoteEventService is active (listening).
     * @return true when active/listening, otherwise false
     */
    boolean isActive();

    /**
     * Returns all active domains (all domains where the client has listeners registered).
     * @return all active domains
     */
    Set<Domain> getActiveDomains();

    /**
     * Returns all registered listeners of a domain.
     * @param aDomain domain
     * @return all registered listeners of the domain
     */
    List<RemoteEventListener> getRegisteredListeners(Domain aDomain);

    /**
     * Removes all RemoteEventListeners and deactivates the RemoteEventService (stop listening).
     */
    void removeListeners();

    /**
     * Removes all RemoteEventListeners and deactivates the RemoteEventService (stop listening).
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    void removeListeners(AsyncCallback<Void> aCallback);

    /**
     * Calls unlisten for a set of domains (stop listening for these domains). The RemoteEventListeners for these
     * domains will also be removed.
     * {@link RemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomains domains to unlisten
     */
    void removeListeners(Set<Domain> aDomains);

    /**
     * Calls unlisten for a set of domains (stop listening for these domains). The RemoteEventListeners for these
     * domains will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomains domains to unlisten
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    void removeListeners(Set<Domain> aDomains, AsyncCallback<Void> aCallback);

    /**
     * Stops listening for the corresponding domain. The RemoteEventFilters for the domain will also be removed.
     * {@link RemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomain domain to unlisten
     */
    void removeListeners(Domain aDomain);

    /**
     * Stops listening for the corresponding domain. The RemoteEventFilters for the domain will also be removed.
     * {@link DefaultRemoteEventService#removeListeners()} can be used to call unlisten for all domains.
     * @param aDomain domain to unlisten
     * @param aCallback callback (only called when a listener is registered for the domain)
     */
    void removeListeners(Domain aDomain, AsyncCallback<Void> aCallback);

    /**
     * Removes an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}.
     * The RemoteEventService will get inactive, when no other listeners are registered.
     * @param anUnlistenEventListener {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} to remove
     * @param aCallback callback
     */
    void removeUnlistenListener(UnlistenEventListener anUnlistenEventListener, AsyncCallback<Void> aCallback);

    /**
     * Stops listening for {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} instances.
     * @param aCallback callback (only called when an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} is registered)
     */
    void removeUnlistenListeners(AsyncCallback<Void> aCallback);

    /**
     * Adds / sends an event to a domain. The event will be received from all clients which are registered to that domain.
     * User-specific events can be added with the usage of this domain: {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN}.
     * @param aDomain domain
     * @param anEvent event
     */
    void addEvent(Domain aDomain, Event anEvent);

    /**
     * Adds / sends an event to a domain. The event will be received from all clients which are registered to that domain.
     * User-specific events can be added with the usage of this domain: {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN}.
     * @param aDomain domain
     * @param anEvent event
     * @param aCallback callback
     */
    void addEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback);
}