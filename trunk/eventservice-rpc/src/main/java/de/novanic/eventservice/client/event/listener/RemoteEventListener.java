/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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
package de.novanic.eventservice.client.event.listener;

import de.novanic.eventservice.client.event.Event;

/**
 * A RemoteEventListener should be used to process events on client side. The apply method ({@link RemoteEventListener#apply(de.novanic.eventservice.client.event.Event)})
 * should check the incoming event and call another method of the extending interface of RemoteEventListener.
 * For example the extending interface of RemoteEventListener is "UserListListener" and The incoming event "UserAddEvent".
 * The apply method should call a method like "userAdded" and the implementation of the listener can handle the userAdded call.
 *
 * @author sstrohschein
 * <br>Date: 06.06.2008
 * <br>Time: 18:57:30
 */
public interface RemoteEventListener
{
    /**
     * The apply method should recognize the incoming events and dispatch to a similar method of the extending interface.
     * For example the extending interface of RemoteEventListener is "UserListListener" and The incoming event "UserAddEvent".
     * The apply method should call a method like "userAdded" and the implementation of the listener can handle the userAdded call.
     * @param anEvent event to process
     */
    void apply(Event anEvent);
}