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
package de.novanic.eventservice.config.loader;

import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import junit.framework.TestCase;
import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 15:43:54
 */
public class DefaultConfigurationLoaderTest extends TestCase
{
    private ConfigurationLoader myConfigurationLoader;

    public void setUp() {
        myConfigurationLoader = new DefaultConfigurationLoader();
    }

    public void testIsAvailable() {
        assertTrue(myConfigurationLoader.isAvailable());
    }

    public void testLoad() {
        EventServiceConfiguration theEventServiceConfiguration = myConfigurationLoader.load();
        assertEquals("Default Configuration", theEventServiceConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(0), theEventServiceConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theEventServiceConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theEventServiceConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theEventServiceConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theEventServiceConfiguration.getConnectionStrategyClientConnectorClassName());
        assertNull(theEventServiceConfiguration.getConnectionStrategyServerConnectorClassName());
    }
}
