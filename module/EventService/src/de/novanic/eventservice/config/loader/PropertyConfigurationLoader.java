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
    private static final String MAX_WAITING_TIME_TAG = "time.waiting.max";
    private static final String MIN_WAITING_TIME_TAG = "time.waiting.min";
    private static final String TIMEOUT_TIME_TAG = "time.timeout";

    private String myPropertyName;

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
     * @throws ConfigurationException occures when the configuration can't be loaded or if it contains unreadable values.
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
        int theMaxWaitingTime = getIntValue(aProperties.getProperty(MAX_WAITING_TIME_TAG));
        int theMinWaitingTime = getIntValue(aProperties.getProperty(MIN_WAITING_TIME_TAG));
        int theTimeoutTime = getIntValue(aProperties.getProperty(TIMEOUT_TIME_TAG));

        return new RemoteEventServiceConfiguration(theMinWaitingTime, theMaxWaitingTime, theTimeoutTime);
    }

    /**
     * Parses the integer value from a {@link String}.
     * @param aString {@link String} to parse the integer values
     * @return integer value which is contained in the {@link String}
     * @throws ConfigurationException thrown when the {@link String} values isn't parsable to an integer value
     */
    private int getIntValue(String aString) {
        Scanner theScanner = new Scanner(aString);
        if(theScanner.hasNextInt()) {
            return theScanner.nextInt();
        } else {
            throw new ConfigurationException("Error on processing configuration \"" + myPropertyName + "\"! " +
                    "The value \"" + aString + "\" couldn't parsed to an integer!");
        }
    }
}