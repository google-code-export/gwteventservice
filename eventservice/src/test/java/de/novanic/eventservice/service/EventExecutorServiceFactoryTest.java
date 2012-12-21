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
package de.novanic.eventservice.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.novanic.eventservice.service.exception.NoSessionAvailableException;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 * Date: 05.08.2008
 * <br>Time: 10:52:26
 */
@RunWith(JUnit4.class)
public class EventExecutorServiceFactoryTest
{
    private static final String TEST_USER_ID = "test_user_id";

    @Test
    public void testFactory() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        HttpSession theSessionMock = mock(HttpSession.class);

        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(theSessionMock);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService(theSessionMock));

        verify(theSessionMock, times(2)).getId();

        assertFalse(theEventExecutorService.isUserRegistered());
    }

    @Test
    public void testFactory_2() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        HttpSession theSessionMock = mock(HttpSession.class);
        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        when(theRequestMock.getSession(any(Boolean.class))).thenReturn(theSessionMock);

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(theRequestMock);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService(theRequestMock));

        verify(theSessionMock, times(2)).getId();
        verify(theRequestMock, times(2)).getSession(any(Boolean.class));

        assertFalse(theEventExecutorService.isUserRegistered());
    }

    @Test
    public void testFactory_3() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(TEST_USER_ID);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService(TEST_USER_ID));

        assertFalse(theEventExecutorService.isUserRegistered());
    }

    @Test
    public void testFactory_SessionLess() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService((HttpSession)null);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService((HttpSession)null));

        //isUserRegistered() shouldn't work without a session (without client-/user-id)
        try {
            theEventExecutorService.isUserRegistered();
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }

        //addEvent() should work without a session, because the client-/user-id isn't required to add a domain specific event.
        try {
            theEventExecutorService.addEvent(DomainFactory.getDomain("X"), new Event() {});
        } catch(NoSessionAvailableException e) {
            fail("No Exception \"" + e.getClass().getName() + "\" expected!");
        }
    }

    @Test
    public void testFactory_SessionLess_2() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService((HttpServletRequest)null);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService((HttpServletRequest)null));

        //isUserRegistered() shouldn't work without a session (without client-/user-id)
        try {
            theEventExecutorService.isUserRegistered();
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }

        //addEvent() should work without a session, because the client-/user-id isn't required to add a domain specific event.
        try {
            theEventExecutorService.addEvent(DomainFactory.getDomain("X"), new Event() {});
        } catch(NoSessionAvailableException e) {
            fail("No Exception \"" + e.getClass().getName() + "\" expected!");
        }
    }

    @Test
    public void testFactory_SessionLess_3() {
        EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        assertSame(theEventExecutorServiceFactory, EventExecutorServiceFactory.getInstance());

        EventExecutorService theEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService((String)null);
        assertNotSame(theEventExecutorService, theEventExecutorServiceFactory.getEventExecutorService((String)null));

        //isUserRegistered shouldn't work without the client-/user-id
        try {
            theEventExecutorService.isUserRegistered();
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }

        //addEvent should work without the client-/user-id, because the client-/user-id isn't required to add a domain specific event.
        try {
            theEventExecutorService.addEvent(DomainFactory.getDomain("X"), new Event() {});
        } catch(NoSessionAvailableException e) {
            fail("No Exception \"" + e.getClass().getName() + "\" expected!");
        }
    }
}
