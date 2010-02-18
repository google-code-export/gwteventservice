/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.clientmock;

import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.*;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;

import java.util.List;
import java.util.ArrayList;

/**
 * @author sstrohschein
 *         <br>Date: 31.10.2009
 *         <br>Time: 00:25:38
 */
public class RemoteEventServiceUnlistenerMockTest extends AbstractRemoteEventServiceMockTest
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");

    private RemoteEventService myRemoteEventService;

    public void setUp() {
        super.setUp();
        myRemoteEventService = DefaultRemoteEventServiceFactoryTestMode.getInstance().getDefaultRemoteEventService(myEventServiceAsyncMock);
    }

    public void testAddUnlistenListener_Local() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new Event() {}));
        mockListen(theDomainEvents, 3, new TestException());
        mockListen(false);
        mockListen(false);

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, "testUser", false);

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, theUnlistenEvent, null);
            assertFalse(myRemoteEventService.isActive());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
            assertTrue(myRemoteEventService.isActive());

            joinAllListenThreads(3);
            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertFalse(theUnlistenEventResult.isTimeout());
            assertTrue(theUnlistenEventResult.isLocal());
            assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomain());
            assertEquals("testUser", theUnlistenEventResult.getUserId());
            assertEquals(theUnlistenEvent, theUnlistenEventResult);
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }

    public void testAddUnlistenListener_Local_2() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new Event() {}));
        mockListen(theDomainEvents, 3, new TestException());
        mockListen(false);
        mockListen(false);

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, "testUser", false);

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
            assertTrue(myRemoteEventService.isActive());

            assertTrue(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, theUnlistenEvent, null);
            assertTrue(myRemoteEventService.isActive());

            joinAllListenThreads(3);
            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertFalse(theUnlistenEventResult.isTimeout());
            assertTrue(theUnlistenEventResult.isLocal());
            assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomain());
            assertEquals("testUser", theUnlistenEventResult.getUserId());
            assertEquals(theUnlistenEvent, theUnlistenEventResult);
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }

    public void testAddUnlistenListener_Local_3() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new Event() {}));
        mockListen(theDomainEvents, 3, new TestException());
        mockListen(false);
        mockListen(false);

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
            assertTrue(myRemoteEventService.isActive());

            assertTrue(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, null);
            assertTrue(myRemoteEventService.isActive());

            joinAllListenThreads(3);
            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertFalse(theUnlistenEventResult.isTimeout());
            assertTrue(theUnlistenEventResult.isLocal());
            assertNull(theUnlistenEventResult.getDomain());
            assertNull(theUnlistenEventResult.getUserId());
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }

    public void testAddUnlistenListener_Unlisten() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN, true);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        mockRegisterUnlistenEvent(null, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new DefaultUnlistenEvent(), DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);
        mockListen(false);

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(theUnlistenEventListener, null);
            assertTrue(myRemoteEventService.isActive());

            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            //un-initialized UnlistenEvent (caused by the mock call)
            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertFalse(theUnlistenEventResult.isTimeout());
            assertFalse(theUnlistenEventResult.isLocal());
            assertNull(theUnlistenEventResult.getDomain());
            assertNull(theUnlistenEventResult.getUserId());
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }

    public void testAddUnlistenListener_Unlisten_2() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN, true);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, "testUser", false);
        mockRegisterUnlistenEvent(theUnlistenEvent, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(theUnlistenEvent, DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);
        mockListen(false);

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.UNLISTEN, theUnlistenEventListener, theUnlistenEvent, null);
            assertTrue(myRemoteEventService.isActive());

            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertFalse(theUnlistenEventResult.isTimeout());
            assertFalse(theUnlistenEventResult.isLocal());
            assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomain());
            assertEquals("testUser", theUnlistenEventResult.getUserId());
            assertEquals(theUnlistenEvent, theUnlistenEventResult);
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }

    public void testAddUnlistenListener_Timeout() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN, true);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, "testUser", true);
        mockRegisterUnlistenEvent(theUnlistenEvent, true);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(theUnlistenEvent, DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);
        mockListen(false);

        myEventServiceAsyncMockControl.replay();
            //add UnlistenListener
            assertEquals(0, theUnlistenEventListener.getEventCount());

            assertFalse(myRemoteEventService.isActive());
            myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.UNLISTEN, theUnlistenEventListener, theUnlistenEvent, null);
            assertTrue(myRemoteEventService.isActive());

            assertEquals(1, theUnlistenEventListener.getEventCount());
            assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

            final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
            assertTrue(theUnlistenEventResult.isTimeout());
            assertFalse(theUnlistenEventResult.isLocal());
            assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomain());
            assertEquals("testUser", theUnlistenEventResult.getUserId());
            assertEquals(theUnlistenEvent, theUnlistenEventResult);
        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();
    }
}