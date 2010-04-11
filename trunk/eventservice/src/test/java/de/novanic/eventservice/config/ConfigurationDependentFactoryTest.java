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
package de.novanic.eventservice.config;

import de.novanic.eventservice.service.connection.id.ConnectionIdGenerator;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sstrohschein
 *         <br>Date: 05.04.2010
 *         <br>Time: 14:12:21
 */
public class ConfigurationDependentFactoryTest extends TestCase
{
    private EventServiceConfiguration myConfigurationBackup;

    public void setUp() {
        myConfigurationBackup = ConfigurationDependentFactory.getConfiguration();
    }

    public void tearDown() {
        //reset for the old configuration
        if(myConfigurationBackup != null) {
            ConfigurationDependentFactory.getInstance(myConfigurationBackup).reset(myConfigurationBackup);
        }
    }

    public void testGetInstance() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName(), null, null);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance(theEventServiceConfiguration));
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance(theEventServiceConfiguration));
    }

    public void testGetInstance_2() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName(), null, null);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
    }

    public void testGetInstance_3() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName(), null, null);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        theConfigurationDependentFactory.reset(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
    }

    public void testGetInstance_Error() {
        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(null);
        theConfigurationDependentFactory.reset(null, false);

        try {
            ConfigurationDependentFactory.getInstance();
            fail("Exception expected, because the factory wasn't initialized with aconfiguration!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_2() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, String.class.getName(), null, null);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because no configuration is available!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_3() {
        try {
            ConfigurationDependentFactory.getInstance(null).reset(null);
            fail("Exception expected, because the type isn't a " + ConnectionIdGenerator.class.getSimpleName() + '!');
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_4() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, "NotExistingClassXY", null, null);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the NotExistingClassXY class couldn't be found!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof ClassNotFoundException);
        }
    }

    public void testGetInstance_Error_5() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, DummyConnectionIdGenerator.class.getName(), null, null);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the private " + DummyConnectionIdGenerator.class.getName() + " couldn't be instantiated!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof InstantiationException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof InstantiationException);
        }
    }

    public void testGetInstance_Error_6() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, DummyConnectionIdGenerator_2.class.getName(), null, null);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the " + DummyConnectionIdGenerator_2.class.getName() + " couldn't be instantiated caused by the private constructor!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof IllegalAccessException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof IllegalAccessException);
        }
    }

    public void testGetConnectionIdGenerator() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionIdGeneratorClassName(SessionConnectionIdGenerator.class.getName());

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theConfig);
        theConfigurationDependentFactory.reset(theConfig);

        final ConnectionIdGenerator theConnectionIdGenerator = theConfigurationDependentFactory.getConnectionIdGenerator();
        assertNotNull(theConnectionIdGenerator);
        assertTrue(theConnectionIdGenerator instanceof SessionConnectionIdGenerator);
    }

    public void testGetConnectionIdGenerator_Error() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionIdGeneratorClassName(String.class.getName());

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theConfig);
        try {
            theConfigurationDependentFactory.reset(theConfig);
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(String.class.getName()));
        }
    }

    private class TestEventServiceConfiguration implements EventServiceConfiguration
    {
        private String myConnectionIdGeneratorClassName;

        public String getConfigDescription() {
            return TestEventServiceConfiguration.class.getName();
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

        public void setConnectionIdGeneratorClassName(String aConnectionIdGeneratorClassName) {
            myConnectionIdGeneratorClassName = aConnectionIdGeneratorClassName;
        }

        public String getConnectionIdGeneratorClassName() {
            return myConnectionIdGeneratorClassName;
        }

        public String getConnectionStrategyClientConnectorClassName() {
            return null;
        }

        public String getConnectionStrategyServerConnectorClassName() {
            return null;
        }

        public Map<ConfigParameter, Object> getConfigMap() {
            return new HashMap<ConfigParameter, Object>();
        }
    }

    private class DummyConnectionIdGenerator implements ConnectionIdGenerator
    {
        public String generateConnectionId(HttpServletRequest aRequest) {
            return null;
        }

        public String getConnectionId(HttpServletRequest aRequest) {
            return null;
        }
    }

    public static class DummyConnectionIdGenerator_2 implements ConnectionIdGenerator
    {
        private DummyConnectionIdGenerator_2() {}

        public String generateConnectionId(HttpServletRequest aRequest) {
            return null;
        }

        public String getConnectionId(HttpServletRequest aRequest) {
            return null;
        }
    }
}