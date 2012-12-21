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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 13:46:57
 */
public class GWTConversationMessagePanel extends HorizontalPanel implements ConversationMessagePanel
{
    private Button mySendButton;
    private TextBox myMessageTextBox;
    private Collection<HandlerRegistration> myHandlerRegistrations;

    public GWTConversationMessagePanel() {
        mySendButton = new Button();
        mySendButton.setText("Send");
        myHandlerRegistrations = new ArrayList<HandlerRegistration>();

        myMessageTextBox = new TextBox();
        myMessageTextBox.setMaxLength(250);
        myMessageTextBox.setWidth("240px");
        myMessageTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent aKeyUpEvent) {
                if(aKeyUpEvent.getNativeKeyCode() == 13) {
                    mySendButton.click();
                }
            }
        });

        enable(false);

        //add the content
        setSpacing(5);
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        add(new Label("Message:"));
        add(myMessageTextBox);
        add(mySendButton);
    }

    public String getMessageText() {
        return myMessageTextBox.getText();
    }

    public void resetMessageText() {
        myMessageTextBox.setText("");
    }

    public void reset() {
        resetMessageText();

        Iterator<HandlerRegistration> theHandlerRegistrationIterator = myHandlerRegistrations.iterator();
        while(theHandlerRegistrationIterator.hasNext()) {
            HandlerRegistration theHandlerRegistration = theHandlerRegistrationIterator.next();
            theHandlerRegistration.removeHandler();
            theHandlerRegistrationIterator.remove();
        }
    }

    public void enable(boolean isEnable) {
        mySendButton.setEnabled(isEnable);
        myMessageTextBox.setEnabled(isEnable);
        if(isEnable) {
            myMessageTextBox.setFocus(true);
        }
    }

    public void setFocus(boolean isFocus) {
        myMessageTextBox.setFocus(isFocus);
    }

    public HandlerRegistration addSendButtonListener(ClickHandler aClickHandler) {
        final HandlerRegistration theHandlerRegistration = mySendButton.addClickHandler(aClickHandler);
        myHandlerRegistrations.add(theHandlerRegistration);
        return theHandlerRegistration;
    }
}
