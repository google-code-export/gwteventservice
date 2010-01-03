/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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

import de.novanic.eventservice.client.event.service.EventServiceAsync;

/**
 * @author sstrohschein
 *         <br>Date: 15.03.2009
 *         <br>Time: 16:29:36
 */
public class TestDefaultRemoteEventServiceFactory
{
    private static class TestDefaultRemoteEventServiceFactoryHolder {
        private static TestDefaultRemoteEventServiceFactory INSTANCE = new TestDefaultRemoteEventServiceFactory();
    }

    public static TestDefaultRemoteEventServiceFactory getInstance() {
        return TestDefaultRemoteEventServiceFactoryHolder.INSTANCE;
    }

    public RemoteEventService getDefaultRemoteEventService() {
        return new DefaultRemoteEventService(new GWTRemoteEventConnector());
    }

    public RemoteEventService getDefaultRemoteEventService(EventServiceAsync anEventService) {
        return new DefaultRemoteEventService(getGWTRemoteEventConnector(anEventService));
    }

    public GWTRemoteEventConnector getGWTRemoteEventConnector(EventServiceAsync anEventService) {
        return new GWTRemoteEventConnector(anEventService);
    }
}