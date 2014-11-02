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
package de.novanic.eventservice.client.connection.strategy.connector.streaming;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.event.DefaultDomainEvent;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.EventNotification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 19:58:30
 */
@RunWith(JUnit4.class)
public class DefaultStreamingClientConnectorTest
{
    @Test
    public void testInit() {
        final ConnectionStrategyClientConnector theStreamingClientConnector = new DummyStreamingClientConnector();
        assertFalse(theStreamingClientConnector.isInitialized());
        theStreamingClientConnector.init(null);
        assertTrue(theStreamingClientConnector.isInitialized());
    }

    @Test
    public void testDeactivate() {
        final ConnectionStrategyClientConnector theStreamingClientConnector = new DummyStreamingClientConnector();

        assertFalse(theStreamingClientConnector.isInitialized());
        theStreamingClientConnector.init(null);
        assertTrue(theStreamingClientConnector.isInitialized());

        assertTrue(theStreamingClientConnector.isInitialized());
        theStreamingClientConnector.deactivate();
        assertTrue(theStreamingClientConnector.isInitialized());//it is deactivated, but still initialized
    }

    @Test
    public void testListen() {
        final DummyStreamingClientConnector theStreamingClientConnector = new DummyStreamingClientConnector();
        theStreamingClientConnector.init(null);

        final DummyEventNotification theEventNotification = new DummyEventNotification();
        final DummyCallback theDummyCallback = new DummyCallback();

        theStreamingClientConnector.listen(theEventNotification, theDummyCallback);

        assertFalse("No cycle was expected! Therefore the callback shouldn't be used!", theDummyCallback.isOnSuccessCalled);
        assertNull(theDummyCallback.myReceivedEvents);
        assertSame(theStreamingClientConnector.myDummyEvent, theEventNotification.myNotifiedEvent);
    }

    @Test
    public void testListen_2() {
        final DummyStreamingClientConnector theStreamingClientConnector = new DummyStreamingClientConnectorCycle();
        theStreamingClientConnector.init(null);

        final DummyEventNotification theEventNotification = new DummyEventNotification();
        final DummyCallback theDummyCallback = new DummyCallback();

        theStreamingClientConnector.listen(theEventNotification, theDummyCallback);

        assertTrue("A cycle was expected, because the " + DummyStreamingClientConnectorCycle.class.getName() + " should send a cycle tag!", theDummyCallback.isOnSuccessCalled);
        assertTrue("No events are expected from the callback, because the events are processed directly at streaming, so no events should be received/processed with the connection cycle.", theDummyCallback.myReceivedEvents.isEmpty());
    }

    private class DummyStreamingClientConnector extends DefaultStreamingClientConnector
    {
        private DomainEvent myDummyEvent;

        private DummyStreamingClientConnector() {
            myDummyEvent = new DefaultDomainEvent(new Event(){});
        }

        protected DomainEvent deserializeEvent(String anEvent) {
            return myDummyEvent;
        }

        protected void listen() {
            receiveEvent(null);
        }
    }

    private class DummyStreamingClientConnectorCycle extends DummyStreamingClientConnector
    {
        protected void listen() {
            receiveEvent(CYCLE_TAG);
        }
    }

    private class DummyEventNotification implements EventNotification
    {
        private DomainEvent myNotifiedEvent;

        public void onNotify(DomainEvent aDomainEvent) {
            myNotifiedEvent = aDomainEvent;
        }

        public void onAbort() {}
    }

    private class DummyCallback implements AsyncCallback<List<DomainEvent>>
    {
        private boolean isOnSuccessCalled;
        private List<DomainEvent> myReceivedEvents;

        public void onSuccess(List<DomainEvent> aDomainEvents) {
            myReceivedEvents = aDomainEvents;
            isOnSuccessCalled = true;
        }

        public void onFailure(Throwable aThrowable) {}
    }
}