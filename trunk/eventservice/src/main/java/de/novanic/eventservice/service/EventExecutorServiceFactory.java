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

import javax.servlet.http.HttpSession;

/**
 * The EventExecutorServiceFactory is used to create the EventExecutorService and to ensure that only one instance of
 * EventExecutorServiceFactory and EventExecutorService exists (singleton).
 * @see EventExecutorService
 *
 * @author sstrohschein
 * <br>Date: 20.07.2008
 * <br>Time: 14:26:03
 */
public class EventExecutorServiceFactory
{
    /**
     * The EventExecutorServiceFactory should be created via the getInstance method.
     * @see EventExecutorServiceFactory#getInstance()
     */
    private EventExecutorServiceFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class EventExecutorServiceFactoryHolder {
        private static final EventExecutorServiceFactory INSTANCE = new EventExecutorServiceFactory();
    }

    /**
     * This method should be used to create an instance of EventExecutorServiceFactory.
     * EventExecutorServiceFactory is a singleton, so this method returns always the same instance of
     * EventExecutorServiceFactory.
     * @return EventExecutorServiceFactory (singleton)
     */
    public static EventExecutorServiceFactory getInstance() {
        return EventExecutorServiceFactoryHolder.INSTANCE;
    }

    /**
     * This method should be used to create an instance of EventExecutorService.
     * EventExecutorService is a singleton, so this method returns always the same instance of EventExecutorService.
     * The session is needed to generate the client/user id.
     * @param aHttpSession the session is needed to generate the client/user id
     * @return EventExecutorService
     */
    public EventExecutorService getEventExecutorService(final HttpSession aHttpSession) {
        String theClientId = null;
        //TODO: You can no longer determine a client id from an HttpSession alone (when SessionExtendedConnectionIdGenerator is configured)!
        if(aHttpSession != null) {
            theClientId = aHttpSession.getId();
        }
        return getEventExecutorService(theClientId);
    }

    /**
     * This method should be used to create an instance of EventExecutorService.
     * EventExecutorService is a singleton, so this method returns always the same instance of EventExecutorService.
     * @param aClientId the client/user id
     * @return EventExecutorService
     */
    public EventExecutorService getEventExecutorService(final String aClientId) {
        return new DefaultEventExecutorService(aClientId);
    }
}