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
package de.novanic.eventservice.client.event.service;

import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.DomainEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Set;
import java.util.List;

/**
 * EventService is the server side interface to register listen requests for domains and to add events.
 *
 * @author sstrohschein
 *         <br>Date: 05.06.2008
 *         <br>Time: 19:07:07
 */
public interface EventServiceAsync
{
    /**
     * Initializes the {@link de.novanic.eventservice.client.event.service.EventService}.
     */
    void initEventService(AsyncCallback<Void> async);

    /**
     * Register listen for a domain.
     *
     * @param aDomain domain to listen to
     */
    void register(Domain aDomain, AsyncCallback<Void> async);

    /**
     * Register listen for a domain.
     *
     * @param aDomain       domain to listen to
     * @param anEventFilter EventFilter to filter events
     */
    void register(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> async);

    /**
     * Register listen for a domain.
     *
     * @param aDomains domains to listen to
     */
    void register(Set<Domain> aDomains, AsyncCallback<Void> async);

    /**
     * Register listen for domains.
     *
     * @param aDomains      domains to listen to
     * @param anEventFilter EventFilter to filter events (applied to all domains)
     */
    void register(Set<Domain> aDomains, EventFilter anEventFilter, AsyncCallback<Void> async);

    /**
     * Registers an {@link de.novanic.eventservice.client.event.filter.EventFilter} for the domain.
     *
     * @param aDomain       domain to register the EventFilter to
     * @param anEventFilter EventFilter to filter events for the domain
     */
    void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> async);

    /**
     * Deregisters the {@link de.novanic.eventservice.client.event.filter.EventFilter} of the domain.
     *
     * @param aDomain domain to drop the EventFilters from
     */
    void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> async);

    /**
     * Returns the EventFilter for the user domain combination.
     *
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    void getEventFilter(Domain aDomain, AsyncCallback<EventFilter> async);

    /**
     * The listen method returns all events for the user (events for all domains where the user is registered and user
     * specific events). If no events are available, the method waits a defined time before the events are returned.
     * The client side calls the method with a defined interval to receive all events. If the client don't call the
     * method in the interval, the user will be removed from the EventRegistry. The timeout time and the waiting time
     * can be configured with EventServiceConfiguration/-Factory (server side) and initialized with the init method of
     * EventRegistryFactory (server side).
     *
     * @return list of events
     */
    void listen(AsyncCallback<List<DomainEvent>> async);

    /**
     * Unlisten for events (for the current user) in all domains (deregisteres the user from all domains).
     */
    void unlisten(AsyncCallback<Void> async);

    /**
     * Unlisten for events
     *
     * @param aDomain the domain to unlisten
     */
    void unlisten(Domain aDomain, AsyncCallback<Void> async);

    /**
     * Unlisten for events (for the current user) in the domains and deregisteres the user from the domains.
     *
     * @param aDomains set of domains to unlisten
     */
    void unlisten(Set<Domain> aDomains, AsyncCallback<Void> async);

    /**
     * Checks if the user is registered for event listening.
     *
     * @param aDomain domain to check
     * @return true when the user is registered for listening, otherwise false
     */
    void isUserRegistered(Domain aDomain, AsyncCallback<Boolean> async);

    /**
     * Adds an event for all users in the domain.
     *
     * @param aDomain domain to add the event
     * @param anEvent event to add
     */
    void addEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> async);

    /**
     * Adds an event only for the current user.
     *
     * @param anEvent event to add to the user
     */
    void addEventUserSpecific(Event anEvent, AsyncCallback<Void> async);

    /**
     * Returns the domain names, where the user is listening to
     *
     * @return collection of domain names
     */
    void getActiveListenDomains(AsyncCallback<Set<Domain>> async);
}
