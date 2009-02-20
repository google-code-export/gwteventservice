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

import de.novanic.eventservice.EventServiceServerThreadingTest;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.ListenStartResult;
import de.novanic.eventservice.test.testhelper.ListenCycleCancelEvent;
import de.novanic.eventservice.test.testhelper.TestEventFilter;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 * <br>Date: 23.08.2008
 * <br>Time: 00:35:00
 */
public class EventRegistry_ExtremeThreadingTest extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USER_ID_2 = "test_user_id_2";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    private EventRegistry myEventRegistry;

    public void setUp() {
        setUp(createConfiguration(0, 30000, 90000));
        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();

        super.setUp(myEventRegistry);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        Thread.sleep(500); //waiting between the test cases is needed to avoid conflicts between the test cases/scenarios
        tearDownEventServiceConfiguration();

        myEventRegistry.unlisten(TEST_USER_ID);
        myEventRegistry.unlisten(TEST_USER_ID_2);
        EventRegistryFactory.reset();
        EventExecutorServiceFactory.reset();
        Thread.sleep(500); //waiting between the test cases is needed to avoid conflicts between the test cases/scenarios
    }

    /**
     * Adding 15000 events and a single listen at the end (without threads).
     * @throws Exception
     */
    public void testListen_Extreme() throws Exception {
        checkListen_Extreme(false);
        assertEquals(5000, getEventCount(TEST_DOMAIN));
        assertEquals(5000, getEventCount(TEST_DOMAIN_2));
        assertEquals(5000, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 15000 events and a single listen at the end (without threads).
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_Extreme_UserSpecific() throws Exception {
        checkListen_Extreme(true);
        assertEquals(15000, getEventCount(TEST_USER_ID));
        assertEquals(0, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 15000 events and a single listen at the end (without threads).
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_Extreme(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        for(int i = 0; i < 5000; i++) {
            addEvent(TEST_USER_ID, TEST_DOMAIN_2, new DummyEvent(), isUserSpecific);
            addEvent(TEST_USER_ID, TEST_DOMAIN, new DummyEvent(), isUserSpecific);
            addEvent(TEST_USER_ID, TEST_DOMAIN_3, new DummyEvent(), isUserSpecific);
            assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        }

        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);
        assertEquals(15000, getEventCount());

        checkEventSequence();
    }

    /**
     * Adding 10000 events and a single listen at the end (without threads). The user is not registered for all domains.
     * @throws Exception
     */
    public void testListen_Extreme_2() throws Exception {
        checkListen_Extreme_2(false);
        assertEquals(10000, getEventCount());
        assertEquals(5000, getEventCount(TEST_DOMAIN));
        assertEquals(5000, getEventCount(TEST_DOMAIN_2));
        assertEquals(0, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 10000 events and a single listen at the end (without threads). The user is not registered for all domains.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_Extreme_2_UserSpecific() throws Exception {
        checkListen_Extreme_2(true);
        //more events than checkListen_Extreme_2, because the user isn't registered for TEST_DOMAIN_3, but unimportant
        //for user-specific tests.
        assertEquals(15000, getEventCount());
        assertEquals(15000, getEventCount(TEST_USER_ID));
        assertEquals(0, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 10000 events and a single listen at the end (without threads). The user is not registered for all domains.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_Extreme_2(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        for(int i = 0; i < 5000; i++) {
            addEvent(TEST_USER_ID, TEST_DOMAIN_2, new DummyEvent(), isUserSpecific);
            addEvent(TEST_USER_ID, TEST_DOMAIN, new DummyEvent(), isUserSpecific);
            addEvent(TEST_USER_ID, TEST_DOMAIN_3, new DummyEvent(), isUserSpecific, false);
            assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        }

        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);

        checkEventSequence();
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end.
     * @throws Exception
     */
    public void testListen_ExtremeThreading() throws Exception {
        checkListen_ExtremeThreading(false);
        assertEquals(1500, getEventCount(TEST_DOMAIN));
        assertEquals(1500, getEventCount(TEST_DOMAIN_2));
        assertEquals(1500, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_UserSpecific() throws Exception {
        checkListen_ExtremeThreading(true);
        assertEquals(4500, getEventCount(TEST_USER_ID));
        assertEquals(0, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        for(int i = 0; i < 500; i++) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 1, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 2, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 4, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 3, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 5, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 6, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 8, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 7, isUserSpecific);
            assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        }

        joinEventThreads();
        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);
        assertEquals(4500, getEventCount());
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. Test with different users.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_2() throws Exception {
        checkListen_ExtremeThreading_2(false);
        assertEquals(1500, getEventCount(TEST_DOMAIN));
        assertEquals(1500, getEventCount(TEST_DOMAIN_2));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. Test with different users.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_2_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_2(true);
        assertEquals(3000, getEventCount(TEST_USER_ID));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. Test with different users.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_2(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID_2, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        for(int i = 0; i < 500; i++) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 1, isUserSpecific);
            startAddEvent(TEST_USER_ID_2, TEST_DOMAIN_3, 2, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 4, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 3, isUserSpecific);
            startAddEvent(TEST_USER_ID_2, TEST_DOMAIN_3, 5, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 6, isUserSpecific);
            startAddEvent(TEST_USER_ID_2, TEST_DOMAIN_3, 8, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 7, isUserSpecific);
            assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
            assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());
        }

        joinEventThreads();
        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);

        assertEquals(3000, getEventCount());
        if(!isUserSpecific) {
            assertEquals(1500, getEventCount(TEST_DOMAIN));
            assertEquals(1500, getEventCount(TEST_DOMAIN_2));
        }
        assertEquals(0, getEventCount(TEST_DOMAIN_3));

        final ListenStartResult theListenStartResult_2 = startListen(TEST_USER_ID_2);
        assertEquals(1500, joinListen(theListenStartResult_2));
    }

    /**
     * Adding 2100 events with multithreading and multi listen threads. Another user has only one listen thread and it
     * will be tested that the listen thread will not be aborted when other events received.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_3() throws Exception {
        checkListen_ExtremeThreading_3(false);
        assertEquals(900, getEventCount(TEST_DOMAIN));
        assertEquals(600, getEventCount(TEST_DOMAIN_2));
        assertEquals(1, getEventCount(TEST_DOMAIN_3)); //event for TEST_USER_ID_2
    }

    /**
     * Adding 2100 events with multithreading and multi listen threads. Another user has only one listen thread and it
     * will be tested that the listen thread will not be aborted when other events received.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_3_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_3(true);
        assertEquals(1500, getEventCount(TEST_USER_ID));
        assertEquals(1, getEventCount(TEST_USER_ID_2)); //event for TEST_USER_ID_2
    }

    /**
     * Adding 2100 events with multithreading and multi listen threads. Another user has only one listen thread and it
     * will be tested that the listen thread will not be aborted when other events received.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_3(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID_2, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        ListenStartResult theListenStartResult = startListen(TEST_USER_ID_2);

        for(int i = 0; i < 300; i++) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 1, isUserSpecific);
            startListen(TEST_USER_ID);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 3, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 4, isUserSpecific);
            startListen(TEST_USER_ID);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 6, isUserSpecific);
            assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
            assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());
        }

        //the listen thread of TEST_USER_ID_2 shouldn't abort when events for other domains occur.
        assertTrue(theListenStartResult.getThread().isAlive());
        assertNull(theListenStartResult.getListenResult());

        //the listen thread of TEST_USER_ID_2 should abort, because there is an event for TEST_DOMAIN_3.
        addEvent(TEST_USER_ID_2, TEST_DOMAIN_3, new DummyEvent(), isUserSpecific);
        Thread.sleep(500);
        assertFalse(theListenStartResult.getThread().isAlive());
        assertNotNull(theListenStartResult.getListenResult());
        if(!isUserSpecific) {
            assertEquals(1, theListenStartResult.getListenResult().getEventCount(TEST_DOMAIN_3));
        }

        joinEventThreads();

        startListen(TEST_USER_ID);
        Thread.sleep(500);
        myEventRegistry.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();

        assertEquals(1501, getEventCount());
    }

    /**
     * Adding 2100 events with multithreading and a listen thread at every third event.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_4() throws Exception {
        checkListen_ExtremeThreading_4(false);

        assertEquals(900, getEventCount(TEST_DOMAIN));
        assertEquals(600, getEventCount(TEST_DOMAIN_2));
        assertEquals(600, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 2100 events with multithreading and a listen thread at every third event.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_4_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_4(true);

        assertEquals(2100, getEventCount(TEST_USER_ID));
        assertEquals(2100, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 2100 events with multithreading and a listen thread at every third event.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_4(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID_2, null);
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        startListen(TEST_USER_ID);
        startListen(TEST_USER_ID_2);

        for(int i = 0; i < 300; i++) {
            startListen(TEST_USER_ID);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_2, 1, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_3, 2, isUserSpecific);
            startListen(TEST_USER_ID_2);
            startListen(TEST_USER_ID);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_2, 3, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN, 4, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_3, 5, isUserSpecific);
            startListen(TEST_USER_ID_2);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN, 6, isUserSpecific);
            assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        }

        joinEventThreads();

        startListen(TEST_USER_ID);
        startListen(TEST_USER_ID_2);
        Thread.sleep(500);
        myEventRegistry.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();
    }

    /**
     * Adding 2000 events with multithreading and a listen thread to one of three registered domains.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_5() throws Exception {
        checkListen_ExtremeThreading_5(false);

        assertEquals(2000, getEventCount());
        assertEquals(2000, getEventCount(TEST_DOMAIN));
        assertEquals(0, getEventCount(TEST_DOMAIN_2));
        assertEquals(0, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 2000 events with multithreading and a listen thread to one of three registered domains.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_5_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_5(true);

        assertEquals(2000, getEventCount());
        assertEquals(2000, getEventCount(TEST_USER_ID));
        assertEquals(0, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 2000 events with multithreading and a listen thread to one of three registered domains.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_5(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains().size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains().size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains().size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        assertEquals(3, myEventRegistry.getListenDomains().size());

        for(int i = 2000; i > 0; i--) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, i, isUserSpecific);
            startListen(TEST_USER_ID);
            assertEquals(3, myEventRegistry.getListenDomains().size());
        }

        joinEventThreads();

        startListen(TEST_USER_ID);
        Thread.sleep(500);
        myEventRegistry.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_EventFilter() throws Exception {
        checkListen_ExtremeThreading_EventFilter(false);

        assertEquals(3750, getEventCount());
        assertEquals(1500, getEventCount(TEST_DOMAIN));
        assertEquals(750, getEventCount(TEST_DOMAIN_2)); //every second event of TEST_DOMAIN_2 was filtered
        assertEquals(1500, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_EventFilter_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_EventFilter(true);

        //All added events will be returned (The EventFilter doesn't filter the events, because the events are added user-specific and not to a domain.).
        assertEquals(4500, getEventCount());
        assertEquals(4500, getEventCount(TEST_USER_ID));
        assertEquals(0, getEventCount(TEST_USER_ID_2));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_EventFilter(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());

        myEventRegistry.setEventFilter(TEST_DOMAIN_2, TEST_USER_ID, new TestEventFilter());

        for(int i = 0; i < 500; i++) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 1, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 2, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 4, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 3, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 5, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 6, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 8, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_2, 7, isUserSpecific);
            assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        }

        joinEventThreads();
        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered, but only
     * for another user.
     * @throws Exception
     */
    public void testListen_ExtremeThreading_EventFilter_2() throws Exception {
        checkListen_ExtremeThreading_EventFilter_2(false);
        
        assertEquals(1500, getEventCount(TEST_DOMAIN));
        assertEquals(1500, getEventCount(TEST_DOMAIN_2));
        assertEquals(1500, getEventCount(TEST_DOMAIN_3));

        //every second event of TEST_DOMAIN_2 was filtered for TEST_USER_ID_2
        final ListenStartResult theListenStartResult_2 = startListen(TEST_USER_ID_2);
        assertEquals(750, joinListen(theListenStartResult_2));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered, but only
     * for another user.
     * That test is executed user-specific (events are registered for single users and not for domains).
     * @throws Exception
     */
    public void testListen_ExtremeThreading_EventFilter_2_UserSpecific() throws Exception {
        checkListen_ExtremeThreading_EventFilter_2(true);

        assertEquals(4500, getEventCount(TEST_USER_ID));

        //All added events will be returned (The EventFilter doesn't filter the events, because the events are added user-specific and not to a domain.).
        final ListenStartResult theListenStartResult_2 = startListen(TEST_USER_ID_2);
        assertEquals(1500, joinListen(theListenStartResult_2));
    }

    /**
     * Adding 4500 events with multithreading and a single listen at the end. 750 events should be filtered, but only
     * for another user.
     * @param isUserSpecific flag to run the method in user- or domain-specific mode
     * @throws Exception
     */
    private void checkListen_ExtremeThreading_EventFilter_2(boolean isUserSpecific) throws Exception {
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertEquals(2, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.registerUser(TEST_DOMAIN_3, TEST_USER_ID, null);
        assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
        assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());

        myEventRegistry.setEventFilter(TEST_DOMAIN_2, TEST_USER_ID_2, new TestEventFilter());

        for(int i = 0; i < 500; i++) {
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 0, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_2, 1, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 2, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_2, 4, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 3, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 5, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN, 6, isUserSpecific);
            startAddEvent(TEST_USER_ID, TEST_DOMAIN_3, 8, isUserSpecific);
            startAddEvent(new String[]{TEST_USER_ID, TEST_USER_ID_2}, TEST_DOMAIN_2, 7, isUserSpecific);
            assertEquals(3, myEventRegistry.getListenDomains(TEST_USER_ID).size());
            assertEquals(1, myEventRegistry.getListenDomains(TEST_USER_ID_2).size());
        }

        joinEventThreads();
        final ListenStartResult theListenStartResult = startListen(TEST_USER_ID);
        joinListen(theListenStartResult);
        assertEquals(4500, getEventCount());
    }
}
