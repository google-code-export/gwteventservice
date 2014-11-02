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
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 05.10.2008
 *         <br>Time: 00:18:39
 */
public abstract class EventExecutorServiceTest_A extends EventServiceTestCase
{
    protected static final String TEST_USER_ID = "test_user_id";
    protected static final String TEST_USER_ID_2 = "test_user_id_2";
    protected static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    protected static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");

    private EventExecutorService myEventExecutorService;

    @Before
    public void setUp() throws Exception {
        setUp(createConfiguration(0, 1, 9999));

        myEventExecutorService = initEventExecutorService();
    }

    public abstract EventExecutorService initEventExecutorService();

    @After
    public void tearDown() throws Exception {
        tearDownEventServiceConfiguration();
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
    }

    @Test
    public void testIsUserRegistered() {
        assertFalse(myEventExecutorService.isUserRegistered());
        EventRegistryFactory.getInstance().getEventRegistry().registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventExecutorService.isUserRegistered());
    }

    @Test
    public void testIsUserRegistered_2() {
        assertFalse(myEventExecutorService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventExecutorService.isUserRegistered(TEST_DOMAIN_2));

        EventRegistryFactory.getInstance().getEventRegistry().registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventExecutorService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventExecutorService.isUserRegistered(TEST_DOMAIN_2));
    }

    @Test
    public void testAddEvent() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        myEventExecutorService.addEvent(TEST_DOMAIN, new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertFalse(theEvents.isEmpty());
        assertEquals(1, theEvents.size());
    }

    @Test
    public void testAddEvent_2() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        myEventExecutorService.addEvent(TEST_DOMAIN_2, new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());
    }

    @Test
    public void testAddEventUserSpecific() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        myEventExecutorService.addEventUserSpecific(new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertFalse(theEvents.isEmpty());
        assertEquals(1, theEvents.size());
    }

    @Test
    public void testAddEventUserSpecific_2() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNull(theEvents);

        myEventExecutorService.addEventUserSpecific(new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID); //the user is not registered
        assertNull(theEvents);
    }

    @Test
    public void testAddEventUserSpecific_3() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        myEventExecutorService.addEventUserSpecific(new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2); //the event was for another user
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());
    }

    @Test
    public void testAddEventUserSpecific_4() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        myEventExecutorService.addEventUserSpecific(new EmptyEvent());
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID); //the event is for another domain, but that is unimportant, because the event is user specific.
        assertNotNull(theEvents);
        assertFalse(theEvents.isEmpty());
        assertEquals(1, theEvents.size());
    }

    @Test
    public void testSetEventFilter() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(1, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        assertNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));

        final EmptyEventFilter theEventFilter = new EmptyEventFilter(0);
        myEventExecutorService.setEventFilter(TEST_DOMAIN, theEventFilter);

        assertNotNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));
        assertEquals(theEventFilter, myEventExecutorService.getEventFilter(TEST_DOMAIN));

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(0, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testRemoveEventFilter() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(1, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        assertNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));

        final EmptyEventFilter theEventFilter = new EmptyEventFilter(0);
        myEventExecutorService.setEventFilter(TEST_DOMAIN, new EmptyEventFilter(0));

        assertNotNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));
        assertEquals(theEventFilter, myEventExecutorService.getEventFilter(TEST_DOMAIN));

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(0, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        myEventExecutorService.removeEventFilter(TEST_DOMAIN);

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(1, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testRemoveEventFilter_2() {
        final EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(1, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        assertNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));

        final EmptyEventFilter theEventFilter = new EmptyEventFilter(0);
        myEventExecutorService.setEventFilter(TEST_DOMAIN, new EmptyEventFilter(0));

        assertNotNull(myEventExecutorService.getEventFilter(TEST_DOMAIN));
        assertEquals(theEventFilter, myEventExecutorService.getEventFilter(TEST_DOMAIN));

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(0, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        myEventExecutorService.setEventFilter(TEST_DOMAIN, null);

        theEventRegistry.addEvent(TEST_DOMAIN, new EmptyEvent());
        assertEquals(1, theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    private static class EmptyEvent implements Event {}

    private static class EmptyEventFilter implements EventFilter
    {
        private final int myId;

        private EmptyEventFilter(int anId) {
            myId = anId;
        }

        public boolean match(Event anEvent) {
            return true;
        }

        public boolean equals(Object anObject) {
            if(this == anObject) {
                return true;
            }
            if(anObject == null || getClass() != anObject.getClass()) {
                return false;
            }
            EmptyEventFilter theOtherEventFilter = (EmptyEventFilter)anObject;
            return myId == theOtherEventFilter.myId;
        }

        public int hashCode() {
            return myId;
        }
    }
}
