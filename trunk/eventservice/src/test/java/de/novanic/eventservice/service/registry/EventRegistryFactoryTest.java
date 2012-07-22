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
package de.novanic.eventservice.service.registry;

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.config.loader.ConfigurationLoader;
import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.service.registry.user.UserManager;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import de.novanic.eventservice.util.PlatformUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * Date: 28.07.2008
 * <br>Time: 22:00:52
 */
@RunWith(JUnit4.class)
public class EventRegistryFactoryTest extends EventServiceTestCase
{
    @After
    public void tearDown() {
        tearDownEventServiceConfiguration();

        FactoryResetService.resetFactory(EventRegistryFactory.class);
        FactoryResetService.resetFactory(UserManagerFactory.class);
    }

    @Test
    public void testGetInstance() {
        EventRegistryFactory theEventRegistryFactory = EventRegistryFactory.getInstance();
        assertSame(theEventRegistryFactory, EventRegistryFactory.getInstance());
        assertSame(theEventRegistryFactory, EventRegistryFactory.getInstance());

        EventRegistry theEventRegistry = theEventRegistryFactory.getEventRegistry();
        assertSame(theEventRegistry, theEventRegistryFactory.getEventRegistry());
    }

    @Test
    public void testResetEventRegistryFactory() {
        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        assertSame(theEventRegistry, EventRegistryFactory.getInstance().getEventRegistry());

        FactoryResetService.resetFactory(EventRegistryFactory.class);
        assertNotSame(theEventRegistry, EventRegistryFactory.getInstance().getEventRegistry());
    }

    @Test
    public void testResetEventRegistry() throws Exception {
        final String theTestUserId = "TestUser1";

        UserManager theUserManager = UserManagerFactory.getInstance().getUserManager(100);
        theUserManager.getUserActivityScheduler().stop();

        theUserManager.addUser(theTestUserId);
        assertNotNull(theUserManager.getUser(theTestUserId));

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        assertNotNull(theEventRegistry);
        Thread.sleep(400);
        assertNull(theUserManager.getUser(theTestUserId)); //cleaned-up by the user-activity scheduler which is activated by the EventRegistry

        EventRegistryFactory.getInstance().resetEventRegistry();
        theUserManager.addUser(theTestUserId);

        Thread.sleep(400);
        assertNotNull(theUserManager.getUser(theTestUserId)); //not cleaned-up by the user-activity scheduler, because the EventRegistry got stopped
    }

    @Test
    public void testInit() {
        EventRegistryFactory theEventRegistryFactory = EventRegistryFactory.getInstance();
        EventRegistry theEventRegistry = theEventRegistryFactory.getEventRegistry();

        EventServiceConfiguration theConfiguration = theEventRegistry.getConfiguration();
        assertSame(theConfiguration, theEventRegistryFactory.getEventRegistry().getConfiguration());

        tearDownEventServiceConfiguration();
        EventServiceConfiguration theNewConfiguration = createConfiguration(0, 1, 2);
        setUp(theNewConfiguration);

        theEventRegistryFactory = EventRegistryFactory.getInstance();

        assertNotSame(theNewConfiguration, theConfiguration);
        assertNotSame(theConfiguration, theEventRegistryFactory.getEventRegistry().getConfiguration());
        assertSame(theNewConfiguration, theEventRegistryFactory.getEventRegistry().getConfiguration());
    }

    @Test
    public void testInit_Log() {
        EventServiceConfiguration theNewConfiguration = createConfiguration(0, 1, 2);

        final TestLoggingHandler theTestLoggingHandler = new TestLoggingHandler();
        
        Logger theLogger = Logger.getLogger(DefaultEventRegistry.class.getName());
        final Level theOldLevel = theLogger.getLevel();

        try {
            theLogger.setLevel(Level.FINEST);
            theLogger.addHandler(theTestLoggingHandler);

            tearDownEventServiceConfiguration();
            setUp(theNewConfiguration);

            EventRegistryFactory.getInstance().getEventRegistry();

            assertEquals("Server: Configuration changed - EventServiceConfiguration (TestConfiguration)" + PlatformUtil.getNewLine() +
                    "  Min.: 0ms; Max.: 1ms; Timeout: 2ms", theTestLoggingHandler.getLastMessage());
        } finally {
            theLogger.setLevel(theOldLevel);
            theLogger.removeHandler(theTestLoggingHandler);
        }
    }

    @Test
    public void testGetEventRegistryError() {
        EventServiceConfigurationFactory.getInstance().addCustomConfigurationLoader(new TestErrorConfigurationLoader());

        try {
            EventRegistryFactory.getInstance().getEventRegistry();
            fail("Exception expected!");
        } catch(ConfigurationException e) {}
    }

    private class TestLoggingHandler extends Handler
    {
        private String myLastMessage;

        public void publish(LogRecord aRecord) {
            myLastMessage = aRecord.getMessage();
        }

        public void flush() {}

        public void close() throws SecurityException {}

        public String getLastMessage() {
            return myLastMessage;
        }
    }

    private class TestErrorConfigurationLoader implements ConfigurationLoader
    {
        public boolean isAvailable() {
            return true;
        }

        public EventServiceConfiguration load() {
            throw new ConfigurationException("testException");
        }
    }
}
