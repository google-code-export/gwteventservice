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
package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Initializes the server/service connection. That is only used as the first server call.
 *
 * @author sstrohschein
 *         <br>Date: 01.04.2009
 *         <br>Time: 23:51:21
 */
public class InitEventServiceCommand extends ServerCallCommand<EventServiceConfigurationTransferable>
{
    /**
     * Creates an InitEventServiceCommand (initializes the server/service connection on first server call).
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}
     * @param aCallback callback of the command
     */
    public InitEventServiceCommand(RemoteEventConnector aRemoteEventConnector, AsyncCallback<EventServiceConfigurationTransferable> aCallback) {
        super(aRemoteEventConnector, aCallback);
    }

    /**
     * Initializes the server/service connection.
     */
    public void execute() {
        getRemoteEventConnector().init(getCommandCallback());
    }
}