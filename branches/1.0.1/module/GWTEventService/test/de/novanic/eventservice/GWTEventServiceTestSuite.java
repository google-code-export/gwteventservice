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
package de.novanic.eventservice;

import junit.framework.TestSuite;
import junit.framework.Test;
import de.novanic.eventservice.client.event.RemoteEventServiceMockTest;
import de.novanic.eventservice.client.event.RemoteEventServiceFactoryTest;
import de.novanic.eventservice.client.event.GWTRemoteEventConnectorTest;

/**
 * @author sstrohschein
 * Date: 04.08.2008
 * Time: 00:50:47
 */
public class GWTEventServiceTestSuite extends TestSuite
{
    public static Test suite() {
        TestSuite theSuite = new TestSuite();

        theSuite.setName("GWTEventService - Tests");

        //Event
        theSuite.addTestSuite(GWTRemoteEventConnectorTest.class);
        theSuite.addTestSuite(RemoteEventServiceFactoryTest.class);
        theSuite.addTestSuite(RemoteEventServiceMockTest.class);

        return theSuite;
    }
}