/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschr�nkt)
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

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.service.connection.id.ConnectionIdGenerator;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnector;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 05.04.2010
 *         <br>Time: 14:12:21
 */
@RunWith(JUnit4.class)
public class ConfigurationDependentFactoryTest
{
    private EventServiceConfiguration myConfigurationBackup;

    @Before
    public void setUp() {
        myConfigurationBackup = ConfigurationDependentFactory.getConfiguration();
    }

    @After
    public void tearDown() {
        //reset for the old configuration
        if(myConfigurationBackup != null) {
            ConfigurationDependentFactory.getInstance(myConfigurationBackup).reset(myConfigurationBackup);
        }
    }

    @Test
    public void testGetInstance() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, SessionConnectionIdGenerator.class.getName(), null, LongPollingServerConnector.class.getName(), "utf-8", 1000);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance(theEventServiceConfiguration));
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance(theEventServiceConfiguration));
    }

    @Test
    public void testGetInstance_2() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, SessionConnectionIdGenerator.class.getName(), null, null, "utf-8", 1000);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
    }

    @Test
    public void testGetInstance_3() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, SessionConnectionIdGenerator.class.getName(), null, null, "utf-8", 1000);

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theEventServiceConfiguration);
        theConfigurationDependentFactory.reset(theEventServiceConfiguration);
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
        assertSame(theConfigurationDependentFactory, ConfigurationDependentFactory.getInstance());
    }

    @Test
    public void testGetInstance_Error() {
        try {
            ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(null);
            theConfigurationDependentFactory.reset(null, false);

            ConfigurationDependentFactory.getInstance();
            fail("Exception expected, because the factory wasn't initialized with a configuration!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetInstance_Error_2() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, String.class.getName(), null, null, "utf-8", 1000);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because no configuration is available!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetInstance_Error_3() {
        try {
            ConfigurationDependentFactory.getInstance(null).reset(null);
            fail("Exception expected, because the type isn't a " + ConnectionIdGenerator.class.getSimpleName() + '!');
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetInstance_Error_4() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, "NotExistingClassXY", null, null, "utf-8", 1000);
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

    @Test
    public void testGetInstance_Error_5() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, DummyConnectionIdGeneratorAbstract.class.getName(), null, null, "utf-8", 1000);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the constructor of " + DummyConnectionIdGeneratorAbstract.class.getName() + " throws an exception!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof InstantiationException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof InstantiationException);
        }
    }

    @Test
    public void testGetInstance_Error_6() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, DummyConnectionIdGenerator.class.getName(), null, null, "utf-8", 1000);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the " + DummyConnectionIdGenerator.class.getName() + " couldn't be instantiated caused by the private constructor!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof IllegalAccessException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof IllegalAccessException);
        }
    }

    @Test
    public void testGetInstance_Error_7() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, DummyConnectionIdGenerator_2.class.getName(), null, null, "utf-8", 1000);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the constructor of " + DummyConnectionIdGenerator_2.class.getName() + " throws an exception!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof InvocationTargetException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof InvocationTargetException);
        }
    }

    @Test
    public void testGetInstance_Error_9() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, null, DummyConnectionIdGenerator_3.class.getName(), null, null, "utf-8", 1000);
        try {
            ConfigurationDependentFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the constructor of " + DummyConnectionIdGenerator_3.class.getName() + " throws an exception!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() == null || e.getCause() instanceof ConfigurationException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() == null || e.getCause().getCause() instanceof ConfigurationException);
        }
    }

    @Test
    public void testGetConnectionIdGenerator() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionIdGeneratorClassName(SessionConnectionIdGenerator.class.getName());

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theConfig);
        theConfigurationDependentFactory.reset(theConfig);

        final ConnectionIdGenerator theConnectionIdGenerator = theConfigurationDependentFactory.getConnectionIdGenerator();
        assertNotNull(theConnectionIdGenerator);
        assertTrue(theConnectionIdGenerator instanceof SessionConnectionIdGenerator);
    }

    @Test
    public void testGetConnectionIdGenerator_Error() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionIdGeneratorClassName(String.class.getName());

        try {
            ConfigurationDependentFactory.getInstance(theConfig).reset(theConfig);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains(String.class.getName()));
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(String.class.getName()));
        }
    }

    @Test
    public void testGetConnectionStrategyServerConnector() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionStrategyServerConnectorClassName(LongPollingServerConnector.class.getName());

        ConfigurationDependentFactory theConfigurationDependentFactory = ConfigurationDependentFactory.getInstance(theConfig);
        theConfigurationDependentFactory.reset(theConfig);
        
        final ConnectionStrategyServerConnector theConnectionStrategyServerConnector = theConfigurationDependentFactory.getConnectionStrategyServerConnector();
        assertNotNull(theConnectionStrategyServerConnector);
        assertTrue(theConnectionStrategyServerConnector instanceof LongPollingServerConnector);
    }

    @Test
    public void testGetConnectionStrategyServerConnector_Error() {
        final TestEventServiceConfiguration theConfig = new TestEventServiceConfiguration();
        theConfig.setConnectionStrategyServerConnectorClassName(String.class.getName());

        try {
            ConfigurationDependentFactory.getInstance(theConfig).reset(theConfig);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains(String.class.getName()));
        } catch(ConfigurationException e) {
            assertTrue(e.getMessage().contains(String.class.getName()));
        }
    }

    private class TestEventServiceConfiguration implements EventServiceConfiguration
    {
        private String myConnectionIdGeneratorClassName;
        private String myConnectionStrategyServerConnectorClassName;

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

        public Integer getReconnectAttemptCount() {
            return 0;
        }

        public String getConnectionIdGeneratorClassName() {
            return myConnectionIdGeneratorClassName;
        }

        public String getConnectionStrategyClientConnectorClassName() {
            return null;
        }

        public String getConnectionStrategyServerConnectorClassName() {
            return myConnectionStrategyServerConnectorClassName;
        }

        public void setConnectionIdGeneratorClassName(String aConnectionIdGeneratorClassName) {
            myConnectionIdGeneratorClassName = aConnectionIdGeneratorClassName;
        }

        public void setConnectionStrategyServerConnectorClassName(String aConnectionStrategyServerConnectorClassName) {
            myConnectionStrategyServerConnectorClassName = aConnectionStrategyServerConnectorClassName;
        }

        public String getConnectionStrategyEncoding() {
            return "utf-8";
        }

        public Integer getMaxEvents() {
            return 1000;
        }

        public Map<ConfigParameter, Object> getConfigMap() {
            return new HashMap<ConfigParameter, Object>();
        }
    }

    public static class DummyConnectionIdGenerator implements ConnectionIdGenerator
    {
        private DummyConnectionIdGenerator() {}

        public String generateConnectionId(HttpServletRequest aRequest) {
            return null;
        }

        public String getConnectionId(HttpServletRequest aRequest) {
            return null;
        }
    }

    public static class DummyConnectionIdGenerator_2 implements ConnectionIdGenerator
    {
        public DummyConnectionIdGenerator_2(EventServiceConfiguration aConfiguration) {
            throw new RuntimeException("Test-Exception: The constructor isn't executable (called with configuration \"" + aConfiguration + "\"!");
        }

        public String generateConnectionId(HttpServletRequest aRequest) {
            return null;
        }

        public String getConnectionId(HttpServletRequest aRequest) {
            return null;
        }
    }

    public static class DummyConnectionIdGenerator_3 implements ConnectionIdGenerator
    {
        public DummyConnectionIdGenerator_3(String aString) {
            /* no default constructor available */
            throw new RuntimeException(DummyConnectionIdGenerator_3.class.getName() + " was constructed with \"" + aString
                    + "\" which shouldn't happen, because the no-arg constructor or the configuration parameter should be used!");
        }

        public String generateConnectionId(HttpServletRequest aRequest) {
            return null;
        }

        public String getConnectionId(HttpServletRequest aRequest) {
            return null;
        }
    }

    public static abstract class DummyConnectionIdGeneratorAbstract implements ConnectionIdGenerator {}
}