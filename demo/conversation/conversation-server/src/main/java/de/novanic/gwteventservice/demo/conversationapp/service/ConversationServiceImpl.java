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
package de.novanic.gwteventservice.demo.conversationapp.service;

import de.novanic.gwteventservice.demo.conversationapp.client.conversation.Channel;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.ConversationService;
import de.novanic.gwteventservice.demo.conversationapp.service.channel.ChannelManager;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.event.*;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.event.filter.ChannelEventFilter;
import de.novanic.eventservice.service.RemoteEventServiceServlet;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 00:23:56
 */
public class ConversationServiceImpl extends RemoteEventServiceServlet implements ConversationService
{
    private static final Logger LOG = LoggerFactory.getLogger(ConversationServiceImpl.class);

    private static final Domain CONVERSATION_DOMAIN;
    private static final String GLOBAL_CHANNEL = "GlobalChannel";
    private static final ChannelManager myChannelManager;

    static {
        CONVERSATION_DOMAIN = DomainFactory.getDomain(ConversationEvent.CONVERSATION_DOMAIN);

        myChannelManager = new ChannelManager();
        myChannelManager.add(GLOBAL_CHANNEL);
    }

    public Channel createChannel(String aContact, String aChannelName) {
        Channel theChannel = myChannelManager.getChannelByName(aChannelName);
        if(theChannel == null) {
            theChannel = myChannelManager.add(aChannelName);
            addEvent(CONVERSATION_DOMAIN, new NewChannelEvent(aContact, theChannel));
            LOG.debug("{} created channel and joined", aContact);
            joinInternal(aChannelName, aContact);
        }

        return theChannel;
    }

    public void closeChannel(String aContact, String aChannelName) {
        if(!GLOBAL_CHANNEL.equals(aChannelName)) {
            Channel theChannel = myChannelManager.getChannelByName(aChannelName);
            if(theChannel != null) {
                myChannelManager.remove(theChannel.getName());
                addEvent(CONVERSATION_DOMAIN, new CloseChannelEvent(aContact, theChannel));
            }
        }
    }

    public Channel join(String aContact, String aChannelName) {
        LOG.debug("{} joined", aContact);
        joinInternal(aChannelName, aContact);
        return myChannelManager.getChannelByName(aChannelName);
    }

    public void leave(String aContact) {
        leaveInternal(aContact);
    }

    public void sendMessage(String aContact, String aMessage) {
        LOG.debug("Server-Message - {}: {}", aContact, aMessage);

        Channel theChannel = myChannelManager.getChannel(aContact);
        addEvent(CONVERSATION_DOMAIN, new NewMessageEvent(aContact, theChannel, aMessage));
    }

    public List<Channel> getChannels() {
        return myChannelManager.getChannels();
    }

    private void joinInternal(String aChannelName, String aContact) {
        leaveInternal(aContact);

        Channel theChannel = myChannelManager.join(aChannelName, aContact);
        addEvent(CONVERSATION_DOMAIN, new UserJoinEvent(aContact, theChannel));
        setEventFilter(CONVERSATION_DOMAIN, new ChannelEventFilter(aChannelName));
    }

    private void leaveInternal(String aContact) {
        Channel theChannel = myChannelManager.getChannel(aContact);
        if(theChannel != null) {
            theChannel.removeContact(aContact);
            addEvent(CONVERSATION_DOMAIN, new UserLeaveEvent(aContact, theChannel));
            if(theChannel.getContacts().isEmpty()) {
                closeChannel(aContact, theChannel.getName());
            }
        }
    }
}