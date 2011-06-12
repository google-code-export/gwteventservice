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
package de.novanic.eventservice.clientmock;

import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.DefaultClientConnector;
import de.novanic.eventservice.client.event.Event;
import org.easymock.EasyMock;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler;
import de.novanic.eventservice.client.event.command.ClientCommand;
import org.easymock.IAnswer;

/**
 * @author sstrohschein
 *         <br>Date: 21.10.2008
 *         <br>Time: 21:06:10
 */
public abstract class AbstractRemoteEventServiceMockTest extends TestCase
{
    protected EventServiceAsync myEventServiceAsyncMock;

    public void setUp() {
        myEventServiceAsyncMock = EasyMock.createMock(EventServiceAsync.class);
        ClientCommandSchedulerFactory.getInstance().setClientCommandSchedulerInstance(new DirectCommandScheduler());
    }

    public void tearDown() {
        ClientCommandSchedulerFactory.getInstance().reset();
        ConfigurationTransferableDependentFactory.getInstance(getDefaultConfiguration()).reset(getDefaultConfiguration());
    }

    protected void mockInit() {
        mockInit(getDefaultConfiguration());
    }

    protected void mockInit(final EventServiceConfigurationTransferable aConfiguration) {
        myEventServiceAsyncMock.initEventService(EasyMock.<AsyncCallback<EventServiceConfigurationTransferable>>anyObject());
        EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<EventServiceConfigurationTransferable>(aConfiguration));
    }

    protected void mockInit(TestException aThrowable) {
        myEventServiceAsyncMock.initEventService(EasyMock.<AsyncCallback<EventServiceConfigurationTransferable>>anyObject());
        EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
    }

    protected void mockRegister(Domain aDomain) {
        mockRegister(aDomain, null, null);
    }

    protected void mockRegister(Domain aDomain, TestException aThrowable) {
        mockRegister(aDomain, null, aThrowable);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter) {
        mockRegister(aDomain, anEventFilter, null);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.register(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
        } else {
            myEventServiceAsyncMock.register(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
        }
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter) {
        mockRegisterEventFilter(aDomain, anEventFilter, null);
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.registerEventFilter(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
        } else {
            myEventServiceAsyncMock.registerEventFilter(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
        }
    }

    protected void mockDeregisterEventFilter(Domain aDomain) {
        myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), EasyMock.<AsyncCallback<Void>>anyObject());
        EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
    }

    protected void mockDeregisterEventFilter(Domain aDomain, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
        } else {
            myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
        }
    }

    protected void mockListen() {
        myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops) {
        for(int i = 0; i < aLoops; i++) {
            myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<List<DomainEvent>>(anEvents));
        }
        if(anEvents != null) {
            myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
        }
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops, TestException aTestException) {
        for(int i = 0; i < aLoops - 1; i++) {
            myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aTestException));
        }

        //When no events are available, there will not follow a successful call.
        if(anEvents != null && !anEvents.isEmpty()) {
            myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer(anEvents));

            myEventServiceAsyncMock.listen(EasyMock.<AsyncCallback<List<DomainEvent>>>anyObject());
        }
    }

    protected void mockUnlisten(Set<Domain> aDomains) {
        mockUnlisten(aDomains, null);
    }

    protected void mockUnlisten(Set<Domain> aDomains, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomains), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
        } else {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomains), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
        }
    }

    protected void mockUnlisten(Domain aDomain) {
        mockUnlisten(aDomain, null);
    }

    protected void mockUnlisten(Domain aDomain, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomain), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackThrowableAnswer(aThrowable));
        } else {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomain), EasyMock.<AsyncCallback<Void>>anyObject());
            EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
        }
    }

    protected void mockRegisterUnlistenEvent(UnlistenEvent anUnlistenEvent) {
        myEventServiceAsyncMock.registerUnlistenEvent(EasyMock.eq(UnlistenEventListener.Scope.UNLISTEN), EasyMock.eq(anUnlistenEvent), EasyMock.<AsyncCallback<Void>>anyObject());
        EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
    }

    protected void mockAddEvent(Domain aDomain) {
        myEventServiceAsyncMock.addEvent(EasyMock.eq(aDomain), EasyMock.<Event>anyObject(), EasyMock.<AsyncCallback<Void>>anyObject());
        EasyMock.expectLastCall().andAnswer(new AsyncCallbackAnswer<Void>(null));
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

    private class AsyncCallbackAnswer<R> implements IAnswer
    {
        private R myCallbackResult;

        public AsyncCallbackAnswer(R aCallbackResult) {
            myCallbackResult = aCallbackResult;
        }

        public Object answer() throws Throwable {
            final Object[] theArguments = EasyMock.getCurrentArguments();
            AsyncCallback<R> theAsyncCallback = (AsyncCallback<R>)theArguments[theArguments.length - 1];
            try {
                theAsyncCallback.onSuccess(myCallbackResult);
            } catch(Throwable e) { /* do nothing, because the matcher wouldn't work, when the answer is aborted by an exception */ }
            return null;
        }
    }

    private class AsyncCallbackThrowableAnswer implements IAnswer
    {
        private Throwable myThrowable;

        public AsyncCallbackThrowableAnswer(Throwable aThrowable) {
            myThrowable = aThrowable;
        }

        public Object answer() throws Throwable {
            final Object[] theArguments = EasyMock.getCurrentArguments();
            AsyncCallback<?> theAsyncCallback = (AsyncCallback<?>)theArguments[theArguments.length - 1];
            try {
                theAsyncCallback.onFailure(myThrowable);
            } catch(Throwable e) { /* do nothing, because the matcher wouldn't work, when the answer is aborted by an exception */ }
            return null;
        }
    }
}