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
package de.novanic.eventservice.config;

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.loader.*;
import de.novanic.eventservice.config.level.ConfigLevelFactory;
import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 18:11:43
 */
public class EventServiceConfigurationFactoryTest extends EventServiceTestCase
{
    public void testInit() {
        EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        assertSame(theEventServiceConfigurationFactory, EventServiceConfigurationFactory.getInstance());
    }

    public void testReset() {
        EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        assertSame(theEventServiceConfigurationFactory, EventServiceConfigurationFactory.getInstance());

        FactoryResetService.resetFactory(EventServiceConfigurationFactory.class);
        assertNotSame(theEventServiceConfigurationFactory, EventServiceConfigurationFactory.getInstance());

        theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        assertSame(theEventServiceConfigurationFactory, EventServiceConfigurationFactory.getInstance());
    }

    public void testLoadEventServiceConfiguration() {
        //loads eventservice.properties
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        EventServiceConfiguration theConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());
    }

    public void testLoadEventServiceConfiguration_2() {
        //loads eventservice.properties
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        EventServiceConfiguration theConfiguration = theConfigurationFactory.loadEventServiceConfiguration(null);
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());
    }

    public void testLoadEventServiceConfiguration_3() {
        //loads eventservice.bak.properties
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        EventServiceConfiguration theConfiguration = theConfigurationFactory.loadEventServiceConfiguration("eventservice.bak.properties");
        assertEquals(Integer.valueOf(2000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(50000), theConfiguration.getTimeoutTime());
    }

    public void testLoadEventServiceConfiguration_Multiple() {
        //loads the DefaultConfiguration at first and then loads the higher priorized PropertyConfiguration with eventservice.bak.properties
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();

        EventServiceConfiguration theConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());

        theConfiguration = theConfigurationFactory.loadEventServiceConfiguration("eventservice.bak.properties");
        assertEquals(Integer.valueOf(2000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(50000), theConfiguration.getTimeoutTime());

        theConfiguration = theConfigurationFactory.loadEventServiceConfiguration("eventservice.bak.properties");
        assertEquals(Integer.valueOf(2000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(50000), theConfiguration.getTimeoutTime());

        theConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(2000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(50000), theConfiguration.getTimeoutTime());
    }

    public void testLoadEventServiceConfiguration_Default() {
        //uses DefaultConfigurationLoader
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        EventServiceConfiguration theConfiguration = theConfigurationFactory.loadEventServiceConfiguration("notAnExistingFile");
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theConfiguration.getTimeoutTime());
    }

    public void testLoadEventServiceConfiguration_Failure() {
        //loads eventservice_error.properties
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        try {
            theConfigurationFactory.loadEventServiceConfiguration("eventservice_error.properties");
            fail("Exception expected!");
        } catch(ConfigurationException e) {}
    }

    public void testLoadEventServiceConfiguration_Failure_2() {
        //loads no configuration, because no configuration loader is attached
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        theConfigurationFactory.removeConfigurationLoader(new DefaultConfigurationLoader());
        theConfigurationFactory.removeConfigurationLoader(new PropertyConfigurationLoader("eventservice.properties"));
        try {
            theConfigurationFactory.loadEventServiceConfiguration();
            fail(ConfigurationException.class.getName() + " expected!");
        } catch(ConfigurationException e) {}
    }

    public void testAddCustomConfigurationLoader() {
        //add custom ConfigurationLoader
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 3000, 70000);

        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        final DummyConfigurationLoader theCustomConfigurationLoader = new DummyConfigurationLoader(theConfiguration);
        theConfigurationFactory.addCustomConfigurationLoader(theCustomConfigurationLoader);

        EventServiceConfiguration theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(3000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(70000), theLoadedConfiguration.getTimeoutTime());

        //remove custom ConfigurationLoader
        theConfigurationFactory.removeConfigurationLoader(theCustomConfigurationLoader);

        theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theLoadedConfiguration.getTimeoutTime());
    }

    public void testAddCustomConfigurationLoader_WebDescriptor() {
        //add custom ConfigurationLoader
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 3000, 70000);

        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();

        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.LOW, new WebDescriptorConfigurationLoader(new ServletConfigDummy(true, false)));

        final DummyConfigurationLoader theCustomConfigurationLoader = new DummyConfigurationLoader(theConfiguration);
        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.HIGH, theCustomConfigurationLoader);

        EventServiceConfiguration theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(30000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(120000), theLoadedConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(3), theLoadedConfiguration.getReconnectAttemptCount());

        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.LOWEST, theCustomConfigurationLoader);

        theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(3000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(70000), theLoadedConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getReconnectAttemptCount());
    }

    public void testAddCustomConfigurationLoader_WebDescriptor_Incomplete() {
        //add custom ConfigurationLoader
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 3000, 70000);

        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();

        final ServletConfigDummy theServletConfig = new ServletConfigDummy(true, false);
        theServletConfig.removeParameter(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG);
        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.LOW, new WebDescriptorConfigurationLoader(theServletConfig));

        final DummyConfigurationLoader theCustomConfigurationLoader = new DummyConfigurationLoader(theConfiguration);
        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.HIGH, theCustomConfigurationLoader);

        EventServiceConfiguration theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(30000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(120000), theLoadedConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getReconnectAttemptCount()); //the default value is taken

        theConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.LOWEST, theCustomConfigurationLoader);

        theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(3000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(70000), theLoadedConfiguration.getTimeoutTime());
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getReconnectAttemptCount());
    }

    public void testResetCustomConfigurationLoaders() {
        //add custom ConfigurationLoader
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 3000, 70000);

        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        final DummyConfigurationLoader theCustomConfigurationLoader = new DummyConfigurationLoader(theConfiguration);
        theConfigurationFactory.addCustomConfigurationLoader(theCustomConfigurationLoader);

        EventServiceConfiguration theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(3000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(70000), theLoadedConfiguration.getTimeoutTime());

        //reset
        FactoryResetService.resetFactory(EventServiceConfigurationFactory.class);
        theConfigurationFactory = EventServiceConfigurationFactory.getInstance();

        theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theLoadedConfiguration.getTimeoutTime());
    }

    public void testResetCustomConfigurationLoaders_2() {
        //add custom ConfigurationLoader
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 3000, 70000);

        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        final DummyConfigurationLoader theCustomConfigurationLoader = new DummyConfigurationLoader(theConfiguration);
        theConfigurationFactory.addCustomConfigurationLoader(theCustomConfigurationLoader);

        EventServiceConfiguration theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(3000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(70000), theLoadedConfiguration.getTimeoutTime());

        //reset and re-init
        FactoryResetService.resetFactory(EventServiceConfigurationFactory.class);
        theConfigurationFactory = EventServiceConfigurationFactory.getInstance();

        theLoadedConfiguration = theConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(0), theLoadedConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(20000), theLoadedConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(90000), theLoadedConfiguration.getTimeoutTime());
    }

    private class DummyConfigurationLoader implements ConfigurationLoader
    {
        private EventServiceConfiguration myEventServiceConfiguration;

        private DummyConfigurationLoader(EventServiceConfiguration anEventServiceConfiguration) {
            myEventServiceConfiguration = anEventServiceConfiguration;
        }

        public boolean isAvailable() {
            return true;
        }

        public EventServiceConfiguration load() {
            return myEventServiceConfiguration;
        }
    }
}
