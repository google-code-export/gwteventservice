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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui.login;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui.ConversationLoginPanel;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 13:46:47
 */
public class GWTConversationLoginPanel extends HorizontalPanel implements ConversationLoginPanel
{
    private TextBox myNicknameTextBox;
    private Button myLoginLogoutButton;
    private boolean myIsLogin;

    public GWTConversationLoginPanel() {
        myLoginLogoutButton = new Button();
        myNicknameTextBox = new TextBox();
        
        //toggle to login mode
        toggle(true);

        myNicknameTextBox.setMaxLength(20);
        myNicknameTextBox.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent aKeyUpEvent) {
                if(aKeyUpEvent.getNativeKeyCode() == 13) {
                    myLoginLogoutButton.click();
                }
            }
        });
        myNicknameTextBox.setFocus(true);

        //add the content
        setSpacing(5);
        add(new Label("Choose a nickname:"));
        add(myNicknameTextBox);
        add(myLoginLogoutButton);
    }

    public String getNicknameText() {
        return myNicknameTextBox.getText();
    }

    public void toggle(boolean isLogin) {
        myIsLogin = isLogin;
        if(myIsLogin) {
            myLoginLogoutButton.setText("Login");
        } else {
            myLoginLogoutButton.setText("Logout");
        }
        myNicknameTextBox.setEnabled(myIsLogin);
    }

    public boolean isLogin() {
        return myIsLogin;
    }

    public void addLoginButtonListener(ClickHandler aLoginButtonListener) {
        myLoginLogoutButton.addClickHandler(aLoginButtonListener);
    }
}
