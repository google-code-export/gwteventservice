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
package de.novanic.eventservice.client.event.domain;

import java.io.Serializable;

/**
 * Domains/contexts are used to specify the range where events should occur and listened. That supports the reuse of
 * events ({@link de.novanic.eventservice.client.event.Event} and listeners ({@link de.novanic.eventservice.client.event.listener.RemoteEventListener}.
 * <br>
 * <br>Example:
 * <br>Your application is a multiplayer online game with a conversation system. You defined the following events and
 * listeners.
 *   <br>
 *   <br>Events: NewUserEvent
 *   <br>Listeners: ConversationListener, ConversationChannelListener, PlayerListListener
 *   <br>
 * <br>It makes sense to divide your elements into different domains, to ensure that a NewUserEvent affects only the right
 * context/domain. For example one conversation domain and one game domain. ConversationListener and
 * ConversationChannelListener should be added to the conversation domain and the PlayerListListener should be added to
 * the game domain. Now you can distinguish the context of the NewUserEvent. If a user joined a conversation (NewUserEvent
 * added to conversation domain), the two conversation listeners will recognize it and the PlayerListListener won't be
 * informed about the NewUserEvent in the conversation context/domain.
 *
 * @author sstrohschein
 * <br>Date: 15.08.2008
 * <br>Time: 22:42:10
 */
public interface Domain extends Serializable, Comparable<Domain>
{
    /**
     * Return the name of the domain.
     * @return domain name
     */
    String getName();
}