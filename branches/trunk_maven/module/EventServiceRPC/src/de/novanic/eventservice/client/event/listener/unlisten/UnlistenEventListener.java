/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.client.event.listener.unlisten;

import de.novanic.eventservice.client.event.listener.RemoteEventListener;

import java.io.Serializable;

/**
 * The UnlistenEventListener can be implemented to listen for {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
 * instances. An {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is for example triggered on a timeout
 * or when a user/client leaves a domain. The {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListenerAdapter}
 * provides the apply method ({@link de.novanic.eventservice.client.event.listener.RemoteEventListener#apply(de.novanic.eventservice.client.event.Event)})
 * and a default implementation of {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener}.
 *
 * @author sstrohschein
 *         <br>Date: 08.06.2009
 *         <br>Time: 22:36:20
 */
public interface UnlistenEventListener extends RemoteEventListener
{
    /**
     * Scopes for listening to {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} instances.
     * See the various scopes ({@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope#LOCAL},
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope#TIMEOUT} and
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener.Scope#UNLISTEN}) for the according description.
     */
    enum Scope implements Serializable {
        /**
         * An {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} registered with the local scope
         * does only recognize local unlisten events for example caused by local timeouts or a lost connection to the server side.
         * Therefore it isn't necessary to register it to the server side, but unlisten events of other users / clients
         * can't be get with the local scope.
         */
        LOCAL,
        /**
         * An {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} registered with the timeout scope
         * does recognize local unlisten events (for example caused by local timeouts or a lost connection to the server side) and
         * unlisten events of other users / clients caused by a connection timeout of the specified user / client.
         */
        TIMEOUT,
        /**
         * An {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} registered with the unlisten scope
         * does recognize all unlisten events: Local unlisten events (for example caused by local timeouts or a lost connection to the server side),
         * unlisten events of other users / clients caused by a timeout and domain unlisten events of other users / clients for
         * example caused by removing a listener for a domain.
         */
        UNLISTEN
    }

    /**
     * The method onUnlisten is called when an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} occurs.
     * @param anUnlistenEvent triggered {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     */
    void onUnlisten(UnlistenEvent anUnlistenEvent);
}
