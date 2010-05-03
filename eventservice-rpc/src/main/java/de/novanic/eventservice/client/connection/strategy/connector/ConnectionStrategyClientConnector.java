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
package de.novanic.eventservice.client.connection.strategy.connector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

import java.util.List;

/**
 * The {@link ConnectionStrategyClientConnector} listens for occurred events ({@link de.novanic.eventservice.client.event.Event})
 * of the server side and has the task to encode / process the transferred events at the client side.
 * 
 * @author sstrohschein
 *         <br>Date: 16.04.2010
 *         <br>Time: 23:22:31
 */
public interface ConnectionStrategyClientConnector
{
    /**
     * Initializes the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} with
     * the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}.
     * @param anEventService the {@link de.novanic.eventservice.client.event.service.EventServiceAsync}
     */
    void init(EventServiceAsync anEventService);

    /**
     * Deactivates the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}.
     */
    void deactivate();

    /**
     * Checks if the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} is
     * initialized.
     * @return true when the {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} is
     * initialized, otherwise false
     */
    boolean isInitialized();

    /**
     * The listen method implements the listen / connection strategy to receive occurred events. The occurred events
     * will be passed to the {@link de.novanic.eventservice.client.event.listener.EventNotification} and to the callback.
     * @param anEventNotification {@link de.novanic.eventservice.client.event.listener.EventNotification} which will be notified about occurred / received events
     * @param aCallback The callback will be notified about occurred / received events.
     */
    void listen(EventNotification anEventNotification, AsyncCallback<List<DomainEvent>> aCallback);
}