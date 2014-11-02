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
package de.novanic.gwteventservice.demo.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.gwteventservice.demo.hello.client.event.ServerGeneratedMessageEvent;

/**
 * @author sstrohschein
 *         <br>Date: 19.02.2010
 *         <br>Time: 08:01:44
 */
public class HelloGWTEventService implements EntryPoint
{
    private ListBox myEventReceiveBoxUI;

    public void onModuleLoad() {
        Panel thePanel = createUI();
        RootPanel.get().add(thePanel);

        //starting the generation of Hello messages from the server
        ServerMessageGeneratorServiceAsync theServerMessageGeneratorServiceAsync = GWT.create(ServerMessageGeneratorService.class);
        theServerMessageGeneratorServiceAsync.start(new VoidAsyncCallback());

        /* Logic for GWTEventService starts here */

        //get the RemoteEventService for registration of RemoteEventListener instances
        RemoteEventService theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
        //add a listener to the SERVER_MESSAGE_DOMAIN
        theRemoteEventService.addListener(ServerGeneratedMessageEvent.SERVER_MESSAGE_DOMAIN, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if(anEvent instanceof ServerGeneratedMessageEvent) {
                    ServerGeneratedMessageEvent theServerGeneratedMessageEvent = (ServerGeneratedMessageEvent)anEvent;
                    myEventReceiveBoxUI.addItem(theServerGeneratedMessageEvent.getServerGeneratedMessage());
                }
            }
        });

        /* Logic for GWTEventService ends here */
    }

    private Panel createUI() {
        FlowPanel thePanel = new FlowPanel();

        thePanel.setSize("60%", "60%");
        thePanel.setStyleName("borderPanel");

        myEventReceiveBoxUI = new ListBox(true);
        myEventReceiveBoxUI.setSize("99%", "91%");
        myEventReceiveBoxUI.addItem("Listening for events...");

        final Label theEventReceiveLabelUI = new Label("Received server events");
        theEventReceiveLabelUI.setSize("99%", "8%");

        thePanel.add(theEventReceiveLabelUI);
        thePanel.add(myEventReceiveBoxUI);
        return thePanel;
    }

    private class VoidAsyncCallback implements AsyncCallback<Void>
    {
        public void onFailure(Throwable aThrowable) {}

        public void onSuccess(Void aResult) {}
    }
}