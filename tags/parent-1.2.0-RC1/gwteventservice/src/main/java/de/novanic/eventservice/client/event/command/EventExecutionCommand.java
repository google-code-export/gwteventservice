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
package de.novanic.eventservice.client.event.command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * The {@link de.novanic.eventservice.client.event.command.EventExecutionCommand} sends an event to a domain or to the
 * sending / calling user (user-specific event, when the {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN} is used).
 *
 * @author sstrohschein
 *         <br>Date: 04.07.2010
 *         <br>Time: 13:45:18
 */
public class EventExecutionCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private Event myEvent;

    /**
     * Creates an EventExecutionCommand to send / register events to the server side.
     *
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}
     * @param aDomain domain
     * @param anEvent event
     * @param aCallback callback of the command
     */
    public EventExecutionCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback) {
        super(aRemoteEventConnector, aCallback);
        myDomain = aDomain;
        myEvent = anEvent;
    }

    /**
     * Sends / registers the event to the server side (domain- or user-specific events)
     */
    public void execute() {
        getRemoteEventConnector().sendEvent(myDomain, myEvent, getCommandCallback());
    }
}