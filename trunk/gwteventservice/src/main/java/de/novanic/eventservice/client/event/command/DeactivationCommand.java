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
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Set;

/**
 * Deactivates the event listening of the client completely or for a specified domain/context.
 *
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:33:35
 */
public class DeactivationCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private Set<Domain> myDomains;

    /**
     * Creates a DeactivationCommand to deactivate the event listening of the client completely.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.connector.RemoteEventConnector}
     */
    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector) {
        super(aRemoteEventConnector, null);
    }

    /**
     * Creates a DeactivationCommand to deactivate the event listening of the client for a specified domain.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.connector.RemoteEventConnector}
     * @param aDomain domain which should be deactivated for event listening
     * @param aAsyncCallback callback for the command
     */
    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
    }

    /**
     * Creates a DeactivationCommand to deactivate the event listening of the client for a set of domains. That should be
     * used instead of {@link de.novanic.eventservice.client.event.command.DeactivationCommand#DeactivationCommand(de.novanic.eventservice.client.connection.connector.RemoteEventConnector , de.novanic.eventservice.client.event.domain.Domain, com.google.gwt.user.client.rpc.AsyncCallback)}
     * when more than one domain should be deactivated, to reduce server calls.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.connector.RemoteEventConnector}
     * @param aDomains domains which should be deactivated for event listening
     * @param aAsyncCallback callback for the command
     */
    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector, Set<Domain> aDomains, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomains = aDomains;
    }

    /**
     * Deactivates the event listening of the client completely or for a specified domain/context.
     * @see de.novanic.eventservice.client.event.command.DeactivationCommand#DeactivationCommand(de.novanic.eventservice.client.connection.connector.RemoteEventConnector)
     * @see de.novanic.eventservice.client.event.command.DeactivationCommand#DeactivationCommand(de.novanic.eventservice.client.connection.connector.RemoteEventConnector , de.novanic.eventservice.client.event.domain.Domain, com.google.gwt.user.client.rpc.AsyncCallback)
     * @see de.novanic.eventservice.client.event.command.DeactivationCommand#DeactivationCommand(de.novanic.eventservice.client.connection.connector.RemoteEventConnector , java.util.Set, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    public void execute() {
        final AsyncCallback<Void> theCallback = getCommandCallback();
        if(theCallback != null) {
            if(myDomains != null) {
                getRemoteEventConnector().deactivate(myDomains, getCommandCallback());
            } else {
                getRemoteEventConnector().deactivate(myDomain, getCommandCallback());
            }
        } else {
            getRemoteEventConnector().deactivate();
        }
    }
}
