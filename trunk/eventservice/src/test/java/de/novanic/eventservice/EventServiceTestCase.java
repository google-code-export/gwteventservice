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

import de.novanic.eventservice.config.ConfigurationDependentFactory;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnector;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import junit.framework.TestCase;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.config.loader.ConfigurationLoader;
import de.novanic.eventservice.config.loader.CustomConfigurationLoaderTestMode;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;
import de.novanic.eventservice.service.DefaultEventExecutorService;
import de.novanic.eventservice.util.LoggingConfiguratorTestMode;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;

import java.io.IOException;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 20:57:44
 */
public abstract class EventServiceTestCase extends TestCase
{
    public void setUp(EventServiceConfiguration anEventServiceConfiguration) {
        ConfigurationLoader theConfigurationLoader = new CustomConfigurationLoaderTestMode(anEventServiceConfiguration);
        EventServiceConfigurationFactory.getInstance().addCustomConfigurationLoader(theConfigurationLoader);
        FactoryResetService.resetFactory(EventRegistryFactory.class);
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
        FactoryResetService.resetFactory(UserManagerFactory.class);
        //TODO reset ConfigurationDependentFactory
        ConfigurationDependentFactory.getInstance(anEventServiceConfiguration).reset(anEventServiceConfiguration);
    }

    public void tearDown() throws Exception {
        tearDownEventServiceConfiguration();
        FactoryResetService.resetFactory(UserManagerFactory.class);
        FactoryResetService.resetFactory(EventServiceConfigurationFactory.class);
    }

    public void tearDownEventServiceConfiguration() {
        FactoryResetService.resetFactory(EventServiceConfigurationFactory.class);
        FactoryResetService.resetFactory(EventRegistryFactory.class);
    }

    public void logOn() throws IOException {
        LoggingConfiguratorTestMode.logOn();
    }

    public void logOff() throws IOException {
        LoggingConfiguratorTestMode.logOff();
    }

    protected EventServiceConfiguration createConfiguration(int aMinTime, int aMaxTime, int aTimeoutTime) {
        return new RemoteEventServiceConfiguration("TestConfiguration", aMinTime, aMaxTime, aTimeoutTime, SessionConnectionIdGenerator.class.getName(), null, LongPollingServerConnector.class.getName());
    }

    protected ConnectionStrategyServerConnector getLongPollingListener() {
        EventServiceConfiguration theConfiguration = EventServiceConfigurationFactory.getInstance().loadEventServiceConfiguration();
        return getLongPollingListener(theConfiguration);
    }

    protected ConnectionStrategyServerConnector getLongPollingListener(EventServiceConfiguration anEventServiceConfiguration) {
        return ConfigurationDependentFactory.getInstance(anEventServiceConfiguration).getConnectionStrategyServerConnector();
    }
}
