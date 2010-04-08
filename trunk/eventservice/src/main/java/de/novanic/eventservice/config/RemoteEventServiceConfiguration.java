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
    private Map<ConfigParameter, Object> myConfigMap;

    private final String myConfigDescription;

    /**
     * Creates a new RemoteEventServiceConfiguration.
     * @param aConfigDescription description of the configuration (for example the location)
     * @param aMinWaitingTime min waiting time before listen returns (in milliseconds)
     * @param aMaxWaitingTime max waiting time before listen returns, when no events recognized (in milliseconds)
     * @param aTimeoutTime timeout time for a listen cycle (in milliseconds)
     */
    public RemoteEventServiceConfiguration(String aConfigDescription, Integer aMinWaitingTime, Integer aMaxWaitingTime, Integer aTimeoutTime) {
        if(aConfigDescription == null) {
            throw new ConfigurationException("The configuration description must be defined!");
        }
        myConfigDescription = aConfigDescription;
        myConfigMap = new HashMap<ConfigParameter, Object>();
        myConfigMap.put(ConfigParameter.MIN_WAITING_TIME_TAG, aMinWaitingTime);
        myConfigMap.put(ConfigParameter.MAX_WAITING_TIME_TAG, aMaxWaitingTime);
        myConfigMap.put(ConfigParameter.TIMEOUT_TIME_TAG, aTimeoutTime);
    }

    /**
     * Creates a new RemoteEventServiceConfiguration.
     * @param aConfigDescription description of the configuration (for example the location)
     * @param aMinWaitingTime min waiting time before listen returns (in milliseconds)
     * @param aMaxWaitingTime max waiting time before listen returns, when no events recognized (in milliseconds)
     * @param aTimeoutTime timeout time for a listen cycle (in milliseconds)
     * @param aConnectionIdGeneratorClassName class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} to generate unique client ids
     * @param aConnectionStrategyClassName class name of the configured {@link de.novanic.eventservice.service.connection.strategy.ConnectionStrategy} to define the communication between client and server side
     */
    public RemoteEventServiceConfiguration(String aConfigDescription, Integer aMinWaitingTime, Integer aMaxWaitingTime, Integer aTimeoutTime,
                                           String aConnectionIdGeneratorClassName, String aConnectionStrategyClassName) {
        this(aConfigDescription, aMinWaitingTime, aMaxWaitingTime, aTimeoutTime);
        myConfigMap.put(ConfigParameter.CONNECTION_ID_GENERATOR, aConnectionIdGeneratorClassName);
        myConfigMap.put(ConfigParameter.CONNECTION_STRATEGY, aConnectionStrategyClassName);
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
     * @return min waiting time
     */
    public Integer getMinWaitingTime() {
        return (Integer)myConfigMap.get(ConfigParameter.MIN_WAITING_TIME_TAG);
    }

    /**
     * Returns the max waiting time. Listening shouldn't hold longer than max waiting time.
     * @return max waiting time
     */
    public Integer getMaxWaitingTime() {
        return (Integer)myConfigMap.get(ConfigParameter.MAX_WAITING_TIME_TAG);
    }

    /**
     * Returns the timeout time (max time for a listen cycle).
     * @return timeout time
     */
    public Integer getTimeoutTime() {
        return (Integer)myConfigMap.get(ConfigParameter.TIMEOUT_TIME_TAG);
    }

    /**
     * Returns the class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}.
     * The {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} generates unique ids to identify the clients.
     * @return class name of the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}
     */
    public String getConnectionIdGeneratorClassName() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_ID_GENERATOR);
    }

    /**
     * Returns the class name of the configured {@link de.novanic.eventservice.service.connection.strategy.ConnectionStrategy}.
     * A {@link de.novanic.eventservice.service.connection.strategy.ConnectionStrategy} is used to define the communication
     * between the client and the server side.
     * @return class name of the configured {@link de.novanic.eventservice.service.connection.strategy.ConnectionStrategy}
     */
    public String getConnectionStrategyClassName() {
        return (String)myConfigMap.get(ConfigParameter.CONNECTION_STRATEGY);
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
        theConfigStringBuilder.append(PlatformUtil.getNewLine());
        theConfigStringBuilder.append("  ");
        theConfigStringBuilder.append("Min.: ");
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
