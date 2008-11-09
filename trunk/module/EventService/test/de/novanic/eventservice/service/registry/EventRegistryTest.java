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
package de.novanic.eventservice.service.registry;

import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.client.event.listen.UnlistenEvent;
import de.novanic.eventservice.service.testhelper.TestEventFilter;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import de.novanic.eventservice.EventServiceServerThreadingTest;

import java.util.Set;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author sstrohschein
 * Date: 28.07.2008
 * <br>Time: 22:49:33
 */
public class EventRegistryTest extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USER_ID_2 = "test_user_id_2";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");

    private EventRegistry myEventRegistry;

    public void setUp() {
        setUp(new RemoteEventServiceConfiguration(0, 500, 2500));
        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        EventRegistryFactory.reset();
        EventExecutorServiceFactory.reset();
        Thread.sleep(500);
    }

    public void testRegisterUser() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    public void testRegisterUser_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    public void testListen() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListenError() throws Exception {
        assertNull(myEventRegistry.listen("noKnownUser"));
    }

    public void testListenError_2() throws Exception {
        assertNull(myEventRegistry.listen(TEST_USER_ID));

        //test without interrupt
        Date theStartTime = new Date();

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());

        Date theEndTime = new Date();
        assertTrue((theEndTime.getTime() - theStartTime.getTime()) >= 500);

        //test with interrupt
        theStartTime = new Date();

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        Thread.currentThread().interrupt();
        assertNull(myEventRegistry.listen(TEST_USER_ID));

        theEndTime = new Date();
        assertFalse((theEndTime.getTime() - theStartTime.getTime()) >= 500);
    }

    public void testListen_Multi() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        startAddEvent(TEST_DOMAIN, 500);
        startAddEvent(TEST_DOMAIN, 600);
        Thread.sleep(500);
        
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).contains(TEST_DOMAIN));
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        //The 6th event can not received, because it is added after the max waiting time.
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).contains(TEST_DOMAIN));
        assertTrue(theResult > 0 && theResult < 6);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(6, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_Domain() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 200);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(TEST_USER_ID).size());
        
        Thread.sleep(600);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_Domain_Isolation() throws Exception {
        EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration(0, 2000, 9999);
        tearDownEventServiceConfiguration();
        setUp(theEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        startAddEvent(TEST_DOMAIN, 500);

        Thread.sleep(700);

        final ListenCallable theListenCallable = new ListenCallable(TEST_USER_ID);
        final FutureTask<ListenResult> theFutureTask = new FutureTask<ListenResult>(theListenCallable);

        final ListenCallable theListenCallable_2 = new ListenCallable(TEST_USER_ID_2);
        final FutureTask<ListenResult> theFutureTask_2 = new FutureTask<ListenResult>(theListenCallable_2);

        new Thread(theFutureTask).start();
        new Thread(theFutureTask_2).start();

        ListenResult theListenResult = theFutureTask.get();
        ListenResult theListenResult_2 = theFutureTask_2.get();

        assertEquals(1, theListenResult.getEvents().size());
        assertEquals(0, theListenResult_2.getEvents().size());

        //the ListenCallable_2 shouldn't abort when another user of another domain gets an event
        final long theRunningTime = theListenResult.getRunningTimeMillis();
        final long theRunningTime_2 = theListenResult_2.getRunningTimeMillis();
        assertTrue(theRunningTime_2 > theRunningTime);
        assertTrue(theRunningTime_2 >= theEventServiceConfiguration.getMaxWaitingTime());
    }

    public void testListen_Domain_Multi() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN_2, 400);
        startAddEvent(TEST_DOMAIN_2, 200);
        startAddEvent(TEST_DOMAIN, 600);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        //The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 6);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(6, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_Domain_Multi_2() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN_2, 400);
        startAddEvent(TEST_DOMAIN_2, 200);
        startAddEvent(TEST_DOMAIN, 600);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        //max two events for TEST_DOMAIN. The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 3);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(3, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());

        //the user is added too late to get the events
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_Domain_Multi_3() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        startAddEvent(TEST_DOMAIN_2, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN_2, 400);
        startAddEvent(TEST_DOMAIN_2, 500);
        startAddEvent(TEST_DOMAIN, 600);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        //max two events for TEST_DOMAIN. The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 8);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(8, theResult);

        //the user isn't out of time / removed and can get all events with the next listen
        assertEquals(3, myEventRegistry.listen(TEST_USER_ID_2).size());

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID_2).size());
    }

    public void testListen_EventFilter() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_EventFilter_2() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, new EmptyEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because another EventFilter was set (the EventFilter doesn't filter events)
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_EventFilter_3() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());

        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because the EventFilter was removed
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testListen_EventFilter_4() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());

        //set event filter to NULL should have the same effect as removeEventFilter
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because the EventFilter was removed
        Thread.sleep(600);
        theResult = theResult + myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testAddUserSpecificEvent() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        Thread theAddEventThread = startAddEvent(TEST_USER_ID, 100);

        theAddEventThread.join();

        assertEquals(0, myEventRegistry.listen(TEST_USER_ID_2).size());
        //only User 1 should get the event
        assertEquals(1, myEventRegistry.listen(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID_2).size());

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID_2).size());
    }

    public void testAddUserSpecificEvent_Isolation() throws Exception {
        EventServiceConfiguration theEventServiceConfiguration = new RemoteEventServiceConfiguration(0, 2000, 9999);
        tearDownEventServiceConfiguration();
        setUp(theEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        startAddEvent(TEST_USER_ID, 500);

        Thread.sleep(700);

        final ListenCallable theListenCallable = new ListenCallable(TEST_USER_ID);
        final FutureTask<ListenResult> theFutureTask = new FutureTask<ListenResult>(theListenCallable);
        theFutureTask.run();

        final ListenCallable theListenCallable_2 = new ListenCallable(TEST_USER_ID_2);
        final FutureTask<ListenResult> theFutureTask_2 = new FutureTask<ListenResult>(theListenCallable_2);
        theFutureTask_2.run();

        ListenResult theListenResult = theFutureTask.get();
        ListenResult theListenResult_2 = theFutureTask_2.get();

        assertEquals(1, theListenResult.getEvents().size());
        assertEquals(0, theListenResult_2.getEvents().size());

        //the ListenCallable_2 shouldn't abort when an other user of the same domain gets a user specific event
        final long theRunningTime = theListenResult.getRunningTimeMillis();
        final long theRunningTime_2 = theListenResult_2.getRunningTimeMillis();
        assertTrue(theRunningTime_2 > theRunningTime);
        assertTrue(theRunningTime_2 >= theEventServiceConfiguration.getMaxWaitingTime());
    }

    public void testTimeOut() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN, 100);
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        Thread.sleep(700);

        List<DomainEvent> theEvents = myEventRegistry.listen(TEST_USER_ID);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        //set the default waiting time greater than time out time to produce a time out
        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        EventServiceConfiguration theEventServiceConfiguration = theEventRegistry.getConfiguration();

        final int theNewMaxWaitingTime = theEventServiceConfiguration.getTimeoutTime() + 100;
        EventServiceConfiguration theNewEventServiceConfiguration = new RemoteEventServiceConfiguration(
                theEventServiceConfiguration.getMinWaitingTime(),
                theNewMaxWaitingTime,
                theEventServiceConfiguration.getTimeoutTime());

        tearDownEventServiceConfiguration();
        setUp(theNewEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        theEvents = myEventRegistry.listen(TEST_USER_ID);
        assertNull(theEvents);
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
    }

    private class EmptyEventFilter implements EventFilter
    {
        public boolean match(Event anEvent) {
            return false;
        }
    }

    private class ListenCallable implements Callable<ListenResult>
    {
        private String myUser;
        private Calendar myStartTime;

        public ListenCallable(String aUser) {
            myUser = aUser;
            myStartTime = Calendar.getInstance();
        }

        public ListenResult call() throws Exception {
            List<DomainEvent> theEvents = myEventRegistry.listen(myUser);
            long theRunningTimeMillis = getRunningTimeMillis();
            return new ListenResult(theEvents, theRunningTimeMillis);
        }

        private long getRunningTimeMillis() {
            final long theStartTimeMillis = myStartTime.getTimeInMillis();
            final long theCurrentMillis = Calendar.getInstance().getTimeInMillis();
            return (theCurrentMillis - theStartTimeMillis);
        }
    }

    private class ListenResult
    {
        private List<DomainEvent> myEvents;
        private long myRunningTimeMillis;

        private ListenResult(List<DomainEvent> anEvents, long aRunningTimeMillis) {
            myEvents = anEvents;
            myRunningTimeMillis = aRunningTimeMillis;
        }

        public List<DomainEvent> getEvents() {
            return myEvents;
        }

        public long getRunningTimeMillis() {
            return myRunningTimeMillis;
        }
    }
}