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

import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.config.loader.ConfigurationException;
import de.novanic.eventservice.util.PlatformUtil;

/**
 * @author sstrohschein
 * Date: 10.08.2008
 * Time: 23:01:56
 */
public class RemoteEventServiceConfigurationTest extends EventServiceTestCase
{
    private static final String TEST_CONFIG_DESCRIPTION = "TestConfig";

    public void testInit() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, 2, 3);
        assertEquals(TEST_CONFIG_DESCRIPTION, theConfiguration.getConfigDescription());
        assertEquals(1, theConfiguration.getMinWaitingTime());
        assertEquals(2, theConfiguration.getMaxWaitingTime());
        assertEquals(3, theConfiguration.getTimeoutTime());
    }

    public void testInit_Error() {
        try {
            new RemoteEventServiceConfiguration(null, 1, 2, 3);
            fail(ConfigurationException.class.getName() + " expected!");
        } catch(ConfigurationException e) {}
    }

    public void testEquals() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2);
        assertEquals(theConfiguration, theConfiguration);
        assertEquals(theConfiguration, new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2));
        assertEquals(theConfiguration, new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2));
        assertEquals(theConfiguration.hashCode(), new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2).hashCode());

        EventServiceConfiguration theConfiguration_2 = null;
        assertFalse(theConfiguration.equals(theConfiguration_2));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 9, 1, 2)));
        assertNotSame(theConfiguration.hashCode(), new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 9, 1, 2).hashCode());
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 9, 2)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 9)));
    }

    public void testToString() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, 2, 3);
        final String theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: 1ms; Max.: 2ms; Timeout: 3ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());
    }
}
