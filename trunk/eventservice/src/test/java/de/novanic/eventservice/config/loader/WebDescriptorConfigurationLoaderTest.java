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
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import junit.framework.TestCase;

import javax.servlet.ServletConfig;

import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * @author sstrohschein
 *         <br>Date: 01.07.2009
 *         <br>Time: 22:17:37
 */
public class WebDescriptorConfigurationLoaderTest extends TestCase
{
    public void testLoad() {
        ServletConfig theServletConfig = new ServletConfigDummy(true, false);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals(Integer.valueOf(30000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(0), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(120000), theConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        //TODO test connection strategy when the first implementation is available / committed
    }

	public void testLoad_FQ() {
        ServletConfig theServletConfig = new ServletConfigDummy(true, true);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertTrue(theConfigurationLoader.isAvailable());

        EventServiceConfiguration theConfiguration = theConfigurationLoader.load();
        assertEquals(Integer.valueOf(40000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(1), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(130000), theConfiguration.getTimeoutTime());
        assertEquals(SessionConnectionIdGenerator.class.getName(), theConfiguration.getConnectionIdGeneratorClassName());
        //TODO test connection strategy when the first implementation is available / committed
    }
	
    public void testLoad_Error() {
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(null);

        assertFalse(theConfigurationLoader.isAvailable());
    }

    public void testLoad_Error_2() {
        ServletConfig theServletConfig = new ServletConfigDummy(false, false);
        ConfigurationLoader theConfigurationLoader = new WebDescriptorConfigurationLoader(theServletConfig);

        assertFalse(theConfigurationLoader.isAvailable());
        try {
            theConfigurationLoader.load();
            fail(ConfigurationException.class.getName() + " expected!");
        } catch(ConfigurationException e) {}
    }
}