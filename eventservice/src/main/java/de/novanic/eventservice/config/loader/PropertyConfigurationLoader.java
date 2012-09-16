/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschr�nkt)
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

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
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
public class PropertyConfigurationLoader extends AbstractConfigurationLoader
{
    private static final Logger LOG = LoggerFactory.getLogger(PropertyConfigurationLoader.class);
    private static final String DEFAULT_PROPERTY_NAME = "eventservice.properties";

    private final String myPropertyName;
    private Properties myProperties;

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
     * Returns the description of the configuration (could for example contain the config file name).
     * @return configuration description
     */
    @Override
    protected String getConfigDescription() {
        StringBuilder theConfigDescriptionBuffer = new StringBuilder(15 + myPropertyName.length());
        theConfigDescriptionBuffer.append("Properties \"");
        theConfigDescriptionBuffer.append(myPropertyName);
        theConfigDescriptionBuffer.append('\"');
        return theConfigDescriptionBuffer.toString();
    }

    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't called. In the case of {@link PropertyConfigurationLoader} the method
     * returns true when the location of the properties file is attached to the classpath.
     * @return true when available, otherwise false
     */
    @Override
    public boolean isAvailable() {
        return getPropertiesStream() != null;
    }

    /**
     * Loads the configuration with the loader.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration or NULL if the
     * properties file couldn't found with the classpath.
     * @throws ConfigurationException occurs when the configuration can't be loaded or if it contains unreadable values.
     */
    @Override
    public EventServiceConfiguration load() {
        InputStream thePropertiesInputStream = getPropertiesStream();
        if(thePropertiesInputStream != null) {
            try {
                myProperties = new Properties();
                myProperties.load(thePropertiesInputStream);
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
        return super.load();
    }

    /**
     * Reads the value for a config parameter from the properties (file).
     * @param aConfigParameterDeclaration config parameter
     * @return red parameter value
     */
    @Override
    protected String readParameterValue(String aConfigParameterDeclaration) {
        return myProperties.getProperty(aConfigParameterDeclaration);
    }

    /**
     * Returns the properties file as a stream ({@link java.io.InputStream}).
     * @return properties file as a stream ({@link java.io.InputStream})
     */
    private InputStream getPropertiesStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(myPropertyName);
    }
}