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
package de.novanic.eventservice;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.tools.GWTTestSuite;

import de.novanic.eventservice.client.event.GwtTestRemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactoryTest;
import de.novanic.eventservice.clientmock.GWTRemoteEventConnectorTest;
import de.novanic.eventservice.clientmock.RemoteEventServiceMockTest;
import de.novanic.eventservice.clientmock.RemoteEventServiceRuntimeExceptionTest;
import de.novanic.eventservice.clientmock.RemoteEventServiceUnlistenerMockTest;
import de.novanic.eventservice.clientmock.event.command.*;
import de.novanic.eventservice.clientmock.event.command.schedule.ClientCommandSchedulerFactoryTest;

/**
 * @author sstrohschein
 * Date: 28.07.2008   
 * Time: 21:41:04
 */
public class GWTEventServiceTestSuite extends GWTTestCase
{
    public String getModuleName() {
    	return "de.novanic.eventservice.GWTEventService";
    }

    public static GWTTestSuite suite() {
        GWTTestSuite theGWTEventServiceTestSuite = new GWTTestSuite("GWTEventService - GWT-Tests");

        // --- Mock-Tests ---

        // Command
        theGWTEventServiceTestSuite.addTestSuite(ClientCommandSchedulerFactoryTest.class);
        theGWTEventServiceTestSuite.addTestSuite(InitEventServiceCommandTest.class);
        theGWTEventServiceTestSuite.addTestSuite(ActivationCommandTest.class);
        theGWTEventServiceTestSuite.addTestSuite(DeactivationCommandTest.class);
        theGWTEventServiceTestSuite.addTestSuite(DeregistrationEventFilterCommandTest.class);
        theGWTEventServiceTestSuite.addTestSuite(RegistrationEventFilterCommandTest.class);

        // Event
        theGWTEventServiceTestSuite.addTestSuite(RemoteEventServiceRuntimeExceptionTest.class);
        theGWTEventServiceTestSuite.addTestSuite(GWTRemoteEventConnectorTest.class);
        theGWTEventServiceTestSuite.addTestSuite(RemoteEventServiceFactoryTest.class);
        theGWTEventServiceTestSuite.addTestSuite(RemoteEventServiceMockTest.class);
        theGWTEventServiceTestSuite.addTestSuite(RemoteEventServiceUnlistenerMockTest.class);

        // --- GWT-Tests ---

        // Event
        theGWTEventServiceTestSuite.addTestSuite(GwtTestRemoteEventService.class);

        return theGWTEventServiceTestSuite;
    }
}