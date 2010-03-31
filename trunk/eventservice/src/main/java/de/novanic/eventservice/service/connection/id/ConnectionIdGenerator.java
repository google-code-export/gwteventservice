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
package de.novanic.eventservice.service.connection.id;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} generates unique ids which are used to
 * identify the connected clients / users.
 *
 * @author sstrohschein
 *         <br>Date: 28.03.2010
 *         <br>Time: 23:34:01
 */
public interface ConnectionIdGenerator
{
    /**
     * Generates a new connection / client id.
     * @param aRequest request from the client
     * @return unique connection / client id
     */
    String generateConnectionId(HttpServletRequest aRequest);

    /**
     * Returns the previous generated connection / client id
     * (see {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator#generateConnectionId(javax.servlet.http.HttpServletRequest)})
     * without generating a new connection / client id.
     * @see de.novanic.eventservice.service.connection.id.ConnectionIdGenerator#generateConnectionId(javax.servlet.http.HttpServletRequest)
     * @param aRequest
     * @return the previous generated connection / client id for the specific client
     */
    String getConnectionId(HttpServletRequest aRequest);
}