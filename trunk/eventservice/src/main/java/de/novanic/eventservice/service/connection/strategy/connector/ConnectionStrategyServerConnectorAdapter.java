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
package de.novanic.eventservice.service.connection.strategy.connector;

import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.registry.user.UserInfo;

/**
 * {@link ConnectionStrategyServerConnectorAdapter} is an abstract default implementation of {@link ConnectionStrategyServerConnector}
 * and offers some general methods for the internal operation which can be useful for various connection strategies.
 * The {@link ConnectionStrategyServerConnector} listens for occurring events ({@link de.novanic.eventservice.client.event.Event})
 * on the server side and has the task to prepare the transfer from the server side to the client side.
 *
 * The reason for the listen and transfer preparation within one single class is, that the {@link ConnectionStrategyServerConnector}
 * should have the control about listening and transfer of the occurred events.
 *
 * @author sstrohschein
 *         <br>Date: 15.03.2010
 *         <br>Time: 23:03:02
 */
public abstract class ConnectionStrategyServerConnectorAdapter implements ConnectionStrategyServerConnector
{
    private EventServiceConfiguration myConfiguration;

    /**
     * Creates a new connection strategy with a configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration}).
     * @param aConfiguration configuration
     */
    protected ConnectionStrategyServerConnectorAdapter(EventServiceConfiguration aConfiguration) {
        myConfiguration = aConfiguration;
    }

    /**
     * Waits for the configured min. waiting time.
     * @see de.novanic.eventservice.config.ConfigParameter#MIN_WAITING_TIME_TAG
     * @throws EventServiceException
     */
    protected void waitMinWaitingTime() throws EventServiceException {
        waitTime(myConfiguration.getMinWaitingTime());
    }

    /**
     * Waits for the configured max. waiting time and returns whether the max. waiting time
     * was exceed or was interrupted by an occurred event (notification).
     * @param aUserInfo user
     * @return true when the max. waiting time was exceed, otherwise (interrupted by a notification) false
     * @throws EventServiceException can occur when the waiting was interrupted by an error
     */
    protected boolean waitMaxWaitingTime(UserInfo aUserInfo) throws EventServiceException {
        final int theMaxWaitingTime = myConfiguration.getMaxWaitingTime();
        if(theMaxWaitingTime <= 0) {
            return true;
        }
        if(aUserInfo.isEventsEmpty()) {
            //monitor for event notification and double checked
            synchronized(aUserInfo) {
                if(aUserInfo.isEventsEmpty()) {
                    try {
                        final long theStartTime = System.currentTimeMillis();
                        aUserInfo.wait(theMaxWaitingTime);
                        return (System.currentTimeMillis() - theStartTime >= theMaxWaitingTime);
                    } catch(InterruptedException e) {
                        throw new EventServiceException("Error on waiting max. waiting time!", e);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Waits for a specified time.
     * @param aWaitingTime time to wait
     * @throws EventServiceException
     */
    private void waitTime(int aWaitingTime) throws EventServiceException {
        if(aWaitingTime > 0) {
            try {
                Thread.sleep(aWaitingTime);
            } catch(InterruptedException e) {
                throw new EventServiceException("Error on waiting min. waiting time!", e);
            }
        }
    }
}
