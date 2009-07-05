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

import de.novanic.eventservice.config.loader.ConfigurationLoader;
import de.novanic.eventservice.config.loader.ConfigurationException;
import de.novanic.eventservice.config.loader.PropertyConfigurationLoader;
import de.novanic.eventservice.config.loader.DefaultConfigurationLoader;
import de.novanic.eventservice.config.level.ConfigLevel;
import de.novanic.eventservice.config.level.ConfigLevelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * EventServiceConfigurationFactory can be used to create an instance of {@link EventServiceConfiguration}. There a
 * various {@link de.novanic.eventservice.config.loader.ConfigurationLoader} strategies to initilize the configuration.
 * {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader} is used at last, when no configuration could
 * be found.
 *
 * <br><br>
 * There are three pre-registered ConfigurationLoaders at various levels.
 * <br> 1) Level {@link de.novanic.eventservice.config.level.ConfigLevelFactory#DEFAULT} (5000) - {@link de.novanic.eventservice.config.loader.PropertyConfigurationLoader}
 * <br> 2) Level {@link de.novanic.eventservice.config.level.ConfigLevelFactory#DEFAULT} (5000) - {@link de.novanic.eventservice.config.loader.WebDescriptorConfigurationLoader}
 * <br> 3) Level {@link de.novanic.eventservice.config.level.ConfigLevelFactory#HIGH}-1 (7999) - {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader}
 * <br>
 * <br> That means when a property file is in the classpath, the property file is used for the configuration. When no property file is available,
 * the web-descriptor is used. When no servlet-parameters are registered in the web-descriptor, the default configuration is used.
 * To manipulate that sequence and to register custom configuration loaders {@link EventServiceConfigurationFactory#addConfigurationLoader(de.novanic.eventservice.config.level.ConfigLevel, de.novanic.eventservice.config.loader.ConfigurationLoader)}
 * and other modifier methods of that class can be used. 
 *
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 14:36:49
 */
public class EventServiceConfigurationFactory
{
    private final Map<ConfigLevel, List<ConfigurationLoader>> myConfigurationLoaders;

    /**
     * The EventServiceConfigurationFactory should be created via the getInstance method.
     * @see EventServiceConfigurationFactory#getInstance()
     */
    private EventServiceConfigurationFactory() {
        myConfigurationLoaders = new TreeMap<ConfigLevel, List<ConfigurationLoader>>();
        initConfigurationLoaders();
    }

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class EventServiceConfigurationFactoryHolder {
        private static EventServiceConfigurationFactory INSTANCE = new EventServiceConfigurationFactory();
    }

    /**
     * This method should be used to create an instance of EventServiceConfigurationFactory.
     * EventServiceConfigurationFactory is a singleton, so this method returns always the same instance of EventServiceConfigurationFactory.
     * @return EventServiceConfigurationFactory (singleton)
     */
    public static EventServiceConfigurationFactory getInstance() {
        return EventServiceConfigurationFactoryHolder.INSTANCE;
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
        replaceConfigurationLoader(ConfigLevelFactory.DEFAULT, new PropertyConfigurationLoader(aPropertyName));
        initConfigurationLoaders();
        return loadEventServiceConfiguration();
    }

    /**
     * Loads the {@link de.novanic.eventservice.config.EventServiceConfiguration} with various
     * {@link de.novanic.eventservice.config.loader.ConfigurationLoader} strategies.
     * @return the configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration})
     * @throws de.novanic.eventservice.config.loader.ConfigurationException thrown when a configuration is available, but can't be loaded
     */
    public EventServiceConfiguration loadEventServiceConfiguration() {
        for(List<ConfigurationLoader> theConfigLoaders: myConfigurationLoaders.values()) {
            for(ConfigurationLoader theConfigLoader: theConfigLoaders) {
                if(theConfigLoader.isAvailable()) {
                    return theConfigLoader.load();
                }
            }
        }
        //can not occure, because the DefaultConfigurationLoader is attached and always available
        throw new ConfigurationException("No configuration is available!");
    }

    /**
     * Adds a custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader} (in the queue before the default
     * configuration loaders).
     * @param aConfigurationLoader custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader}
     */
    public void addCustomConfigurationLoader(ConfigurationLoader aConfigurationLoader) {
        addConfigurationLoader(ConfigLevelFactory.LOWEST, aConfigurationLoader);
    }

    /**
     * Adds a {@link de.novanic.eventservice.config.loader.ConfigurationLoader} (in the queue before the default
     * configuration loaders).
     * @param aLevel {@link de.novanic.eventservice.config.level.ConfigLevel} to specify the priority/level for the {@link de.novanic.eventservice.config.loader.ConfigurationLoader}
     * @param aConfigurationLoader custom {@link de.novanic.eventservice.config.loader.ConfigurationLoader}
     */
    public void addConfigurationLoader(ConfigLevel aLevel, ConfigurationLoader aConfigurationLoader) {
        List<ConfigurationLoader> theConfigLoaders = myConfigurationLoaders.get(aLevel);
        if(theConfigLoaders == null) {
            theConfigLoaders = new ArrayList<ConfigurationLoader>();
            myConfigurationLoaders.put(aLevel, theConfigLoaders);
        }
        theConfigLoaders.add(aConfigurationLoader);
    }

    /**
     * Removes a {@link de.novanic.eventservice.config.loader.ConfigurationLoader}.
     * @param aConfigurationLoader {@link de.novanic.eventservice.config.loader.ConfigurationLoader} to remove from the queue
     */
    public void removeConfigurationLoader(ConfigurationLoader aConfigurationLoader) {
        for(List<ConfigurationLoader> theConfigLoaders: myConfigurationLoaders.values()) {
            theConfigLoaders.remove(aConfigurationLoader);
        }
    }

    /**
     * Replaces a configuration loader at the specified configuration level.
     * @param aLevel configuration level to search the {@link de.novanic.eventservice.config.loader.ConfigurationLoader}
     * @param aConfigurationLoader {@link de.novanic.eventservice.config.loader.ConfigurationLoader} to add
     */
    public void replaceConfigurationLoader(ConfigLevel aLevel, ConfigurationLoader aConfigurationLoader) {
        removeConfigurationLoader(aConfigurationLoader);
        addConfigurationLoader(aLevel, aConfigurationLoader);
    }

    /**
     * Initializes and registers the pre-definied ConfigurationLoaders ({@link de.novanic.eventservice.config.loader.ConfigurationLoader}).
     * See the class description of {@link de.novanic.eventservice.config.EventServiceConfigurationFactory} for more information. 
     */
    private void initConfigurationLoaders() {
        replaceConfigurationLoader(ConfigLevelFactory.DEFAULT, new PropertyConfigurationLoader());
        replaceConfigurationLoader(ConfigLevelFactory.HIGHEST, new DefaultConfigurationLoader());
    }
}
