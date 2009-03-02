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
package de.novanic.eventservice.service;

import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.exception.NoSessionAvailableException;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * DefaultEventExecutorService can be used to add events via the server side.
 *
 * @author sstrohschein
 * <br>Date: 20.07.2008
 * <br>Time: 14:20:48
 */
public class DefaultEventExecutorService implements EventExecutorService
{
    private static EventRegistry myEventRegistry;

    private final String myClientId;

    static {
        init();
    }

    /**
     * Creates a new EventExecutorService with a client/user id. All methods of EventExecutorService use the client/user
     * id as default.
     * This method shouldn't called directly. To create an instance of the service, {@link EventExecutorServiceFactory}
     * should be used.
     * @param aClientId client/user id
     * @see EventExecutorServiceFactory
     */
    DefaultEventExecutorService(String aClientId) {
        myClientId = aClientId;
    }

    /**
     * Checks if the user is registered for event listening.
     * @return true when the user is listening, otherwise false
     */
    public boolean isUserRegistered() {
        return myEventRegistry.isUserRegistered(getClientId());
    }

    /**
     * Checks if the user is registered for event listening.
     * @param aDomain domain to check the registration for the user
     * @return true when the user is listening, otherwise false
     */
    public boolean isUserRegistered(Domain aDomain) {
        return myEventRegistry.isUserRegistered(aDomain, getClientId());
    }

    /**
     * Adds an event for all users
     * @param aDomain the domain to add the event
     * @param anEvent event to add
     */
    public void addEvent(Domain aDomain, Event anEvent) {
        myEventRegistry.addEvent(aDomain, anEvent);
    }

    /**
     * Adds an event for a specific user
     * @param anEvent event to add
     */
    public void addEventUserSpecific(Event anEvent) {
        myEventRegistry.addEventUserSpecific(getClientId(), anEvent);
    }

    /**
     * Changes the {@link de.novanic.eventservice.client.event.filter.EventFilter} for the user-domain combination.
     * The {@link de.novanic.eventservice.client.event.filter.EventFilter} can be removed with the method
     * {@link de.novanic.eventservice.service.EventExecutorService#removeEventFilter(de.novanic.eventservice.client.event.domain.Domain)}
     * or when that method is called with NULL as the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * parameter value.
     */
    public void setEventFilter(Domain aDomain, EventFilter anEventFilter) {
        myEventRegistry.setEventFilter(aDomain, getClientId(), anEventFilter);
    }

    /**
     * Removes the {@link de.novanic.eventservice.client.event.filter.EventFilter} of the domain.
     * @param aDomain domain to drop the EventFilter from
     */
    public void removeEventFilter(Domain aDomain) {
        myEventRegistry.removeEventFilter(aDomain, getClientId());
    }

    /**
     * Returns the client id.
     * @return client id
     */
    private String getClientId() {
        if(myClientId == null) {
            throw new NoSessionAvailableException();
        }
        return myClientId;
    }

    /**
     * Initializes the EventExecutorService.
     */
    private static void init() {
        final EventRegistryFactory theEventRegistryFactory = EventRegistryFactory.getInstance();
        myEventRegistry = theEventRegistryFactory.getEventRegistry();
    }
}