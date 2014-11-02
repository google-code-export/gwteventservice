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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.DefaultRemoteEventService;
import de.novanic.eventservice.client.event.GWTRemoteEventConnector;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.service.creator.DefaultEventServiceCreator;
import de.novanic.eventservice.client.event.service.creator.EventServiceCreator;

/**
 * @author sstrohschein
 *         <br>Date: 15.03.2009
 *         <br>Time: 16:29:36
 */
public class DefaultRemoteEventServiceFactoryTestMode
{
    private static class TestDefaultRemoteEventServiceFactoryHolder {
        private static DefaultRemoteEventServiceFactoryTestMode INSTANCE = new DefaultRemoteEventServiceFactoryTestMode();
    }

    public static DefaultRemoteEventServiceFactoryTestMode getInstance() {
        return TestDefaultRemoteEventServiceFactoryHolder.INSTANCE;
    }

    public RemoteEventService getDefaultRemoteEventService() {
        EventServiceCreator theEventServiceCreator = DefaultEventServiceCreator.getInstance();
        return new DefaultRemoteEventServiceAccessible(new GWTRemoteEventConnectorAccessible(theEventServiceCreator));
    }

    public RemoteEventService getDefaultRemoteEventService(EventServiceAsync anEventService) {
        return getDefaultRemoteEventService(getGWTRemoteEventConnector(anEventService));
    }

    public RemoteEventService getDefaultRemoteEventService(RemoteEventConnector aRemoteEventConnector) {
        return new DefaultRemoteEventServiceAccessible(aRemoteEventConnector);
    }

    public GWTRemoteEventConnector getGWTRemoteEventConnector(final EventServiceAsync anEventService) {
        return getGWTRemoteEventConnector(new EventServiceCreator() {
            public EventServiceAsync createEventService() {
                return anEventService;
            }
        });
    }

    public GWTRemoteEventConnector getGWTRemoteEventConnector(EventServiceCreator anEventServiceCreator) {
        return new GWTRemoteEventConnectorAccessible(anEventServiceCreator);
    }

    private class GWTRemoteEventConnectorAccessible extends GWTRemoteEventConnector
    {
        protected GWTRemoteEventConnectorAccessible(EventServiceCreator aGWTEventServiceCreator) {
            super(aGWTEventServiceCreator);
        }
    }

    private class DefaultRemoteEventServiceAccessible extends DefaultRemoteEventService
    {
        protected DefaultRemoteEventServiceAccessible(RemoteEventConnector aRemoteEventConnector) {
            super(aRemoteEventConnector);
        }
    }
}