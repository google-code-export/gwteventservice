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

import de.novanic.eventservice.config.loader.ConfigurationException;
import de.novanic.eventservice.util.PlatformUtil;

/**
 * An EventServiceConfiguration holds the configuration for {@link de.novanic.eventservice.client.event.service.EventService}.
 * The time for a timeout and the min- and max-waiting-time can be configured.
 * <br>
 * <br>- Min waiting time - Listening should hold at least for min waiting time.
 * <br>- Max waiting time - Listening shouldn't hold longer than max waiting time.
 * <br>- Timeout time - Max time for a listen cycle. If the timeout time is overlapsed, the client will be deregistered.
 *
 * @author sstrohschein
 * <br>Date: 09.08.2008
 * <br>Time: 23:17:38
 */
public class RemoteEventServiceConfiguration implements EventServiceConfiguration
{
    private final int myMinWaitingTime;
    private final int myMaxWaitingTime;
    private final int myTimeoutTime;
    private final String myConfigDescription;

    /**
     * Creates a new RemoteEventServiceConfiguration.
     * @param aConfigDescription description of the configuration (for example the location)
     * @param aMinWaitingTime min waiting time before listen returns (in milliseconds)
     * @param aMaxWaitingTime max waiting time before listen returns, when no events recognized (in milliseconds)
     * @param aTimeoutTime timeout time for a listen cycle (in milliseconds)
     */
    public RemoteEventServiceConfiguration(String aConfigDescription, int aMinWaitingTime, int aMaxWaitingTime, int aTimeoutTime) {
        if(aConfigDescription == null) {
            throw new ConfigurationException("The configuration description must be defined!");
        }
        myConfigDescription = aConfigDescription;
        myMinWaitingTime = aMinWaitingTime;
        myMaxWaitingTime = aMaxWaitingTime;
        myTimeoutTime = aTimeoutTime;
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
    public int getMinWaitingTime() {
        return myMinWaitingTime;
    }

    /**
     * Returns the max waiting time. Listening shouldn't hold longer than max waiting time.
     * @return max waiting time
     */
    public int getMaxWaitingTime() {
        return myMaxWaitingTime;
    }

    /**
     * Returns the timeout time (max time for a listen cycle).
     * @return timeout time
     */
    public int getTimeoutTime() {
        return myTimeoutTime;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        EventServiceConfiguration theConfiguration = (EventServiceConfiguration)anObject;

        return (myMaxWaitingTime == theConfiguration.getMaxWaitingTime()
                && myMinWaitingTime == theConfiguration.getMinWaitingTime()
                && myTimeoutTime == theConfiguration.getTimeoutTime()
                && myConfigDescription.equals(theConfiguration.getConfigDescription()));

    }

    public int hashCode() {
        int theResult = myMinWaitingTime;
        theResult = 31 * theResult + myMaxWaitingTime;
        theResult = 31 * theResult + myTimeoutTime;
        theResult = 31 * theResult + myConfigDescription.hashCode();
        return theResult;
    }

    public String toString() {
        StringBuilder theConfigStringBuilder = new StringBuilder(120);
        theConfigStringBuilder.append("EventServiceConfiguration (");
        theConfigStringBuilder.append(getConfigDescription());
        theConfigStringBuilder.append(')');
        theConfigStringBuilder.append(PlatformUtil.getNewLine());
        theConfigStringBuilder.append("  ");
        theConfigStringBuilder.append("Min.: ");
        theConfigStringBuilder.append(getMinWaitingTime());
        theConfigStringBuilder.append("ms; Max.: ");
        theConfigStringBuilder.append(getMaxWaitingTime());
        theConfigStringBuilder.append("ms; Timeout: ");
        theConfigStringBuilder.append(getTimeoutTime());
        theConfigStringBuilder.append("ms");
        return theConfigStringBuilder.toString();
    }
}
