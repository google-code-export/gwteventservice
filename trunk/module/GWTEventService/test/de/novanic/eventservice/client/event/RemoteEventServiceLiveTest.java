/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listen.UnlistenEvent;

import java.util.*;

/**
 * @author sstrohschein
 *         <br>Date: 02.03.2009
 *         <br>Time: 16:24:55
 */
public abstract class RemoteEventServiceLiveTest extends GWTTestCase
{
    protected static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    protected static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    protected static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    protected RemoteEventService myRemoteEventService;
    protected EventServiceAsync myEventService;
    private Queue<TestAction> myTestActions;
    private TestListener myGlobalListener;

    public void gwtSetUp() throws Exception {
        myTestActions = new LinkedList<TestAction>();
        myGlobalListener = new TestListener();
        final RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        myRemoteEventService = theRemoteEventServiceFactory.getRemoteEventService();
        assertFalse(myRemoteEventService.isActive());

        myEventService = (EventServiceAsync)getService(GWT.create(EventService.class), "gwteventservice");
    }

    protected void gwtTearDown() throws Exception {
        RemoteEventServiceFactory.reset();
    }

    public String getModuleName() {
        return "de.novanic.eventservice.GWTEventService";
    }

    protected void addAction(TestAction aTestAction) {
        myTestActions.add(aTestAction);
    }

    protected void executeActions() {
        executeActions(20000);
    }

    protected void executeActions(int aTimeoutMilliseconds) {
        //add tearDown actions
        addAction(new TestAction() {
            void execute() {
                if(myRemoteEventService.isActive()) {
                    myRemoteEventService.removeListeners(getCallback());
                }
            }
        });
        addAction(new TestAction() {
            void execute() {
                myEventService.addEvent(TEST_DOMAIN, new UnlistenEvent(), getCallback());
            }
        });
        addAction(new TestAction() {
            void execute() {
                myEventService.addEvent(TEST_DOMAIN_2, new UnlistenEvent(), getCallback());
            }
        });
        addAction(new TestAction() {
            void execute() {
                myEventService.addEvent(TEST_DOMAIN_3, new UnlistenEvent(), getCallback());
            }
        });

        executeNextAction();
        delayTestFinish(aTimeoutMilliseconds);
    }

    private void executeNextAction() {
        if(!myTestActions.isEmpty()) {
            myTestActions.poll().autoExecute();
        } else {
            finishTest();
        }
    }

    private ServiceDefTarget getService(Object aService, String aServiceMappingName) {
        String theServiceURL = GWT.getModuleBaseURL() + aServiceMappingName;
        ServiceDefTarget theServiceEndPoint = (ServiceDefTarget)aService;
        theServiceEndPoint.setServiceEntryPoint(theServiceURL);
        return theServiceEndPoint;
    }

    protected class TestCallback implements AsyncCallback<Void>
    {
        private boolean isExecuteNextAction;

        protected TestCallback(boolean isExecuteNextAction) {
            this.isExecuteNextAction = isExecuteNextAction;
        }

        public void onFailure(Throwable aThrowable) {
            fail("Error occurred: " + aThrowable);
        }

        public void onSuccess(Void anObject) {
            if(isExecuteNextAction) {
                executeNextAction();
            }
        }
    }

    private class TestListener implements RemoteEventListener
    {
        private Map<String, Integer> myEventMap;

        public TestListener() {
            myEventMap = new HashMap<String, Integer>();
        }

        public void apply(Event anEvent) {
            addEvent(anEvent);
            executeNextAction();
        }

        public int getEventCount() {
            int theCount = 0;
            for(Integer theEventCount: myEventMap.values()) {
                theCount += theEventCount;
            }
            return theCount;
        }

        public int getEventCount(String anEventType) {
            Integer theCount = getEventCountInternal(anEventType);
            if(theCount == null) {
                theCount = 0;
            }
            return theCount;
        }

        private void addEvent(Event anEvent) {
            Integer theEventCount = getEventCountInternal(anEvent.getClass().getName());
            if(theEventCount == null || theEventCount == 0) {
                myEventMap.put(anEvent.getClass().getName(), 1);
            } else {
                myEventMap.put(anEvent.getClass().getName(), theEventCount + 1);
            }
        }

        private Integer getEventCountInternal(String anEventType) {
            return myEventMap.get(anEventType);
        }
    }

    protected abstract class TestAction
    {
        private TestCallback myCallback;
        private boolean isWaitForEvent;

        protected TestAction() {}

        protected TestAction(boolean isWaitForEvent) {
            this.isWaitForEvent = isWaitForEvent;
        }

        public TestCallback getCallback() {
            if(myCallback == null) {
                myCallback = new TestCallback(!isWaitForEvent);
            } else {
                throw new AssertionError("getCallback() was called two times! Only one Callback per TestAction is allowed.");
            }
            return myCallback;
        }

        public RemoteEventListener getListener() {
            return myGlobalListener;
        }

        public EventFilter getEventFilter(Class anIgnoredEventClass) {
            return new TestTypeEventFilter(anIgnoredEventClass);
        }

        protected void autoExecute() {
            execute();
            //when a callback is available, the callback will execute the next action when the callback is finished
            if(myCallback == null && !isWaitForEvent) {
                executeNextAction();
            }
        }

        abstract void execute();

        public int getEventCount() {
            return myGlobalListener.getEventCount();
        }

        public int getEventCount(String anEventType) {
            return myGlobalListener.getEventCount(anEventType);
        }
    }
}