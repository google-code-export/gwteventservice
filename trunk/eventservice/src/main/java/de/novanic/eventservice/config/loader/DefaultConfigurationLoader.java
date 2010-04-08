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
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.longpolling.LongPollingConnectionStrategy;

/**
 * DefaultConfigurationLoader is used by {@link de.novanic.eventservice.config.EventServiceConfigurationFactory} if no
 * configuration could be found.
 * <br>The default values:
 * <br> - Min waiting time: 0
 * <br> - Max waiting time: 20000
 * <br> - Timeout time: 90000
 *
 * A description for the values can be found in {@link de.novanic.eventservice.config.EventServiceConfiguration} or in
 * the manual.
 *
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 14:42:26
 */
public class DefaultConfigurationLoader implements ConfigurationLoader
{
    private static final String DEFAULT_CONFIG_DESCRIPTION = "Default Configuration";
    private static final int DEFAULT_MIN_WAITING_TIME = 0;
    private static final int DEFAULT_MAX_WAITING_TIME = 20000;
    private static final int DEFAULT_TIME_OUT = 90000;
    private static final String DEFAULT_CLIENT_ID_GENERATOR_CLASS_NAME = SessionConnectionIdGenerator.class.getName();
    private static final String DEFAULT_CONNECTION_STRATEGY_CLASS_NAME = LongPollingConnectionStrategy.class.getName();

    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't called. In the case of {@link DefaultConfigurationLoader} it returns
     * always true, because the default configuration is available in any case.
     * @return true when available, otherwise false. In the case of {@link DefaultConfigurationLoader} it returns
     * always true, because the default configuration is available in any case.
     */
    public boolean isAvailable() {
        return true;
    }

    /**
     * Loads the configuration with the loader.
     * @return the loaded configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration})
     */
    public EventServiceConfiguration load() {
        return new RemoteEventServiceConfiguration(DEFAULT_CONFIG_DESCRIPTION, DEFAULT_MIN_WAITING_TIME, DEFAULT_MAX_WAITING_TIME, DEFAULT_TIME_OUT,
                                                   DEFAULT_CLIENT_ID_GENERATOR_CLASS_NAME, DEFAULT_CONNECTION_STRATEGY_CLASS_NAME);
    }

    public boolean equals(Object anObject) {
        return (anObject instanceof DefaultConfigurationLoader);
    }
}
