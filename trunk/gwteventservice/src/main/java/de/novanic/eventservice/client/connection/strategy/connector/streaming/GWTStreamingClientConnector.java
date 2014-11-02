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
package de.novanic.eventservice.client.connection.strategy.connector.streaming;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

/**
 * The {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} listens for occurred events ({@link de.novanic.eventservice.client.event.Event})
 * of the server side and has the task to encode / process the transferred events at the client side.
 *
 * The {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector} is an
 * GWT specific implementation of streaming. It implements the forever frame technique with a high number of GWT on-board methods.
 *
 * Streaming means that the connection is hold open for a specified time and when an event
 * occurs, the answer / event is streamed directly to the client without closing and re-open the connection. The connection is
 * closed and re-opened (by the client) when the configured max. waiting time is reached.
 *
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 23:02:50
 */
public class GWTStreamingClientConnector extends DefaultStreamingClientConnector
{
    private Frame myStreamingConnectorFrame;
    private String myServiceURL;

    /**
     * Initializes the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} with
     * the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}.
     *
     * That implementation prepares the processing of occurred, serialized events.
     * @param anEventService the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}
     */
    public void init(EventServiceAsync anEventService) {
        myServiceURL = ((ServiceDefTarget)anEventService).getServiceEntryPoint();
        initReceiveEventScript(this);
        super.init(anEventService);
    }

    /**
     * Deactivates the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}.
     *
     * That implementation removes the forever frame, so the listening is stopped and cleared up. 
     */
    public void deactivate() {
        if(myStreamingConnectorFrame != null) {
            RootPanel.get().remove(myStreamingConnectorFrame);
            myStreamingConnectorFrame = null;
        }
    }

    /**
     * Initializes or refreshes the forever frame.
     */
    private void initStreamingConnectorFrame() {
    	if(myStreamingConnectorFrame == null) {
			myStreamingConnectorFrame = new Frame(myServiceURL);
			myStreamingConnectorFrame.setVisible(false);
    		RootPanel.get().add(myStreamingConnectorFrame);
    	} else {
    		//refresh / restart the connection
    		myStreamingConnectorFrame.setUrl(myServiceURL);
    	}
    }

    /**
     * De-serializes an occurred event with GWT serialization methods.
     * @param anEvent event to de-serialize
     * @return de-serialized event
     */
    protected DomainEvent deserializeEvent(String anEvent) {
        try {
            SerializationStreamFactory theSerializationStreamFactory = GWT.create(EventService.class);
            SerializationStreamReader theSerializationStreamReader = theSerializationStreamFactory.createStreamReader(anEvent);
            return (DomainEvent)theSerializationStreamReader.readObject();
        } catch(SerializationException e) {
            throw new RemoteEventServiceRuntimeException("Error on de-serializing event \"" + anEvent + "\"!", e);
        }
    }

    /**
     * Initializes or refreshes the forever frame (see {@link GWTStreamingClientConnector#initStreamingConnectorFrame()}).
     */
    protected void listen() {
        initStreamingConnectorFrame();
    }

    /**
     * Initializes the processing of occurring events.
     * @param aThisReference reference
     */
    private native void initReceiveEventScript(DefaultStreamingClientConnector aThisReference) /*-{
    	$wnd.receiveEvent = function(anEvent) { aThisReference.@de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector::receiveEvent(Ljava/lang/String;)(anEvent) };
  	}-*/;
}