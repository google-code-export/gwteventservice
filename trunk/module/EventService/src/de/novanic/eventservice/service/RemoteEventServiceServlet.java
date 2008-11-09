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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;

/**
 * @author sstrohschein
 *         <br>Date: 20.09.2008
 *         <br>Time: 15:31:18
 */
public abstract class RemoteEventServiceServlet extends RemoteServiceServlet implements EventExecutorService
{
    /**
     * Checks if the user is registered for event listening.
     * @return true when the user is listening, otherwise false
     */
    public boolean isUserRegistered() {
        return getEventExecutorService().isUserRegistered();
    }

    /**
     * Checks if the user is registered for event listening.
     * @param aDomain domain to check the registration for the user
     * @return true when the user is listening, otherwise false
     */
    public boolean isUserRegistered(Domain aDomain) {
        return getEventExecutorService().isUserRegistered(aDomain);
    }

    /**
     * Adds an event for all users
     * @param aDomain the domain to add the event
     * @param anEvent event to add
     */
    public void addEvent(Domain aDomain, Event anEvent) {
        getEventExecutorService().addEvent(aDomain, anEvent);
    }

    /**
     * Adds an event for a specific user
     * @param anEvent event to add
     */
    public void addEventUserSpecific(Event anEvent) {
        getEventExecutorService().addEventUserSpecific(anEvent);
    }

    /**
     * Changes the {@link EventFilter} for the user-domain combination.
     * @param aDomain domain to set the {@link EventFilter} (user-domain combination)
     * @param anEventFilter new {@link EventFilter}
     */
    public void setEventFilter(Domain aDomain, EventFilter anEventFilter) {
        getEventExecutorService().setEventFilter(aDomain, anEventFilter);
    }

    /**
     * Creates an instance of {@link de.novanic.eventservice.service.EventExecutorService}.
     * @return a new instance of {@link de.novanic.eventservice.service.EventExecutorService}
     */
    protected EventExecutorService getEventExecutorService() {
        final EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        return theEventExecutorServiceFactory.getEventExecutorService(getThreadLocalRequest().getSession());
    }
}