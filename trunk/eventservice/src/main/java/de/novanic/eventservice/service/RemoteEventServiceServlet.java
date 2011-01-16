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

import javax.servlet.http.HttpServletRequest;

/**
 * RemoteEventServiceServlet is an implementation of {@link de.novanic.eventservice.service.EventExecutorService} as a servlet
 * and can be used to add events via the server side.
 *
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
     * Changes the {@link de.novanic.eventservice.client.event.filter.EventFilter} for the user-domain combination.
     * The {@link de.novanic.eventservice.client.event.filter.EventFilter} can be removed with the method
     * {@link de.novanic.eventservice.service.EventExecutorService#removeEventFilter(de.novanic.eventservice.client.event.domain.Domain)}
     * or when that method is called with NULL as the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * parameter value.
     * @param aDomain domain to set the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * @param anEventFilter new {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    public void setEventFilter(Domain aDomain, EventFilter anEventFilter) {
        getEventExecutorService().setEventFilter(aDomain, anEventFilter);
    }

    /**
     * Returns the EventFilter for the user domain combination.
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    public EventFilter getEventFilter(Domain aDomain) {
        return getEventExecutorService().getEventFilter(aDomain);
    }

    /**
     * Removes the {@link de.novanic.eventservice.client.event.filter.EventFilter} of the domain.
     * @param aDomain domain to drop the {@link de.novanic.eventservice.client.event.filter.EventFilter} from
     */
    public void removeEventFilter(Domain aDomain) {
        getEventExecutorService().removeEventFilter(aDomain);
    }

    /**
     * Creates an instance of {@link de.novanic.eventservice.service.EventExecutorService}.
     * @return a new instance of {@link de.novanic.eventservice.service.EventExecutorService}
     */
    private EventExecutorService getEventExecutorService() {
        final EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        return theEventExecutorServiceFactory.getEventExecutorService(getRequest());
    }

    /**
     * Returns the current request.
     * @return current request
     */
    protected HttpServletRequest getRequest() {
        return getThreadLocalRequest();
    }

    /**
     * This method is overridden because applications with various GWT versions got a {@link SecurityException}
     * @throws SecurityException
     */
    @Override
    protected void checkPermutationStrongName() throws SecurityException {}
}
