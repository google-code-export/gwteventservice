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

/**
 * The RemoteEventServiceFactory is used to create the RemoteEventService and to ensure that only one instance of
 * RemoteEventServiceFactory and RemoteEventService exists (singleton).
 * @see DefaultRemoteEventService
 *
 * @author sstrohschein
 * <br>Date: 08.06.2008
 * <br>Time: 14:44:17
 */
public class RemoteEventServiceFactory
{
    private static RemoteEventServiceFactory myInstance;

    private RemoteEventService myRemoteEventService;

    /**
     * The RemoteEventServiceFactory should be created via the getInstance method.
     * @see RemoteEventServiceFactory#getInstance()
     */
    private RemoteEventServiceFactory() {}

    /**
     * This method should be used to create an instance of RemoteEventServiceFactory.
     * RemoteEventServiceFactory is a singleton, so this method returns always the same instance of
     * RemoteEventServiceFactory.
     * @return RemoteEventServiceFactory (singleton)
     */
    public static synchronized RemoteEventServiceFactory getInstance() {
        if(myInstance == null) {
            myInstance = new RemoteEventServiceFactory();
        }
        return myInstance;
    }

    /**
     * This method should be used to create an instance of RemoteEventService.
     * RemoteEventService is a singleton, so this method returns always the same instance of RemoteEventService.
     * The session is needed to generate the client/user id.
     * @return RemoteEventService (singleton)
     */
    public synchronized RemoteEventService getRemoteEventService() {
        if(myRemoteEventService == null) {
            myRemoteEventService = new DefaultRemoteEventService(new GWTRemoteEventConnector());
        }
        return myRemoteEventService;
    }

    /**
     * Resets/removes all listeners, but doesn't reset the {@link RemoteEventService} instance.
     */
    public static synchronized void reset() {
        RemoteEventService theRemoteEventService = getInstance().getRemoteEventService();
        theRemoteEventService.removeListeners();
    }
}