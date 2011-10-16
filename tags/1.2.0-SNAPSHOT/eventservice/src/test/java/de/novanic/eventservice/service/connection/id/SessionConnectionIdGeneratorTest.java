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
import junit.framework.TestCase;
import org.easymock.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2010
 *         <br>Time: 14:28:17
 */
public class SessionConnectionIdGeneratorTest extends TestCase
{
    public void testGenerateConnectionId() {
        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);
        HttpSession theSessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(theRequestMock.getSession(true)).andReturn(theSessionMock);

        EasyMock.expect(theSessionMock.getId()).andReturn("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();

        EasyMock.replay(theRequestMock, theSessionMock);
            assertEquals("123b", theConnectionIdGenerator.generateConnectionId(theRequestMock));
        EasyMock.verify(theRequestMock, theSessionMock);
        EasyMock.reset(theRequestMock, theSessionMock);
    }

    public void testGetConnectionId() {
        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);
        HttpSession theSessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(theRequestMock.getSession(false)).andReturn(theSessionMock);

        EasyMock.expect(theSessionMock.getId()).andReturn("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();

        EasyMock.replay(theRequestMock, theSessionMock);
            assertEquals("123b", theConnectionIdGenerator.getConnectionId(theRequestMock));
        EasyMock.verify(theRequestMock, theSessionMock);
        EasyMock.reset(theRequestMock, theSessionMock);
    }

    public void testGetConnectionId_2() {
        HttpServletRequest theRequestMock = EasyMock.createMock(HttpServletRequest.class);
        HttpSession theSessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(theRequestMock.getSession(false)).andReturn(null);

        EasyMock.replay(theRequestMock, theSessionMock);
            try {
                new SessionConnectionIdGenerator().getConnectionId(theRequestMock);
                fail("An exception is expected, because the session is NULL!");
            } catch(NoSessionAvailableException e) {}
        EasyMock.verify(theRequestMock, theSessionMock);
        EasyMock.reset(theRequestMock, theSessionMock);
    }
}