/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschr�nkt)
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
package de.novanic.eventservice;

import de.novanic.eventservice.client.DefaultClientHandlerTest;
import de.novanic.eventservice.client.config.ConfigurationExceptionTest;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferableTest;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnectorTest;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnectorTest;
import junit.framework.TestSuite;
import junit.framework.Test;
import de.novanic.eventservice.client.logger.ClientLoggerFactoryTest;
import de.novanic.eventservice.client.logger.GWTClientLoggerTest;
import de.novanic.eventservice.client.event.DomainEventTest;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventTest;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListenerAdapterTest;
import de.novanic.eventservice.client.event.domain.DefaultDomainTest;
import de.novanic.eventservice.client.event.domain.DomainFactoryTest;
import de.novanic.eventservice.client.event.filter.DefaultEventFilterTest;
import de.novanic.eventservice.client.event.filter.CompositeEventFilterTest;
import de.novanic.eventservice.client.event.filter.EventFilterFactoryTest;
import de.novanic.eventservice.client.command.DefaultRemoteCommandTest;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 20:20:23
 */
public class EventServiceRPCTestSuite
{
    public static Test suite() {
        TestSuite theSuite = new TestSuite();

        theSuite.setName("EventServiceRPC - Tests");

        // Client
        theSuite.addTestSuite(DefaultClientHandlerTest.class);

        // Logging
        theSuite.addTestSuite(ClientLoggerFactoryTest.class);
        theSuite.addTestSuite(GWTClientLoggerTest.class);

        // Configuration
        theSuite.addTestSuite(ConfigurationExceptionTest.class);
        theSuite.addTestSuite(RemoteEventServiceConfigurationTransferableTest.class);

        // Domain
        theSuite.addTestSuite(DomainFactoryTest.class);
        theSuite.addTestSuite(DefaultDomainTest.class);

        // Connection
        theSuite.addTestSuite(DefaultClientConnectorTest.class);
        theSuite.addTestSuite(DefaultStreamingClientConnectorTest.class);

        // Command
        theSuite.addTestSuite(DefaultRemoteCommandTest.class);

        // Event
        theSuite.addTestSuite(UnlistenEventTest.class);
        theSuite.addTestSuite(UnlistenEventListenerAdapterTest.class);
        theSuite.addTestSuite(DomainEventTest.class);
        theSuite.addTestSuite(DefaultEventFilterTest.class);
        theSuite.addTestSuite(CompositeEventFilterTest.class);
        theSuite.addTestSuite(EventFilterFactoryTest.class);

        return theSuite;
    }
}