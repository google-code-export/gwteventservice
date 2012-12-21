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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2010
 *         <br>Time: 14:28:17
 */
@RunWith(JUnit4.class)
public class SessionConnectionIdGeneratorTest
{
    @Test
    public void testGenerateConnectionId() {
        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(true)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();
        assertEquals("123b", theConnectionIdGenerator.generateConnectionId(theRequestMock));
    }

    @Test
    public void testGetConnectionId() {
        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(false)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();
        assertEquals("123b", theConnectionIdGenerator.getConnectionId(theRequestMock));
    }

    @Test
    public void testGetConnectionId_2() {
        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);

        when(theRequestMock.getSession(false)).thenReturn(null);

        try {
            new SessionConnectionIdGenerator().getConnectionId(theRequestMock);
            fail("An exception is expected, because the session is NULL!");
        } catch(NoSessionAvailableException e) {}
    }
}