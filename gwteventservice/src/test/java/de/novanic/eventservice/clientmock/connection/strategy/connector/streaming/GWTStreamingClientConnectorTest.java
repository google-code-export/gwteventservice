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
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.support.membermodification.MemberModifier.suppress;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

/**
 * @author sstrohschein
 *         <br>Date: 23.10.2010
 *         <br>Time: 18:20:37
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GWTStreamingClientConnector.class, RootPanel.class, GWT.class})
public class GWTStreamingClientConnectorTest extends TestCase
{
    private GWTStreamingClientConnector myGWTStreamingClientConnector;

    public void setUp() {
        myGWTStreamingClientConnector = new GWTStreamingClientConnector();
    }

    public void testInit() {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());
    }

    public void testListen() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock);

            myGWTStreamingClientConnector.listen(theEventNotification, null);

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    public void testReceiveEvent() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        DomainEvent theDomainEvent = new DummyDomainEvent();

        SerializationStreamReader theSerializationStreamReaderMock = EasyMock.createMock(SerializationStreamReader.class);
        EasyMock.expect(theSerializationStreamReaderMock.readObject()).andReturn(theDomainEvent);

        SerializationStreamFactory theSerializationStreamFactoryMock = mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

        assertTrue(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        assertEquals(1, theEventNotification.myDomainEvents.size());
        assertSame(theDomainEvent, theEventNotification.myDomainEvents.get(0));
    }

    public void testReceiveEvent_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        DomainEvent theDomainEvent = new DummyDomainEvent();

        SerializationStreamReader theSerializationStreamReaderMock = EasyMock.createMock(SerializationStreamReader.class);
        EasyMock.expect(theSerializationStreamReaderMock.readObject()).andReturn(theDomainEvent);

        SerializationStreamFactory theSerializationStreamFactoryMock = mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        theFrameMock.setUrl("dummyurl"); //re-init of the frame (caused by the second listen call)

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

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

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
    }

    public void testReceiveEvent_Error() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        final String theSerializedEvent = "corrupt_serialized_event";

        SerializationStreamReader theSerializationStreamReaderMock = EasyMock.createMock(SerializationStreamReader.class);
        EasyMock.expect(theSerializationStreamReaderMock.readObject()).andThrow(new SerializationException("The event is corrupt and can not be deserialized!"));

        SerializationStreamFactory theSerializationStreamFactoryMock = mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

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

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    public void testDeactivate() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        EasyMock.expect(theRootPanelMock.remove(theFrameMock)).andReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            //deactivate / remove the streaming frame
            myGWTStreamingClientConnector.deactivate();

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    public void testDeactivate_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        EasyMock.expect(theRootPanelMock.remove(theFrameMock)).andReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            //deactivate / remove the streaming frame
            myGWTStreamingClientConnector.deactivate();

            //deactivate / remove the streaming frame again
            myGWTStreamingClientConnector.deactivate();

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    public void testDeactivate_and_ReInit() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        EasyMock.expect(theRootPanelMock.remove(theFrameMock)).andReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            //deactivate / remove the streaming frame
            myGWTStreamingClientConnector.deactivate();

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        theFrameMock = mockInitFrame();
        theRootPanelMock = mockInitRootPanel(theFrameMock);

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //re-initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    public void testDeactivate_and_ReInit_2() throws Exception {
        mockInitJS();

        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        EasyMock.expect(theRootPanelMock.remove(theFrameMock)).andReturn(Boolean.TRUE);

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            //deactivate / remove the streaming frame
            myGWTStreamingClientConnector.deactivate();

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        theFrameMock = mockInitFrame();
        theRootPanelMock = mockInitRootPanel(theFrameMock);

        //RootPanel reset caused by the deactivation
        EasyMock.expect(theRootPanelMock.remove(theFrameMock)).andReturn(Boolean.TRUE);

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

            //re-initializes the streaming frame
            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            //deactivate / remove the streaming frame
            myGWTStreamingClientConnector.deactivate();

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, GWT.class);

        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    private static void mockInitJS() {
        suppress(method(GWTStreamingClientConnector.class, "initReceiveEventScript", DefaultStreamingClientConnector.class));
    }

    private static Frame mockInitFrame() throws Exception {
        GWTMockUtilities.disarm();

        Frame theFrameMock = PowerMock.createMock(Frame.class);
        PowerMock.expectNew(Frame.class, "dummyurl").andReturn(theFrameMock);

        GWTMockUtilities.restore();

        theFrameMock.setVisible(false);
        return theFrameMock;
    }

    private static RootPanel mockInitRootPanel(Frame aFrame) {
        RootPanel theRootPanelMock = EasyMock.createMock(RootPanel.class);
        theRootPanelMock.add(aFrame);

        PowerMock.mockStatic(RootPanel.class);
        EasyMock.expect(RootPanel.get()).andReturn(theRootPanelMock).anyTimes();

        return theRootPanelMock;
    }

    private static SerializationStreamFactory mockInitSerializationStreamFactory(SerializationStreamReader aSerializationStreamReader, String aSerializedEvent) throws Exception {
        SerializationStreamFactory theSerializationStreamFactoryMock = EasyMock.createMock(SerializationStreamFactory.class);
        EasyMock.expect(theSerializationStreamFactoryMock.createStreamReader(aSerializedEvent)).andReturn(aSerializationStreamReader);

        PowerMock.mockStatic(GWT.class);
        EasyMock.expect(GWT.create(EventService.class)).andReturn(theSerializationStreamFactoryMock);

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