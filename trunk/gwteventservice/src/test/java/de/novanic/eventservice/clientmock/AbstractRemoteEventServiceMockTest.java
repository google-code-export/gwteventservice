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

    protected void mockInit(EventServiceConfigurationTransferable aConfiguration) {
        myEventServiceAsyncMock.initEventService(eqAsyncCallback(aConfiguration));
    }

    protected void mockInit(TestException aThrowable) {
        myEventServiceAsyncMock.initEventService(eqAsyncCallbackFailure(aThrowable));
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
            myEventServiceAsyncMock.register(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), eqAsyncCallbackFailure(aThrowable));
        } else {
            myEventServiceAsyncMock.register(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), eqAsyncCallback(null));
        }
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter) {
        mockRegisterEventFilter(aDomain, anEventFilter, null);
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.registerEventFilter(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), eqAsyncCallbackFailure(aThrowable));
        } else {
            myEventServiceAsyncMock.registerEventFilter(EasyMock.eq(aDomain), EasyMock.eq(anEventFilter), eqAsyncCallback(null));
        }
    }

    protected void mockDeregisterEventFilter(Domain aDomain) {
        myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), eqAsyncCallback(null));
    }

    protected void mockDeregisterEventFilter(Domain aDomain, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), eqAsyncCallbackFailure(aThrowable));
        } else {
            myEventServiceAsyncMock.deregisterEventFilter(EasyMock.eq(aDomain), eqAsyncCallback(null));
        }
    }

    protected void mockListen() {
        myEventServiceAsyncMock.listen(eqAsyncNeverEndingCallback());
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops) {
        myEventServiceAsyncMock.listen(eqAsyncListenCallback(anEvents, aLoops));
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops, TestException aTestException) {
        myEventServiceAsyncMock.listen(eqAsyncListenCallback(anEvents, aLoops, aTestException));
    }

    protected void mockUnlisten(Set<Domain> aDomains) {
        mockUnlisten(aDomains, null);
    }

    protected void mockUnlisten(Set<Domain> aDomains, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomains), eqAsyncCallbackFailure(aThrowable));
        } else {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomains), eqAsyncCallback(null));
        }
    }

    protected void mockUnlisten(Domain aDomain) {
        mockUnlisten(aDomain, null);
    }

    protected void mockUnlisten(Domain aDomain, TestException aThrowable) {
        if(aThrowable != null) {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomain), eqAsyncCallbackFailure(aThrowable));
        } else {
            myEventServiceAsyncMock.unlisten(EasyMock.eq(aDomain), eqAsyncCallback(null));
        }
    }

    protected void mockRegisterUnlistenEvent(UnlistenEvent anUnlistenEvent) {
        myEventServiceAsyncMock.registerUnlistenEvent(EasyMock.eq(UnlistenEventListener.Scope.UNLISTEN), EasyMock.eq(anUnlistenEvent), EasyMock.<AsyncCallback<Void>>anyObject());
    }

    protected void mockAddEvent(Domain aDomain) {
        myEventServiceAsyncMock.addEvent(EasyMock.eq(aDomain), EasyMock.<Event>anyObject(), eqAsyncCallback(null));
    }

    protected void mockAddEventUserSpecific() {
        myEventServiceAsyncMock.addEventUserSpecific(EasyMock.<Event>anyObject(), eqAsyncCallback(null));
    }

    private EventServiceConfigurationTransferable getDefaultConfiguration() {
        return new RemoteEventServiceConfigurationTransferable(0, 20000, 90000, null, DefaultClientConnector.class.getName());
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

    protected static AsyncCallback eqAsyncCallback(Object aCallbackResult) {
        EasyMock.reportMatcher(new AsyncCallbackMatcher(aCallbackResult));
        return null;
    }

    protected static AsyncCallback eqAsyncCallbackFailure(Throwable aThrowable) {
        EasyMock.reportMatcher(new AsyncCallbackMatcher(aThrowable));
        return null;
    }

    protected static AsyncCallback eqAsyncNeverEndingCallback() {
        EasyMock.reportMatcher(new AsyncCallbackMatcher(false));
        return null;
    }

    protected static AsyncCallback eqAsyncListenCallback(List<DomainEvent> anEvents, int aListenLoops) {
        return eqAsyncListenCallback(anEvents, aListenLoops, null);
    }

    protected static AsyncCallback eqAsyncListenCallback(List<DomainEvent> anEvents, int aListenLoops, Throwable aThrowable) {
        EasyMock.reportMatcher(new AsyncListenCallbackMatcher(anEvents, aListenLoops, aThrowable));
        return null;
    }
}