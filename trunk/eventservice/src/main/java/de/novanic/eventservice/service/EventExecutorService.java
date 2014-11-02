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
package de.novanic.eventservice.service;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * EventExecutorService can be used to add events via the server side.
 *
 * @author sstrohschein
 * <br>Date: 20.07.2008
 * <br>Time: 14:26:53
 */
public interface EventExecutorService
{
    /**
     * Checks if the user is registered for event listening.
     * @return true when the user is listening, otherwise false
     */
    boolean isUserRegistered();

    /**
     * Checks if the user is registered for event listening.
     * @param aDomain domain to check the registration for the user
     * @return true when the user is listening, otherwise false
     */
    boolean isUserRegistered(Domain aDomain);

    /**
     * Adds an event for all users
     * @param aDomain the domain to add the event
     * @param anEvent event to add
     */
    void addEvent(Domain aDomain, Event anEvent);

    /**
     * Adds an event for a specific user
     * @param anEvent event to add
     */
    void addEventUserSpecific(Event anEvent);

    /**
     * Changes the {@link de.novanic.eventservice.client.event.filter.EventFilter} for the user-domain combination.
     * The {@link de.novanic.eventservice.client.event.filter.EventFilter} can be removed with the method
     * {@link de.novanic.eventservice.service.EventExecutorService#removeEventFilter(de.novanic.eventservice.client.event.domain.Domain)}
     * or when that method is called with NULL as the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * parameter value.
     * @param aDomain domain to set the {@link de.novanic.eventservice.client.event.filter.EventFilter} (user-domain combination)
     * @param anEventFilter new {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    void setEventFilter(Domain aDomain, EventFilter anEventFilter);

    /**
     * Returns the EventFilter for the user domain combination.
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    EventFilter getEventFilter(Domain aDomain);

    /**
     * Removes the {@link de.novanic.eventservice.client.event.filter.EventFilter} of the domain.
     * @param aDomain domain to drop the {@link de.novanic.eventservice.client.event.filter.EventFilter} from
     */
    void removeEventFilter(Domain aDomain);
}