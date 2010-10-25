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
package de.novanic.eventservice.clientmock.connection.strategy.connector.streaming.specific;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.DefaultStreamingClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.specific.GWTStreamingClientConnectorGecko;
import de.novanic.eventservice.client.event.DomainEvent;
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

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 * @author sstrohschein
 *         <br>Date: 24.10.2010
 *         <br>Time: 20:52:09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GWTStreamingClientConnectorGecko.class, RootPanel.class, Element.class, GWT.class})
public class GWTStreamingClientConnectorGeckoTest extends TestCase
{
    private GWTStreamingClientConnectorGecko myGWTStreamingClientConnector;

    static {
        suppress(method(GWTStreamingClientConnectorGecko.class, "createFrameElement"));
    }

    public void setUp() {
        myGWTStreamingClientConnector = new GWTStreamingClientConnectorGecko();
    }

    public void testInit() throws Exception {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());
    }

    public void testReceiveEvent() throws Exception {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMock.mockStatic(Element.class);
        Element theElementMock = PowerMock.createMock(Element.class);
        EasyMock.expect(theElementMock.appendChild(EasyMock.<Node>anyObject())).andReturn(null);
        EasyMock.expect(theElementMock.removeChild(EasyMock.<Node>anyObject())).andReturn(null);

        GWTMockUtilities.disarm();

        Frame theFrameMock = mockInitFrame();
        RootPanel theRootPanelMock = mockInitRootPanel(theFrameMock);
        EasyMock.expect(RootPanel.getBodyElement()).andReturn(theElementMock).times(2);

        GWTMockUtilities.restore();

        DomainEvent theDomainEvent = new DummyDomainEvent();
        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        SerializationStreamReader theSerializationStreamReaderMock = EasyMock.createMock(SerializationStreamReader.class);
        EasyMock.expect(theSerializationStreamReaderMock.readObject()).andReturn(theDomainEvent);

        SerializationStreamFactory theSerializationStreamFactoryMock = mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        PowerMock.replay(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, theElementMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);

            myGWTStreamingClientConnector.listen(theEventNotification, null);
            assertFalse(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

            myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);
            assertTrue(theEventNotification.isNotified());
            assertFalse(theEventNotification.isAborted);

        PowerMock.verify(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, theElementMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
        PowerMock.reset(Frame.class, theFrameMock, RootPanel.class, theRootPanelMock, theElementMock, GWT.class, theSerializationStreamFactoryMock, theSerializationStreamReaderMock);
    }

    private static void mockInitJS() {
        suppress(method(GWTStreamingClientConnectorGecko.class, "initReceiveEventScript", DefaultStreamingClientConnector.class));
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