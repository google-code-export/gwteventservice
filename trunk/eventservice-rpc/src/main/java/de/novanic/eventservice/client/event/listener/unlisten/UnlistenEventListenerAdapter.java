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
package de.novanic.eventservice.client.event.listener.unlisten;

import de.novanic.eventservice.client.event.Event;

/**
 * The UnlistenEventListener can be implemented to listen for {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
 * instances. An {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is for example triggered on a timeout
 * or when a user/client leaves a domain. The {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListenerAdapter}
 * provides the apply method ({@link de.novanic.eventservice.client.event.listener.RemoteEventListener#apply(de.novanic.eventservice.client.event.Event)})
 * and a default implementation of {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}.
 *
 * @author sstrohschein
 *         <br>Date: 08.06.2009
 *         <br>Time: 22:35:00
 */
public class UnlistenEventListenerAdapter implements UnlistenEventListener
{
    /**
     * The apply method checks if the occurred event is an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     * and dispatches the event to {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener#onUnlisten(UnlistenEvent)}.
     * @param anEvent event to process
     */
    public void apply(Event anEvent) {
        if(anEvent instanceof UnlistenEvent) {
            UnlistenEvent theUnlistenEvent = (UnlistenEvent)anEvent;
            onUnlisten(theUnlistenEvent);
        }
    }

    /**
     * The method onUnlisten is called when an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} occurs.
     * @param anUnlistenEvent triggered {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     */
    public void onUnlisten(UnlistenEvent anUnlistenEvent) {}
}
