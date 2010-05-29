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

import de.novanic.eventservice.client.config.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * A {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} generates unique ids which are used to
 * identify the connected clients / users.
 *
 * That implementation of {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} uses and extends the
 * session ids to generate a unique connection / client id. That allows the user / client to use more than one browser
 * instance for the same application (multiple session ids / session sharing).
 *
 * @author sstrohschein
 *         <br>Date: 28.03.2010
 *         <br>Time: 23:34:39
 */
public class SessionExtendedConnectionIdGenerator implements ConnectionIdGenerator
{
    private Random myRandomizer;

    /**
     * Creates a new instance of {@link de.novanic.eventservice.service.connection.id.SessionExtendedConnectionIdGenerator}
     * and initializes the random number generation.
     */
    public SessionExtendedConnectionIdGenerator() {
        myRandomizer = new Random();
    }

    /**
     * Generates a new connection / client id.
     * The {@link de.novanic.eventservice.service.connection.id.SessionExtendedConnectionIdGenerator} creates a new session with
     * that call when no session is available. The session id will be extended with a random number to support multiple session ids / session sharing.
     * @param aRequest request from the client
     * @return unique connection / client id
     */
    public String generateConnectionId(HttpServletRequest aRequest) {
        return aRequest.getSession(true).getId() + myRandomizer.nextInt();
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
        final String theConnectionId = aRequest.getParameter("id");
        if(theConnectionId == null) {
            throw new ConfigurationException("A client id was requested without generating a connection id first or the connection id was not transferred with the request!");
        }
        return theConnectionId;
    }
}