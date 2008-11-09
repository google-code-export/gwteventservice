/*
 * GWTEventService
 * Copyright (c) 2008, GWTEventService Committers
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
package de.novanic.eventservice.service;

import junit.framework.TestCase;
import org.easymock.MockControl;

import javax.servlet.http.HttpSession;

/**
 * @author sstrohschein
 * Date: 05.08.2008
 * <br>Time: 10:52:26
 */
public class EventExecutorServiceFactoryTest extends TestCase
{
    private static final String TEST_USER_ID = "test_user_id";

    public void testFactory() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        MockControl theSessionMockControl = MockControl.createControl(HttpSession.class);
        HttpSession theSessionMock = (HttpSession)theSessionMockControl.getMock();

        theSessionMock.getId();
        theSessionMockControl.setReturnValue(TEST_USER_ID);

        theSessionMock.getId();
        theSessionMockControl.setReturnValue(TEST_USER_ID);

        theSessionMockControl.replay();
            EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(theSessionMock);
            assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService(theSessionMock));
        theSessionMockControl.verify();
        theSessionMockControl.reset();
    }
}