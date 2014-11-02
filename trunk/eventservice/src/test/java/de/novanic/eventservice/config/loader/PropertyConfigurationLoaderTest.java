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
package de.novanic.eventservice.config.loader;

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.InputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 15:46:58
 */
@RunWith(JUnit4.class)
public class PropertyConfigurationLoaderTest extends EventServiceTestCase
{
    @Test
    public void testAvailable() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader();
        assertTrue(theConfigurationLoader.isAvailable());
    }

    @Test
    public void testAvailable_2() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice_error.properties");
        assertTrue(theConfigurationLoader.isAvailable());
    }

    @Test
    public void testAvailable_Error() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("notAnExistingFile");
        assertFalse(theConfigurationLoader.isAvailable());
        assertNull(theConfigurationLoader.load());
    }

    @Test
    public void testLoad() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader();
        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals("Properties \"eventservice.properties\"", theConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());
        assertNull(theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(LongPollingServerConnector.class.getName(), theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertNull(theConfiguration.getConnectionStrategyEncoding());
        assertNull(theConfiguration.getMaxEvents());
    }

    @Test
    public void testLoad_2() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice.bak.properties");
        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals("Properties \"eventservice.bak.properties\"", theConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(2000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(50000), theConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        assertEquals("de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector", theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(StreamingServerConnector.class.getName(), theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertEquals("iso-8859-1", theConfiguration.getConnectionStrategyEncoding());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxEvents());
    }

    @Test
    public void testLoad_3() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("empty.properties");
        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();

        assertNotNull(theConfiguration.getConfigDescription());
        assertEquals("Properties \"empty.properties\"", theConfiguration.getConfigDescription());

        assertNull(theConfiguration.getMinWaitingTime());
        assertNull(theConfiguration.getMaxWaitingTime());
        assertNull(theConfiguration.getTimeoutTime());
        assertNull(theConfiguration.getConnectionIdGeneratorClassName());
        assertNull(theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertNull(theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertNull(theConfiguration.getMaxEvents());
    }

    @Test
    public void testLoad_With_Defaults() {
        EventServiceConfiguration theConfiguration = EventServiceConfigurationFactory.getInstance().loadEventServiceConfiguration();
        assertEquals("Properties \"eventservice.properties\"", theConfiguration.getConfigDescription());
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());
        assertEquals(DefaultClientConnector.class.getName(), theConfiguration.getConnectionStrategyClientConnectorClassName());
        assertEquals(LongPollingServerConnector.class.getName(), theConfiguration.getConnectionStrategyServerConnectorClassName());
        assertEquals("utf-8", theConfiguration.getConnectionStrategyEncoding());
        assertEquals(Integer.valueOf(1000), theConfiguration.getMaxEvents());
    }

    @Test
    public void testLoad_Failure() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice_error.properties");
        try {
            theConfigurationLoader.load();
            fail("Exception expected!");
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testLoad_ClassLoaderError() throws Exception {
        ClassLoader theDefaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new DummyClassLoader(new DummyInputStream()));

        try {
            logOff();

            ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice_error.properties");
            theConfigurationLoader.load();
            fail("Exception expected!");
        } catch(ConfigurationException e) {
        } finally {
            Thread.currentThread().setContextClassLoader(theDefaultClassLoader);
            logOn();
        }
    }

    @Test
    public void testEquals() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader();
        ConfigurationLoader theConfigurationLoader_2 = new PropertyConfigurationLoader("eventservice_error.properties");
        ConfigurationLoader theConfigurationLoader_2_1 = new PropertyConfigurationLoader("eventservice_error.properties");
        ConfigurationLoader theConfigurationLoader_3 = new PropertyConfigurationLoader("eventservice.bak.properties");
        ConfigurationLoader theConfigurationLoader_4 = new PropertyConfigurationLoader("notAnExistingFile");

        assertTrue(theConfigurationLoader.equals(theConfigurationLoader));
        assertEquals(theConfigurationLoader.hashCode(), theConfigurationLoader.hashCode());
        assertFalse(theConfigurationLoader.equals(theConfigurationLoader_2));
        assertFalse(theConfigurationLoader.equals(theConfigurationLoader_2_1));
        assertFalse(theConfigurationLoader.equals(theConfigurationLoader_3));
        assertFalse(theConfigurationLoader.equals(theConfigurationLoader_4));

        assertTrue(theConfigurationLoader_2.equals(theConfigurationLoader_2));
        assertEquals(theConfigurationLoader_2.hashCode(), theConfigurationLoader_2.hashCode());
        assertTrue(theConfigurationLoader_2.equals(theConfigurationLoader_2_1));
        assertFalse(theConfigurationLoader_2.equals(theConfigurationLoader_3));
        assertFalse(theConfigurationLoader_2.equals(theConfigurationLoader_4));

        assertTrue(theConfigurationLoader_2_1.equals(theConfigurationLoader_2_1));
        assertEquals(theConfigurationLoader_2_1.hashCode(), theConfigurationLoader_2_1.hashCode());
        assertTrue(theConfigurationLoader_2_1.equals(theConfigurationLoader_2));
        assertFalse(theConfigurationLoader_2_1.equals(theConfigurationLoader_3));
        assertFalse(theConfigurationLoader_2_1.equals(theConfigurationLoader_4));

        assertTrue(theConfigurationLoader_3.equals(theConfigurationLoader_3));
        assertEquals(theConfigurationLoader_3.hashCode(), theConfigurationLoader_3.hashCode());
        assertFalse(theConfigurationLoader_3.equals(theConfigurationLoader_4));
    }

    private class DummyClassLoader extends ClassLoader
    {
        private InputStream myInputStream;

        private DummyClassLoader(InputStream anInputStream) {
            myInputStream = anInputStream;
        }

        public InputStream getResourceAsStream(String name) {
            return myInputStream;
        }
    }

    private class DummyInputStream extends InputStream
    {
        public void close() throws IOException {
            throw new IOException("testDummyException on closing");
        }

        public int read() throws IOException {
            throw new IOException("testDummyException on reading");
        }
    }
}
