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
package de.novanic.eventservice.clientmock.connection.strategy.connector.streaming;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.GWTStreamingClientConnector;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.test.testhelper.DummyDomainEvent;
import de.novanic.eventservice.test.testhelper.EventServiceAsyncSuccessDummy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.support.membermodification.MemberModifier.suppress;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2010
 *         <br>Time: 18:20:37
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GWTStreamingClientConnector.class, RootPanel.class, GWT.class})
public class GWTStreamingClientConnectorTest
{
    private GWTStreamingClientConnector myGWTStreamingClientConnector;

    @Before
    public void setUp() {
        myGWTStreamingClientConnector = new GWTStreamingClientConnector();
    }

    @Test
    public void testInit() {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());
    }

    @Test
    public void testListen() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        myGWTStreamingClientConnector.listen(theEventNotification, null);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    @Test
    public void testReceiveEvent() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        DomainEvent theDomainEvent = new DummyDomainEvent();

        SerializationStreamReader theSerializationStreamReaderMock = mock(SerializationStreamReader.class);
        when(theSerializationStreamReaderMock.readObject()).thenReturn(theDomainEvent);

        mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);

        assertTrue(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        assertEquals(1, theEventNotification.myDomainEvents.size());
        assertSame(theDomainEvent, theEventNotification.myDomainEvents.get(0));
    }

    @Test
    public void testReceiveEvent_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        DomainEvent theDomainEvent = new DummyDomainEvent();

        SerializationStreamReader theSerializationStreamReaderMock = mock(SerializationStreamReader.class);
        when(theSerializationStreamReaderMock.readObject()).thenReturn(theDomainEvent);

        mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);

        assertTrue(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        assertEquals(1, theEventNotification.myDomainEvents.size());
        assertSame(theDomainEvent, theEventNotification.myDomainEvents.get(0));

        myGWTStreamingClientConnector.listen(theEventNotification, null);

        assertEquals(1, theEventNotification.myDomainEvents.size()); //the earlier received event is still contained within the dummy notification...
        assertSame(theDomainEvent, theEventNotification.myDomainEvents.get(0)); //..., but no new event
    }

    @Test
    public void testReceiveEvent_Error() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "corrupt_serialized_event";

        SerializationStreamReader theSerializationStreamReaderMock = mock(SerializationStreamReader.class);
        when(theSerializationStreamReaderMock.readObject()).thenThrow(new SerializationException("The event is corrupt and can not be deserialized!"));

        mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        try {
            myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);
            fail("Exception expected, because the event is corrupt and can not be deserialized!");
        } catch(RemoteEventServiceRuntimeException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof SerializationException);
        }

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    @Test
    public void testDeactivate() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        when(theRootPanelMock.remove(theFrameMock)).thenReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        //initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        //deactivate / remove the streaming frame
        myGWTStreamingClientConnector.deactivate();

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    @Test
    public void testDeactivate_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        when(theRootPanelMock.remove(theFrameMock)).thenReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        //initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        //deactivate / remove the streaming frame
        myGWTStreamingClientConnector.deactivate();

        //deactivate / remove the streaming frame again
        myGWTStreamingClientConnector.deactivate();

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    @Test
    public void testDeactivate_and_ReInit() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        when(theRootPanelMock.remove(theFrameMock)).thenReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        //initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        //deactivate / remove the streaming frame
        myGWTStreamingClientConnector.deactivate();

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        theFrameMock = mockInitFrame();
        mockInitRootPanel(theFrameMock);

        //re-initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    @Test
    public void testDeactivate_and_ReInit_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        when(theRootPanelMock.remove(theFrameMock)).thenReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        //initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        //deactivate / remove the streaming frame
        myGWTStreamingClientConnector.deactivate();

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        theFrameMock = mockInitFrame();
        theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        when(theRootPanelMock.remove(theFrameMock)).thenReturn(Boolean.TRUE);

        //re-initializes the streaming frame
        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        //deactivate / remove the streaming frame
        myGWTStreamingClientConnector.deactivate();

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    private static void mockInitJS() {
        suppress(method(GWTStreamingClientConnector.class, "initReceiveEventScript", DefaultStreamingClientConnector.class));
    }

    private static Frame mockInitFrame() throws Exception {
        GWTMockUtilities.disarm();

        Frame theFrameMock = mock(Frame.class);
        PowerMockito.whenNew(Frame.class).withArguments("dummyurl").thenReturn(theFrameMock);

        GWTMockUtilities.restore();

        return theFrameMock;
    }

    private static RootPanel mockInitRootPanel(Frame aFrame) {
        RootPanel theRootPanelMock = mock(RootPanel.class);
        theRootPanelMock.add(aFrame);

        PowerMockito.mockStatic(RootPanel.class);
        when(RootPanel.get()).thenReturn(theRootPanelMock);

        return theRootPanelMock;
    }

    private static SerializationStreamFactory mockInitSerializationStreamFactory(SerializationStreamReader aSerializationStreamReader, String aSerializedEvent) throws Exception {
        SerializationStreamFactory theSerializationStreamFactoryMock = mock(SerializationStreamFactory.class);
        when(theSerializationStreamFactoryMock.createStreamReader(aSerializedEvent)).thenReturn(aSerializationStreamReader);

        PowerMockito.mockStatic(GWT.class);
        when(GWT.create(EventService.class)).thenReturn(theSerializationStreamFactoryMock);

        return theSerializationStreamFactoryMock;
    }

    private class EventNotificationTestHandler implements EventNotification
    {
        private List<DomainEvent> myDomainEvents;
        private boolean isAborted;

        public EventNotificationTestHandler() {
            myDomainEvents = new ArrayList<DomainEvent>();
        }

        public void onNotify(DomainEvent aDomainEvent) {
            myDomainEvents.add(aDomainEvent);
        }

        public boolean isNotified() {
            return !myDomainEvents.isEmpty();
        }

        public void onAbort() {
            isAborted = true;
        }
    }
}