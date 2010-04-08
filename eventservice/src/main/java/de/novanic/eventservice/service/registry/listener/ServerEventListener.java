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
package de.novanic.eventservice.service.registry.listener;

import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.registry.user.UserInfo;

import java.util.List;

/**
 * The {@link de.novanic.eventservice.service.registry.listener.ServerEventListener} listens for occurring events ({@link de.novanic.eventservice.client.event.Event})
 * on the server side and has the task to prepare the transfer from the server side to the client side.
 *
 * The reason for the listen and transfer preparation within one single class is, that the {@link de.novanic.eventservice.service.registry.listener.ServerEventListener}
 * should have the control about listening and transfer of the occurred events.
 *
 * @author sstrohschein
 *         <br>Date: 15.03.2010
 *         <br>Time: 23:02:32
 */
public interface ServerEventListener
{
    /**
     * Listens for occurring events (can be retrieved from the {@link de.novanic.eventservice.service.registry.user.UserInfo} with
     * {@link de.novanic.eventservice.service.registry.user.UserInfo#retrieveEvents()}) and should prepare or transfer the retrieved events
     * directly. The reason for the listen and transfer preparation within one single method is, that the {@link de.novanic.eventservice.service.registry.listener.ServerEventListener}
     * should have the control about listening and transfer of the occurred events.
     * @param aUserInfo {@link de.novanic.eventservice.service.registry.user.UserInfo} which holds new occurred events
     * @return occurred events
     * @throws EventServiceException
     */
    List<DomainEvent> listen(UserInfo aUserInfo) throws EventServiceException;
}
