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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.event;

import de.novanic.gwteventservice.demo.conversationapp.client.conversation.Channel;

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 00:18:00
 */
public class NewMessageEvent extends ConversationEvent
{
    private String myMessage;

    public NewMessageEvent() {}

    public NewMessageEvent(String anActor, Channel aChannel, String aMessage) {
        super(anActor, aChannel);
        myMessage = aMessage;
    }

    public String getMessage() {
        return myMessage;
    }

    public List<String> getRecipients() {
        return getChannel().getContacts();
    }

    public String toString() {
        return "User \"" + getActor() + "\" (channel \"" + getChannel() + "\") send message \"" + getMessage() + '\"';
    }
}