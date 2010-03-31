/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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

import de.novanic.eventservice.config.ConfigurationException;
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
        return  myServletConfig != null &&
                (isAvailable(myServletConfig.getInitParameter(ConfigParameter.FQ_MAX_WAITING_TIME_TAG.declaration()))
                        || isAvailable(myServletConfig.getInitParameter(ConfigParameter.MAX_WAITING_TIME_TAG.declaration())))
                && (isAvailable(myServletConfig.getInitParameter(ConfigParameter.FQ_MIN_WAITING_TIME_TAG.declaration()))
                        || isAvailable(myServletConfig.getInitParameter(ConfigParameter.MIN_WAITING_TIME_TAG.declaration())))
                && (isAvailable(myServletConfig.getInitParameter(ConfigParameter.FQ_TIMEOUT_TIME_TAG.declaration()))
                        || isAvailable(myServletConfig.getInitParameter(ConfigParameter.TIMEOUT_TIME_TAG.declaration())));
    }

    /**
     * Loads the configuration with the {@link WebDescriptorConfigurationLoader}.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration
     * @throws ConfigurationException occurs when the configuration can't be loaded or if it contains unreadable values.
     */
    public EventServiceConfiguration load() {
        return new RemoteEventServiceConfiguration("Web-Descriptor-Configuration", readIntParameter(ConfigParameter.MIN_WAITING_TIME_TAG.declaration()),
                readIntParameter(ConfigParameter.MAX_WAITING_TIME_TAG.declaration()), readIntParameter(ConfigParameter.TIMEOUT_TIME_TAG.declaration()),
                readParameter(ConfigParameter.CONNECTION_ID_GENERATOR.declaration()));
    }

    /**
     * Checks if the parameter is available and numeric.
     * @param aParameterValue value to check
     * @return true when it is available and numeric, otherwise false
     */
    private boolean isAvailable(String aParameterValue) {
        return aParameterValue != null && aParameterValue.trim().length() > 0 && StringUtil.isNumeric(aParameterValue);
    }

    /**
     * Reads the numeric value of the parameter. When the value isn't numeric, an {@link de.novanic.eventservice.config.ConfigurationException} is thrown.
     * @param aParameterName parameter
     * @return numeric parameter value
     */
    private int readIntParameter(String aParameterName) {
        final String theParameterValue = readParameter(aParameterName);
        try {
            return StringUtil.readIntegerChecked(theParameterValue);
        } catch(ServiceUtilException e) {
            throw new ConfigurationException("The value of the parameter \"" + aParameterName
                    + "\" was expected to be numeric, but was \"" + theParameterValue + "\"!", e);
        }
    }

    /**
     * Reads the value of a servlet parameter.
     * @param aParameterName servlet parameter
     * @return value of the servlet parameter
     */
    private String readParameter(String aParameterName) {
        return myServletConfig.getInitParameter(aParameterName);
    }
}