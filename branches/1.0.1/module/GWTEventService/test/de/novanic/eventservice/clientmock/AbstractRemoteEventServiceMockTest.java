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

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;
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
    protected MockControl myEventServiceAsyncMockControl;
    protected EventServiceAsync myEventServiceAsyncMock;

    public void setUp() {
        myEventServiceAsyncMockControl = MockControl.createControl(EventServiceAsync.class);
        myEventServiceAsyncMock = (EventServiceAsync)myEventServiceAsyncMockControl.getMock();
        ClientCommandSchedulerFactory.getInstance().setClientCommandSchedulerInstance(new DirectCommandScheduler());
    }

    public void tearDown() {
        myEventServiceAsyncMockControl.reset();
        ClientCommandSchedulerFactory.getInstance().reset();
    }

    protected void mockInit() {
        mockInit(null);
    }

    protected void mockInit(Throwable aThrowable) {
        myEventServiceAsyncMock.initEventService(null);
        if(aThrowable != null) {
            myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
        } else {
            myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher((Object)null));
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockRegister(Domain aDomain, boolean isFirstCall) {
        mockRegister(aDomain, null, null, isFirstCall);
    }

    protected void mockRegister(Domain aDomain, Throwable aThrowable, boolean isFirstCall) {
        mockRegister(aDomain, null, aThrowable, isFirstCall);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter, boolean isFirstCall) {
        mockRegister(aDomain, anEventFilter, null, isFirstCall);
    }

    protected void mockRegister(Domain aDomain, EventFilter anEventFilter, Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.register(aDomain, anEventFilter, null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter, boolean isFirstCall) {
        mockRegisterEventFilter(aDomain, anEventFilter, null, isFirstCall);
    }

    protected void mockRegisterEventFilter(Domain aDomain, EventFilter anEventFilter, Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.registerEventFilter(aDomain, anEventFilter, null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockDeregisterEventFilter(Domain aDomain, boolean isFirstCall) {
        myEventServiceAsyncMock.deregisterEventFilter(aDomain, null);
        if(isFirstCall) {
            myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockDeregisterEventFilter(Domain aDomain, Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.deregisterEventFilter(aDomain, null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockListen(boolean isFirstCall) {
        mockListen(null, isFirstCall);
    }

    protected void mockListen(Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.listen(null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(false));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockListen(List<DomainEvent> anEvents, int aLoops) {
        myEventServiceAsyncMock.listen(null);
        if(aLoops > 0) {
            myEventServiceAsyncMockControl.setMatcher(new AsyncListenCallArgumentsMatcher(anEvents, aLoops));
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockUnlisten(Set<Domain> aDomains, boolean isFirstCall) {
        mockUnlisten(aDomains, null, isFirstCall);
    }

    protected void mockUnlisten(Set<Domain> aDomains, Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.unlisten(aDomains, null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected void mockUnlisten(Domain aDomain, boolean isFirstCall) {
        mockUnlisten(aDomain, null, isFirstCall);
    }

    protected void mockUnlisten(Domain aDomain, Throwable aThrowable, boolean isFirstCall) {
        myEventServiceAsyncMock.unlisten(aDomain, null);
        if(isFirstCall) {
            if(aThrowable != null) {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(aThrowable));
            } else {
                myEventServiceAsyncMockControl.setMatcher(new AsyncCallArgumentsMatcher(true));
            }
        }
        myEventServiceAsyncMockControl.setVoidCallable();
    }

    protected class AsyncCallArgumentsMatcher implements ArgumentsMatcher
    {
        private Object myCallbackResult;
        private Throwable myCallbackThrowable;
        private boolean myIsCall;

        public AsyncCallArgumentsMatcher(boolean isCall) {
            myIsCall = isCall;
        }

        public AsyncCallArgumentsMatcher(Object aCallbackResult) {
            myIsCall = true;
            myCallbackResult = aCallbackResult;
        }

        public AsyncCallArgumentsMatcher(Throwable aCallbackThrowable) {
            myCallbackThrowable = aCallbackThrowable;
        }

        public boolean matches(Object[] anObjects, Object[] anObjects_2) {
            MockArguments theMockArguments = new MockArguments(anObjects, anObjects_2);
            boolean isValid = theMockArguments.isValid();
            if(isValid) {
                runCall(theMockArguments.extractCallback());
            }
            return isValid;
        }

        public String toString(Object[] anObjects) {
            return MockControl.ARRAY_MATCHER.toString(anObjects);
        }

        protected void runCall(final AsyncCallback<? super Object> anAsyncCallback) {
            if(anAsyncCallback != null) {
                if(myIsCall) {
                    anAsyncCallback.onSuccess(myCallbackResult);
                } else if(myCallbackThrowable != null) {
                    anAsyncCallback.onFailure(myCallbackThrowable);
                }
            }
        }

        protected class MockArguments
        {
            private Object[] myExpected;
            private Object[] myRealCall;

            private MockArguments(Object[] aExpected, Object[] aRealCall) {
                myExpected = aExpected;
                myRealCall = aRealCall;
            }

            public AsyncCallback<? super Object> extractCallback() {
                int theSize = myRealCall.length;
                if(theSize > 0) {
                    //the last attribute should be the AsyncCallback-Argument
                    return (AsyncCallback<? super Object>)myRealCall[theSize - 1];
                }
                return null;
            }

            public boolean isValid() {
                boolean isValid = myExpected.length == myRealCall.length;
                if(isValid) {
                    //-1 because the last attribute should be the AsyncCallback-Argument
                    for(int i = 0; i < myRealCall.length - 1; i++) {
                        if(!isEqual(myExpected[i], myRealCall[i])) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            private boolean isEqual(Object anObject, Object anObject_2) {
                if(anObject != null && anObject_2 != null) {
                    return anObject.equals(anObject_2);
                } else if(anObject == null && anObject_2 == null) {
                    return true;
                }
                return false;
            }
        }
    }

    protected class AsyncListenCallArgumentsMatcher extends AsyncCallArgumentsMatcher
    {
        private int myExpectedLoops;
        private int myLoops;

        public AsyncListenCallArgumentsMatcher(boolean isCall) {
            super(isCall);
        }

        public AsyncListenCallArgumentsMatcher(List<DomainEvent> aCallbackResult, int aExpectedLoops) {
            super(aCallbackResult);
            myExpectedLoops = aExpectedLoops;
        }

        public AsyncListenCallArgumentsMatcher(Throwable aCallbackThrowable) {
            super(aCallbackThrowable);
        }

        protected void runCall(AsyncCallback<? super Object> anAsyncCallback) {
            if(myExpectedLoops > myLoops) {
                myLoops++;
                super.runCall(anAsyncCallback);
            }
        }
    }

    protected static class TestException extends Exception
    {
        public TestException() {
            super("test_exception");
        }

        public static TestException getInstance() {
            return new TestException();
        }
    }

    protected class RecordedCallback implements AsyncCallback<Void>
    {
        private boolean myIsOnSuccessCalled;
        private boolean myIsOnFailureCalled;

        public void onSuccess(Void aResult) {
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
}