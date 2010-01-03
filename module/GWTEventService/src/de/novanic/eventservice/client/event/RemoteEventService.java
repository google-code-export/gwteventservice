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
}