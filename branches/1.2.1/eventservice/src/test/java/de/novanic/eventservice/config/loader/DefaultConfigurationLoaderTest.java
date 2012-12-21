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

import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import de.novanic.eventservice.config.EventServiceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 15:43:54
 */
@RunWith(JUnit4.class)
public class DefaultConfigurationLoaderTest
{
    private ConfigurationLoader myConfigurationLoader;

    @Before
    public void setUp() {
        myConfigurationLoader = new DefaultConfigurationLoader();
    }

    @Test
    public void testIsAvailable() {
        assertTrue(myConfigurationLoader.isAvailable());
    }

    @Test
    public void testLoad() {
        EventServiceConfiguration theEventServiceConfiguration = myConfigurationLoader.load();
        assertEquals("Default Configuration", theEventServiceConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(0), theEventServiceConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theEventServiceConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theEventServiceConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theEventServiceConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theEventServiceConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(LongPollingServerConnector.class.getName(), theEventServiceConfiguration.getConnectionStrategyServerConnectorClassName());
        assertEquals("utf-8", theEventServiceConfiguration.getConnectionStrategyEncoding());
    }
}
