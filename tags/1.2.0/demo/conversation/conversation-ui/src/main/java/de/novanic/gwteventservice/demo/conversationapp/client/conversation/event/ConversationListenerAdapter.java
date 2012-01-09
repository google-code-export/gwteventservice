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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.Channel;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 13:20:04
 */
public class ConversationListenerAdapter implements ConversationListener
{
    public void apply(Event anEvent) {
        if(anEvent instanceof NewMessageEvent) {
            NewMessageEvent theNewMessageEvent = (NewMessageEvent)anEvent;
            newMessage(theNewMessageEvent.getChannel(), theNewMessageEvent.getActor(), theNewMessageEvent.getMessage());
        } else if(anEvent instanceof UserJoinEvent) {
            UserJoinEvent theUserJoinEvent = (UserJoinEvent)anEvent;
            userEntered(theUserJoinEvent.getChannel(), theUserJoinEvent.getActor());
        } else if(anEvent instanceof UserLeaveEvent) {
            UserLeaveEvent theUserLeaveEvent = (UserLeaveEvent)anEvent;
            userLeaved(theUserLeaveEvent.getChannel(), theUserLeaveEvent.getActor());
        } else if(anEvent instanceof NewChannelEvent) {
            NewChannelEvent theNewChannelEvent = (NewChannelEvent)anEvent;
            newChannel(theNewChannelEvent.getChannel());
        } else if(anEvent instanceof CloseChannelEvent) {
            CloseChannelEvent theCloseChannelEvent = (CloseChannelEvent)anEvent;
            removedChannel(theCloseChannelEvent.getChannel());
        }
    }

    public void newChannel(Channel aChannel) {}

    public void removedChannel(Channel aChannel) {}

    public void userEntered(Channel aChannel, String aUser) {}

    public void userLeaved(Channel aChannel, String aUser) {}

    public void newMessage(Channel aChannel, String aSender, String aMessage) {}
}