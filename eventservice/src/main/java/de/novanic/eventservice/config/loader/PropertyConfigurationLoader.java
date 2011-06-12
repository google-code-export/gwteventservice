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

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;

import java.util.Properties;
import java.util.Scanner;
import java.io.InputStream;
import java.io.IOException;

/**
 * PropertyConfigurationLoader is used by {@link de.novanic.eventservice.config.EventServiceConfigurationFactory}
 * to load the {@link de.novanic.eventservice.config.EventServiceConfiguration} with a properties file.
 * The default name of the properties is "eventservice.properties". For that file will be searched if not other defined.
 * 
 * @author sstrohschein
 *         <br>Date: 23.10.2008
 *         <br>Time: 14:37:13
 */
public class PropertyConfigurationLoader implements ConfigurationLoader
{
    private static final ServerLogger LOG = ServerLoggerFactory.getServerLogger(PropertyConfigurationLoader.class.getName());
    private static final String DEFAULT_PROPERTY_NAME = "eventservice.properties";

    private final String myPropertyName;

    /**
     * Creates a {@link PropertyConfigurationLoader} with the default properties ("eventservice.properties").
     */
    public PropertyConfigurationLoader() {
        this(null);
    }

    /**
     * Creates a {@link PropertyConfigurationLoader} with a properties file.
     * @param aPropertyName properties file to load (the location must be attached to the classpath)
     */
    public PropertyConfigurationLoader(String aPropertyName) {
        if(aPropertyName != null) {
            myPropertyName = aPropertyName;
        } else {
            myPropertyName = DEFAULT_PROPERTY_NAME;
        }
    }

    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't called. In the case of {@link PropertyConfigurationLoader} the method
     * returns true when the location of the properties file is attached to the classpath.
     * @return true when available, otherwise false
     */
    public boolean isAvailable() {
        return getPropertiesStream() != null;
    }

    /**
     * Loads the configuration with the loader.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration or NULL if the
     * properties file couldn't found with the classpath.
     * @throws ConfigurationException occurs when the configuration can't be loaded or if it contains unreadable values.
     */
    public EventServiceConfiguration load() {
        InputStream thePropertiesInputStream = getPropertiesStream();
        if(thePropertiesInputStream != null) {
            try {
                Properties theProperties = new Properties();
                theProperties.load(thePropertiesInputStream);
                return load(theProperties);
            } catch(IOException e) {
                throw new ConfigurationException("Error on loading \"" + myPropertyName + "\"!", e);
            } finally {
                try {
                    thePropertiesInputStream.close();
                } catch(IOException e) {
                    LOG.error("Error on closing stream of \"" + myPropertyName + "\"!", e);
                }
            }
        }
        return null;
    }

    /**
     * Returns the properties file as a stream ({@link java.io.InputStream}).
     * @return properties file as a stream ({@link java.io.InputStream})
     */
    private InputStream getPropertiesStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(myPropertyName);
    }

    /**
     * Loads the {@link de.novanic.eventservice.config.EventServiceConfiguration} with the values of the properties file.
     * @param aProperties properties to initialize the {@link de.novanic.eventservice.config.EventServiceConfiguration}
     * @return initialized {@link de.novanic.eventservice.config.EventServiceConfiguration}
     * @throws ConfigurationException thrown when the values aren't parsable to an integer
     */
    private EventServiceConfiguration load(Properties aProperties) {
        final Integer theMaxWaitingTime = getIntValue(getPropertyValue(aProperties, ConfigParameter.FQ_MAX_WAITING_TIME_TAG, ConfigParameter.MAX_WAITING_TIME_TAG));
        final Integer theMinWaitingTime = getIntValue(getPropertyValue(aProperties, ConfigParameter.FQ_MIN_WAITING_TIME_TAG, ConfigParameter.MIN_WAITING_TIME_TAG));
        final Integer theTimeoutTime = getIntValue(getPropertyValue(aProperties, ConfigParameter.FQ_TIMEOUT_TIME_TAG, ConfigParameter.TIMEOUT_TIME_TAG));
        final Integer theReconnectAttemptCount = getIntValue(getPropertyValue(aProperties, ConfigParameter.FQ_RECONNECT_ATTEMPT_COUNT_TAG, ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG));
        final String theConnectionIdGenerator = getPropertyValue(aProperties, ConfigParameter.FQ_CONNECTION_ID_GENERATOR, ConfigParameter.CONNECTION_ID_GENERATOR);
        final String theConnectionStrategyClientConnector = getPropertyValue(aProperties, ConfigParameter.FQ_CONNECTION_STRATEGY_CLIENT_CONNECTOR, ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR);
        final String theConnectionStrategyServerConnector = getPropertyValue(aProperties, ConfigParameter.FQ_CONNECTION_STRATEGY_SERVER_CONNECTOR, ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR);
        final String theConnectionStrategyEncoding = getPropertyValue(aProperties, ConfigParameter.FQ_CONNECTION_STRATEGY_ENCODING, ConfigParameter.CONNECTION_STRATEGY_ENCODING);

        return new RemoteEventServiceConfiguration(getConfigDescription(), theMinWaitingTime, theMaxWaitingTime, theTimeoutTime,
                theReconnectAttemptCount,
                theConnectionIdGenerator, theConnectionStrategyClientConnector, theConnectionStrategyServerConnector, theConnectionStrategyEncoding);
    }

    /**
     * Parses the integer value from a {@link String}.
     * @param aString {@link String} to parse the integer values
     * @return integer value which is contained in the {@link String}
     * @throws de.novanic.eventservice.client.config.ConfigurationException thrown when the {@link String} values isn't parsable to an integer value
     */
    private Integer getIntValue(String aString) {
        if(aString != null) {
            Scanner theScanner = new Scanner(aString);
            if(theScanner.hasNextInt()) {
                return theScanner.nextInt();
            } else {
                throw new ConfigurationException("Error on processing configuration \"" + myPropertyName + "\"! " +
                        "The value \"" + aString + "\" couldn't parsed to an integer!");
            }
        }
        return null;
    }

    /**
     * Returns the description of the configuration (could for example contain the config file name).
     * @return configuration description
     */
    private String getConfigDescription() {
        StringBuilder theConfigDescriptionBuffer = new StringBuilder(15 + myPropertyName.length());
        theConfigDescriptionBuffer.append("Properties \"");
        theConfigDescriptionBuffer.append(myPropertyName);
        theConfigDescriptionBuffer.append('\"');
        return theConfigDescriptionBuffer.toString();
    }

    /**
     * Returns the best property value. The specified properties are checked in sequence and the value of the first available property is returned.
     * @param aProperties properties
     * @param aConfigParameters properties in sequence to check
     * @return return the value of the best / first available property
     */
    private String getPropertyValue(Properties aProperties, ConfigParameter... aConfigParameters) {
        for(ConfigParameter theConfigParameter: aConfigParameters) {
            String theValue = aProperties.getProperty(theConfigParameter.declaration());
            if(theValue != null) {
                return theValue;
            }
        }
        return null;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }
        PropertyConfigurationLoader theOtherLoader = (PropertyConfigurationLoader)anObject;
        return myPropertyName.equals(theOtherLoader.myPropertyName);
    }

    public int hashCode() {
        return myPropertyName.hashCode();
    }
}
