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

import de.novanic.eventservice.client.event.DomainEvent;

/**
 * EventNotification can be used to get notified about incoming events.
 *
 * @author sstrohschein
 *         <br>Date: 12.10.2008
 *         <br>Time: 11:49:42
 */
public interface EventNotification
{
    /**
     * That method will be called when a new event is arriving.
     * @param aDomainEvent incoming event
     */
    void onNotify(DomainEvent aDomainEvent);

    /**
     * That method will be called when the listening for events is aborted (unexpected).
     */
    void onAbort();
}