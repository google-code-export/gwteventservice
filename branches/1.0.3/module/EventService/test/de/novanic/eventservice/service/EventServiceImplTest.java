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
package de.novanic.eventservice.service;

import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.service.testhelper.*;
import de.novanic.eventservice.EventServiceServerThreadingTest;

import java.util.*;

/**
 * @author sstrohschein
 * Date: 05.06.2008
 * <br>Time: 23:26:19
 */
public class EventServiceImplTest extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    private EventService myEventService;

    public void setUp() throws Exception {
        setUp(new RemoteEventServiceConfiguration(0, 30000, 90000));

        myEventService = new DummyEventServiceImpl();
        super.setUp(myEventService);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        myEventService.unlisten();
        EventExecutorServiceFactory.reset();
        Thread.sleep(500); //waiting between the test cases is needed to avoid conflicts between the test cases/scenarios
    }

    public void testInit() {
        EventServiceImpl theEventService = new EventServiceImpl();
        try {
            theEventService.getActiveListenDomains();
            fail("Exception expected, because no client id can be found!");
        } catch(Exception e) {}

        theEventService = new DummyEventServiceImpl();

        assertEquals(0, theEventService.getActiveListenDomains().size());
    }

    public void testListen() throws Exception {
        myEventService.register(TEST_DOMAIN);

        assertEquals(1, myEventService.getActiveListenDomains().size());
        startListen();
        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN);
        assertEquals(0, myEventService.getActiveListenDomains().size());
        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        startListen();
        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        startListen();
        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN_2);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(TEST_DOMAIN_3);
        assertEquals(0, myEventService.getActiveListenDomains().size());
    }

    public void testListen_2() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());
        startListen();
        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        myEventService.register(TEST_DOMAIN_2);
        myEventService.register(TEST_DOMAIN_2); //shouldn't affect the ActiveListenDomain count
        startListen();
        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        startListen();
        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.unlisten();
        assertEquals(0, myEventService.getActiveListenDomains().size());
    }

    public void testListen_3() throws Exception {
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

        assertEquals(1, joinListen(theListenResult_1));
    }

    public void testListen_4() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());
        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());
        ListenStartResult theListenStartResult = startListen();
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
        assertEquals(1, joinListen(theListenStartResult));

        theListenStartResult = startListen();
        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        assertEquals(0, joinListen(theListenStartResult));
        theListenStartResult = startListen();

        assertEquals(3, myEventService.getActiveListenDomains().size());
        myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        assertEquals(1, joinListen(theListenStartResult));

        theListenStartResult = startListen();
        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        assertEquals(0, joinListen(theListenStartResult));
        theListenStartResult = startListen();

        assertEquals(3, myEventService.getActiveListenDomains().size());
        myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
        myEventService.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        //depend on the timing, it could be that the two events recognized with the first listen or that one get recognized with the first and one with the second listen.
        int theEventCount = joinListen(theListenStartResult);
        assertTrue(theEventCount == 1 || theEventCount == 2);
        if(theEventCount == 1) {
            theListenStartResult = startListen();
            assertEquals(1, joinListen(theListenStartResult));
        }
    }

    public void testListen_5() throws Exception {
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        ListenStartResult theListenResult = startListen();
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_2);
        assertEquals(2, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.addEvent(TEST_DOMAIN_2, new DummyEvent());
        myEventService.addEvent(TEST_DOMAIN_3, new DummyEvent());
        assertEquals(3, myEventService.getActiveListenDomains().size());

        //depend on the timing, it could be that two events recognized with the first listen or that one get recognized with the first and one with the second listen.
        int theEventCount = joinListen(theListenResult);
        assertTrue(theEventCount == 1 || theEventCount == 2);
        if(theEventCount == 1) {
            theListenResult = startListen();
            assertEquals(1, joinListen(theListenResult));
        }
    }

    public void testListen_UserSpecific() throws Exception {
        tearDownEventServiceConfiguration();
        setUp(new RemoteEventServiceConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        Set<Domain> theDomains = new HashSet<Domain>();
        theDomains.add(TEST_DOMAIN);
        theDomains.add(TEST_DOMAIN_3);
        myEventService.register(theDomains);

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(TEST_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN_3, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN_3));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //the user isn't registered for this domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEventUserSpecific(new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size()); //in this case the domain isn't important, because the event is added directly to the user
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());
    }

    public void testRegisterMultiDomain() throws Exception {
        tearDownEventServiceConfiguration();
        setUp(new RemoteEventServiceConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        Set<Domain> theDomains = new HashSet<Domain>();
        theDomains.add(TEST_DOMAIN);
        theDomains.add(TEST_DOMAIN_3);
        myEventService.register(theDomains);

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(TEST_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN_3, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN_3));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for another domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());
    }

    public void testRegisterMultiDomain_2() throws Exception {
        tearDownEventServiceConfiguration();
        setUp(new RemoteEventServiceConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        Set<Domain> theDomains = new HashSet<Domain>();
        theDomains.add(TEST_DOMAIN);
        theDomains.add(TEST_DOMAIN_3);
        myEventService.register(theDomains, new TestEventFilter());

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(TEST_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN_3, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN_3));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is filtered by TestEventFilter
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for another domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for another domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for another domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is filtered by TestEventFilter,
        // because the three events above are for another domain and the EventFilter wasn't triggered
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());
    }

    public void testRegisterEventFilter() throws Exception {
        tearDownEventServiceConfiguration();
        setUp(new RemoteEventServiceConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        myEventService.register(TEST_DOMAIN);

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(1, theActiveDomains.size());
        assertEquals(TEST_DOMAIN, theActiveDomains.get(0));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.registerEventFilter(TEST_DOMAIN_2, new TestEventFilter());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.registerEventFilter(TEST_DOMAIN, new TestEventFilter());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is filtered by TestEventFilter
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is filtered by TestEventFilter
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.deregisterEventFilter(TEST_DOMAIN);

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());
    }

    public void testUnlisten() throws InterruptedException {
        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(myEventService.getActiveListenDomains());
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        myEventService.register(TEST_DOMAIN_2);
        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.unlisten(myEventService.getActiveListenDomains());
        assertEquals(0, myEventService.getActiveListenDomains().size());
    }

    public void testUnlisten_2() throws InterruptedException {
        tearDownEventServiceConfiguration();
        setUp(new RemoteEventServiceConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        myEventService.register(TEST_DOMAIN);
        assertEquals(1, myEventService.getActiveListenDomains().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.unlisten(myEventService.getActiveListenDomains());
        assertEquals(0, myEventService.getActiveListenDomains().size());

        myEventService.register(TEST_DOMAIN);
        myEventService.register(TEST_DOMAIN_2);
        myEventService.register(TEST_DOMAIN_3);
        assertEquals(3, myEventService.getActiveListenDomains().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.unlisten(myEventService.getActiveListenDomains());
        assertEquals(0, myEventService.getActiveListenDomains().size());
    }

    private class DummyEventServiceImpl extends EventServiceImpl
    {
        protected String getClientId(boolean isInitSession) {
            return TEST_USER_ID;
        }
    }
}
