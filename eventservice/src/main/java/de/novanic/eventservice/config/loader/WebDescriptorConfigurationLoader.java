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
public class WebDescriptorConfigurationLoader extends AbstractConfigurationLoader
{
    private static final String CONFIG_DESCRIPTION = "Web-Descriptor-Configuration";

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
     * Returns the name/description of the created configuration.
     * In this case {@link #CONFIG_DESCRIPTION} is returned.
     * @return name/description of the configuration
     */
    @Override
    protected String getConfigDescription() {
        return CONFIG_DESCRIPTION;
    }

    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't called. In the case of {@link WebDescriptorConfigurationLoader} the method
     * returns true when the init-parameters for the servlet are registered in the web-descriptor.
     * @return true when available, otherwise false
     */
    @Override
    public boolean isAvailable() {
        return myServletConfig != null && super.isAvailable();
    }

    /**
     * Reads the value of a config parameter from the servlet configuration / web descriptor.
     * @param aConfigParameterDeclaration config parameter
     * @return red parameter value
     */
    @Override
    protected String readParameterValue(String aConfigParameterDeclaration) {
        return myServletConfig.getInitParameter(aConfigParameterDeclaration);
    }
}