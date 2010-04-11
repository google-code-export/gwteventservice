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

import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collections;

/**
 * @author sstrohschein
 *         <br>Date: 02.07.2009
 *         <br>Time: 23:34:45
 */
public class ServletConfigDummy implements ServletConfig
{
    private Map<String, String> myInitParameters;

    public ServletConfigDummy(boolean isInit, boolean isFQ) {
        myInitParameters = new HashMap<String, String>();
        if(isInit) {
            if(isFQ) {
                myInitParameters.put(ConfigParameter.FQ_MAX_WAITING_TIME_TAG.declaration(), "40000");
                myInitParameters.put(ConfigParameter.FQ_MIN_WAITING_TIME_TAG.declaration(), "001");
                myInitParameters.put(ConfigParameter.FQ_TIMEOUT_TIME_TAG.declaration(), "130000");
                myInitParameters.put(ConfigParameter.FQ_CONNECTION_ID_GENERATOR.declaration(), SessionConnectionIdGenerator.class.getName());
                myInitParameters.put(ConfigParameter.FQ_CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration(), LongPollingServerConnector.class.getName());
            } else {
                myInitParameters.put(ConfigParameter.MAX_WAITING_TIME_TAG.declaration(), "30000");
                myInitParameters.put(ConfigParameter.MIN_WAITING_TIME_TAG.declaration(), "000");
                myInitParameters.put(ConfigParameter.TIMEOUT_TIME_TAG.declaration(), "120000");
                myInitParameters.put(ConfigParameter.CONNECTION_ID_GENERATOR.declaration(), SessionConnectionIdGenerator.class.getName());
                myInitParameters.put(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration(), LongPollingServerConnector.class.getName());
            }
        }
    }

    public String getServletName() {
        return ServletConfigDummy.class.getName();
    }

    public ServletContext getServletContext() {
        return null;
    }

    public String getInitParameter(String aName) {
        return myInitParameters.get(aName);
    }

    public Enumeration getInitParameterNames() {
        return Collections.enumeration(myInitParameters.keySet());
    }
}