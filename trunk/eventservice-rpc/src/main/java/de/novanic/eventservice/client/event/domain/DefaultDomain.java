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
package de.novanic.eventservice.client.event.domain;

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
 * <br>Time: 22:42:20
 */
public final class DefaultDomain implements Domain
{
    private String myName;

    /**
     * @deprecated That constructor is only for serialization! Please use
     * {@link de.novanic.eventservice.client.event.domain.DefaultDomain#DefaultDomain(String)} instead.
     * @see de.novanic.eventservice.client.event.domain.DefaultDomain#DefaultDomain(String)
     */
    @Deprecated
    public DefaultDomain() {}

    /**
     * Creates a new domain with a specified name. The name should be unique to avoid 'collision' of events.
     * @param aName unique domain name
     */
    public DefaultDomain(String aName) {
        myName = aName;
    }

    /**
     * Return the name of the domain.
     * @return domain name
     */
    public String getName() {
        return myName;
    }

    public int compareTo(Domain aDomain) {
        if(aDomain != null) {
            return myName.compareTo(aDomain.getName());
        }
        return 1;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        Domain theDomain = (Domain)anObject;
        return myName.equals(theDomain.getName());
    }

    public int hashCode() {
        return myName.hashCode();
    }

    public String toString() {
        return myName;
    }
}