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
package de.novanic.eventservice.client;

/**
 * The {@link de.novanic.eventservice.client.ClientHandler} can be used to transfer the connection-/client-id to the server-side
 * and to identify the clients. The connection-/client-id is necessary to add user-specific events or domain-user-specific
 * EventFilters dynamically from the server-side.
 *
 * @author sstrohschein
 *         <br>Date: 01.08.2010
 *         <br>Time: 13:06:20
 */
public class DefaultClientHandler implements ClientHandler
{
    private String myConnectionId;

    /**
     * Creates a new {@link de.novanic.eventservice.client.ClientHandler} with a connection-/client-id.
     * @param aConnectionId connection-/client-id of the client
     */
    public DefaultClientHandler(String aConnectionId) {
        myConnectionId = aConnectionId;
    }

    /**
     * Returns the specific connection-/client-id of the client.
     * @return connection-/client-id of the client
     */
    public String getConnectionId() {
        return myConnectionId;
    }
}