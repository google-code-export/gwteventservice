/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import de.novanic.eventservice.client.event.EventNotification;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Activates the listen cycle for event listening and registrates the client/user for a domain (server side).
 *
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:31:34
 */
public class ActivationCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private EventFilter myEventFilter;
    private EventNotification myEventNotification;

    /**
     * Creates an ActivationCommand (activates the listen cycle for event listening)
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.event.RemoteEventConnector}
     * @param aDomain {@link de.novanic.eventservice.client.event.domain.Domain} domain/context for event listening
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to filter events
     * @param anEventNotification {@link de.novanic.eventservice.client.event.EventNotification} to get informed about incoming events
     * @param aCallback callback for the command
     */
    public ActivationCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, EventFilter anEventFilter,
                             EventNotification anEventNotification, AsyncCallback<Void> aCallback) {
        super(aRemoteEventConnector, aCallback);
        myDomain = aDomain;
        myEventFilter = anEventFilter;
        myEventNotification = anEventNotification;
    }

    /**
     * Registers the client for event listening for a specified domain and starts the event listening cycle.
     */
    public void execute() {
        getRemoteEventConnector().activate(myDomain, myEventFilter, myEventNotification, getCommandCallback());
    }
}
