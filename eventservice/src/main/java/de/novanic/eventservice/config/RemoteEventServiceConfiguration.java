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
package de.novanic.eventservice.config;

import de.novanic.eventservice.util.PlatformUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * An EventServiceConfiguration holds the configuration for {@link de.novanic.eventservice.client.event.service.EventService}.
 * The time for a timeout and the min- and max-waiting-time can be configured.
 * <br>
 * <br>- Min waiting time - Listening should hold at least for min waiting time.
 * <br>- Max waiting time - Listening shouldn't hold longer than max waiting time.
 * <br>- Timeout time - Max time for a listen cycle. If the timeout time is exceeded, the client will be deregistered.
 *
 * @author sstrohschein
 * <br>Date: 09.08.2008
 * <br>Time: 23:17:38
 */
public class RemoteEventServiceConfiguration implements EventServiceConfiguration
{
    private final String myConfigDescription;

    private Map<ConfigParameter, Object> myConfigMap;

    /**
     * Creates a new RemoteEventServiceConfiguration.
     * @param aConfigDescription description of the configuration (for example the location)
     * @param aMinWaitingTime min waiting time before listen returns (in milliseconds)
     * @param aMaxWaitingTime max waiting time before listen returns, when no events recognized (in milliseconds)
     * @param aTimeoutTime timeout time for a listen cycle (in milliseconds)
     * @param aReconnectAttemptCount number of reconnect attempts
     * @param aConnectionIdGeneratorClassName class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} to generate unique client ids
     * @param aConnectionStrategyClientClassName class name of the configured connection strategy (client side part)
     * @param aConnectionStrategyServerClassName class name of the configured connection strategy (server side part)
     * @param aConnectionStrategyEncoding encoding / charset which is used by the connection strategies
     */
    public RemoteEventServiceConfiguration(String aConfigDescription, Integer aMinWaitingTime, Integer aMaxWaitingTime, Integer aTimeoutTime,
                                           Integer aReconnectAttemptCount,
                                           String aConnectionIdGeneratorClassName, String aConnectionStrategyClientClassName, String aConnectionStrategyServerClassName, String aConnectionStrategyEncoding,
                                           Integer aMaxEvents) {
        myConfigDescription = aConfigDescription;
        myConfigMap = new HashMap<ConfigParameter, Object>();
        myConfigMap.put(ConfigParameter.MIN_WAITING_TIME_TAG, aMinWaitingTime);
        myConfigMap.put(ConfigParameter.MAX_WAITING_TIME_TAG, aMaxWaitingTime);
        myConfigMap.put(ConfigParameter.TIMEOUT_TIME_TAG, aTimeoutTime);
        myConfigMap.put(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG, aReconnectAttemptCount);
        myConfigMap.put(ConfigParameter.CONNECTION_ID_GENERATOR, aConnectionIdGeneratorClassName);
        myConfigMap.put(ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR, aConnectionStrategyClientClassName);
        myConfigMap.put(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR, aConnectionStrategyServerClassName);
        myConfigMap.put(ConfigParameter.CONNECTION_STRATEGY_ENCODING, aConnectionStrategyEncoding);
        myConfigMap.put(ConfigParameter.MAX_EVENTS, aMaxEvents);
    }

    /**
     * Returns the description of the configuration (for example the location).
     * @return configuration description
     */
    public String getConfigDescription() {
        return myConfigDescription;
    }

    /**
     * Returns the min waiting time. Listening should hold at least for min waiting time.
     * @see de.novanic.eventservice.config.ConfigParameter#MIN_WAITING_TIME_TAG
     * @return min waiting time
     */
    public Integer getMinWaitingTime() {
        return (Integer)myConfigMap.get(ConfigParameter.MIN_WAITING_TIME_TAG);
    }

    /**
     * Returns the max waiting time. Listening shouldn't hold longer than max waiting time.
     * @see de.novanic.eventservice.config.ConfigParameter#MAX_WAITING_TIME_TAG
     * @return max waiting time
     */
    public Integer getMaxWaitingTime() {
        return (Integer)myConfigMap.get(ConfigParameter.MAX_WAITING_TIME_TAG);
    }

    /**
     * Returns the timeout time (max time for a listen cycle).
     * @see de.novanic.eventservice.config.ConfigParameter#TIMEOUT_TIME_TAG
     * @return timeout time
     */
    public Integer getTimeoutTime() {
        return (Integer)myConfigMap.get(ConfigParameter.TIMEOUT_TIME_TAG);
    }

    /**
     * Returns the number of reconnect attempts to execute.
     * @see de.novanic.eventservice.config.ConfigParameter#RECONNECT_ATTEMPT_COUNT_TAG
     * @return reconnect attempt count
     */
    public Integer getReconnectAttemptCount() {
        return (Integer)myConfigMap.get(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG);
    }

    /**
     * Returns the class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}.
     * The {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} generates unique ids to identify the clients.
     * @see de.novanic.eventservice.config.ConfigParameter#CONNECTION_ID_GENERATOR
     * @return class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}
     */
    public String getConnectionIdGeneratorClassName() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_ID_GENERATOR);
    }

    /**
     * Returns the class name of the configured connection strategy (client side part).
     * @see de.novanic.eventservice.config.ConfigParameter#CONNECTION_STRATEGY_CLIENT_CONNECTOR
     * @return connection strategy (client side part)
     */
    public String getConnectionStrategyClientConnectorClassName() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR);
    }

    /**
     * Returns the class name of the configured connection strategy (server side part).
     * @see de.novanic.eventservice.config.ConfigParameter#CONNECTION_STRATEGY_SERVER_CONNECTOR
     * @return connection strategy (server side part)
     */
    public String getConnectionStrategyServerConnectorClassName() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR);
    }

    /**
     * Returns the configured encoding / charset for the connection strategy.
     * @see de.novanic.eventservice.config.ConfigParameter#CONNECTION_STRATEGY_ENCODING
     * @return configured encoding / charset
     */
    public String getConnectionStrategyEncoding() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_STRATEGY_ENCODING);
    }

    /**
     * Returns the configured maximum amount of events which should be transferred to the client at once.
     * @return configured maximum amount of events at once
     */
    public Integer getMaxEvents() {
        return (Integer)myConfigMap.get(ConfigParameter.MAX_EVENTS);
    }

    /**
     * Returns the configurations as a {@link java.util.Map} with {@link de.novanic.eventservice.config.ConfigParameter}
     * instances as the key.
     * @return {@link java.util.Map} with the configurations with {@link de.novanic.eventservice.config.ConfigParameter}
     * instances as the key
     */
    public Map<ConfigParameter, Object> getConfigMap() {
        return myConfigMap;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        EventServiceConfiguration theConfiguration = (EventServiceConfiguration)anObject;

        return (getConfigMap().equals(theConfiguration.getConfigMap())
                && myConfigDescription.equals(theConfiguration.getConfigDescription()));

    }

    public int hashCode() {
        int theResult = getConfigMap().hashCode();
        theResult = 31 * theResult + myConfigDescription.hashCode();
        return theResult;
    }

    public String toString() {
        final String UNDEFINED = "<undefined>";
        final Integer theMinWaitingTime = getMinWaitingTime();
        final Integer theMaxWaitingTime = getMaxWaitingTime();
        final Integer theTimeoutTime = getTimeoutTime();

        StringBuilder theConfigStringBuilder = new StringBuilder(120);
        theConfigStringBuilder.append("EventServiceConfiguration (");
        theConfigStringBuilder.append(getConfigDescription());
        theConfigStringBuilder.append(')');
        //print time settings
        theConfigStringBuilder.append(PlatformUtil.getNewLine());
        theConfigStringBuilder.append("  Min.: ");
        if(theMinWaitingTime != null) {
            theConfigStringBuilder.append(theMinWaitingTime);
        } else {
            theConfigStringBuilder.append(UNDEFINED);
        }
        theConfigStringBuilder.append("ms; Max.: ");
        if(theMaxWaitingTime != null) {
            theConfigStringBuilder.append(theMaxWaitingTime);
        } else {
            theConfigStringBuilder.append(UNDEFINED);
        }
        theConfigStringBuilder.append("ms; Timeout: ");
        if(theTimeoutTime != null) {
            theConfigStringBuilder.append(theTimeoutTime);
        } else {
            theConfigStringBuilder.append(UNDEFINED);
        }
        theConfigStringBuilder.append("ms");
        return theConfigStringBuilder.toString();
    }
}
