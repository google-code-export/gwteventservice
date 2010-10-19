/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.client.event.service.creator;

import com.google.gwt.core.client.GWT;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

/**
 * The {@link de.novanic.eventservice.client.event.service.creator.EventServiceCreator} is a factory to
 * create a new {@link de.novanic.eventservice.client.event.service.EventServiceAsync} which is required
 * for the connection to the server side.
 *
 * The default implementation uses a standard way to map GWT services and creates a service instance of
 * {@link de.novanic.eventservice.client.event.service.EventService}. 
 *
 * @author sstrohschein
 *         <br>Date: 19.10.2010
 *         <br>Time: 22:47:12
 */
public final class DefaultEventServiceCreator implements EventServiceCreator
{
    /**
     * The {@link DefaultEventServiceCreator} should be created via the getInstance method.
     * @see DefaultEventServiceCreator#getInstance()
     */
    private DefaultEventServiceCreator() {}

    /**
     * The {@link DefaultEventServiceCreator} should be created via the getInstance method.
     * @see DefaultEventServiceCreatorHolder#getInstance()
     */
    private static class DefaultEventServiceCreatorHolder {
        private static DefaultEventServiceCreator INSTANCE = new DefaultEventServiceCreator();
    }

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    public static EventServiceCreator getInstance() {
        return DefaultEventServiceCreatorHolder.INSTANCE;
    }

    /**
     * Creates a new {@link de.novanic.eventservice.client.event.service.EventServiceAsync} which is required
     * for the connection to the server side.
     * This implementation uses a standard way to map GWT services and creates a service instance of
     * {@link de.novanic.eventservice.client.event.service.EventService}.
     * @return {@link de.novanic.eventservice.client.event.service.EventServiceAsync} interface for the server side service
     */
    public EventServiceAsync createEventService() {
        return (EventServiceAsync)GWT.create(EventService.class);
    }
}