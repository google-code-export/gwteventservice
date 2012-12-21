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
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.util.StringUtil;
import de.novanic.eventservice.util.ServiceUtilException;

import javax.servlet.ServletConfig;

/**
 * WebDescriptorConfigurationLoader is used by {@link de.novanic.eventservice.config.EventServiceConfigurationFactory}
 * to load the {@link de.novanic.eventservice.config.EventServiceConfiguration} with the servlet parameters / init-parameters
 * of the web-descriptor.
 * 
 * @author sstrohschein
 *         <br>Date: 05.03.2009
 *         <br>Time: 23:37:15
 */
public class WebDescriptorConfigurationLoader implements ConfigurationLoader
{
    private final ServletConfig myServletConfig;

    /**
     * Creates a {@link de.novanic.eventservice.config.loader.WebDescriptorConfigurationLoader} with a servlet config
     * ({@link javax.servlet.ServletConfig}).
     * @param aServletConfig servlet config
     */
    public WebDescriptorConfigurationLoader(ServletConfig aServletConfig) {
        myServletConfig = aServletConfig;
    }

    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't called. In the case of {@link WebDescriptorConfigurationLoader} the method
     * returns true when the init-parameters for the servlet are registered in the web-descriptor.
     * @return true when available, otherwise false
     */
    public boolean isAvailable() {
        if(myServletConfig != null) {
            //The configuration is available when at least one parameter is configured.
            for(ConfigParameter theConfigParameter: ConfigParameter.values()) {
                final String theParameterValue = myServletConfig.getInitParameter(theConfigParameter.declaration());
                if(theParameterValue != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Loads the configuration with the {@link WebDescriptorConfigurationLoader}.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration
     * @throws ConfigurationException occurs when the configuration can't be loaded or if it contains unreadable values.
     */
    public EventServiceConfiguration load() {
        if(isAvailable()) {
            return new RemoteEventServiceConfiguration("Web-Descriptor-Configuration",
                    readIntParameter(ConfigParameter.MIN_WAITING_TIME_TAG.declaration(), ConfigParameter.FQ_MIN_WAITING_TIME_TAG.declaration()),
                    readIntParameter(ConfigParameter.MAX_WAITING_TIME_TAG.declaration(), ConfigParameter.FQ_MAX_WAITING_TIME_TAG.declaration()),
                    readIntParameter(ConfigParameter.TIMEOUT_TIME_TAG.declaration(), ConfigParameter.FQ_TIMEOUT_TIME_TAG.declaration()),
                    readIntParameter(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG.declaration(), ConfigParameter.FQ_RECONNECT_ATTEMPT_COUNT_TAG.declaration()),
                    readParameter(ConfigParameter.CONNECTION_ID_GENERATOR.declaration(), ConfigParameter.FQ_CONNECTION_ID_GENERATOR.declaration()),
                    readParameter(ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR.declaration(), ConfigParameter.FQ_CONNECTION_STRATEGY_CLIENT_CONNECTOR.declaration()),
                    readParameter(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration(), ConfigParameter.FQ_CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration()),
                    readParameter(ConfigParameter.CONNECTION_STRATEGY_ENCODING.declaration(), ConfigParameter.FQ_CONNECTION_STRATEGY_ENCODING.declaration()),
                    readIntParameter(ConfigParameter.MAX_EVENTS.declaration(), ConfigParameter.FQ_MAX_EVENTS.declaration()));
        }
        return null;
    }

    /**
     * Checks if the parameter is available.
     * @param aParameterValue value to check
     * @return true when it is available, otherwise false
     */
    private boolean isAvailable(String aParameterValue) {
        return aParameterValue != null && aParameterValue.trim().length() > 0;
    }

    /**
     * Reads the numeric value of the parameter. When the value isn't numeric, an {@link de.novanic.eventservice.client.config.ConfigurationException} is thrown.
	 * @param aParameterName parameter
	 * @param aParameterNameFQ parameter (full-qualified variant)
     * @return numeric parameter value
     * @throws ConfigurationException (when the value isn't numeric)
     */
    private Integer readIntParameter(String aParameterName, String aParameterNameFQ) {
        final String theParameterValue = readParameter(aParameterName, aParameterNameFQ);
        if(theParameterValue != null) {
            try {
                return StringUtil.readInteger(theParameterValue);
            } catch(ServiceUtilException e) {
                throw new ConfigurationException("The value of the parameter \"" + aParameterName
                        + "\" was expected to be numeric, but was \"" + theParameterValue + "\"!", e);
            }
        }
        return null;
    }

    /**
     * Reads the value of a servlet parameter.
     * @param aParameterName servlet parameter
	 * @param aParameterNameFQ parameter (full-qualified variant)
     * @return value of the servlet parameter
     */
    private String readParameter(String aParameterName, String aParameterNameFQ) {
        if(isAvailable(myServletConfig.getInitParameter(aParameterNameFQ))) {
            return myServletConfig.getInitParameter(aParameterNameFQ);
		}
        return myServletConfig.getInitParameter(aParameterName);
    }
}