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
package de.novanic.eventservice.service.connection.strategy.longpolling;

import de.novanic.eventservice.client.connection.connector.RemoteEventConnector;
import de.novanic.eventservice.service.connection.strategy.ConnectionStrategy;
import de.novanic.eventservice.service.registry.listener.ServerEventListener;

/**
 * TODO the long-polling implementation is currently in development
 *
 * @author sstrohschein
 *         <br>Date: 07.04.2010
 *         <br>Time: 21:51:05
 */
public class LongPollingConnectionStrategy implements ConnectionStrategy
{
    /**
     * TODO the long-polling implementation is currently in development
     *
     * The {@link de.novanic.eventservice.service.registry.listener.ServerEventListener} listens for occurring events ({@link de.novanic.eventservice.client.event.Event})
     * on the server side and has the task to prepare the transfer from the server side to the client side.
     * @return configured {@link de.novanic.eventservice.service.registry.listener.ServerEventListener}
     */
    public ServerEventListener getServerConnector() {
        return null;//TODO
    }

    /**
     * TODO the long-polling implementation is currently in development
     *
     * The {@link de.novanic.eventservice.client.connection.connector.RemoteEventConnector} is used on the client side to
     * create the connection to the server side and has the task to extract the transferred {@link de.novanic.eventservice.client.event.Event} instances.
     * @return
     */
    public RemoteEventConnector getClientConnector() {
        return null;//TODO
    }
}