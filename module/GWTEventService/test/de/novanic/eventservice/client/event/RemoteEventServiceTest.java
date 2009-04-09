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

import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Timer;

/**
 * @author sstrohschein
 * Date: 20.07.2008
 * Time: 13:53:49
 */
public class RemoteEventServiceTest extends GWTTestCase
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");

    private RemoteEventService myRemoteEventService;
    private EventServiceAsync myEventService;

    public void gwtSetUp() throws Exception {
        super.gwtSetUp();
        final RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        RemoteEventServiceFactory.reset();
        myRemoteEventService = theRemoteEventServiceFactory.getRemoteEventService();
        assertFalse(myRemoteEventService.isActive());

        myEventService = (EventServiceAsync)getService(GWT.create(EventService.class), "gwteventservice");
    }

    public String getModuleName() {
        return "de.novanic.eventservice.GWTEventService";
    }

    public void testAddListener() {
        final DummyEvent theTestEvent = new DummyEvent();

        //start listen
        final TestEventListener theTestListener = new TestEventListener();
        myRemoteEventService.addListener(TEST_DOMAIN, theTestListener);
        assertEquals(0, theTestListener.getEventCount(DummyEvent.class.getName()));

        new Timer() {
            public void run() {
                assertTrue(myRemoteEventService.isActive());
                
                //add event
                myEventService.addEvent(TEST_DOMAIN, theTestEvent, new TestCallback() {
                    public void onSuccess(Object anObject) {
                        new Timer() {
                            public void run() {
                                //check event count
                                assertTrue(myRemoteEventService.isActive());
                                assertEquals(1, theTestListener.getEventCount(DummyEvent.class.getName()));

                                new Timer() {
                                    public void run() {
                                        //add second event
                                        myEventService.addEvent(TEST_DOMAIN, theTestEvent, new TestCallback() {
                                            public void onSuccess(Object anObject) {

                                                new Timer() {
                                                    public void run() {
                                                        //check event count
                                                        assertTrue(myRemoteEventService.isActive());
                                                        assertEquals(2, theTestListener.getEventCount(DummyEvent.class.getName()));
                                                        finishTest();
                                                    }
                                                }.schedule(2000);
                                            }
                                        });
                                    }
                                }.schedule(2000);
                            }
                        }.schedule(2000);
                    }
                });
            }
        }.schedule(2000);

        delayTestFinish(20000);
    }

    public void testRemoveListener_Callback() {
        final DummyEvent theTestEvent = new DummyEvent();

        //1. start listen
        final TestEventListener theTestListener = new TestEventListener();
        myRemoteEventService.addListener(TEST_DOMAIN, theTestListener, new TestCallback() {
            public void onSuccess(Object anObject) {
                theTestListener.setListener(new RemoteEventListener() {
                    public void apply(Event anEvent) {
                        //check event count
                        assertTrue(myRemoteEventService.isActive());
                        assertEquals(1, theTestListener.getEventCount(DummyEvent.class.getName()));

                        new Timer() {
                            public void run() {
                                //3. remove listener
                                myRemoteEventService.removeListener(TEST_DOMAIN, theTestListener, new TestCallback() {
                                    public void onSuccess(Object anObject) {
                                        assertFalse(myRemoteEventService.isActive());

                                        //4. add ignored event
                                        myEventService.addEvent(TEST_DOMAIN, theTestEvent, new TestCallback() {
                                            public void onSuccess(Object anObject) {
                                                new Timer() {
                                                    public void run() {

                                                        //check event count
                                                        assertFalse(myRemoteEventService.isActive());
                                                        assertEquals(1, theTestListener.getEventCount(DummyEvent.class.getName()));

                                                        finishTest();
                                                    }
                                                }.schedule(3000);
                                            }
                                        });
                                    }
                                });
                            }
                        }.schedule(6000);
                    }
                });
                //2. add event
                myEventService.addEvent(TEST_DOMAIN, theTestEvent, new TestCallback());
            }
        });

        delayTestFinish(20000);
    }

    private ServiceDefTarget getService(Object aService, String aServiceMappingName) {
        String theServiceURL = GWT.getModuleBaseURL() + aServiceMappingName;
        ServiceDefTarget theServiceEndPoint = (ServiceDefTarget)aService;
        theServiceEndPoint.setServiceEntryPoint(theServiceURL);
        return theServiceEndPoint;
    }

    private class TestCallback implements AsyncCallback
    {
        public void onFailure(Throwable aThrowable) {
            fail("Error occurred: " + aThrowable);
        }

        public void onSuccess(Object anObject) {}
    }
}