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
package de.novanic.eventservice.service.connection.id;

import de.novanic.eventservice.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2010
 *         <br>Time: 14:37:10
 */
public class ConnectionIdGeneratorFactoryTest extends TestCase
{
    private EventServiceConfiguration myConfigurationBackup;

    public void setUp() {
        myConfigurationBackup = ConnectionIdGeneratorFactory.getConfiguration();
    }

    public void tearDown() {
        //reset for the old configuration
        if(myConfigurationBackup != null) {
            ConnectionIdGeneratorFactory.getInstance(myConfigurationBackup).reset(myConfigurationBackup);
        }
    }

    public void testGetInstance() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName());

        ConnectionIdGeneratorFactory theConnectionIdGeneratorFactory = ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration);
        assertSame(theConnectionIdGeneratorFactory, ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration));
        assertSame(theConnectionIdGeneratorFactory, ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration));
    }

    public void testGetInstance_Error() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, String.class.getName());
        try {
            ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because no configuration is available!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_2() {
        try {
            ConnectionIdGeneratorFactory.getInstance(null).reset(null);
            fail("Exception expected, because the type isn't a " + ConnectionIdGenerator.class.getSimpleName() + '!');
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}
    }

    public void testGetInstance_Error_3() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, "NotExistingClassXY");
        try {
            ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the NotExistingClassXY class couldn't be found!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof ClassNotFoundException);
        }
    }

    public void testGetInstance_Error_4() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, DummyConnectionIdGenerator.class.getName());
        try {
            ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the private " + DummyConnectionIdGenerator.class.getName() + " couldn't be instantiated!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof InstantiationException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof InstantiationException);
        }
    }

    public void testGetInstance_Error_5() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, DummyConnectionIdGenerator_2.class.getName());
        try {
            ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration).reset(theEventServiceConfiguration);
            fail("Exception expected, because the " + DummyConnectionIdGenerator_2.class.getName() + " couldn't be instantiated caused by the private constructor!");
        } catch(ConfigurationException e) {
            assertTrue(e.getCause() instanceof IllegalAccessException);
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getCause() instanceof IllegalAccessException);
        }
    }

    public void testGetConnectionIdGenerator() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName());

        ConnectionIdGeneratorFactory theConnectionIdGeneratorFactory = ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration);
        theConnectionIdGeneratorFactory.reset(theEventServiceConfiguration);
        ConnectionIdGenerator theConnectionIdGenerator = theConnectionIdGeneratorFactory.getConnectionIdGenerator();
        assertNotNull(theConnectionIdGenerator);
        assertTrue(theConnectionIdGenerator instanceof SessionConnectionIdGenerator);
    }

    public void testGetConnectionIdGenerator_Error() {
        final EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration("Test-Config", null, null, null, SessionConnectionIdGenerator.class.getName());

        ConnectionIdGeneratorFactory theConnectionIdGeneratorFactory = ConnectionIdGeneratorFactory.getInstance(theEventServiceConfiguration);
        assertNotNull(theConnectionIdGeneratorFactory);

        try {
            theConnectionIdGeneratorFactory.reset(null);
            fail("Exception expected, because no configuration is available!");
        } catch(ExceptionInInitializerError e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
        } catch(ConfigurationException e) {}

        assertNotNull(theConnectionIdGeneratorFactory);

        try {
            theConnectionIdGeneratorFactory.getConnectionIdGenerator();
            fail("Exception expected, because the " + ConnectionIdGeneratorFactory.class.getName() + " isn't initialized correctly and therefore no valid "
                    + ConnectionIdGenerator.class.getName() + " is available!");
        } catch(ConfigurationException e) {}
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