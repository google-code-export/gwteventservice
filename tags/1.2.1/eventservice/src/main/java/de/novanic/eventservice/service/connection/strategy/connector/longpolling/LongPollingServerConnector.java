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
package de.novanic.eventservice.service.connection.strategy.connector.longpolling;

import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnectorAdapter;
import de.novanic.eventservice.service.registry.user.UserInfo;

import java.util.List;

/**
 * The {@link de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector} implements
 * the long-polling event listen method. Long-polling means that the connection is hold open for a specified time and when an event
 * occurs, the answer / event is sent directly to the client.
 *
 * The {@link de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnector} listens for occurring events ({@link de.novanic.eventservice.client.event.Event})
 * on the server side and has the task to prepare the transfer from the server side to the client side.
 * @author sstrohschein
 *         <br>Date: 15.03.2010
 *         <br>Time: 23:00:24
 */
public class LongPollingServerConnector extends ConnectionStrategyServerConnectorAdapter
{
    /**
     * Creates a new {@link de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector}.
     * The {@link de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector} implements
     * the long-polling event listen method.
     * @param aConfiguration configuration
     */
    public LongPollingServerConnector(EventServiceConfiguration aConfiguration) {
        super(aConfiguration);
    }

    /**
     * Listens for occurring events with the long-polling strategy. The connection is hold open for a specified time and when an event occurs,
     * the answer / event is sent directly to the client.
     * @param aUserInfo {@link de.novanic.eventservice.service.registry.user.UserInfo} which holds new occurred events
     * @return occurred events
     * @throws EventServiceException
     */
    public List<DomainEvent> listen(UserInfo aUserInfo) throws EventServiceException {
        waitMinWaitingTime();
        waitMaxWaitingTime(aUserInfo);
        return aUserInfo.retrieveEvents(getConfiguration().getMaxEvents());
    }
}
