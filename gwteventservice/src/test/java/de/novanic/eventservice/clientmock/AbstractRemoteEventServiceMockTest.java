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

import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.event.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.Set;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler;
import de.novanic.eventservice.client.event.command.ClientCommand;
import org.junit.After;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 21.10.2008
 *         <br>Time: 21:06:10
 */
public abstract class AbstractRemoteEventServiceMockTest
{
    protected EventServiceAsync myEventServiceAsyncMock;

    @Before
    public void setUp() {
        myEventServiceAsyncMock = mock(EventServiceAsync.class, withSettings().extraInterfaces(ServiceDefTarget.class));
        ClientCommandSchedulerFactory.getInstance().setClientCommandSchedulerInstance(new DirectCommandScheduler());
    }

    @After
    public void tearDown() {
        ClientCommandSchedulerFactory.getInstance().reset();
        ConfigurationTransferableDependentFactory.reset();
        ConfigurationTransferableDependentFactory.getInstance(getDefaultConfiguration());
        reset(myEventServiceAsyncMock);
    }

    protected void mockInit() {
        mockInit(getDefaultConfiguration());
    }

    protected void mockInit(final EventServiceConfigurationTransferable aConfiguration) {
        doAnswer(new AsyncCallbackAnswer<EventServiceConfigurationTransferable>(aConfiguration)).when(myEventServiceAsyncMock).initEventService(any(AsyncCallback.class));
    }

    protected void mockInit(Throwable aThrowable) {
        doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).initEventService(any(AsyncCallback.class));
    }

    protected void mockRegister(Domain aDomain) {
        mockRegister(aDomain, null, null);
    }

    protected void mockRegister(Domain aDomain, Throwable aThrowable) {
        mockRegister(aDomain, null, aThrowable);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter) {
        mockRegister(aDomain, anEventFilter, null);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter, Throwable aThrowable) {
        if(aThrowable != null) {
            doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).register(eq(aDomain), eq(anEventFilter), any(AsyncCallback.class));
        } else {
            doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).register(eq(aDomain), eq(anEventFilter), any(AsyncCallback.class));
        }
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter) {
        mockRegisterEventFilter(aDomain, anEventFilter, null);
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter, Throwable aThrowable) {
        if(aThrowable != null) {
            doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).registerEventFilter(eq(aDomain), eq(anEventFilter), any(AsyncCallback.class));
        } else {
            doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).registerEventFilter(eq(aDomain), eq(anEventFilter), any(AsyncCallback.class));
        }
    }

    protected void mockDeregisterEventFilter(Domain aDomain) {
        doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).deregisterEventFilter(eq(aDomain), any(AsyncCallback.class));
    }

    protected void mockDeregisterEventFilter(Domain aDomain, Throwable aThrowable) {
        if(aThrowable != null) {
            doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).deregisterEventFilter(eq(aDomain), any(AsyncCallback.class));
        } else {
            doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).deregisterEventFilter(eq(aDomain), any(AsyncCallback.class));
        }
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops) {
        int i = 0;
        Stubber theStubber = null;
        do {
            if(theStubber == null) {
                theStubber = doAnswer(new AsyncCallbackAnswer<List<DomainEvent>>(anEvents));
            } else {
                theStubber.doAnswer(new AsyncCallbackAnswer<List<DomainEvent>>(anEvents));
            }
        } while(++i < aLoops);
        theStubber.doNothing().when(myEventServiceAsyncMock).listen(any(AsyncCallback.class));
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops, Throwable aTestException) {
        int i = 0;
        Stubber theStubber = null;
        do {
            if(theStubber == null) {
                theStubber = doAnswer(new AsyncCallbackThrowableAnswer(aTestException));
            } else {
                theStubber.doAnswer(new AsyncCallbackThrowableAnswer(aTestException));
            }
        } while(++i < aLoops);
        theStubber.doNothing().when(myEventServiceAsyncMock).listen(any(AsyncCallback.class));

        //When no events are available, there will not follow a successful call.
        if(aLoops > 0 && anEvents != null && !anEvents.isEmpty()) {
            doAnswer(new AsyncCallbackAnswer(anEvents)).doNothing().when(myEventServiceAsyncMock).listen(any(AsyncCallback.class));
        }
    }

    protected void mockUnlisten(Set<Domain> aDomains) {
        mockUnlisten(aDomains, null);
    }

    protected void mockUnlisten(Set<Domain> aDomains, Throwable aThrowable) {
        if(aThrowable != null) {
            doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).unlisten(eq(aDomains), any(AsyncCallback.class));
        } else {
            doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).unlisten(eq(aDomains), any(AsyncCallback.class));
        }
    }

    protected void mockUnlisten(Domain aDomain) {
        mockUnlisten(aDomain, null);
    }

    protected void mockUnlisten(Domain aDomain, Throwable aThrowable) {
        if(aThrowable != null) {
            doAnswer(new AsyncCallbackThrowableAnswer(aThrowable)).when(myEventServiceAsyncMock).unlisten(eq(aDomain), any(AsyncCallback.class));
        } else {
            doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).unlisten(eq(aDomain), any(AsyncCallback.class));
        }
    }

    protected void mockRegisterUnlistenEvent(UnlistenEvent anUnlistenEvent) {
        doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).registerUnlistenEvent(eq(UnlistenEventListener.Scope.UNLISTEN), eq(anUnlistenEvent), any(AsyncCallback.class));
    }

    protected void mockAddEvent(Domain aDomain) {
        doAnswer(new AsyncCallbackAnswer<Void>(null)).when(myEventServiceAsyncMock).addEvent(eq(aDomain), any(Event.class), any(AsyncCallback.class));
    }

    private EventServiceConfigurationTransferable getDefaultConfiguration() {
        return new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, 2, null, DefaultClientConnector.class.getName());
    }

    protected static class TestException extends Exception
    {
        public TestException() {
            super("test_exception");
        }
    }

    protected class RecordedCallback implements AsyncCallback
    {
        private boolean myIsOnSuccessCalled;
        private boolean myIsOnFailureCalled;

        public void onSuccess(Object aResult) {
            if(myIsOnSuccessCalled) {
                throw new RuntimeException("onSuccess was called more than one time!");
            } else if(myIsOnFailureCalled) {
                throw new RuntimeException("onSuccess and onFailure were called on the same callback!");
            }
            myIsOnSuccessCalled = true;
        }

        public void onFailure(Throwable aThrowable) {
            if(myIsOnFailureCalled) {
                throw new RuntimeException("onFailure was called more than one time!");
            } else if(myIsOnSuccessCalled) {
                throw new RuntimeException("onSuccess and onFailure were called on the same callback!");
            }
            myIsOnFailureCalled = true;
        }

        public boolean isOnSuccessCalled() {
            return myIsOnSuccessCalled;
        }

        public boolean isOnFailureCalled() {
            return myIsOnFailureCalled;
        }
    }

    private static class DirectCommandScheduler implements ClientCommandScheduler
    {
        public void schedule(ClientCommand aCommand) {
            schedule(aCommand, 0);
        }

        public void schedule(ClientCommand aCommand, int aDelay) {
            aCommand.execute();
        }
    }

    private class AsyncCallbackAnswer<R> implements Answer
    {
        private R myCallbackResult;

        public AsyncCallbackAnswer(R aCallbackResult) {
            myCallbackResult = aCallbackResult;
        }

        public Object answer(InvocationOnMock anInvocation) throws Throwable {
            final Object[] theArguments = anInvocation.getArguments();
            AsyncCallback<R> theAsyncCallback = (AsyncCallback<R>)theArguments[theArguments.length - 1];
            if(theAsyncCallback != null) {
                theAsyncCallback.onSuccess(myCallbackResult);
            }
            return null;
        }
    }

    private class AsyncCallbackThrowableAnswer implements Answer
    {
        private Throwable myThrowable;

        public AsyncCallbackThrowableAnswer(Throwable aThrowable) {
            myThrowable = aThrowable;
        }

        public Object answer(InvocationOnMock anInvocation) throws Throwable {
            final Object[] theArguments = anInvocation.getArguments();
            AsyncCallback<?> theAsyncCallback = (AsyncCallback<?>)theArguments[theArguments.length - 1];
            if(theAsyncCallback != null) {
                theAsyncCallback.onFailure(myThrowable);
            }
            return null;
        }
    }
}