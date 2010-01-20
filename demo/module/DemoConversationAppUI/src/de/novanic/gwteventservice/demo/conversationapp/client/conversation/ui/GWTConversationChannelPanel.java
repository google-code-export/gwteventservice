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
package de.novanic.gwteventservice.demo.conversationapp.client.conversation.ui;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;

import java.util.*;

/**
 * @author sstrohschein
 *         <br>Date: 16.09.2008
 *         <br>Time: 13:44:19
 */
public class GWTConversationChannelPanel extends VerticalPanel implements ConversationChannelPanel
{
    private Tree myChannelTree;
    private Button myAddChannelButton;
    private Collection<HandlerRegistration> myHandlerRegistrations;

    public GWTConversationChannelPanel() {
        setTitle("Channels");
        setStyleName("styledPanel");

        myHandlerRegistrations = new ArrayList<HandlerRegistration>();

        myChannelTree = new Tree();
        myChannelTree.setAnimationEnabled(true);

        myAddChannelButton = new Button();
        myAddChannelButton.setText("New Channel");
        myAddChannelButton.setWidth("100px");

        enable(false);

        //add the content
        add(new Label(getTitle()));
        add(myAddChannelButton);
        add(myChannelTree);
    }

    public boolean addChannel(String aChannel) {
        final Iterator<TreeItem> theIterator = myChannelTree.treeItemIterator();
        while(theIterator.hasNext()) {
            TreeItem theTreeItem = theIterator.next();
            //adds the channel if the channel doesn't already exists
            if(aChannel.equals(theTreeItem.getText())) {
                return false;
            }
        }
        myChannelTree.addItem(aChannel);
        return true;
    }
    
    public boolean removeChannel(String aChannel) {
        final Iterator<TreeItem> theIterator = myChannelTree.treeItemIterator();
        while(theIterator.hasNext()) {
            TreeItem theTreeItem = theIterator.next();
            //adds the channel if the channel doesn't already exists
            if(aChannel.equals(theTreeItem.getText())) {
                theIterator.remove();
                theTreeItem.remove();
                return true;
            }
        }
        return false;
    }

    public boolean addContact(String aChannel, String aContactName) {
        boolean isContactAdded = false;
        
        final Iterator<TreeItem> theIterator = myChannelTree.treeItemIterator();
        while(theIterator.hasNext()) {
            TreeItem theChannelTreeItem = theIterator.next();
            if(isChannelItem(theChannelTreeItem)) {
                if(aChannel.equals(theChannelTreeItem.getText())) {
                    if(!containsContact(theChannelTreeItem, aContactName)) {
                        //adds the contact if the contact doesn't already exists in that channel
                        theChannelTreeItem.addItem(aContactName);
                        isContactAdded = true;
                    }
                }
            }
        }
        return isContactAdded;
    }
    
    public boolean removeContact(String aContactName) {
        final Iterator<TreeItem> theIterator = myChannelTree.treeItemIterator();
        while(theIterator.hasNext()) {
            TreeItem theTreeItem = theIterator.next();
            if(isChannelItem(theTreeItem)) {
                if(containsContact(theTreeItem, aContactName)) {
                    return removeItem(theTreeItem, aContactName);
                }
            }
        }
        return false;
    }

    public Set<String> getContacts() {
        Set<String> theContacts = new HashSet<String>();

        final Iterator<TreeItem> theIterator = myChannelTree.treeItemIterator();
        while(theIterator.hasNext()) {
            TreeItem theChannelTreeItem = theIterator.next();
            if(isChannelItem(theChannelTreeItem)) {
                theContacts.addAll(getContacts(theChannelTreeItem));
            }
        }
        return theContacts;
    }

    public void reset() {
        myChannelTree.clear();
        Iterator<HandlerRegistration> theHandlerRegistrationIterator = myHandlerRegistrations.iterator();
        while(theHandlerRegistrationIterator.hasNext()) {
            HandlerRegistration theHandlerRegistration = theHandlerRegistrationIterator.next();
            theHandlerRegistration.removeHandler();
            theHandlerRegistrationIterator.remove();
        }
    }

    public void enable(boolean isEnable) {
        myAddChannelButton.setEnabled(isEnable);
    }

    public HandlerRegistration addAddChannelButtonListener(ClickHandler aClickHandler) {
        final HandlerRegistration theClickHandlerRegistration = myAddChannelButton.addClickHandler(aClickHandler);
        myHandlerRegistrations.add(theClickHandlerRegistration);
        return theClickHandlerRegistration;
    }

    public HandlerRegistration addChannelSelectListener(final ChannelSelectListener aChannelSelectListener) {
        final SelectionHandler<TreeItem> theTreeSelectionHandler = new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> aSelectionEvent) {
                final TreeItem theSelectedItem = aSelectionEvent.getSelectedItem();
                if(isChannelItem(theSelectedItem)) {
                    final String theChannelName = theSelectedItem.getText();
                    aChannelSelectListener.onSelect(theChannelName);
                }
            }
        };

        final HandlerRegistration theSelectionHandlerRegistration = myChannelTree.addSelectionHandler(theTreeSelectionHandler);
        myHandlerRegistrations.add(theSelectionHandlerRegistration);
        return theSelectionHandlerRegistration;
    }

    private boolean isChannelItem(TreeItem aTreeItem) {
        //the channels are at the first level (has no parent item)
        return (aTreeItem.getParentItem() == null);
    }

    private boolean containsContact(TreeItem aParentItem, String aContact) {
        Set<String> theContacts = getContacts(aParentItem);
        return theContacts.contains(aContact);
    }
    
    private boolean removeItem(TreeItem aParentItem, String anEntry) {
        for(int i = 0; i < aParentItem.getChildCount(); i++) {
            final TreeItem theChild = aParentItem.getChild(i);
            if(theChild != null) {
                if(anEntry.equals(theChild.getText())) {
                    aParentItem.removeItem(theChild);
                    return true;
                }
            }
        }
        return false;
    }

    private Set<String> getContacts(TreeItem aChannelTreeItem) {
        int theChildCount = aChannelTreeItem.getChildCount();
        Set<String> theContacts = new HashSet<String>(theChildCount);
        for(int i = 0; i < theChildCount; i++) {
            theContacts.add(aChannelTreeItem.getChild(i).getText());
        }
        return theContacts;
    }
}
