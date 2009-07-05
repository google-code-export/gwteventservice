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
package de.novanic.eventservice;

import de.novanic.eventservice.service.EventServiceImplTest;
import de.novanic.eventservice.service.EventExecutorServiceFactoryTest;
import de.novanic.eventservice.service.DefaultEventExecutorServiceTest;
import de.novanic.eventservice.service.RemoteEventServiceServletTest;
import de.novanic.eventservice.service.exception.NoSessionAvailableExceptionTest;
import de.novanic.eventservice.service.registry.EventRegistryFactoryTest;
import de.novanic.eventservice.service.registry.EventRegistryTest;
import de.novanic.eventservice.service.registry.user.UserInfoTest;
import de.novanic.eventservice.service.registry.user.UserActivitySchedulerTest;
import de.novanic.eventservice.service.registry.user.UserManagerTest;
import de.novanic.eventservice.service.registry.user.UserManagerFactoryTest;
import de.novanic.eventservice.logger.ServerLoggerFactoryTest;
import de.novanic.eventservice.logger.DefaultServerLoggerTest;
import de.novanic.eventservice.config.RemoteEventServiceConfigurationTest;
import de.novanic.eventservice.config.EventServiceConfigurationFactoryTest;
import de.novanic.eventservice.config.level.ConfigLevelFactoryTest;
import de.novanic.eventservice.config.loader.DefaultConfigurationLoaderTest;
import de.novanic.eventservice.config.loader.PropertyConfigurationLoaderTest;
import de.novanic.eventservice.config.loader.ConfigurationExceptionTest;
import de.novanic.eventservice.config.loader.WebDescriptorConfigurationLoaderTest;
import de.novanic.eventservice.util.PlatformUtilTest;
import de.novanic.eventservice.util.TestLoggingConfigurator;
import de.novanic.eventservice.util.TestLoggingConfiguratorTest;
import de.novanic.eventservice.util.StringUtilTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author sstrohschein
 * Date: 05.06.2008
 * <br>Time: 23:23:09
 */
public class EventServiceTestSuite
{
    private EventServiceTestSuite() {}

    public static Test suite() throws Exception {
        TestSuite theSuite = new TestSuite("EventService - Tests");

        TestLoggingConfigurator.configureLogging();

        // Util
        theSuite.addTestSuite(TestLoggingConfiguratorTest.class);
        theSuite.addTestSuite(StringUtilTest.class);
        theSuite.addTestSuite(PlatformUtilTest.class);

        // Logging
        theSuite.addTestSuite(ServerLoggerFactoryTest.class);
        theSuite.addTestSuite(DefaultServerLoggerTest.class);

        // Configuration
        theSuite.addTestSuite(ConfigLevelFactoryTest.class);
        theSuite.addTestSuite(RemoteEventServiceConfigurationTest.class);
        theSuite.addTestSuite(ConfigurationExceptionTest.class);
        theSuite.addTestSuite(DefaultConfigurationLoaderTest.class);
        theSuite.addTestSuite(PropertyConfigurationLoaderTest.class);
        theSuite.addTestSuite(WebDescriptorConfigurationLoaderTest.class);
        theSuite.addTestSuite(EventServiceConfigurationFactoryTest.class);

        // Registry
        theSuite.addTestSuite(UserActivitySchedulerTest.class);
        theSuite.addTestSuite(UserInfoTest.class);
        theSuite.addTestSuite(UserManagerFactoryTest.class);
        theSuite.addTestSuite(UserManagerTest.class);
        theSuite.addTestSuite(EventRegistryFactoryTest.class);
        theSuite.addTestSuite(EventRegistryTest.class);

        // EventService
        theSuite.addTestSuite(EventServiceImplTest.class);

        // EventExecutorService
        theSuite.addTestSuite(EventExecutorServiceFactoryTest.class);
        theSuite.addTestSuite(DefaultEventExecutorServiceTest.class);
        theSuite.addTestSuite(NoSessionAvailableExceptionTest.class);

        // Servlets
        theSuite.addTestSuite(RemoteEventServiceServletTest.class);

        return theSuite;
    }
}
