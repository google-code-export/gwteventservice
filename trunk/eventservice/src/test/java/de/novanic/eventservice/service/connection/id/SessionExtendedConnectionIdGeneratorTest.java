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
import junit.framework.TestCase;
import org.easymock.MockControl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author sstrohschein
 *         <br>Date: 29.05.2010
 *         <br>Time: 00:45:18
 */
public class SessionExtendedConnectionIdGeneratorTest extends TestCase
{
    public void testGenerateConnectionId() {
        final String theSessionId = "123b";

        MockControl theRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theRequestMock = (HttpServletRequest)theRequestMockControl.getMock();

        MockControl theSessionMockControl = MockControl.createControl(HttpSession.class);
        HttpSession theSessionMock = (HttpSession)theSessionMockControl.getMock();

        theRequestMock.getSession(true);
        theRequestMockControl.setReturnValue(theSessionMock);

        theSessionMock.getId();
        theSessionMockControl.setReturnValue(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        theRequestMockControl.replay();
        theSessionMockControl.replay();
            final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
            assertTrue(theConnectionId.startsWith(theSessionId));
            assertTrue(theSessionId.length() < theConnectionId.length());
        theSessionMockControl.verify();
        theRequestMockControl.verify();
        theSessionMockControl.reset();
        theRequestMockControl.reset();
    }

    public void testGetConnectionId() {
        final String theSessionId = "123b";

        MockControl theRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theRequestMock = (HttpServletRequest)theRequestMockControl.getMock();

        MockControl theSessionMockControl = MockControl.createControl(HttpSession.class);
        HttpSession theSessionMock = (HttpSession)theSessionMockControl.getMock();

        theRequestMock.getSession(true);
        theRequestMockControl.setReturnValue(theSessionMock);

        theSessionMock.getId();
        theSessionMockControl.setReturnValue(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        theRequestMockControl.replay();
        theSessionMockControl.replay();
            //generate a connection id at first
            final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
            assertTrue(theConnectionId.startsWith(theSessionId));
            assertTrue(theSessionId.length() < theConnectionId.length());
        theRequestMockControl.verify();
        theSessionMockControl.verify();
        theRequestMockControl.reset();
        theSessionMockControl.reset();

        MockControl theSecondRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theSecondRequestMock = (HttpServletRequest)theSecondRequestMockControl.getMock();

        theSecondRequestMock.getParameter("id");
        theSecondRequestMockControl.setReturnValue(theConnectionId);

        theSecondRequestMockControl.replay();
            final String theSecondConnectionId = theConnectionIdGenerator.getConnectionId(theSecondRequestMock);
            assertEquals(theConnectionId, theSecondConnectionId);
        theSecondRequestMockControl.verify();
        theSecondRequestMockControl.reset();
    }

    public void testGetConnectionId_Error() {
        MockControl theSecondRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theSecondRequestMock = (HttpServletRequest)theSecondRequestMockControl.getMock();

        theSecondRequestMock.getParameter("id");
        theSecondRequestMockControl.setReturnValue(null);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        theSecondRequestMockControl.replay();
            try {
                theConnectionIdGenerator.getConnectionId(theSecondRequestMock);
                fail("Exception expected, because no connection id was generated at first (no connection id in the request)!");
            } catch(ConfigurationException e) {}
        theSecondRequestMockControl.verify();
        theSecondRequestMockControl.reset();
    }
}