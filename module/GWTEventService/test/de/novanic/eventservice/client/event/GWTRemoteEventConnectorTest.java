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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.logger.ClientLoggerFactory;
import de.novanic.eventservice.client.logger.AbstractClientLogger;

import java.util.List;
import java.util.ArrayList;

/**
 * @author sstrohschein
 *         <br>Date: 21.10.2008
 *         <br>Time: 20:56:53
 */
public class GWTRemoteEventConnectorTest extends AbstractRemoteEventServiceMockTest
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test-domain");

    private RemoteEventConnector myRemoteEventConnector;
    private GWTRemoteEventConnectorTest.DummyClientLogger myClientLogger;

    public void setUp() {
        super.setUp();
        myClientLogger = new DummyClientLogger();
        ClientLoggerFactory.getClientLogger().attach(myClientLogger);

        myRemoteEventConnector = new GWTRemoteEventConnector(myEventServiceAsyncMock);
    }

    public void tearDown() {
        super.tearDown();
        myClientLogger.clearLogMessages();
        ClientLoggerFactory.getClientLogger().detach(myClientLogger);
    }

    public void testDeactivate() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN, true);
        mockListen(true);

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        myEventServiceAsyncMockControl.replay();

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector.", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        myClientLogger.clearLogMessages();

        //deactivate
        myRemoteEventConnector.deactivate();

        //check deactivation
        assertFalse(myRemoteEventConnector.isActive());
        assertFalse(theEventNotification.isAborted());

        theLogMessages = myClientLogger.getLogMessages();
        assertEquals(1, theLogMessages.size());
        assertEquals("Log: RemoteEventConnector deactivated.", theLogMessages.get(0));
    }

    public void testDeactivate_After_Timeout() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN, true);
        mockListen(null, 1);

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        myEventServiceAsyncMockControl.replay();

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        myEventServiceAsyncMockControl.verify();
        myEventServiceAsyncMockControl.reset();

        //check deactivation
        assertFalse(myRemoteEventConnector.isActive());
        assertTrue(theEventNotification.isAborted());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(3, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector.", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        assertEquals("Log: RemoteEventConnector deactivated.", theLogMessages.get(2));
    }

    private class DummyClientLogger extends AbstractClientLogger
    {
        private static final String MESSAGE_LOG_PREFIX = "Log: ";
        private static final String ERROR_LOG_PREFIX = "Error: ";

        private List<String> myLogMessages;

        public DummyClientLogger() {
            myLogMessages = new ArrayList<String>();
        }

        public void log_internal(String aMessage) {
            myLogMessages.add(MESSAGE_LOG_PREFIX + aMessage);
        }

        public void error_internal(String aMessage) {
            error(aMessage, null);
        }

        public void error_internal(String aMessage, Throwable aThrowable) {
            myLogMessages.add(ERROR_LOG_PREFIX + aMessage);
        }

        public List<String> getLogMessages() {
            return myLogMessages;
        }

        public void clearLogMessages() {
            myLogMessages.clear();
        }
    }

    private class TestEventNotification implements EventNotification
    {
        private boolean myIsAborted;

        public void onNotify(List<DomainEvent> anEvents) {
        }

        public void onAbort() {
            myIsAborted = true;
        }

        public boolean isAborted() {
            return myIsAborted;
        }
    }
}