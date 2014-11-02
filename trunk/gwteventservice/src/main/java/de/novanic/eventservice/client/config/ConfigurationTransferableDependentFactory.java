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
package de.novanic.eventservice.client.config;

import com.google.gwt.core.client.GWT;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector;

/**
 * The {@link de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory} can create instances from a transferable configuration
 * ({@link de.novanic.eventservice.client.config.EventServiceConfigurationTransferable}) which can be configured with a class name.
 * The created instances are hold as a singleton.
 *
 * @author sstrohschein
 *         <br>Date: 16.04.2010
 *         <br>Time: 23:59:58
 */
public final class ConfigurationTransferableDependentFactory
{
    private static EventServiceConfigurationTransferable myConfiguration;

    private ConnectionStrategyClientConnector myConnectionStrategyClientConnector;

    /**
     * That constructor is only called one time, because the factory is created as a singleton.
     */
    private ConfigurationTransferableDependentFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class ConfigTransferableDependentFactoryHolder {
        private static ConfigurationTransferableDependentFactory INSTANCE = new ConfigurationTransferableDependentFactory();
    }

    /**
     * This method should be used to create an instance of {@link ConfigurationTransferableDependentFactory}.
     * {@link ConfigurationTransferableDependentFactory} is a singleton, so this method returns always the same instance of
     * {@link ConfigurationTransferableDependentFactory}. This method initializes the factory with a configuration and should be
     * called before {@link ConfigurationTransferableDependentFactory#getInstance()} is used, because it has to be initialized with
     * with a configuration at first.
     * @return {@link ConfigurationTransferableDependentFactory} (singleton)
     */
    public static ConfigurationTransferableDependentFactory getInstance(EventServiceConfigurationTransferable aConfiguration) {
        if(aConfiguration == null) {
            throw new ConfigurationException(ConfigurationTransferableDependentFactory.class.getName() + " has to be initialized with a configuration!");
        }
        final boolean isReInit;
        if(myConfiguration == null) {
            myConfiguration = aConfiguration;
            isReInit = true;
        } else {
            isReInit = false;
        }
        ConfigurationTransferableDependentFactory theInstance = ConfigTransferableDependentFactoryHolder.INSTANCE;
        if(isReInit) {
            theInstance.init();
        }
        return theInstance;
    }

    /**
     * This method returns the {@link ConfigurationTransferableDependentFactory} as a singleton. It has to be initialized
     * with a configuration at first. Therefore the method {@link ConfigurationTransferableDependentFactory#getInstance(EventServiceConfigurationTransferable)} should
     * be used at first.
     * @return {@link ConfigurationTransferableDependentFactory} (singleton)
     */
    public static ConfigurationTransferableDependentFactory getInstance() {
        if(myConfiguration == null) {
            throw new ConfigurationException(ConfigurationTransferableDependentFactory.class.getName() + " has to be initialized with a configuration before!");
        }
        return ConfigTransferableDependentFactoryHolder.INSTANCE;
    }

    /**
     * Creates the configured instances from the configuration.
     */
    private void init() {
        if(myConfiguration != null) {
            myConnectionStrategyClientConnector = createObject(myConfiguration.getConnectionStrategyClientConnector());
        } else {
            myConnectionStrategyClientConnector = null;
        }
    }

    /**
     * Returns the configured {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}.
     * @return configured {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}
     */
    public ConnectionStrategyClientConnector getConnectionStrategyClientConnector() {
        return myConnectionStrategyClientConnector;
    }

    /**
     * Creates and initializes an object of a specific type.
     */
    @SuppressWarnings("unchecked")
    private static <T> T createObject(String aClassName) {
        //GWT doesn't support instance creation from a String (via reflection)
        if(aClassName.equals(DefaultClientConnector.class.getName())) {
            return (T)new DefaultClientConnector();
        } else if(aClassName.equals(GWTStreamingClientConnector.class.getName())) {
            return (T)GWT.create(GWTStreamingClientConnector.class);
        } else {
            throw new ConfigurationException("The configured class \"" + aClassName + "\" is unknown!");
        }
    }

    /**
     * Returns the configuration which was specified with the initialization of the {@link ConfigurationTransferableDependentFactory}.
     * @see {@link ConfigurationTransferableDependentFactory#getInstance(EventServiceConfigurationTransferable)}
     * @return configuration which was specified with the initialization of the {@link ConfigurationTransferableDependentFactory}
     */
    public static EventServiceConfigurationTransferable getConfiguration() {
        return myConfiguration;
    }

    /**
     * Resets the {@link ConfigurationTransferableDependentFactory}.
     * It has to be re-initialized with {@link #getInstance(EventServiceConfigurationTransferable)}.
     */
    public static void reset() {
        final ConfigurationTransferableDependentFactory theSingleton = ConfigTransferableDependentFactoryHolder.INSTANCE;
        myConfiguration = null;
        theSingleton.init();
    }
}