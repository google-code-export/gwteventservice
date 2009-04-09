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
package de.novanic.eventservice.config;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 * Date: 10.08.2008
 * Time: 23:01:56
 */
public class RemoteEventServiceConfigurationTest extends TestCase
{
    public void testInit() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(1, 2, 3);
        assertEquals(1, theConfiguration.getMinWaitingTime());
        assertEquals(2, theConfiguration.getMaxWaitingTime());
        assertEquals(3, theConfiguration.getTimeoutTime());
    }

    public void testEquals() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(0, 1, 2);
        assertEquals(theConfiguration, theConfiguration);
        assertEquals(theConfiguration, new RemoteEventServiceConfiguration(0, 1, 2));
        assertTrue(theConfiguration.equals(new RemoteEventServiceConfiguration(0, 1, 2)));
        assertEquals(theConfiguration.hashCode(), new RemoteEventServiceConfiguration(0, 1, 2).hashCode());

        EventServiceConfiguration theConfiguration_2 = null;
        assertFalse(theConfiguration.equals(theConfiguration_2));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(9, 1, 2)));
        assertFalse(theConfiguration.hashCode() == new RemoteEventServiceConfiguration(9, 1, 2).hashCode());
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(0, 9, 2)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(0, 1, 9)));
    }
}