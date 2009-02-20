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

import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.EventServiceTestCase;

import java.io.InputStream;
import java.io.IOException;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 15:46:58
 */
public class PropertyConfigurationLoaderTest extends EventServiceTestCase
{
    public void testAvailable() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader();
        assertTrue(theConfigurationLoader.isAvailable());
    }

    public void testAvailable_2() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice_error.properties");
        assertTrue(theConfigurationLoader.isAvailable());
    }

    public void testAvailable_Error() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("notAnExistingFile");
        assertFalse(theConfigurationLoader.isAvailable());
        assertNull(theConfigurationLoader.load());
    }

    public void testLoad() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader();
        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals("Properties \"eventservice.properties\"", theConfiguration.getConfigDescription());
        assertEquals(0, theConfiguration.getMinWaitingTime());
        assertEquals(20000, theConfiguration.getMaxWaitingTime());
        assertEquals(90000, theConfiguration.getTimeoutTime());
    }

    public void testLoad_2() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice.bak.properties");
        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals("Properties \"eventservice.bak.properties\"", theConfiguration.getConfigDescription());
        assertEquals(2000, theConfiguration.getMinWaitingTime());
        assertEquals(5000, theConfiguration.getMaxWaitingTime());
        assertEquals(50000, theConfiguration.getTimeoutTime());
    }

    public void testLoad_Failure() {
        ConfigurationLoader theConfigurationLoader = new PropertyConfigurationLoader("eventservice_error.properties");
        try {
            theConfigurationLoader.load();
            fail("Exception expected!");
        } catch(ConfigurationException e) {}
    }

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
