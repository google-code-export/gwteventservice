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
import de.novanic.eventservice.client.event.listen.UnlistenEvent;
import de.novanic.eventservice.test.testhelper.TestEventFilter;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import de.novanic.eventservice.EventServiceServerThreadingTest;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
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
    private TestLoggingHandler myTestLoggingHandler;
    private Level myOldLoggingLevel;
    private Logger myLogger;

    public void setUp() throws Exception {
        super.setUp();
        setUp(createConfiguration(0, 500, 2500));
        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);
        setUpLoggingTest();
    }

    private void setUpLoggingTest() {
        myTestLoggingHandler = new TestLoggingHandler();

        myLogger = Logger.getLogger(DefaultEventRegistry.class.getName());
        myOldLoggingLevel = myLogger.getLevel();

        myLogger.setLevel(Level.FINEST);
        myLogger.addHandler(myTestLoggingHandler);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        myLogger.setLevel(myOldLoggingLevel);
        myLogger.removeHandler(myTestLoggingHandler);
        myTestLoggingHandler.clear();

        EventRegistryFactory.reset();
        EventExecutorServiceFactory.reset();
        Thread.sleep(500);
    }

    public void testRegisterUser() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

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
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    public void testIsUserRegistered() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));
        
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(3, "Server: User \"test_user_id\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());
        assertFalse(theListenDomains.get(0).equals(theListenDomains.get(1)));
        assertTrue(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));
    }

    public void testIsUserRegistered_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        checkLog(3, "Server: User \"test_user_id_2\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID_2));

        theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        List<Domain> theListenDomains_2 = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID_2));
        assertFalse(theListenDomains_2.isEmpty());
        assertEquals(1, theListenDomains_2.size());
        assertEquals(TEST_DOMAIN_2, theListenDomains_2.get(0));
    }

    public void testIsUserRegistered_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        //unlisten for a false domain
        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(5, "Server: test_user_id: unlisten (domain \"test_domain_2\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain_2\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain_2\") for user \"test_user_id\".");
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        //unlisten
        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(9, "Server: test_user_id: unlisten (domain \"test_domain\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain\") for user \"test_user_id\".",
                "Server: User \"test_user_id\" removed from domain \"test_domain\".");
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
    }

    public void testGetListenDomains() {
        Set<Domain> theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertTrue(theDomains.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertEquals(2, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));
        assertTrue(theDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertEquals(2, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));
        assertTrue(theDomains.contains(TEST_DOMAIN_2));
    }

    public void testGetListenDomains_2() {
        Set<Domain> theDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theDomains);
        assertTrue(theDomains.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theDomains = myEventRegistry.getListenDomains(TEST_USER_ID_2);
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN_2));

        theDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theDomains = myEventRegistry.getListenDomains(TEST_USER_ID_2);
        assertNotNull(theDomains);
        assertEquals(2, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));
        assertTrue(theDomains.contains(TEST_DOMAIN_2));

        theDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));
    }

    public void testUnlisten() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());

        myEventRegistry.unlisten(TEST_USER_ID);
        checkLog(6, "Server: test_user_id: unlisten.",
                "Server: User specific event \"Event: Unlisten\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten for user \"test_user_id\".",
                "Server: User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
    }

    public void testUnlisten_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(6, "Server: test_user_id: unlisten (domain \"test_domain\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain\") for user \"test_user_id\".",
                "Server: User \"test_user_id\" removed from domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        
        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertFalse(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(10, "Server: test_user_id: unlisten (domain \"test_domain_2\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain_2\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain_2\") for user \"test_user_id\".",
                "Server: User \"test_user_id\" removed from domain \"test_domain_2\".");

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
    }

    public void testUnlisten_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(6, "Server: test_user_id: unlisten (domain \"test_domain\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain\") for user \"test_user_id\".",
                "Server: User \"test_user_id\" removed from domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertFalse(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(9);

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));
    }

    public void testUnlisten_Error() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        //the user isn't registered
        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
    }

    public void testUnlisten_Error_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());

        //The log "... removed from domain ..." is missing, because the user is registered for a other domain.
        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(4, "Server: test_user_id: unlisten (domain \"test_domain_2\").",
                "Server: User specific event \"Event: Unlisten (Domain \"test_domain_2\")\" added to client id \"test_user_id\".",
                "Server: Event: Unlisten (Domain \"test_domain_2\") for user \"test_user_id\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    public void testListen() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");
        
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

        logOff();
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        Thread.currentThread().interrupt();
        assertNull(myEventRegistry.listen(TEST_USER_ID));
        logOn();

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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        EventServiceConfiguration theEventServiceConfiguration = createConfiguration(0, 2000, 9999);
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
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
        theResult += myEventRegistry.listen(TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        Thread.sleep(300);
        assertEquals(0, myEventRegistry.listen(TEST_USER_ID).size());
    }

    public void testAddUserSpecificEvent() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        
        myEventRegistry.addEventUserSpecific(TEST_USER_ID, new DummyEvent());
        checkLog(4, "Server: User specific event \"DummyEvent (id 1)\" added to client id \"test_user_id\".",
                "Server: DummyEvent (id 1) for user \"test_user_id\".");

        myEventRegistry.addEventUserSpecific(TEST_USER_ID_2, new DummyEvent());
        checkLog(6, "Server: User specific event \"DummyEvent (id 2)\" added to client id \"test_user_id_2\".",
                "Server: DummyEvent (id 2) for user \"test_user_id_2\".");
    }

    public void testAddUserSpecificEvent_2() throws Exception {
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
        EventServiceConfiguration theEventServiceConfiguration = createConfiguration(0, 2000, 9999);
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
        //set the default waiting time greater than time out time to produce a time out
        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        EventServiceConfiguration theEventServiceConfiguration = theEventRegistry.getConfiguration();

        final int theTimeoutTime = 400;
        final int theNewMaxWaitingTime = theTimeoutTime + 1700;
        EventServiceConfiguration theNewEventServiceConfiguration = createConfiguration(
                theEventServiceConfiguration.getMinWaitingTime(),
                theNewMaxWaitingTime,
                theTimeoutTime);

        tearDownEventServiceConfiguration();
        setUp(theNewEventServiceConfiguration);

        theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();

        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theEventRegistry.addEvent(TEST_DOMAIN, new DummyEvent());
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        List<DomainEvent> theEvents = theEventRegistry.listen(TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        //It is waiting for events and will cause a timeout, because the max. waiting time is configured longer than the timeout time.
        //The result is a UnlistenEvent, because the timeout doesn't effect that method, but the next call.
        theEvents = theEventRegistry.listen(TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.iterator().next().getEvent() instanceof UnlistenEvent);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));

        theEvents = theEventRegistry.listen(TEST_USER_ID);
        assertNull(theEvents);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));
    }

    public void testChangeEventFilter() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: test_user_id: EventFilter changed for domain \"test_domain\".");
        
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(3, "Server: test_user_id: EventFilter removed from domain \"test_domain\".");
    }

    public void testChangeEventFilter_2() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(3, "Server: test_user_id: EventFilter removed from domain \"test_domain\".");
    }

    public void testChangeEventFilter_Error() {
        myEventRegistry.setEventFilter(TEST_DOMAIN, "noKnownUser", new TestEventFilter());
        checkLog(0);
    }

    public void testRemoveEventFilter() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new TestEventFilter());
        checkLog(2, "Server: User \"test_user_id\" registered for domain \"test_domain\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, new TestEventFilter());
        checkLog(4, "Server: User \"test_user_id\" registered for domain \"test_domain_2\".",
                "Server: test_user_id: EventFilter changed for domain \"test_domain_2\".");

        myEventRegistry.removeEventFilter(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(5, "Server: test_user_id: EventFilter removed from domain \"test_domain_2\".");

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(6, "Server: test_user_id: EventFilter removed from domain \"test_domain\".");
    }

    public void testRemoveEventFilter_2() {
        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        checkLog(0);
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(0);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "Server: User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        checkLog(1);
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1);
    }

    private void checkLog(int anExpectedLogSize, String... anExpectedLogMessageList) {
        assertTrue(myTestLoggingHandler.containsMessage(anExpectedLogMessageList));
        assertEquals(anExpectedLogSize, myTestLoggingHandler.getLogMessageSize());
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

        private ListenCallable(String aUser) {
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

    private class TestLoggingHandler extends Handler
    {
        private List<String> myMessages;

        private TestLoggingHandler() {
            myMessages = new ArrayList<String>();
        }

        public void publish(LogRecord aRecord) {
            myMessages.add(aRecord.getMessage());
        }

        public void flush() {
            clear();
        }

        public void close() throws SecurityException {
            clear();
        }

        public void clear() {
            myMessages.clear();
        }

        public boolean containsMessage(String... aMessages) {
            for(String theMessage: aMessages) {
                if(!myMessages.contains(theMessage)) {
                    return false;
                }
            }
            return true;
        }

        public int getLogMessageSize() {
            return myMessages.size();
        }
    }
}
