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
package de.novanic.eventservice.client.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2010
 *         <br>Time: 13:42:18
 */
@RunWith(JUnit4.class)
public class RemoteEventServiceConfigurationTransferableTest
{
    @Test
    public void testInit() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable();
        assertNull(theConfiguration.getMinWaitingTime());
        assertNull(theConfiguration.getMaxWaitingTime());
        assertNull(theConfiguration.getTimeoutTime());
        assertNull(theConfiguration.getReconnectAttemptCount());
        assertNull(theConfiguration.getConnectionId());
        assertNull(theConfiguration.getConnectionStrategyClientConnector());
    }

    @Test
    public void testInit_2() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "5", "client_connector");
        assertEquals(Integer.valueOf(1), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(2), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(3), theConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(4), theConfiguration.getReconnectAttemptCount());
        assertEquals("5", theConfiguration.getConnectionId());
        assertEquals("client_connector", theConfiguration.getConnectionStrategyClientConnector());
    }

    @Test
    public void testEquals() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "5", null);
        assertEquals(theConfiguration, theConfiguration);
        assertEquals(theConfiguration, new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "5", null));
        assertEquals(theConfiguration, new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "5", null));
        assertEquals(theConfiguration.hashCode(), new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "5", null).hashCode());

        EventServiceConfigurationTransferable theConfiguration_2 = null;
        assertFalse(theConfiguration.equals(theConfiguration_2));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(9, 2, 3, 4, "5", null)));
        assertNotSame(theConfiguration.hashCode(), new RemoteEventServiceConfigurationTransferable(9, 2, 3, 4, "5", null).hashCode());
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 9, 3, 4, "5", null)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 2, 9, 4, "5", null)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 2, 3, 9, "5", null)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 2, 3, 4, "9", null)));
    }
}