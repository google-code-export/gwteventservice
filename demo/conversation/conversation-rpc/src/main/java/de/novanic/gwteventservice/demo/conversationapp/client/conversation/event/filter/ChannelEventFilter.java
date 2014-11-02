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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.event.filter;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.event.*;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.Channel;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 00:10:31
 */
public class ChannelEventFilter implements EventFilter
{
    private String myEventChannelName;

    public ChannelEventFilter() {
        myEventChannelName = null;
    }

    public ChannelEventFilter(String aChannelName) {
        myEventChannelName = aChannelName;
    }

    public boolean match(Event anEvent) {
        if(anEvent instanceof ConversationEvent) {
            ConversationEvent theConversationEvent = (ConversationEvent)anEvent;
            if(isGlobalEvent(theConversationEvent)) {
                return false;
            }
            Channel theChannel = theConversationEvent.getChannel();
            if(theChannel != null) {
                return !myEventChannelName.equals(theChannel.getName());
            }
        }
        return true;
    }

    private boolean isGlobalEvent(Event aConversationEvent) {
        return (aConversationEvent instanceof NewChannelEvent
                || aConversationEvent instanceof UserJoinEvent
                || aConversationEvent instanceof UserLeaveEvent
                || aConversationEvent instanceof CloseChannelEvent);
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }
        ChannelEventFilter theChannelEventFilter = (ChannelEventFilter)anObject;
        return myEventChannelName.equals(theChannelEventFilter.myEventChannelName);

    }

    public int hashCode() {
        return myEventChannelName.hashCode();
    }

    public String toString() {
        return "ChannelEventFilter: " + myEventChannelName;
    }
}