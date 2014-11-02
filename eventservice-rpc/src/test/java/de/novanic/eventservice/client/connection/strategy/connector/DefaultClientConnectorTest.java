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
package de.novanic.eventservice.client.connection.strategy.connector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 22.04.2010
 *         <br>Time: 00:14:17
 */
@RunWith(JUnit4.class)
public class DefaultClientConnectorTest
{
    @Test
    public void testInit() {
        EventServiceAsync theEventServiceMock = mock(EventServiceAsync.class);

        ConnectionStrategyClientConnector theClientConnector = new DefaultClientConnector();
        assertFalse(theClientConnector.isInitialized());
        theClientConnector.init(theEventServiceMock);
        assertTrue(theClientConnector.isInitialized());
    }

    @Test
    public void testDeactivate() {
        EventServiceAsync theEventServiceMock = mock(EventServiceAsync.class);

        ConnectionStrategyClientConnector theClientConnector = new DefaultClientConnector();

        assertFalse(theClientConnector.isInitialized());
        theClientConnector.init(theEventServiceMock);
        assertTrue(theClientConnector.isInitialized());

        assertTrue(theClientConnector.isInitialized());
        theClientConnector.deactivate();
        assertTrue(theClientConnector.isInitialized());//it is deactivated, but still initialized
    }

    @Test
    public void testListen() {
        final AsyncCallback<List<DomainEvent>> theDummyAsyncCallback = new AsyncCallback<List<DomainEvent>>() {
            public void onFailure(Throwable aThrowable) {
            }

            public void onSuccess(List<DomainEvent> aDomainEvents) {
            }
        };

        EventServiceAsync theEventServiceMock = mock(EventServiceAsync.class);

        theEventServiceMock.listen(theDummyAsyncCallback);

        ConnectionStrategyClientConnector theClientConnector = new DefaultClientConnector();
        theClientConnector.init(theEventServiceMock);
        theClientConnector.listen(new DummyEventNotification(), theDummyAsyncCallback);
    }

    private class DummyEventNotification implements EventNotification
    {
        public void onNotify(DomainEvent aDomainEvent) {}

        public void onAbort() {}
    }
}