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

import junit.framework.TestCase;
import org.easymock.MockControl;

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
        MockControl theRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theRequestMock = (HttpServletRequest)theRequestMockControl.getMock();

        MockControl theSessionMockControl = MockControl.createControl(HttpSession.class);
        HttpSession theSessionMock = (HttpSession)theSessionMockControl.getMock();

        theRequestMock.getSession(true);
        theRequestMockControl.setReturnValue(theSessionMock);
        
        theSessionMock.getId();
        theSessionMockControl.setReturnValue("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();

        theRequestMockControl.replay();
        theSessionMockControl.replay();
            assertEquals("123b", theConnectionIdGenerator.generateConnectionId(theRequestMock));
        theSessionMockControl.verify();
        theRequestMockControl.verify();
        theSessionMockControl.reset();
        theRequestMockControl.reset();
    }

    public void testGetConnectionId() {
        MockControl theRequestMockControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest theRequestMock = (HttpServletRequest)theRequestMockControl.getMock();

        MockControl theSessionMockControl = MockControl.createControl(HttpSession.class);
        HttpSession theSessionMock = (HttpSession)theSessionMockControl.getMock();

        theRequestMock.getSession(false);
        theRequestMockControl.setReturnValue(theSessionMock);
        
        theSessionMock.getId();
        theSessionMockControl.setReturnValue("123b");

        ConnectionIdGenerator theConnectionIdGenerator = new SessionConnectionIdGenerator();

        theRequestMockControl.replay();
        theSessionMockControl.replay();
            assertEquals("123b", theConnectionIdGenerator.getConnectionId(theRequestMock));
        theSessionMockControl.verify();
        theRequestMockControl.verify();
        theSessionMockControl.reset();
        theRequestMockControl.reset();
    }
}