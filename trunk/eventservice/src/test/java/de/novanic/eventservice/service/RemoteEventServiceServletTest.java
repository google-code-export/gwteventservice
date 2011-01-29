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

import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.exception.NoSessionAvailableException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import org.easymock.EasyMock;

/**
 * @author sstrohschein
 *         <br>Date: 05.10.2008
 *         <br>Time: 00:17:08
 */
public class RemoteEventServiceServletTest extends EventExecutorServiceTest_A
{
    private HttpServletRequest myRequestMock;
    private HttpSession mySessionMock;

    public EventExecutorService initEventExecutorService() {
        return setUpRemoteEventServiceServlet();
    }

    public void setUp() throws Exception {
        super.setUp();
        setUpRemoteEventServiceServlet();
    }

    public void tearDown() throws Exception {
        tearDownRemoteEventServiceServlet();
        super.tearDown();
    }

    public void testInit_SessionLess() {
        final DummyRemoteEventServiceServletOriginal theServletOriginal = new DummyRemoteEventServiceServletOriginal();
        try {
            theServletOriginal.isUserRegistered();
            fail("Exception expected, because the HTTPRequest shouldn't be available!");
        } catch(Exception e) {}

        try {
            theServletOriginal.addEvent(DomainFactory.getDomain("X"), new Event() {});
        } catch(Exception e) {
            fail("No Exception expected, because the HTTPRequest shouldn't be used!");
        }
    }

    public void testInit_SessionDummy() {
        RemoteEventServiceServlet theRemoteEventServiceServlet = setUpRemoteEventServiceServlet();
        assertFalse(theRemoteEventServiceServlet.isUserRegistered());
    }

    private RemoteEventServiceServlet setUpRemoteEventServiceServlet() {
        myRequestMock = EasyMock.createMock(HttpServletRequest.class);
        mySessionMock = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(myRequestMock.getSession(false)).andReturn(mySessionMock).anyTimes();

        EasyMock.expect(mySessionMock.getId()).andReturn(TEST_USER_ID).anyTimes();

        EasyMock.replay(myRequestMock, mySessionMock);

        return new DummyRemoteEventServiceServlet_Request(myRequestMock);
    }

    private void tearDownRemoteEventServiceServlet() {
        EasyMock.verify(myRequestMock, mySessionMock);
        EasyMock.reset(myRequestMock, mySessionMock);
    }

    public void testAddEvent_Init_WithoutSession() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("X");
        final String theUserId = "test_user";
        final Event theEvent = new Event() {};

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(theDomain, theUserId, null);

        RemoteEventServiceServlet theRemoteEventServiceServlet = new DummyRemoteEventServlet(theDomain, theEvent);
        theRemoteEventServiceServlet.init(null);

        final List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), theUserId);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertSame(theDomain, theEvents.get(0).getDomain());
        assertSame(theEvent, theEvents.get(0).getEvent());
    }

    public void testAddEvent_WithoutSession() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("X");
        final String theUserId = "test_user";
        final Event theEvent = new Event() {};

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(theDomain, theUserId, null);

        DummyRemoteEventServlet theRemoteEventServiceServlet = new DummyRemoteEventServlet(theDomain, theEvent);
        theRemoteEventServiceServlet.addEventCall();

        final List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), theUserId);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertSame(theDomain, theEvents.get(0).getDomain());
        assertSame(theEvent, theEvents.get(0).getEvent());
    }

    public void testAddEventUserSpecific_WithoutSession() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("X");
        final String theUserId = "test_user";
        final Event theEvent = new Event() {};

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(theDomain, theUserId, null);

        DummyRemoteEventServlet theRemoteEventServiceServlet = new DummyRemoteEventServlet(theDomain, theEvent);
        try {
            theRemoteEventServiceServlet.addEventUserSpecificCall();
            fail("Exception expected, because the HTTPRequest shouldn't be available!");
        } catch(NoSessionAvailableException e) {}

        final List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), theUserId);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());
    }

    public void testCheckPermutationStrongName() {
        RemoteEventServiceServlet theRemoteEventServiceServlet = setUpRemoteEventServiceServlet();
        boolean isSuccessful = false;
        try {
            theRemoteEventServiceServlet.checkPermutationStrongName();
            isSuccessful = true;
        } finally {
            assertTrue("The execution of checkPermutationStrongName couldn't be completed successfully!", isSuccessful);
        }
    }

    private class DummyRemoteEventServiceServletOriginal extends RemoteEventServiceServlet {}

    private class DummyRemoteEventServlet extends RemoteEventServiceServlet
    {
        private final Domain myDomain;
        private final Event myEvent;

        private DummyRemoteEventServlet(final Domain aDomain, final Event anEvent) {
            myDomain = aDomain;
            myEvent = anEvent;
        }

        public void init(ServletConfig aServletConfig) throws ServletException {
            addEvent(myDomain, myEvent);
        }

        public void addEventCall() {
            addEvent(myDomain, myEvent);
        }

        public void addEventUserSpecificCall() {
            addEventUserSpecific(myEvent);
        }
    }

    private class DummyRemoteEventServiceServlet_Request extends RemoteEventServiceServlet
    {
        private HttpServletRequest myRequest;

        private DummyRemoteEventServiceServlet_Request(HttpServletRequest aRequest) {
            myRequest = aRequest;
        }

        protected HttpServletRequest getRequest() {
            return myRequest;
        }
    }
}
