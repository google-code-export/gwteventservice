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

import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 12:24:26
 */
public class ConfigurationTransferableDependentFactoryTest extends TestCase
{
    private EventServiceConfigurationTransferable myConfigurationBackup;

    public void setUp() {
        myConfigurationBackup = ConfigurationTransferableDependentFactory.getConfiguration();
    }

    public void tearDown() {
        //reset for the old configuration
        if(myConfigurationBackup != null) {
            ConfigurationTransferableDependentFactory.getInstance(myConfigurationBackup).reset(myConfigurationBackup);
        }
    }

    public void testGetInstance() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration));
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration));
    }

    public void testGetInstance_2() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
    }

    public void testGetInstance_3() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, "12345678", DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration);
        theConfigurationTransferableDependentFactory.reset(theEventServiceConfiguration);
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
        assertSame(theConfigurationTransferableDependentFactory, ConfigurationTransferableDependentFactory.getInstance());
    }

    public void testGetInstance_Error() {
        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(null);
        theConfigurationTransferableDependentFactory.reset(null, false);

        try {
            ConfigurationTransferableDependentFactory.getInstance();
            fail("Exception expected, because the factory wasn't initialized with a configuration!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_2() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, "12345678", String.class.getName());
        try {
            ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the configured class isn't registered / unknown!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_3() {
        try {
            ConfigurationTransferableDependentFactory.getInstance(null).reset(null);
            fail("Exception expected, because the type isn't a " + ConnectionStrategyClientConnector.class.getSimpleName() + '!');
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_4() {
        final EventServiceConfigurationTransferable theEventServiceConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, "12345678", "NotExistingClassXY");
        try {
            ConfigurationTransferableDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the NotExistingClassXY class couldn't be found!");
        } catch(ConfigurationException e) {
            assertNull(e.getCause());
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertNull(e.getCause().getCause());
        }
    }
    
    public void testGetConnectionStrategyClientConnector() {
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable();
        theConfig.setConnectionStrategyClientConnector(DefaultClientConnector.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theConfig);
        theConfigurationTransferableDependentFactory.reset(theConfig);

        final ConnectionStrategyClientConnector theConnectionStrategyClientConnector = theConfigurationTransferableDependentFactory.getConnectionStrategyClientConnector();
        assertNotNull(theConnectionStrategyClientConnector);
        assertTrue(theConnectionStrategyClientConnector instanceof DefaultClientConnector);
    }

    public void testGetConnectionStrategyClientConnector_Default() {
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable();
        theConfig.setConnectionStrategyClientConnector(null);

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theConfig);
        theConfigurationTransferableDependentFactory.reset(theConfig);

        final ConnectionStrategyClientConnector theConnectionStrategyClientConnector = theConfigurationTransferableDependentFactory.getConnectionStrategyClientConnector();
        assertNotNull(theConnectionStrategyClientConnector);
        assertTrue(theConnectionStrategyClientConnector instanceof DefaultClientConnector);
    }

    public void testGetConnectionStrategyClientConnector_Error() {
        final TestEventServiceConfigurationTransferable theConfig = new TestEventServiceConfigurationTransferable();
        theConfig.setConnectionStrategyClientConnector(String.class.getName());

        ConfigurationTransferableDependentFactory theConfigurationTransferableDependentFactory = ConfigurationTransferableDependentFactory.getInstance(theConfig);
        try {
            theConfigurationTransferableDependentFactory.reset(theConfig);
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(String.class.getName()));
        }
    }

    private class TestEventServiceConfigurationTransferable implements EventServiceConfigurationTransferable
    {
        private String myConnectionStrategyClientConnectorClassName;

        public Integer getMinWaitingTime() {
            return 0;
        }

        public Integer getMaxWaitingTime() {
            return 0;
        }

        public Integer getTimeoutTime() {
            return 0;
        }

        public String getConnectionId() {
            return "12345678";
        }

        public String getConnectionStrategyClientConnector() {
            return myConnectionStrategyClientConnectorClassName;
        }

        public void setConnectionStrategyClientConnector(String aConnectionStrategyClientConnectorClassName) {
            myConnectionStrategyClientConnectorClassName = aConnectionStrategyClientConnectorClassName;
        }
    }
}