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
package de.novanic.eventservice.config;

import de.novanic.eventservice.config.loader.PropertyConfigurationLoader;
import de.novanic.eventservice.config.loader.ConfigurationLoader;
import de.novanic.eventservice.config.loader.DefaultConfigurationLoader;

import java.util.List;
import java.util.ArrayList;

/**
 * EventServiceConfigurationFactory can be used to create an instance of {@link EventServiceConfiguration}. There a
 * various {@link de.novanic.eventservice.config.loader.ConfigurationLoader} strategies to initilize the configuration.
 * {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader} is used at last, when no configuration could
 * be found.
 *
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 14:36:49
 */
public class EventServiceConfigurationFactory
{
    private static EventServiceConfigurationFactory myInstance;

    private final List<ConfigurationLoader> myCustomConfigurationLoaders;

    /**
     * The EventServiceConfigurationFactory should be created via the getInstance method.
     * @see EventServiceConfigurationFactory#getInstance()
     */
    private EventServiceConfigurationFactory() {
        myCustomConfigurationLoaders = new ArrayList<ConfigurationLoader>();
    }

    /**
     * This method should be used to create an instance of EventServiceConfigurationFactory.
     * EventServiceConfigurationFactory is a singleton, so this method returns always the same instance of EventServiceConfigurationFactory.
     * @return EventServiceConfigurationFactory (singleton)
     */
    public static synchronized EventServiceConfigurationFactory getInstance() {
        if(myInstance == null) {
            myInstance = new EventServiceConfigurationFactory();
        }
        return myInstance;
    }

    /**
     * Loads the {@link de.novanic.eventservice.config.EventServiceConfiguration} with various
     * {@link de.novanic.eventservice.config.loader.ConfigurationLoader} strategies.
     * @return the configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration})
     * @throws de.novanic.eventservice.config.loader.ConfigurationException thrown when a configuration is available, but can't be loaded
     */
    public EventServiceConfiguration loadEventServiceConfiguration() {
        return loadEventServiceConfiguration(null);
    }

    /**
     * Loads the {@link de.novanic.eventservice.config.EventServiceConfiguration} with various
     * {@link de.novanic.eventservice.config.loader.ConfigurationLoader} strategies.
     * @param aPropertyName properties file if another properties file is preferred as the default properties file"
     * (see description of {@link de.novanic.eventservice.config.loader.PropertyConfigurationLoader}).
     * @return the configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration})
     * @throws de.novanic.eventservice.config.loader.ConfigurationException thrown when a configuration is available, but can't be loaded
     */
    public EventServiceConfiguration loadEventServiceConfiguration(String aPropertyName) {
        //process custom ConfigurationLoaders at first
        for(ConfigurationLoader theCustomConfigurationLoader: myCustomConfigurationLoaders) {
            if(theCustomConfigurationLoader.isAvailable()) {
                return theCustomConfigurationLoader.load();
            }
        }

        ConfigurationLoader thePropertyConfigurationLoader = new PropertyConfigurationLoader(aPropertyName);
        if(thePropertyConfigurationLoader.isAvailable()) {
            return thePropertyConfigurationLoader.load();
        }
        return new DefaultConfigurationLoader().load();
    }

    /**
     * Adds a custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader} (in the queue before the default
     * configuration loaders).
     * @param aConfigurationLoader custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader}
     */
    public void addCustomConfigurationLoader(ConfigurationLoader aConfigurationLoader) {
        myCustomConfigurationLoaders.add(aConfigurationLoader);
    }

    /**
     * Removes a custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader}.
     * @param aConfigurationLoader {@link de.novanic.eventservice.config.loader.ConfigurationLoader} to remove from the queue
     */
    public void removeCustomConfigurationLoader(ConfigurationLoader aConfigurationLoader) {
        myCustomConfigurationLoaders.remove(aConfigurationLoader);
    }

    /**
     * Resets the factory and the {@link de.novanic.eventservice.config.loader.ConfigurationLoader} queue.
     */
    public static void reset() {
        if(myInstance != null) {
            myInstance.myCustomConfigurationLoaders.clear();
            myInstance = null;
        }
    }
}
