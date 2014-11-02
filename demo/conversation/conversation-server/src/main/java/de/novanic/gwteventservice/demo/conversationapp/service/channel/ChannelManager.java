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
package de.novanic.gwteventservice.demo.conversationapp.service.channel;

import de.novanic.gwteventservice.demo.conversationapp.client.conversation.Channel;

import java.util.*;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 00:21:27
 */
public class ChannelManager
{
    private static Map<String, Channel> myChannels;

    public ChannelManager() {
        myChannels = new TreeMap<String, Channel>();
    }

    public Channel add(String aChannelName) {
        Channel theChannel = new Channel(aChannelName);
        myChannels.put(aChannelName, theChannel);
        return theChannel;
    }

    public Channel remove(String aChannelName) {
        return myChannels.remove(aChannelName);
    }

    public Channel join(String aChannelName, String aContact) {
        Channel theChannel = myChannels.get(aChannelName);
        theChannel.addContact(aContact);
        return theChannel;
    }

    public Channel getChannelByName(String aChannelName) {
        return myChannels.get(aChannelName);
    }

    public Channel getChannel(String aContact) {
        for(Channel theChannel : myChannels.values()) {
            if(theChannel.getContacts().contains(aContact)) {
                return theChannel;
            }
        }
        return null;
    }

    public List<Channel> getChannels() {
        return new ArrayList<Channel>(myChannels.values());
    }
}