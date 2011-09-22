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
package de.novanic.eventservice.client.connection.strategy.connector.streaming;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link ConnectionStrategyClientConnector} listens for occurred events ({@link de.novanic.eventservice.client.event.Event})
 * of the server side and has the task to encode / process the transferred events at the client side.
 *
 * The {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector} is an
 * abstract implementation for streaming listen methods and needs an implementation to deserialize sent events.
 *
 * Streaming means that the connection is hold open for a specified time and when an event
 * occurs, the answer / event is streamed directly to the client without closing and re-open the connection. The connection is
 * closed and re-opened (by the client) when the configured max. waiting time is reached.
 *
 * @author sstrohschein
 *         <br>Date: 18.03.2010
 *         <br>Time: 00:07:46
 */
public abstract class DefaultStreamingClientConnector implements ConnectionStrategyClientConnector
{
	protected static final String CYCLE_TAG = "cycle";

    private EventNotification myEventNotification;
    private AsyncCallback<List<DomainEvent>> myCallback;
    private boolean isInitialized;

    /**
     * Initializes the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} with
     * the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}.
     * @param anEventService the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}
     */
    public void init(EventServiceAsync anEventService) {
        isInitialized = true;
    }

    /**
     * Deactivates the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}.
     */
    public void deactivate() {}

    /**
     * Checks if the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} is
     * initialized.
     * @return true when the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} is
     * initialized, otherwise false
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * The listen method implements the listen / connection strategy to receive occurred events. The occurred events
     * will be passed to the {@link de.novanic.eventservice.client.event.listener.EventNotification} and to the callback.
     *
     * That abstract streaming implementation handles the notifications itself. The concrete implementation has to implement
     * the abstract listen method ({@link DefaultStreamingClientConnector#listen()}) and can notify about events with a simple call to
     * {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#receiveEvent(String)}.
     * @param anEventNotification {@link de.novanic.eventservice.client.event.listener.EventNotification} which will be notified about occurred / received events
     * @param aCallback The callback will be notified about occurred / received events.
     */
    public void listen(EventNotification anEventNotification, AsyncCallback<List<DomainEvent>> aCallback) {
        myEventNotification = anEventNotification;
        myCallback = aCallback;
        listen();
    }

    /**
     * That method can be used by a concrete implementation to sent received events. It de-serializes the event
     * and notifies the callback and the {@link de.novanic.eventservice.client.event.listener.EventNotification} about the occurred
     * event, itself. The callback isn't notified about events when the cycle ({@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#CYCLE_TAG})
     * is triggered, because the events were already processed to the EventNotification before.
     * @param anEvent event or cycle tag ({@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#CYCLE_TAG})
     */
    public void receiveEvent(String anEvent) {
    	if(CYCLE_TAG.equals(anEvent)) {
    		myCallback.onSuccess(new ArrayList<DomainEvent>(0));
    	} else {
            DomainEvent theDeserializedEvent = deserializeEvent(anEvent);
            myEventNotification.onNotify(theDeserializedEvent);
    	}
    }

    /**
     * That de-serialization method has to be implemented by the extending implementation to de-serialize occurred events.
     * @param anEvent event to de-serialize
     * @return de-serialized event
     */
    protected abstract DomainEvent deserializeEvent(String anEvent);

    /**
     * That method can be implemented to react on a listen start call. That method is executed by
     * {@link de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector#listen(de.novanic.eventservice.client.event.listener.EventNotification, com.google.gwt.user.client.rpc.AsyncCallback)}.
     */
    protected abstract void listen();
}
