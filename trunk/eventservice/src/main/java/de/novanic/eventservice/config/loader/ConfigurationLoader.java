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
package de.novanic.eventservice.config.loader;

import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * ConfigurationLoader is used by {@link de.novanic.eventservice.config.EventServiceConfigurationFactory}
 * to load the {@link de.novanic.eventservice.config.EventServiceConfiguration}. There are different types/strategies to
 * load the {@link de.novanic.eventservice.config.EventServiceConfiguration}.
 *
 * @see de.novanic.eventservice.config.loader.PropertyConfigurationLoader
 * @see de.novanic.eventservice.config.loader.DefaultConfigurationLoader
 *
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 14:37:38
 */
public interface ConfigurationLoader
{
    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't be called.
     * @return true when available, otherwise false
     */
    boolean isAvailable();

    /**
     * Loads the configuration with the loader.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration
     * @throws de.novanic.eventservice.client.config.ConfigurationException occurs when an loading error occurs or if it contains unreadable values.
     */
    EventServiceConfiguration load();
}