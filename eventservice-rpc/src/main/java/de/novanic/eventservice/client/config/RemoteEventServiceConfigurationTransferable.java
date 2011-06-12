/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.client.config;

/**
 * An {@link de.novanic.eventservice.client.config.EventServiceConfigurationTransferable} holds the configuration options for
 * {@link de.novanic.eventservice.client.event.service.EventService} which could be usable by the client side.
 * The whole covered configuration for {@link de.novanic.eventservice.client.event.service.EventService} is only available
 * to the server side and isn't transferable / serializable.
 *
 * @author sstrohschein
 *         <br>Date: 29.03.2010
 *         <br>Time: 22:11:06
 */
public class RemoteEventServiceConfigurationTransferable implements EventServiceConfigurationTransferable
{
    private Integer myMinWaitingTime;
    private Integer myMaxWaitingTime;
    private Integer myTimeoutTime;
    private Integer myReconnectAttemptCount;
    private String myConnectionId;
    private String myConnectionStrategyClientConnector;

    /**
     * This constructor is only required for serialization.
     */
    public RemoteEventServiceConfigurationTransferable() {}

    /**
     * Creates a new RemoteEventServiceConfigurationTransferable.
     * @param aMinWaitingTime min waiting time before listen returns (in milliseconds)
     * @param aMaxWaitingTime max waiting time before listen returns, when no events recognized (in milliseconds)
     * @param aTimeoutTime timeout time for a listen cycle (in milliseconds)
     * @param aReconnectAttemptCount number of reconnect attempts to execute
     * @param aConnectionId unique id to identify the client
     * @param aConnectionStrategyClientConnector class name of the configured connection strategy (client side part)
     */
    public RemoteEventServiceConfigurationTransferable(int aMinWaitingTime, int aMaxWaitingTime, int aTimeoutTime, int aReconnectAttemptCount,
                                                       String aConnectionId, String aConnectionStrategyClientConnector) {
        myMinWaitingTime = aMinWaitingTime;
        myMaxWaitingTime = aMaxWaitingTime;
        myTimeoutTime = aTimeoutTime;
        myReconnectAttemptCount = aReconnectAttemptCount;
        myConnectionId = aConnectionId;
        myConnectionStrategyClientConnector = aConnectionStrategyClientConnector;
    }

    /**
     * Returns the min waiting time. Listening should hold at least for min waiting time.
     * @return min waiting time
     */
    public Integer getMinWaitingTime() {
        return myMinWaitingTime;
    }

    /**
     * Returns the max waiting time. Listening shouldn't hold longer than max waiting time.
     * @return max waiting time
     */
    public Integer getMaxWaitingTime() {
        return myMaxWaitingTime;
    }

    /**
     * Returns the timeout time. The timeout time is the max time for a listen cycle. If the timeout time is exceeded,
     * the client will be deregistered.
     * @return timeout time
     */
    public Integer getTimeoutTime() {
        return myTimeoutTime;
    }

    /**
     * Returns the number of reconnect attempts to execute.
     * @return number of reconnect attempts
     */
    public Integer getReconnectAttemptCount() {
        return myReconnectAttemptCount;
    }

    /**
     * Returns the connection / client id.
     * @return connection / client id
     */
    public String getConnectionId() {
        return myConnectionId;
    }

    /**
     * Returns the class name of the configured connection strategy (client side part).
     * @return connection strategy (client side part)
     */
    public String getConnectionStrategyClientConnector() {
        return myConnectionStrategyClientConnector;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        RemoteEventServiceConfigurationTransferable theOther = (RemoteEventServiceConfigurationTransferable) anObject;

        if(myConnectionId != null ? !myConnectionId.equals(theOther.myConnectionId) : theOther.myConnectionId != null) {
            return false;
        }
        if(myMaxWaitingTime != null ? !myMaxWaitingTime.equals(theOther.myMaxWaitingTime) : theOther.myMaxWaitingTime != null) {
            return false;
        }
        if(myMinWaitingTime != null ? !myMinWaitingTime.equals(theOther.myMinWaitingTime) : theOther.myMinWaitingTime != null) {
            return false;
        }
        if(myReconnectAttemptCount != null ? !myReconnectAttemptCount.equals(theOther.myReconnectAttemptCount) : theOther.myReconnectAttemptCount != null) {
            return false;
        }
        if(myTimeoutTime != null ? !myTimeoutTime.equals(theOther.myTimeoutTime) : theOther.myTimeoutTime != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int theResult = myMinWaitingTime != null ? myMinWaitingTime.hashCode() : 0;
        theResult = 31 * theResult + (myMaxWaitingTime != null ? myMaxWaitingTime.hashCode() : 0);
        theResult = 31 * theResult + (myTimeoutTime != null ? myTimeoutTime.hashCode() : 0);
        theResult = 31 * theResult + (myReconnectAttemptCount != null ? myReconnectAttemptCount.hashCode() : 0);
        theResult = 31 * theResult + (myConnectionId != null ? myConnectionId.hashCode() : 0);
        return theResult;
    }
}