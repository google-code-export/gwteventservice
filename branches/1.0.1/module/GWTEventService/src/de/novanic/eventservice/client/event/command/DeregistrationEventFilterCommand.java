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
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Deregisters {@link de.novanic.eventservice.client.event.filter.EventFilter} instances of a domain. {@link de.novanic.eventservice.client.event.filter.EventFilter}
 * instances can be registered with {@link de.novanic.eventservice.client.event.command.RegistrationEventFilterCommand}.
 *
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:33:18
 */
public class DeregistrationEventFilterCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;

    /**
     * Creates a DeregistrationEventFilterCommand to deregister {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.event.RemoteEventConnector}
     * @param aDomain {@link de.novanic.eventservice.client.event.domain.Domain} domain/context where the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * is registered.
     * @param aAsyncCallback callback for the command
     */
    public DeregistrationEventFilterCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
    }

    /**
     * Deregisters an {@link de.novanic.eventservice.client.event.filter.EventFilter} of a domain.
     */
    public void execute() {
        getRemoteEventConnector().deregisterEventFilter(myDomain, getCommandCallback());
    }
}
