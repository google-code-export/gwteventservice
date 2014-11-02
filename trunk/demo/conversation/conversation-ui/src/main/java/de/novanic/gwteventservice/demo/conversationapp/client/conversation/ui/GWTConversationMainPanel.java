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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui;

import com.google.gwt.user.client.ui.*;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui.login.GWTConversationLoginPanel;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 13:17:37
 */
public class GWTConversationMainPanel extends DockPanel implements ConversationMainPanel
{
    private TextArea myMessageHistoryTextArea;
    private GWTConversationLoginPanel myConversationLoginPanel;
    private GWTConversationChannelPanel myConversationChannelPanel;
    private GWTConversationMessagePanel myConversationMessagePanel;

    public GWTConversationMainPanel() {
        setSize("60%", "60%");
        setStyleName("borderPanel");

        myMessageHistoryTextArea = createMessageHistoryTextArea();
        myConversationLoginPanel = createConversationLoginPanel();
        myConversationChannelPanel = createConversationChannelPanel();
        myConversationMessagePanel = createConversationMessagePanel();

        ScrollPanel theMessageHistoryScrollPanel = createMessageHistoryScrollPanel(myMessageHistoryTextArea);

        add(myConversationLoginPanel, DockPanel.NORTH);
        add(myConversationChannelPanel, DockPanel.WEST);
        add(theMessageHistoryScrollPanel, DockPanel.CENTER);
        add(myConversationMessagePanel, DockPanel.SOUTH);
    }

    private GWTConversationLoginPanel createConversationLoginPanel() {
        return new GWTConversationLoginPanel();
    }

    private GWTConversationMessagePanel createConversationMessagePanel() {
        return new GWTConversationMessagePanel();
    }

    private GWTConversationChannelPanel createConversationChannelPanel() {
        return new GWTConversationChannelPanel();
    }

    private TextArea createMessageHistoryTextArea() {
        TextArea theMessageHistoryTextArea = new TextArea();
        theMessageHistoryTextArea.setWidth("400px");
        theMessageHistoryTextArea.setHeight("300px");
        theMessageHistoryTextArea.setReadOnly(true);
        return theMessageHistoryTextArea;
    }

    private ScrollPanel createMessageHistoryScrollPanel(TextArea aTextArea) {
        return new ScrollPanel(aTextArea);
    }

    public void addMessageHistoryText(String aMessage) {
        myMessageHistoryTextArea.setText(myMessageHistoryTextArea.getText() + '\n' + aMessage);
        
//        myMessageHistoryScrollPanel.scrollToBottom(); //TODO doesn't work, little work-around needed...
        myMessageHistoryTextArea.setCursorPos(myMessageHistoryTextArea.getText().length() - 1);
    }

    public void clearMessageHistory() {
        myMessageHistoryTextArea.setText("");
    }

    public void reset() {
        clearMessageHistory();
        myConversationChannelPanel.reset();
        myConversationMessagePanel.reset();
    }

    public ConversationLoginPanel getConversationLoginPanel() {
        return myConversationLoginPanel;
    }

    public ConversationChannelPanel getConversationChannelPanel() {
        return myConversationChannelPanel;
    }

    public ConversationMessagePanel getConversationMessagePanel() {
        return myConversationMessagePanel;
    }
}