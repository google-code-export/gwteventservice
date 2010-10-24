/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.client.connection.strategy.connector.streaming.specific;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector;

/**
 * The {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} listens for occurred events ({@link de.novanic.eventservice.client.event.Event})
 * of the server side and has the task to encode / process the transferred events at the client side.
 *
 * The {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.specific.GWTStreamingClientConnectorGecko} is a
 * Gecko / FF specific implementation of {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector} which
 * contains a special implementation to avoid loading messages / animations while streaming.
 *
 * @author sstrohschein
 *         <br>Date: 07.05.2010
 *         <br>Time: 23:29:33
 */
public class GWTStreamingClientConnectorGecko extends GWTStreamingClientConnector
{
    private static final Element DUMMY_FRAME_ELEMENT;

    static {
        DUMMY_FRAME_ELEMENT = createFrameElement();
    }

    private static Element createFrameElement() {
        Frame theDummyFrame = new Frame();
    	theDummyFrame.setVisible(false);
        final Element theDummyFrameElement = theDummyFrame.getElement();
        theDummyFrameElement.setId("gwteventservice_dummy_frame");
        return theDummyFrameElement;
    }

    /**
     * That method can be used by a concrete implementation to sent received events. It de-serializes the event
     * and notifies the callback and the {@link de.novanic.eventservice.client.event.listener.EventNotification} about the occurred
     * event, itself. The callback is notified about events when the cycle ({@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#CYCLE_TAG})
     * is triggered.
     *
     * That implementation creates a dummy frame to avoid loading messages / animation while streaming.
     * @param anEvent event or cycle tag ({@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#CYCLE_TAG})
     */
    public void receiveEvent(String anEvent) {
        //Gecko / FF hack start (avoid loading messages)
		RootPanel.getBodyElement().appendChild(DUMMY_FRAME_ELEMENT);
		RootPanel.getBodyElement().removeChild(DUMMY_FRAME_ELEMENT);
    	//Gecko / FF hack end (avoid loading messages)
        super.receiveEvent(anEvent);
    }
}