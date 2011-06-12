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
package de.novanic.eventservice.clientmock.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.ClientHandler;
import de.novanic.eventservice.client.DefaultClientHandler;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.*;
import de.novanic.eventservice.client.event.command.ClientCommand;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.service.creator.EventServiceCreator;
import de.novanic.eventservice.test.testhelper.DefaultRemoteEventServiceFactoryTestMode;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.Set;

/**
 * @author sstrohschein
 * Date: 16.08.2008
 * Time: 20:37:44
 */
public class RemoteEventServiceFactoryTest extends TestCase
{
    public void setUp() {
        ConfigurationTransferableDependentFactory.getInstance(getDefaultConfiguration()).reset(getDefaultConfiguration());
        ClientCommandSchedulerFactory.getInstance().setClientCommandSchedulerInstance(new DummyClientCommandScheduler());
    }

    public void tearDown() {
        ClientCommandSchedulerFactory.getInstance().reset();
        RemoteEventServiceFactory.reset();
    }

    public void testReset() {
        boolean isThrowableOccurred = false;
        try {
            RemoteEventServiceFactory.reset();
        } catch(Throwable e) {
            isThrowableOccurred = true;
        }

        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        try {
            theRemoteEventServiceFactory.getRemoteEventService();
        } catch(Throwable e) {
            isThrowableOccurred = true;
        }

        try {
            RemoteEventServiceFactory.reset();
        } catch(Throwable e) {
            isThrowableOccurred = true;
        }
        if(!isThrowableOccurred) {
            fail("Exception expected, because GWT can not be initialized in MockTest!");
        }
    }

    public void testFactory() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        assertSame(theRemoteEventServiceFactory, RemoteEventServiceFactory.getInstance());
    }

    public void testGetInstance() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        assertSame(theRemoteEventServiceFactory, RemoteEventServiceFactory.getInstance());

        boolean isThrowableOccurred = false;
        try {
            RemoteEventService theRemoteEventService = theRemoteEventServiceFactory.getRemoteEventService();
            assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService());
        } catch(Throwable e) {
            isThrowableOccurred = true;
        }
        //The throwable has to be catched this way because the AssertionError would be catched instead.
        if(!isThrowableOccurred) {
            fail("Exception expected, because GWT can not be initialized in MockTest!");
        }
    }

    public void testGetInstance_2() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = new RemoteEventServiceFactoryTestMode();
        try {
            theRemoteEventServiceFactory.getRemoteEventService();
            theRemoteEventServiceFactory.getRemoteEventService();
        } catch(Error e) {
            fail("No exception was expected! - " + e.getMessage());
        }
    }

    public void testGetInstance_3() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        assertSame(theRemoteEventServiceFactory, RemoteEventServiceFactory.getInstance());

        RemoteEventConnector theRemoteEventConnector = DefaultRemoteEventServiceFactoryTestMode.getInstance().getGWTRemoteEventConnector(new EventServiceCreator() {
            public EventServiceAsync createEventService() {
                return null;
            }
        });

        RemoteEventService theRemoteEventService = theRemoteEventServiceFactory.getRemoteEventService(theRemoteEventConnector);
        assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService());
        assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService());
        assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService(theRemoteEventConnector));
        assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService(theRemoteEventConnector));
        assertSame(theRemoteEventService, theRemoteEventServiceFactory.getRemoteEventService());
    }

    public void testRequestClientHandler() throws Exception {
        RemoteEventServiceFactory theRemoteEventServiceFactory = new RemoteEventServiceFactoryTestMode();

        class ClientHandlerAsyncCallback implements AsyncCallback<ClientHandler>
        {
            private boolean isExecuted;

            public void onFailure(Throwable aThrowable) {
                fail("Unexpected error occurred: " + aThrowable.getMessage());
            }

            public void onSuccess(ClientHandler aClientHandler) {
                assertEquals("ABC123", aClientHandler.getConnectionId());
                isExecuted = true;
            }
        }

        final ClientHandlerAsyncCallback theClientHandlerAsyncCallback = new ClientHandlerAsyncCallback();
        theRemoteEventServiceFactory.requestClientHandler(theClientHandlerAsyncCallback);

        //The callback should already be executed because here aren't real async calls.
        assertTrue(theClientHandlerAsyncCallback.isExecuted);
    }

    public void testMapEventExecutionService() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();

        ServiceDefTarget theServiceDefTargetMock = EasyMock.createMock(ServiceDefTarget.class);
        
        EasyMock.expect(theServiceDefTargetMock.getServiceEntryPoint()).andReturn("Test-URL");
        theServiceDefTargetMock.setServiceEntryPoint("Test-URL?id=ABC123");

        ClientHandler theClientHandler = new DefaultClientHandler("ABC123");

        EasyMock.replay(theServiceDefTargetMock);
        
        theRemoteEventServiceFactory.registerClientSpecificHandler(theServiceDefTargetMock, theClientHandler);

        EasyMock.verify(theServiceDefTargetMock);
        EasyMock.reset(theServiceDefTargetMock);
    }

    private EventServiceConfigurationTransferable getDefaultConfiguration() {
        return new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, "ABC123", DefaultClientConnector.class.getName());
    }
    
    private class RemoteEventServiceFactoryTestMode extends RemoteEventServiceFactory
    {
        public RemoteEventService getRemoteEventService() {
            return DefaultRemoteEventServiceFactoryTestMode.getInstance().getDefaultRemoteEventService(new DummyRemoteEventConnector());
        }

        private class DummyRemoteEventConnector extends DefaultRemoteEventConnector
        {
            protected void activateStart(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {}

            public void init(AsyncCallback<EventServiceConfigurationTransferable> aCallback) {
                aCallback.onSuccess(getDefaultConfiguration());
            }

            public void deactivate(Set<Domain> aDomains, AsyncCallback<Void> aCallback) {}

            public void deactivate(Domain aDomain, AsyncCallback<Void> aCallback) {}

            public void sendEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback) {}

            public void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {}

            public void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback) {}
        }
    }

    private class DummyClientCommandScheduler implements ClientCommandScheduler
    {
        public void schedule(ClientCommand<?> aCommand) {
            this.schedule(aCommand, 0);
        }

        public void schedule(ClientCommand<?> aCommand, int aDelay) {
            aCommand.execute();
        }
    }
}