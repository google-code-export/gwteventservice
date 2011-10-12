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
package de.novanic.eventservice.service.connection.id;

import de.novanic.eventservice.service.exception.NoSessionAvailableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} generates unique ids which are used to
 * identify the connected clients / users.
 *
 * That implementation of {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} uses session ids
 * as a unique connection / client id.
 *
 * @author sstrohschein
 *         <br>Date: 28.03.2010
 *         <br>Time: 23:34:26
 */
public class SessionConnectionIdGenerator implements ConnectionIdGenerator
{
    /**
     * Generates a new connection / client id.
     * The {@link de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator} creates a new session with that call when
     * no session is available.
     * @param aRequest request from the client
     * @return unique connection / client id
     */
    public String generateConnectionId(HttpServletRequest aRequest) {
        return aRequest.getSession(true).getId();
    }

    /**
     * Returns the previous generated connection / client id
     * (see {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator#generateConnectionId(javax.servlet.http.HttpServletRequest)})
     * without generating a new connection / client id.
     * @see de.novanic.eventservice.service.connection.id.ConnectionIdGenerator#generateConnectionId(javax.servlet.http.HttpServletRequest)
     * @param aRequest request
     * @return the previous generated connection / client id for the specific client
     */
    public String getConnectionId(HttpServletRequest aRequest) {
        final HttpSession theSession = aRequest.getSession(false);
        if(theSession == null) {
            throw new NoSessionAvailableException("There is no session available! Maybe no session was generated explicitly by the connection id generator.");
        }
        return theSession.getId();
    }
}