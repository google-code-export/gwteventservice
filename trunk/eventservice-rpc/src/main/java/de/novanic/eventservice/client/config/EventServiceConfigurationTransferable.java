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
package de.novanic.eventservice.client.config;

import java.io.Serializable;

/**
 * An {@link de.novanic.eventservice.client.config.EventServiceConfigurationTransferable} holds the configuration options for
 * {@link de.novanic.eventservice.client.event.service.EventService} which could be usable by the client side.
 * The whole covered configuration for {@link de.novanic.eventservice.client.event.service.EventService} is only available
 * to the server side and isn't transferable / serializable.
 *
 * @author sstrohschein
 *         <br>Date: 29.03.2010
 *         <br>Time: 22:10:41
 */
public interface EventServiceConfigurationTransferable extends Serializable
{
    /**
     * Returns the min waiting time. Listening should hold at least for min waiting time.
     * @return min waiting time
     */
    Integer getMinWaitingTime();

    /**
     * Returns the max waiting time. Listening shouldn't hold longer than max waiting time.
     * @return max waiting time
     */
    Integer getMaxWaitingTime();

    /**
     * Returns the timeout time. The timeout time is the max time for a listen cycle. If the timeout time is exceeded,
     * the client will be deregistered.
     * @return timeout time
     */
    Integer getTimeoutTime();

    /**
     * Returns the number of reconnect attempts to execute.
     * @return number of reconnect attempts
     */
    Integer getReconnectAttemptCount();

    /**
     * Returns the connection / client id.
     * @return connection / client id
     */
    String getConnectionId();

    /**
     * Returns the class name of the configured connection strategy (client side part).
     * @return connection strategy (client side part)
     */
    String getConnectionStrategyClientConnector();
}