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
package de.novanic.eventservice.client.config;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2010
 *         <br>Time: 13:42:18
 */
public class RemoteEventServiceConfigurationTransferableTest extends TestCase
{
    public void testInit() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable();
        assertNull(theConfiguration.getMinWaitingTime());
        assertNull(theConfiguration.getMaxWaitingTime());
        assertNull(theConfiguration.getTimeoutTime());
        assertNull(theConfiguration.getConnectionId());
    }

    public void testInit_2() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(1, 2, 3, "4");
        assertEquals(Integer.valueOf(1), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(2), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(3), theConfiguration.getTimeoutTime());
        assertEquals("4", theConfiguration.getConnectionId());
    }

    public void testEquals() {
        EventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(1, 2, 3, "4");
        assertEquals(theConfiguration, theConfiguration);
        assertEquals(theConfiguration, new RemoteEventServiceConfigurationTransferable(1, 2, 3, "4"));
        assertEquals(theConfiguration, new RemoteEventServiceConfigurationTransferable(1, 2, 3, "4"));
        assertEquals(theConfiguration.hashCode(), new RemoteEventServiceConfigurationTransferable(1, 2, 3, "4").hashCode());

        EventServiceConfigurationTransferable theConfiguration_2 = null;
        assertFalse(theConfiguration.equals(theConfiguration_2));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(9, 2, 3, "4")));
        assertNotSame(theConfiguration.hashCode(), new RemoteEventServiceConfigurationTransferable(9, 2, 3, "4").hashCode());
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 9, 3, "4")));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 2, 9, "4")));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfigurationTransferable(1, 2, 3, "9")));
    }
}