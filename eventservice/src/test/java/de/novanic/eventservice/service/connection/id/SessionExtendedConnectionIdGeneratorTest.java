/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(true)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
        assertTrue(theConnectionId.startsWith(theSessionId));
        assertTrue(theSessionId.length() < theConnectionId.length());
    }

    @Test
    public void testGetConnectionId() {
        final String theSessionId = "123b";

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(true)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(theSessionId);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        //generate a connection id at first
        final String theConnectionId = theConnectionIdGenerator.generateConnectionId(theRequestMock);
        assertTrue(theConnectionId.startsWith(theSessionId));
        assertTrue(theSessionId.length() < theConnectionId.length());

        HttpServletRequest theSecondRequestMock = mock(HttpServletRequest.class);

        when(theSecondRequestMock.getParameter("id")).thenReturn(theConnectionId);

        final String theSecondConnectionId = theConnectionIdGenerator.getConnectionId(theSecondRequestMock);
        assertEquals(theConnectionId, theSecondConnectionId);
    }

    @Test
    public void testGetConnectionId_Error() {
        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);

        when(theRequestMock.getParameter("id")).thenReturn(null);

        ConnectionIdGenerator theConnectionIdGenerator = new SessionExtendedConnectionIdGenerator();

        try {
            theConnectionIdGenerator.getConnectionId(theRequestMock);
            fail("Exception expected, because no connection id was generated at first (no connection id in the request)!");
        } catch(ConfigurationException e) {}
    }
}