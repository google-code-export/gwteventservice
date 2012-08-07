/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschränkt)
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
                myInitParameters.put(ConfigParameter.MAX_WAITING_TIME_TAG.declarationFQ(), "40000");
                myInitParameters.put(ConfigParameter.MIN_WAITING_TIME_TAG.declarationFQ(), "001");
                myInitParameters.put(ConfigParameter.TIMEOUT_TIME_TAG.declarationFQ(), "130000");
                myInitParameters.put(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG.declarationFQ(), "1");
                myInitParameters.put(ConfigParameter.CONNECTION_ID_GENERATOR.declarationFQ(), SessionConnectionIdGenerator.class.getName());
                myInitParameters.put(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR.declarationFQ(), LongPollingServerConnector.class.getName());
                myInitParameters.put(ConfigParameter.CONNECTION_STRATEGY_ENCODING.declarationFQ(), "utf-8");
                myInitParameters.put(ConfigParameter.MAX_EVENTS.declarationFQ(), "5000");
            } else {
                myInitParameters.put(ConfigParameter.MAX_WAITING_TIME_TAG.declaration(), "30000");
                myInitParameters.put(ConfigParameter.MIN_WAITING_TIME_TAG.declaration(), "000");
                myInitParameters.put(ConfigParameter.TIMEOUT_TIME_TAG.declaration(), "120000");
                myInitParameters.put(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG.declaration(), "3");
                myInitParameters.put(ConfigParameter.CONNECTION_ID_GENERATOR.declaration(), SessionConnectionIdGenerator.class.getName());
                myInitParameters.put(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration(), LongPollingServerConnector.class.getName());
                myInitParameters.put(ConfigParameter.CONNECTION_STRATEGY_ENCODING.declaration(), "iso-8859-1");
                myInitParameters.put(ConfigParameter.MAX_EVENTS.declaration(), "7000");
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

    public void addParameter(ConfigParameter aParameter, String aValue) {
        myInitParameters.put(aParameter.declaration(), aValue);
    }

    public boolean removeParameter(ConfigParameter aParameter) {
        return myInitParameters.remove(aParameter.declaration()) != null;
    }
}