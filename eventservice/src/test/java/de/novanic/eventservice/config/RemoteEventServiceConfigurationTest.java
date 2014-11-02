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
package de.novanic.eventservice.config;

import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGeneratorTest;
import de.novanic.eventservice.util.PlatformUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * Date: 10.08.2008
 * Time: 23:01:56
 */
@RunWith(JUnit4.class)
public class RemoteEventServiceConfigurationTest extends EventServiceTestCase
{
    private static final String TEST_CONFIG_DESCRIPTION = "TestConfig";

    @Test
    public void testInit() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, 2, 3, 4, SessionConnectionIdGeneratorTest.class.getName(), "client_connector", "server_connector", "utf-8", 1000);
        assertEquals(TEST_CONFIG_DESCRIPTION, theConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(1), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(2), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(3), theConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGeneratorTest.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        assertEquals("client_connector", theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals("server_connector", theConfiguration.getConnectionStrategyServerConnectorClassName());
    }

    @Test
    public void testEquals() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000);
        assertEquals(theConfiguration, theConfiguration);
        assertEquals(theConfiguration, new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000));
        assertEquals(theConfiguration, new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000));
        assertEquals(theConfiguration.hashCode(), new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000).hashCode());

        EventServiceConfiguration theConfiguration_2 = null;
        assertFalse(theConfiguration.equals(theConfiguration_2));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 9, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000)));
        assertNotSame(theConfiguration.hashCode(), new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 9, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000).hashCode());
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 9, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 9, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 9, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000)));
        assertFalse(theConfiguration.equals(new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 0, 1, 2, 3, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1001)));
    }

    @Test
    public void testToString() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, 2, 3, 4, SessionConnectionIdGeneratorTest.class.getName(), null, null, "utf-8", 1000);
        final String theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: 1ms; Max.: 2ms; Timeout: 3ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());
    }

    @Test
    public void testToString_2() {
        EventServiceConfiguration theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, null, 2, 3, 4, null, null, null, "utf-8", 1000);
        String theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: <undefined>ms; Max.: 2ms; Timeout: 3ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());
        
        theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, null, 3, 4, null, null, null, "utf-8", 1000);
        theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: 1ms; Max.: <undefined>ms; Timeout: 3ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());

        theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, 1, 2, null, null, null, null, null, "utf-8", 1000);
        theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: 1ms; Max.: 2ms; Timeout: <undefined>ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());

        theConfiguration = new RemoteEventServiceConfiguration(TEST_CONFIG_DESCRIPTION, null, null, null, null, null, null, null, "utf-8", 1000);
        theExpectedRepresentation = "EventServiceConfiguration (TestConfig)" + PlatformUtil.getNewLine()
                + "  Min.: <undefined>ms; Max.: <undefined>ms; Timeout: <undefined>ms";
        assertEquals(theExpectedRepresentation, theConfiguration.toString());
    }
}
