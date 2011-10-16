/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschränkt)
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
package de.novanic.eventservice.config.loader;

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import junit.framework.TestCase;

import javax.servlet.ServletConfig;

import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * @author sstrohschein
 *         <br>Date: 01.07.2009
 *         <br>Time: 22:17:37
 */
public class WebDescriptorConfigurationLoaderTest extends TestCase
{
    public void testLoad() {
        ServletConfig theServletConfig = new ServletConfigDummy(true, false);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals(Integer.valueOf(30000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(120000), theConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(3), theConfiguration.getReconnectAttemptCount());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(LongPollingServerConnector.class.getName(), theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertEquals("iso-8859-1", theConfiguration.getConnectionStrategyEncoding());
    }

	public void testLoad_FQ() {
        ServletConfig theServletConfig = new ServletConfigDummy(true, true);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals(Integer.valueOf(40000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(1), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(130000), theConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(1), theConfiguration.getReconnectAttemptCount());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(LongPollingServerConnector.class.getName(), theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertEquals("utf-8", theConfiguration.getConnectionStrategyEncoding());
    }

    public void testLoad_IncompleteConfiguration() {
        ServletConfigDummy theServletConfig = new ServletConfigDummy(true, false);
        assertTrue(theServletConfig.removeParameter(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG));
        assertTrue(theServletConfig.removeParameter(ConfigParameter.CONNECTION_ID_GENERATOR));
        assertFalse(theServletConfig.removeParameter(ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR)); //isn't already configured
        assertTrue(theServletConfig.removeParameter(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR));
        assertTrue(theServletConfig.removeParameter(ConfigParameter.CONNECTION_STRATEGY_ENCODING));

        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals(Integer.valueOf(30000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(120000), theConfiguration.getTimeoutTime());
        assertNull(theConfiguration.getReconnectAttemptCount());
        assertNull(theConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertNull(theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertNull(theConfiguration.getConnectionStrategyEncoding());
    }

    public void testLoad_Error() {
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(null);

        assertFalse(theConfigurationLoader.isAvailable());
    }

    public void testLoad_Error_2() {
        ServletConfig theServletConfig = new ServletConfigDummy(false, false);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertFalse(theConfigurationLoader.isAvailable());
        try {
            theConfigurationLoader.load();
            fail(ConfigurationException.class.getName() + " expected!");
        } catch(ConfigurationException e) {}
    }
}