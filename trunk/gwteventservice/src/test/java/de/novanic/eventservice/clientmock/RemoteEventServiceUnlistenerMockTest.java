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
package de.novanic.eventservice.clientmock;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.*;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.test.testhelper.DefaultRemoteEventServiceFactoryTestMode;
import de.novanic.eventservice.test.testhelper.EventListenerTestMode;
import de.novanic.eventservice.test.testhelper.UnlistenEventListenerTestMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 31.10.2009
 *         <br>Time: 00:25:38
 */
@RunWith(JUnit4.class)
public class RemoteEventServiceUnlistenerMockTest extends AbstractRemoteEventServiceMockTest
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");

    private RemoteEventService myRemoteEventService;

    @Before
    public void setUp() {
        super.setUp();
        myRemoteEventService = DefaultRemoteEventServiceFactoryTestMode.getInstance().getDefaultRemoteEventService(myEventServiceAsyncMock);
    }

    @Test
    public void testAddUnlistenListener_Local() {
        final RemoteEventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 0, null, DefaultClientConnector.class.getName());
        mockInit(theConfiguration);

        //caused by add Listener
        mockRegister(TEST_DOMAIN);

        //mock listen
        mockListen(null, 1, new TestException()); //reconnect is configured to zero reconnect attempts. The reconnect attempts are executed, because an exception is simulated

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), "testUser", false);

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, theUnlistenEvent, null);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
        assertTrue(myRemoteEventService.isActive());

        assertEquals(1, theUnlistenEventListener.getEventCount());
        assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
        assertFalse(theUnlistenEventResult.isTimeout());
        assertTrue(theUnlistenEventResult.isLocal());
        assertEquals(1, theUnlistenEventResult.getDomains().size());
        assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomains().iterator().next());
        assertEquals("testUser", theUnlistenEventResult.getUserId());
        assertEquals(theUnlistenEvent, theUnlistenEventResult);

        verify(myEventServiceAsyncMock, times(1)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Local_2() {
        final RemoteEventServiceConfigurationTransferable theConfiguration = new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 0, null, DefaultClientConnector.class.getName());
        mockInit(theConfiguration);

        //caused by add Listener
        mockRegister(TEST_DOMAIN);

        //mock listen
        mockListen(null, 1, new TestException()); //reconnect is configured to zero reconnect attempts. The reconnect attempts are executed, because an exception is simulated

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, null);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
        assertTrue(myRemoteEventService.isActive());

        assertEquals(1, theUnlistenEventListener.getEventCount());
        assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
        assertFalse(theUnlistenEventResult.isTimeout());
        assertTrue(theUnlistenEventResult.isLocal());
        assertNull(theUnlistenEventResult.getDomains());
        assertNull(theUnlistenEventResult.getUserId());

        verify(myEventServiceAsyncMock, times(1)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Local_3() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new Event() {}));
        mockListen(theDomainEvents, 2, new TestException());

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, null);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
        assertTrue(myRemoteEventService.isActive());

        assertEquals(0, theUnlistenEventListener.getEventCount());
        assertEquals(0, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        verify(myEventServiceAsyncMock, times(2)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Local_Reconnect() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN);

        //mock listen
        mockListen(null, 3, new TestException()); //reconnect is configured to 2 reconnect attempts. The reconnect attempts are executed, because an exception is simulated

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), "testUser", false);

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, theUnlistenEvent, null);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
        assertTrue(myRemoteEventService.isActive());

        assertEquals(1, theUnlistenEventListener.getEventCount());
        assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
        assertFalse(theUnlistenEventResult.isTimeout());
        assertTrue(theUnlistenEventResult.isLocal());
        assertEquals(1, theUnlistenEventResult.getDomains().size());
        assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomains().iterator().next());
        assertEquals("testUser", theUnlistenEventResult.getUserId());
        assertEquals(theUnlistenEvent, theUnlistenEventResult);

        verify(myEventServiceAsyncMock, times(3)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Local_Reconnect_2() {
        mockInit();

        //caused by add Listener
        mockRegister(TEST_DOMAIN);

        //mock listen
        mockListen(null, 3, new TestException()); //reconnect is configured to 2 reconnect attempts. The reconnect attempts are executed, because an exception is simulated

        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.LOCAL, theUnlistenEventListener, null);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addListener(TEST_DOMAIN, new EventListenerTestMode());
        assertTrue(myRemoteEventService.isActive());

        assertEquals(1, theUnlistenEventListener.getEventCount());
        assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
        assertFalse(theUnlistenEventResult.isTimeout());
        assertTrue(theUnlistenEventResult.isLocal());
        assertNull(theUnlistenEventResult.getDomains());
        assertNull(theUnlistenEventResult.getUserId());

        verify(myEventServiceAsyncMock, times(3)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Unlisten() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        mockRegisterUnlistenEvent(null);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(new DefaultUnlistenEvent(), DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);

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
        assertNull(theUnlistenEventResult.getDomains());
        assertNull(theUnlistenEventResult.getUserId());

        verify(myEventServiceAsyncMock, times(2)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Unlisten_2() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), "testUser", false);
        mockRegisterUnlistenEvent(theUnlistenEvent);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(theUnlistenEvent, DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);

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
        assertEquals(1, theUnlistenEventResult.getDomains().size());
        assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomains().iterator().next());
        assertEquals("testUser", theUnlistenEventResult.getUserId());
        assertEquals(theUnlistenEvent, theUnlistenEventResult);

        verify(myEventServiceAsyncMock, times(2)).listen(any(AsyncCallback.class));
    }

    @Test
    public void testAddUnlistenListener_Unlisten_3() {
        mockInit();

        //caused by add UnlistenListener (the server couldn't connected)
        mockRegister(DomainFactory.UNLISTEN_DOMAIN, new TestException());
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), "testUser", false);

        //add UnlistenListener
        assertEquals(0, theUnlistenEventListener.getEventCount());

        RecordedCallback theRecordedCallback = new RecordedCallback();

        assertFalse(myRemoteEventService.isActive());
        myRemoteEventService.addUnlistenListener(UnlistenEventListener.Scope.UNLISTEN, theUnlistenEventListener, theUnlistenEvent, theRecordedCallback);
        assertFalse(myRemoteEventService.isActive());

        assertFalse(theRecordedCallback.isOnSuccessCalled());
        assertTrue(theRecordedCallback.isOnFailureCalled());

        assertEquals(1, theUnlistenEventListener.getEventCount());
        assertEquals(1, theUnlistenEventListener.getEventCount(DefaultUnlistenEvent.class));

        final UnlistenEvent theUnlistenEventResult = (UnlistenEvent)theUnlistenEventListener.getEvents().get(0);
        assertFalse(theUnlistenEventResult.isTimeout());
        assertTrue(theUnlistenEventResult.isLocal());
    }

    @Test
    public void testAddUnlistenListener_Timeout() {
        mockInit();

        //caused by add UnlistenListener
        mockRegister(DomainFactory.UNLISTEN_DOMAIN);
        final UnlistenEventListenerTestMode theUnlistenEventListener = new UnlistenEventListenerTestMode();
        final UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), "testUser", true);
        mockRegisterUnlistenEvent(theUnlistenEvent);

        //mock listen
        List<DomainEvent> theDomainEvents = new ArrayList<DomainEvent>();
        theDomainEvents.add(new DefaultDomainEvent(theUnlistenEvent, DomainFactory.UNLISTEN_DOMAIN));
        mockListen(theDomainEvents, 1);

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
        assertEquals(1, theUnlistenEventResult.getDomains().size());
        assertEquals(TEST_DOMAIN, theUnlistenEventResult.getDomains().iterator().next());
        assertEquals("testUser", theUnlistenEventResult.getUserId());
        assertEquals(theUnlistenEvent, theUnlistenEventResult);

        verify(myEventServiceAsyncMock, times(2)).listen(any(AsyncCallback.class));
    }
}