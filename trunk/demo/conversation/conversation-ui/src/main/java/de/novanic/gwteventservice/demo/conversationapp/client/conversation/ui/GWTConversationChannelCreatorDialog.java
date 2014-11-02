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
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author sstrohschein
 *         <br>Date: 21.09.2008
 *         <br>Time: 18:14:54
 */
public class GWTConversationChannelCreatorDialog extends DialogBox implements ConversationChannelCreatorDialog
{
    private String myChannelName;

    public GWTConversationChannelCreatorDialog() {
        setText("New channel");
        setAnimationEnabled(true);

        setWidget(createContentPanel());

        center();
    }

    private Panel createContentPanel() {
        final Button theCreateChannelButton = new Button("Create");
        final Button theCancelButton = new Button("Cancel");

        final TextBox theChannelNameText = new TextBox();
        theChannelNameText.setMaxLength(30);
        theChannelNameText.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent aKeyUpEvent) {
                switch(aKeyUpEvent.getNativeKeyCode()) {
                    case 13: theCreateChannelButton.click();
                             break;
                    case 27: theCancelButton.click();
                }
            }
        });
        theChannelNameText.setFocus(true);

        theCreateChannelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent aClickEvent) {
                String theChannelName = theChannelNameText.getText();
                close(theChannelName);
            }
        });

        theCancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent aClickEvent) {
                close(null);
            }
        });

        HorizontalPanel theActionPanel = new HorizontalPanel();
        theActionPanel.setSpacing(5);
        theActionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        theActionPanel.add(theCreateChannelButton);
        theActionPanel.add(theCancelButton);

        VerticalPanel theContentPanel = new VerticalPanel();
        theContentPanel.setSpacing(5);
        theContentPanel.add(theChannelNameText);
        theContentPanel.add(theActionPanel);

        return theContentPanel;
    }

    private void close(String aChannelName) {
        myChannelName = aChannelName;
        hide(true);
    }

    public boolean isCanceled() {
        return myChannelName == null || myChannelName.trim().length() <= 0;
    }

    public String getChannelName() {
        return myChannelName;
    }
}
