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
package de.novanic.eventservice.service;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.test.testhelper.ListenStartResult;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.ListenCycleCancelEvent;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import de.novanic.eventservice.EventServiceServerThreadingTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 21:35:19
 */
@RunWith(JUnit4.class)
public class EventServiceImpl_ExtremeThreadingTestStress extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    private DummyEventServiceImpl myEventService;

    @Before
    public void setUp() throws Exception {
        setUp(createConfiguration(0, 30000, 90000));

        myEventService = new DummyEventServiceImpl();
        myEventService.init(null);
        super.setUp(myEventService);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        myEventService.unlisten();
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
        //clean up resources
        System.gc();
    }

    @Test
    public void testListen() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());
        ListenStartResult theListenResult_1 = startListen();
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        joinListen(theListenResult_1);
        assertEquals(1, getEventCount());
        assertEquals(0, getEventCount(TEST_DOMAIN));
        assertEquals(1, getEventCount(TEST_DOMAIN_2));
        assertEquals(0, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 15000 events and a single listen at the end (without threads).
     * @throws Exception
     */
    @Test
    public void testListen_Extreme() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        for(int i = 0; i < 5000; i++) {
            myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN_3, new DummyEvent());
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        final ListenStartResult theListenStartResult = startListen();
        joinListen(theListenStartResult);
        assertEquals(15000, getEventCount());
        assertEquals(5000, getEventCount(TEST_DOMAIN));
        assertEquals(5000, getEventCount(TEST_DOMAIN_2));
        assertEquals(5000, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 4500 events with multi-threading and a single listen at the end.
     * @throws Exception
     */
    @Test
    public void testListen_ExtremeThreading() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        for(int i = 0; i < 500; i++) {
            startAddEvent(TEST_DOMAIN, 0);
            startAddEvent(TEST_DOMAIN_2, 1);
            startAddEvent(TEST_DOMAIN_3, 2);
            startAddEvent(TEST_DOMAIN_2, 4);
            startAddEvent(TEST_DOMAIN, 3);
            startAddEvent(TEST_DOMAIN_3, 5);
            startAddEvent(TEST_DOMAIN, 6);
            startAddEvent(TEST_DOMAIN_3, 8);
            startAddEvent(TEST_DOMAIN_2, 7);
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        joinThreads();
        final ListenStartResult theListenStartResult = startListen();
        joinListen(theListenStartResult);
        assertEquals(4500, getEventCount());
        assertEquals(1500, getEventCount(TEST_DOMAIN));
        assertEquals(1500, getEventCount(TEST_DOMAIN_2));
        assertEquals(1500, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 2100 events (without multi-threading) and a listen thread at every third event.
     * @throws Exception
     */
    @Test
    public void testListen_ExtremeThreading_2() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        startListen();
        for(int i = 0; i < 300; i++) {
            myEventService.addEvent(TEST_DOMAIN, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN_3, new DummyEvent());
            startListen();
            myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN, new DummyEvent());
            myEventService.addEvent(TEST_DOMAIN_3, new DummyEvent());
            startListen();
            myEventService.addEvent(TEST_DOMAIN, new DummyEvent());
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        joinThreads();

        startListen();
        Thread.sleep(200);
        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();

        assertEquals(2100, getEventCount());
        assertEquals(900, getEventCount(TEST_DOMAIN));
        assertEquals(600, getEventCount(TEST_DOMAIN_2));
        assertEquals(600, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 2100 events with multi-threading and a listen thread at every third event.
     * @throws Exception
     */
    @Test
    public void testListen_ExtremeThreading_3() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        startListen();

        for(int i = 0; i < 300; i++) {
            startAddEvent(TEST_DOMAIN, 0);
            startAddEvent(TEST_DOMAIN_2, 1);
            startAddEvent(TEST_DOMAIN_3, 2);
            startListen();
            startAddEvent(TEST_DOMAIN_2, 3);
            startAddEvent(TEST_DOMAIN, 4);
            startAddEvent(TEST_DOMAIN_3, 5);
            startListen();
            startAddEvent(TEST_DOMAIN, 6);
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        joinThreads();

        startListen();
        Thread.sleep(200);
        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();

        assertEquals(2100, getEventCount());
        assertEquals(900, getEventCount(TEST_DOMAIN));
        assertEquals(600, getEventCount(TEST_DOMAIN_2));
        assertEquals(600, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 4500 events with multi-threading (one event per millisecond) and a single listen at the end.
     * @throws Exception
     */
    @Test
    public void testListen_ExtremeThreading_4() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        for(int i = 4500; i > 0; i--) {
            startAddEvent(TEST_DOMAIN, i);
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        joinThreads();
        listen();
        assertEquals(4500, getEventCount());
        assertEquals(4500, getEventCount(TEST_DOMAIN));
        assertEquals(0, getEventCount(TEST_DOMAIN_2));
        assertEquals(0, getEventCount(TEST_DOMAIN_3));
    }

    /**
     * Adding 2000 events with multi-threading (one event per millisecond) and a listen thread after every event.
     * @throws Exception
     */
    @Test
    public void testListen_ExtremeThreading_5() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        for(int i = 2000; i > 0; i--) {
            startAddEvent(TEST_DOMAIN, i);
            startListen();
            assertEquals(3, myEventService.getActiveListenDomains().size());
        }

        joinThreads();

        startListen();
        Thread.sleep(200);
        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());

        joinListenThreads();

        assertEquals(2000, getEventCount());
        assertEquals(2000, getEventCount(TEST_DOMAIN));
        assertEquals(0, getEventCount(TEST_DOMAIN_2));
        assertEquals(0, getEventCount(TEST_DOMAIN_3));
    }

    private class DummyEventServiceImpl extends EventServiceImpl
    {
        protected String getClientId() {
            return TEST_USER_ID;
        }

        protected String generateClientId() {
            return TEST_USER_ID;
        }
    }
}
