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
package de.novanic.eventservice.clientmock.config;

import com.google.gwt.junit.GWTMockUtilities;
import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 12:24:26
 */
@RunWith(JUnit4.class)
public class ConfigurationTransferableDependentFactoryTest
{
    private EventServiceConfigurationTransferable myConfigurationBackup;

    @Before
    public void setUp() {
        myConfigurationBackup = ConfigurationTransferableDependentFactory.getConfiguration();
        if(myConfigurationBackup != null) {
            ConfigurationTransferableDependentFactory.getInstance(myConfigurationBackup);
        }
    }

    @After
    public void tearDown() {
        //reset for the old configuration
        ConfigurationTransferableDependentFactory.reset();
        if(myConfigurationBackup != null) {
            ConfigurationTransferableDependentFactory.getInstance(myConfigurationBackup);
        }
    }

    @Test
    public void testGetInstance() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration));
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration));
    }

    @Test
    public void testGetInstance_2() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
    }

    @Test
    public void testGetInstance_3() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory.reset();
        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
    }

    @Test
    public void testGetInstance_Error() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", String.class.getName());
        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
            fail("Exception expected, because the configured class isn't registered / unknown!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetInstance_Error_2() {
        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance(null);
            fail("Exception expected, because the type isn't a " + ConnectionStrategyClientConnector.class.getSimpleName() + '!');
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetInstance_Error_3() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", "NotExistingClassXY");
        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
            fail("Exception expected, because the NotExistingClassXY class couldn't be found!");
        } catch(ConfigurationException e) {
            assertNull(e.getCause());
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertNull(e.getCause().getCause());
        }
    }

    @Test
    public void testGetInstance_Error_4() {
        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance();
            fail("Exception expected, because the configured class isn't registered / unknown!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetConnectionStrategyClientConnector() {
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable(DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory.reset();
        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theConfig);

        final ConnectionStrategyClientConnector theConnectionStrategyClientConnector = theConfigurationTransferableDependentFactory.getConnectionStrategyClientConnector();
        assertNotNull(theConnectionStrategyClientConnector);
        assertTrue(theConnectionStrategyClientConnector instanceof DefaultClientConnector);
    }

    @Test
    public void testGetConnectionStrategyClientConnector_Streaming() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "12345678", GWTStreamingClientConnector.class.getName());

        GWTMockUtilities.disarm();

        ConfigurationTransferableDependentFactory.reset();
        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);

        GWTMockUtilities.restore();

        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());

        //NULL because GWTMockUtilities is used
        assertNull(theConfigurationTransferableDependentFactory.getConnectionStrategyClientConnector());
    }

    @Test
    public void testGetConnectionStrategyClientConnector_Error() {
        final String theStringClassName = String.class.getName();
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable(theStringClassName);

        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance(theConfig);
            fail("Exception expected, because the \"" + theStringClassName + "\" class is not compatible with the class \"" + ConnectionStrategyClientConnector.class + "\"!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains(theStringClassName));
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(theStringClassName));
        }
    }

    @Test
    public void testGetConnectionStrategyClientConnector_Error_2() {
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable(DefaultStreamingClientConnector.class.getName());

        try {
            ConfigurationTransferableDependentFactory.reset();
            ConfigurationTransferableDependentFactory.getInstance(theConfig);
            fail("Exception expected, because " + DefaultStreamingClientConnector.class.getName() + " is unknown!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains(DefaultStreamingClientConnector.class.getName()));
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(DefaultStreamingClientConnector.class.getName()));
        }
    }

    private class TestEventServiceConfigurationTransferable implements EventServiceConfigurationTransferable
    {
        private String myConnectionStrategyClientConnectorClassName;

        public TestEventServiceConfigurationTransferable(String aConnectionStrategyClientConnectorClassName) {
            myConnectionStrategyClientConnectorClassName = aConnectionStrategyClientConnectorClassName;
        }

        public Integer getMinWaitingTime() {
            return 0;
        }

        public Integer getMaxWaitingTime() {
            return 0;
        }

        public Integer getTimeoutTime() {
            return 0;
        }

        public Integer getReconnectAttemptCount() {
            return 0;
        }

        public String getConnectionId() {
            return "12345678";
        }

        public String getConnectionStrategyClientConnector() {
            return myConnectionStrategyClientConnectorClassName;
        }
    }
}