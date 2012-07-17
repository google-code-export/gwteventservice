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
package de.novanic.eventservice.clientmock;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.*;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.logger.ClientLoggerFactory;
import de.novanic.eventservice.client.logger.AbstractClientLogger;
import de.novanic.eventservice.test.testhelper.DefaultRemoteEventServiceFactoryTestMode;
import de.novanic.eventservice.test.testhelper.EventServiceAsyncSuccessDummy;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 21.10.2008
 *         <br>Time: 20:56:53
 */
@RunWith(JUnit4.class)
public class GWTRemoteEventConnectorTest extends AbstractRemoteEventServiceMockTest
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test-domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test-domain-2");

    private RemoteEventConnector myRemoteEventConnector;
    private GWTRemoteEventConnectorTest.DummyClientLogger myClientLogger;

    @Before
    public void setUp() {
        super.setUp();
        myClientLogger = new DummyClientLogger();
        ClientLoggerFactory.getClientLogger().attach(myClientLogger);

        myRemoteEventConnector = DefaultRemoteEventServiceFactoryTestMode.getInstance().getGWTRemoteEventConnector(myEventServiceAsyncMock);
        myRemoteEventConnector.initListen(new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "dummy-connection-id", DefaultClientConnector.class.getName()));
    }

    @After
    public void tearDown() {
        super.tearDown();
        myClientLogger.clearLogMessages();
        ClientLoggerFactory.getClientLogger().detach(myClientLogger);
    }

    @Test
    public void testInit() {
        assertFalse(myRemoteEventConnector.isActive());

        mockInit();
        
        //init connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.init(new AsyncCallback<EventServiceConfigurationTransferable>() {
                public void onSuccess(EventServiceConfigurationTransferable anEventServiceConfigurationTransferable) {}

                public void onFailure(Throwable aThrowable) {}
            });

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        assertFalse(myRemoteEventConnector.isActive()); //false because an exception is occurred while the initialization / refreshing of the EventService
    }

    @Test
    public void testInit_2() {
        assertFalse(myRemoteEventConnector.isActive());

        mockInit(new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "dummy-connection-id", DefaultClientConnector.class.getName()));

        //init connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.init(new AsyncCallback<EventServiceConfigurationTransferable>() {
                public void onSuccess(EventServiceConfigurationTransferable anEventServiceConfigurationTransferable) {}

                public void onFailure(Throwable aThrowable) {}
            });

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        assertFalse(myRemoteEventConnector.isActive());
    }

    @Test
    public void testInit_3() {
        DefaultRemoteEventServiceFactoryTestMode theEventServiceFactoryTestMode = DefaultRemoteEventServiceFactoryTestMode.getInstance();

        EventServiceAsync theEventServiceAsyncDummy = new EventServiceAsyncSuccessDummy();

        GWTRemoteEventConnector theGWTRemoteEventConnector = theEventServiceFactoryTestMode.getGWTRemoteEventConnector(theEventServiceAsyncDummy);
        theGWTRemoteEventConnector.init(new AsyncCallback<EventServiceConfigurationTransferable>() {
            public void onSuccess(EventServiceConfigurationTransferable anEventServiceConfigurationTransferable) {}

            public void onFailure(Throwable aThrowable) {}
        });
    }

    @Test
    public void testActivate() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockListen();

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        myClientLogger.clearLogMessages();
    }

    @Test
    public void testActivate_Error() {
        assertFalse(myRemoteEventConnector.isActive());
        try {
            myRemoteEventConnector.initListen(null);
            fail("No configuration was provided");
        } catch(RemoteEventServiceRuntimeException e) {}
        assertFalse(myRemoteEventConnector.isActive());
    }

    @Test
    public void testActivate_Error_2() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockListen();

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        myClientLogger.clearLogMessages();

        try {
            myRemoteEventConnector.initListen(new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "dummy-connection-id", DefaultClientConnector.class.getName()));
            fail("An exception was expected, because it was tried to change the connection strategy after the start of listening!");
        } catch(RemoteEventServiceRuntimeException e) {
            assertTrue(e.getMessage().contains("connection"));
            assertTrue(e.getMessage().contains("strategy"));
        }
    }

    @Test
    public void testDeactivate() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockListen();

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
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

    @Test
    public void testDeactivate_2() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockListen();

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        myClientLogger.clearLogMessages();

        //deactivate (3 times)
        myRemoteEventConnector.deactivate();
        myRemoteEventConnector.deactivate();
        myRemoteEventConnector.deactivate();

        //check deactivation
        assertFalse(myRemoteEventConnector.isActive());
        assertFalse(theEventNotification.isAborted());

        theLogMessages = myClientLogger.getLogMessages();
        assertEquals(1, theLogMessages.size());
        assertEquals("Log: RemoteEventConnector deactivated.", theLogMessages.get(0));
    }

    @Test
    public void testDeactivate_3() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockRegister(TEST_DOMAIN_2);
        mockListen();

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);
            myRemoteEventConnector.activate(TEST_DOMAIN_2, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check activation
        assertTrue(myRemoteEventConnector.isActive());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(3, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
        assertEquals("Log: RemoteEventConnector activated.", theLogMessages.get(1));
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain-2\".", theLogMessages.get(2));
        myClientLogger.clearLogMessages();

        //deactivate (3 times)
        myRemoteEventConnector.deactivate();

        //check deactivation
        assertFalse(myRemoteEventConnector.isActive());
        assertFalse(theEventNotification.isAborted());

        theLogMessages = myClientLogger.getLogMessages();
        assertEquals(1, theLogMessages.size());
        assertEquals("Log: RemoteEventConnector deactivated.", theLogMessages.get(0));
    }

    @Test
    public void testDeactivate_After_Timeout() {
        assertFalse(myRemoteEventConnector.isActive());

        mockRegister(TEST_DOMAIN);
        mockListen(null, 1);

        final TestEventNotification theEventNotification = new TestEventNotification();

        //activate connector
        EasyMock.replay(myEventServiceAsyncMock);

            myRemoteEventConnector.activate(TEST_DOMAIN, null, theEventNotification, null);

        EasyMock.verify(myEventServiceAsyncMock);
        EasyMock.reset(myEventServiceAsyncMock);

        //check deactivation
        assertFalse(myRemoteEventConnector.isActive());
        assertTrue(theEventNotification.isAborted());

        List<String> theLogMessages = myClientLogger.getLogMessages();
        assertEquals(3, theLogMessages.size());
        assertEquals("Log: Activate RemoteEventConnector for domain \"test-domain\".", theLogMessages.get(0));
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

        public void onNotify(DomainEvent aDomainEvent) {}

        public void onAbort() {
            myIsAborted = true;
        }

        public boolean isAborted() {
            return myIsAborted;
        }
    }
}