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

import de.novanic.eventservice.client.connection.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Registers an {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain. {@link de.novanic.eventservice.client.event.filter.EventFilter}
 * instances can be deregistered with {@link DeregistrationEventFilterCommand}.
 *
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:32:11
 */
public class RegistrationEventFilterCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private EventFilter myEventFilter;

    /**
     * Creates a RegistrationEventFilterCommand to register an {@link de.novanic.eventservice.client.event.filter.EventFilter} to a domain.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.connector.RemoteEventConnector}
     * @param aDomain {@link de.novanic.eventservice.client.event.domain.Domain} domain/context to register the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to register
     * @param aAsyncCallback callback for the command
     */
    public RegistrationEventFilterCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, EventFilter anEventFilter,
                                          AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
        myEventFilter = anEventFilter;
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain.
     */
    public void execute() {
        getRemoteEventConnector().registerEventFilter(myDomain, myEventFilter, getCommandCallback());
    }
}
