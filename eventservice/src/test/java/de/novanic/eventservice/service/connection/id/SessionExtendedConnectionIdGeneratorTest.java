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

import de.novanic.eventservice.client.config.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 29.05.2010
 *         <br>Time: 00:45:18
 */
@RunWith(JUnit4.class)
public class SessionExtendedConnectionIdGeneratorTest
{
    @Test
    public void testGenerateConnectionId() {
        final String theSessionId = "123b";

        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);
        HttpSession theSessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(theRequestMock.getSession(true)).andReturn(theSessionMock);

        EasyMock.expect(theSessionMock.getId()).andReturn(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        EasyMock.replay(theRequestMock, theSessionMock);
            final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
            assertTrue(theConnectionId.startsWith(theSessionId));
            assertTrue(theSessionId.length() < theConnectionId.length());
        EasyMock.verify(theRequestMock, theSessionMock);
        EasyMock.reset(theRequestMock, theSessionMock);
    }

    @Test
    public void testGetConnectionId() {
        final String theSessionId = "123b";

        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);
        HttpSession theSessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(theRequestMock.getSession(true)).andReturn(theSessionMock);

        EasyMock.expect(theSessionMock.getId()).andReturn(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        EasyMock.replay(theRequestMock, theSessionMock);
            //generate a connection id at first
            final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
            assertTrue(theConnectionId.startsWith(theSessionId));
            assertTrue(theSessionId.length() < theConnectionId.length());
        EasyMock.verify(theRequestMock, theSessionMock);
        EasyMock.reset(theRequestMock, theSessionMock);

        HttpServletRequest theSecondRequestMock = EasyMock.createMock(HttpServletRequest.class);

        EasyMock.expect(theSecondRequestMock.getParameter("id")).andReturn(theConnectionId);

        EasyMock.replay(theSecondRequestMock);
            final String theSecondConnectionId = theConnectionIdGenerator.getConnectionId(theSecondRequestMock);
            assertEquals(theConnectionId, theSecondConnectionId);
        EasyMock.verify(theSecondRequestMock);
        EasyMock.reset(theSecondRequestMock);
    }

    @Test
    public void testGetConnectionId_Error() {
        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);

        EasyMock.expect(theRequestMock.getParameter("id")).andReturn(null);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        EasyMock.replay(theRequestMock);
            try {
                theConnectionIdGenerator.getConnectionId(theRequestMock);
                fail("Exception expected, because no connection id was generated at first (no connection id in the request)!");
            } catch(ConfigurationException e) {}
        EasyMock.verify(theRequestMock);
        EasyMock.reset(theRequestMock);
    }
}