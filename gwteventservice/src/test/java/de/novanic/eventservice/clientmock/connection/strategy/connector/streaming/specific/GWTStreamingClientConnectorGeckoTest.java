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
package de.novanic.eventservice.clientmock.connection.strategy.connector.streaming.specific;

import com.google.gwt.core.client.GWT;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 24.10.2010
 *         <br>Time: 20:52:09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GWTStreamingClientConnectorGecko.class, RootPanel.class, Element.class, GWT.class})
public class GWTStreamingClientConnectorGeckoTest
{
    private GWTStreamingClientConnectorGecko myGWTStreamingClientConnector;

    static {
        suppress(method(GWTStreamingClientConnectorGecko.class, "createFrameElement"));
    }

    @Before
    public void setUp() {
        myGWTStreamingClientConnector = new GWTStreamingClientConnectorGecko();
    }

    @Test
    public void testInit() throws Exception {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());
    }

    @Test
    public void testReceiveEvent() throws Exception {
        mockInitJS();

        assertFalse(myGWTStreamingClientConnector.isInitialized());
        myGWTStreamingClientConnector.init(new EventServiceAsyncSuccessDummy());
        assertTrue(myGWTStreamingClientConnector.isInitialized());

        EventNotificationTestHandler theEventNotification = new EventNotificationTestHandler();

        PowerMockito.mockStatic(Element.class);
        Element theElementMock = PowerMockito.mock(Element.class);

        GWTMockUtilities.disarm();

        mockInitFrame();
        mockInitRootPanel();
        when(RootPanel.getBodyElement()).thenReturn(theElementMock);

        GWTMockUtilities.restore();

        DomainEvent theDomainEvent = new DummyDomainEvent();
        final String theSerializedEvent = "[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",null],0,5]";

        SerializationStreamReader theSerializationStreamReaderMock = mock(SerializationStreamReader.class);
        when(theSerializationStreamReaderMock.readObject()).thenReturn(theDomainEvent);

        mockInitSerializationStreamFactory(theSerializationStreamReaderMock, theSerializedEvent);

        myGWTStreamingClientConnector.listen(theEventNotification, null);
        assertFalse(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);

        myGWTStreamingClientConnector.receiveEvent(theSerializedEvent);
        assertTrue(theEventNotification.isNotified());
        assertFalse(theEventNotification.isAborted);
    }

    private static void mockInitJS() {
        suppress(method(GWTStreamingClientConnectorGecko.class, "initReceiveEventScript", DefaultStreamingClientConnector.class));
    }

    private static Frame mockInitFrame() throws Exception {
        GWTMockUtilities.disarm();

        Frame theFrameMock = mock(Frame.class);
        PowerMockito.whenNew(Frame.class).withArguments("dummyurl").thenReturn(theFrameMock);

        GWTMockUtilities.restore();

        return theFrameMock;
    }

    private static RootPanel mockInitRootPanel() {
        RootPanel theRootPanelMock = mock(RootPanel.class);

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