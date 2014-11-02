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
package de.novanic.eventservice.service.registry;

import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.service.registry.user.UserManager;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.EventFilterTestMode;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import de.novanic.eventservice.service.DefaultEventExecutorService;
import de.novanic.eventservice.EventServiceServerThreadingTest;
import de.novanic.eventservice.util.PlatformUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 * Date: 28.07.2008
 * <br>Time: 22:49:33
 */
@RunWith(JUnit4.class)
public class EventRegistryTest extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USER_ID_2 = "test_user_id_2";
    private static final String TEST_USER_ID_3 = "test_user_id_3";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    private EventRegistry myEventRegistry;
    private TestLoggingHandler myTestLoggingHandler;
    private Level myOldLoggingLevel;
    private Logger myLogger;

    @Before
    public void setUp() throws Exception {
        setUp(createConfiguration(0, 500, 99999999));
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

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        myLogger.setLevel(myOldLoggingLevel);
        myLogger.removeHandler(myTestLoggingHandler);
        myTestLoggingHandler.clear();

        FactoryResetService.resetFactory(EventRegistryFactory.class);
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
    }

    @Test
    public void testRegisterUser() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    @Test
    public void testRegisterUser_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    @Test
    public void testRegisterUser_DomainLess() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(null, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
    }

    @Test
    public void testIsUserRegistered() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(3, "User \"test_user_id\" registered for domain \"test_domain_2\".");

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

    @Test
    public void testIsUserRegistered_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertNull(myEventRegistry.getEventFilter(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID_2));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        checkLog(3, "User \"test_user_id_2\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID_2));
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID_2));
        assertNull(myEventRegistry.getEventFilter(DomainFactory.getDomain("X"), TEST_USER_ID_2));

        theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        List<Domain> theListenDomains_2 = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID_2));
        assertFalse(theListenDomains_2.isEmpty());
        assertEquals(1, theListenDomains_2.size());
        assertEquals(TEST_DOMAIN_2, theListenDomains_2.get(0));
    }

    @Test
    public void testIsUserRegistered_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        //unlisten for a false domain
        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(3, "test_user_id: unlisten (domain \"test_domain_2\")."); //no event is send to the domain (the user wasn't removed)
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertNull(myEventRegistry.getEventFilter(DomainFactory.getDomain("X"), TEST_USER_ID));

        //unlisten
        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(6, "test_user_id: unlisten (domain \"test_domain\").",
                "User \"test_user_id\" removed from domain \"test_domain\".",
                "Event \"Event: Unlisten (user \"test_user_id\" for domain \"test_domain\")\" added to domain \"service_unlisten_domain\".");
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
    }

    @Test
    public void testIsUserRegistered_UserSpecificDomain() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        List<Domain> theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);
        checkLog(3, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(null, TEST_USER_ID)); //can't be checked for the NULL-domain
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));

        theListenDomains = new ArrayList<Domain>(myEventRegistry.getListenDomains(TEST_USER_ID));
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.get(0));
    }

    @Test
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

    @Test
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

    @Test
    public void testGetListenDomains_3() {
        Set<Domain> theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertTrue(theDomains.isEmpty());

        Set<Domain> theUserDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theUserDomains);
        assertTrue(theUserDomains.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));

        theUserDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theUserDomains);
        assertEquals(1, theUserDomains.size());
        assertTrue(theUserDomains.contains(TEST_DOMAIN));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theDomains = myEventRegistry.getListenDomains();
        assertNotNull(theDomains);
        assertEquals(2, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN));
        assertTrue(theDomains.contains(TEST_DOMAIN_2));

        theUserDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theUserDomains);
        assertEquals(1, theUserDomains.size());
        assertTrue(theUserDomains.contains(TEST_DOMAIN));

        Set<Domain> theUserDomains_2 = myEventRegistry.getListenDomains(TEST_USER_ID_2);
        assertNotNull(theUserDomains_2);
        assertEquals(1, theUserDomains_2.size());
        assertTrue(theUserDomains_2.contains(TEST_DOMAIN_2));

        //Remove TEST_USER_ID. TEST_DOMAIN should be removed automatically, because no other users are registered to the domain.
        myEventRegistry.unlisten(TEST_USER_ID);
        theUserDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertNotNull(theUserDomains);
        assertEquals(0, theUserDomains.size());

        theUserDomains_2 = myEventRegistry.getListenDomains(TEST_USER_ID_2);
        assertNotNull(theUserDomains_2);
        assertEquals(1, theUserDomains_2.size());
        assertTrue(theUserDomains_2.contains(TEST_DOMAIN_2));

        theDomains = myEventRegistry.getListenDomains();
        assertEquals(1, theDomains.size());
        assertTrue(theDomains.contains(TEST_DOMAIN_2));
    }

    @Test
    public void testGetRegisteredUsers() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds();
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));
    }

    @Test
    public void testGetRegisteredUsers_2() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds();
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(1, theUserIds.size());
        assertFalse(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(1, theUserIds.size());
        assertFalse(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID_2);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertNotNull(theUserIds);
        assertEquals(0, theUserIds.size());
        assertFalse(theUserIds.contains(TEST_USER_ID));
        assertFalse(theUserIds.contains(TEST_USER_ID_2));
    }

    @Test
    public void testGetRegisteredUsers_2_1() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertEquals(0, theUserIds.size());

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID_2));
    }

    @Test
    public void testGetRegisteredUsers_2_2() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID);
        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertEquals(1, theUserIds.size());
        assertFalse(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID_2);
        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN);
        assertNotNull(theUserIds);
        assertEquals(0, theUserIds.size());

        theUserIds = myEventRegistry.getRegisteredUserIds(TEST_DOMAIN_2);
        assertNotNull(theUserIds);
        assertEquals(0, theUserIds.size());
    }

    @Test
    public void testGetRegisteredUsers_UserSpecificDomain() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds();
        assertNotNull(theUserIds);
        assertTrue(theUserIds.isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(1, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID_2, null);
        theUserIds = myEventRegistry.getRegisteredUserIds();
        assertEquals(2, theUserIds.size());
        assertTrue(theUserIds.contains(TEST_USER_ID));
        assertTrue(theUserIds.contains(TEST_USER_ID_2));
    }

    @Test
    public void testGetRegisteredUsers_2_Error() {
        Set<String> theUserIds = myEventRegistry.getRegisteredUserIds(null);
        assertNotNull(theUserIds);
        assertEquals(0, theUserIds.size());
    }

    @Test
    public void testUnlisten() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);
        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());

        myEventRegistry.unlisten(TEST_USER_ID);
        checkLog(8, "test_user_id: unlisten.",
                "Event \"Event: Unlisten (user \"test_user_id\" for 2 domains)\" added to domain \"service_unlisten_domain\".",
                "Event: Unlisten (user \"test_user_id\" for 2 domains) for user \"test_user_id\".",
                "User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
    }

    @Test
    public void testUnlisten_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(5, "test_user_id: unlisten (domain \"test_domain\").",
                "Event \"Event: Unlisten (user \"test_user_id\" for domain \"test_domain\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed from domain \"test_domain\".");

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
        checkLog(8, "test_user_id: unlisten (domain \"test_domain_2\").",
                "Event \"Event: Unlisten (user \"test_user_id\" for domain \"test_domain_2\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed from domain \"test_domain_2\".");

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
    }

    @Test
    public void testUnlisten_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain_2\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(2, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN));
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        checkLog(5, "test_user_id: unlisten (domain \"test_domain\").",
                "Event \"Event: Unlisten (user \"test_user_id\" for domain \"test_domain\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed from domain \"test_domain\".");

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
        checkLog(6); //only the unlisten call is logged. No effect is logged, because the user is already removed from the domain.

        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertTrue(theListenDomains.contains(TEST_DOMAIN_2));
    }

    @Test
    public void testUnlisten_ImportantDomain() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
    }

    @Test
    public void testUnlisten_ImportantDomain_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(2, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
        assertTrue(theEvents.get(1).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(1).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN_2, ((UnlistenEvent)theEvents.get(1).getEvent()).getDomains().iterator().next());
    }

    @Test
    public void testUnlisten_ImportantDomain_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());

        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN_2, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
    }

    @Test
    public void testUnlisten_UnimportantDomain() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(0, theEvents.size());
    }

    @Test
    public void testUnlisten_UnimportantDomain_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_3));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_3, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_3));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_3, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_3));

        //all three users have an unlisten listener registered
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_3, UnlistenEventListener.Scope.UNLISTEN, null);
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_3));

        //the removed user itself, gets the own unlisten event on next listen call, because the unlisten listener isn't removed
        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
        assertEquals(TEST_USER_ID, ((UnlistenEvent)theEvents.get(0).getEvent()).getUserId());

        //user 1 wasn't registered to the same domain of user 2
        theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(0, theEvents.size());

        //user 1 was registered to the same domain of user 3
        theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_3);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
        assertEquals(TEST_USER_ID, ((UnlistenEvent)theEvents.get(0).getEvent()).getUserId());
    }

    @Test
    public void testUnlisten_Global() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(1, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().size());
        assertEquals(TEST_DOMAIN, ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains().iterator().next());
    }

    @Test
    public void testUnlisten_Global_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        final Set<Domain> theUnlistenedDomains = ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains();
        assertEquals(2, theUnlistenedDomains.size());
        assertTrue(theUnlistenedDomains.contains(TEST_DOMAIN));
        assertTrue(theUnlistenedDomains.contains(TEST_DOMAIN_2));
        assertFalse(theUnlistenedDomains.contains(TEST_DOMAIN_3));
    }

    @Test
    public void testUnlisten_UnimportantDomain_Global() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(0, theEvents.size());
    }

    @Test
    public void testUnlisten_UnimportantDomain_Global_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID_3));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_3, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_3));

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_3, null);
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID_3));

        //all three users have an unlisten listener registered
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_3, UnlistenEventListener.Scope.UNLISTEN, null);
        myEventRegistry.registerUnlistenEvent(TEST_USER_ID_2, UnlistenEventListener.Scope.UNLISTEN, null);

        myEventRegistry.unlisten(TEST_USER_ID);
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_3));

        //the removed user itself, can't get the own unlisten event, because the user is completely removed (the unlisten listener is also removed)
        List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNull(theEvents);

        //user 2 gets the unlisten event, because it is a global unlisten event (no domain information)
        theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        Set<Domain> theUnlistenedDomains = ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains();
        assertEquals(2, theUnlistenedDomains.size());
        assertTrue(theUnlistenedDomains.contains(DomainFactory.UNLISTEN_DOMAIN));
        assertTrue(theUnlistenedDomains.contains(TEST_DOMAIN));
        assertFalse(theUnlistenedDomains.contains(TEST_DOMAIN_2));
        assertEquals(TEST_USER_ID, ((UnlistenEvent)theEvents.get(0).getEvent()).getUserId());

        //user 1 was registered to the same domain of user 3
        theEvents = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_3);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        theUnlistenedDomains = ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains();
        assertEquals(2, theUnlistenedDomains.size());
        assertTrue(theUnlistenedDomains.contains(DomainFactory.UNLISTEN_DOMAIN));
        assertTrue(theUnlistenedDomains.contains(TEST_DOMAIN));
        assertFalse(theUnlistenedDomains.contains(TEST_DOMAIN_2));
        assertEquals(TEST_USER_ID, ((UnlistenEvent)theEvents.get(0).getEvent()).getUserId());
    }

    @Test
    public void testUnlisten_UserSpecificDomain() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());

        myEventRegistry.unlisten(TEST_USER_ID);
        checkLog(4, "test_user_id: unlisten.",
                "Event \"Event: Unlisten (user \"test_user_id\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
    }

    @Test
    public void testUnlisten_UserSpecificDomain_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());

        myEventRegistry.unlisten(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID);
        checkLog(4, "test_user_id: unlisten.",
                "Event \"Event: Unlisten (user \"test_user_id\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertTrue(theListenDomains.isEmpty());
    }

    @Test
    public void testUnlisten_UserSpecificDomain_3() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());

        myEventRegistry.unlisten(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID);

        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

//    @Test
//    public void testUnlisten_UserSpecificDomain_4() {
//        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
//        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
//        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID));
//        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
//
//        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);
//
//        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
//        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
//        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain
//
//        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
//
//        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
//        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
//        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain
//
//        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
//        assertEquals(1, theListenDomains.size());
//        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
//
//        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID);
//
//        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID)); //can't be checked for the NULL-domain
//        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
//        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID)); //TODO the user is completely removed, because it isn't noticed that the user was registered to the "NULL"-domain...
//
//        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
//        assertTrue(theListenDomains.isEmpty());
//    }

    @Test
    public void testUnlisten_Termination() {
        UserInfo theTestUser_1 = mock(UserInfo.class, TEST_USER_ID);
        when(theTestUser_1.getUserId()).thenReturn(TEST_USER_ID);
        when(theTestUser_1.getLastActivityTime()).thenReturn(PlatformUtil.getCurrentTime());
        when(theTestUser_1.getUnlistenEvent()).thenReturn(new DefaultUnlistenEvent());

        UserInfo theTestUser_2 = mock(UserInfo.class, TEST_USER_ID_2);
        when(theTestUser_2.getUserId()).thenReturn(TEST_USER_ID_2);
        when(theTestUser_2.getLastActivityTime()).thenReturn(PlatformUtil.getCurrentTime());
        when(theTestUser_2.getUnlistenEvent()).thenReturn(new DefaultUnlistenEvent());

        UserManager theUserManager = UserManagerFactory.getInstance().getUserManager(99999);
        theUserManager.addUser(theTestUser_1);
        theUserManager.addUser(theTestUser_2);

        setUp(createConfiguration(0, 90000, 99999999));
        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));
        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID);
        verify(theTestUser_1, times(1)).notifyEventListening();
        verify(theTestUser_2, times(0)).notifyEventListening();

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID_2);
        verify(theTestUser_1, times(1)).notifyEventListening(); //still from the previous unlisten call (mock isn't reset)
        verify(theTestUser_2, times(1)).notifyEventListening();
    }

    @Test
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

    @Test
    public void testUnlisten_Error_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        Set<Domain> theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());

        //The log "... removed from domain ..." is missing, because the user is registered for a other domain.
        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(2, "test_user_id: unlisten (domain \"test_domain_2\")."); //only the unlisten call is logged. No effect is logged, because the user is already removed from the domain.

        assertTrue(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN_2, TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));

        theListenDomains = myEventRegistry.getListenDomains(TEST_USER_ID);
        assertFalse(theListenDomains.isEmpty());
        assertEquals(1, theListenDomains.size());
        assertEquals(TEST_DOMAIN, theListenDomains.iterator().next());
    }

    @Test
    public void testUnlisten_DomainLess() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(null, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.unlisten(TEST_USER_ID);
        checkLog(4, "test_user_id: unlisten.",
                "Event \"Event: Unlisten (user \"test_user_id\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
    }

    @Test
    public void testUnlisten_DomainLess_2() {
        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.registerUser(null, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered.");

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());

        myEventRegistry.unlisten(null, TEST_USER_ID);
        checkLog(4, "test_user_id: unlisten.",
                "Event \"Event: Unlisten (user \"test_user_id\")\" added to domain \"service_unlisten_domain\".",
                "User \"test_user_id\" removed.");

        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(TEST_DOMAIN, TEST_USER_ID));
        assertFalse(myEventRegistry.isUserRegistered(DomainFactory.getDomain("X"), TEST_USER_ID));
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).isEmpty());
    }

    @Test
    public void testListen() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_2() throws Exception {
        //Tests listen with a min. waiting time
        EventServiceConfiguration theEventServiceConfiguration = createConfiguration(500, 1500, 9999);
        tearDownEventServiceConfiguration();
        setUp(theEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                    "Configuration changed - EventServiceConfiguration (TestConfiguration)" + PlatformUtil.getNewLine() +
                        "  Min.: 500ms; Max.: 1500ms; Timeout: 9999ms");

        long theStartTime = PlatformUtil.getCurrentTime();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        long theRunTime = PlatformUtil.getCurrentTime() - theStartTime;
        assertTrue(theRunTime >= 1500);

        myEventRegistry.addEvent(TEST_DOMAIN, new DummyEvent());
        theStartTime = PlatformUtil.getCurrentTime();
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        theRunTime = PlatformUtil.getCurrentTime() - theStartTime;
        assertTrue(theRunTime >= 500);
        assertTrue(theRunTime < 1500);
    }

    @Test
    public void testListenError() throws Exception {
        assertNull(myEventRegistry.listen(getLongPollingListener(), "noKnownUser"));
    }

    @Test
    public void testListenError_2() throws Exception {
        assertNull(myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID));

        //test without interrupt
        Date theStartTime = new Date();

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        Date theEndTime = new Date();
        assertTrue((theEndTime.getTime() - theStartTime.getTime()) >= 400);

        //test with interrupt
        theStartTime = new Date();

        logOff();
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        Thread.currentThread().interrupt();
        assertNull(myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID));
        logOn();

        theEndTime = new Date();
        assertFalse((theEndTime.getTime() - theStartTime.getTime()) >= 400);
    }

    @Test
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
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        //The 6th event can not received, because it is added after the max waiting time.
        assertTrue(myEventRegistry.getListenDomains(TEST_USER_ID).contains(TEST_DOMAIN));
        assertTrue(theResult > 0 && theResult < 6);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(6, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_Domain() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 200);
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        startAddEvent(TEST_DOMAIN, 200);
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_Domain_Isolation() throws Exception {
        EventServiceConfiguration theEventServiceConfiguration = createConfiguration(0, 2000, 9999);
        tearDownEventServiceConfiguration();
        setUp(theEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        startAddEvent(TEST_DOMAIN, 250);

        joinThreads();

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

    @Test
    public void testListen_Domain_Multi() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN_2, 400);
        startAddEvent(TEST_DOMAIN_2, 200);
        startAddEvent(TEST_DOMAIN, 600);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        //The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 6);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(6, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_Domain_Multi_2() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN_2, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN_2, 400);
        startAddEvent(TEST_DOMAIN_2, 200);
        startAddEvent(TEST_DOMAIN, 600);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        //max two events for TEST_DOMAIN. The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 3);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(3, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        //the user is added too late to get the events
        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, null);
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
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
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        //max two events for TEST_DOMAIN. The 6th is added after the max waiting time.
        assertTrue(theResult > 0 && theResult < 8);

        //after the max waiting time, all events must be received
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(8, theResult);

        //the user isn't out of time / removed and can get all events with the next listen
        assertEquals(3, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());
    }

    @Test
    public void testListen_EventFilter() throws Exception {
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_EventFilter_2() throws Exception {
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, new EmptyEventFilter());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because another EventFilter was set (the EventFilter doesn't filter events)
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_EventFilter_3() throws Exception {
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because the EventFilter was removed
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testListen_EventFilter_4() throws Exception {
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        int theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 3);
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        //only two events are found, because the EventFilter filters every second event
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(2, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());

        //set event filter to NULL should have the same effect as removeEventFilter
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        startAddEvent(TEST_DOMAIN, 100);
        startAddEvent(TEST_DOMAIN, 200);
        startAddEvent(TEST_DOMAIN, 300);
        startAddEvent(TEST_DOMAIN, 400);
        theResult = myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertTrue(theResult > 0 && theResult < 5);

        //all events found, because the EventFilter was removed
        Thread.sleep(600);
        theResult += myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size();
        assertEquals(4, theResult);

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
    }

    @Test
    public void testAddUserSpecificEvent() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);

        myEventRegistry.addEventUserSpecific(TEST_USER_ID, new DummyEvent());
        checkLog(4, "User specific event \"DummyEvent (id 1)\" added to client id \"test_user_id\".",
                "DummyEvent (id 1) for user \"test_user_id\".");

        myEventRegistry.addEventUserSpecific(TEST_USER_ID_2, new DummyEvent());
        checkLog(6, "User specific event \"DummyEvent (id 2)\" added to client id \"test_user_id_2\".",
                "DummyEvent (id 2) for user \"test_user_id_2\".");
    }

    @Test
    public void testAddUserSpecificEvent_2() throws Exception {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        Thread theAddEventThread = startAddEvent(TEST_USER_ID, 100);

        theAddEventThread.join();

        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());
        //only User 1 should get the event
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());

        //all events got
        joinThreads();
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());
    }

    @Test
    public void testAddUserSpecificEvent_3() throws Exception {
        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID_2, null);

        startAddEvent(TEST_USER_ID, 100);
        joinThreads();

        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());
        //only User 1 should get the event
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());

        //all events got
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());

        //deregister test user 1
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_USER_ID);

        assertFalse(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));

        startAddEvent(TEST_USER_ID, 100);
        joinThreads();

        //no events received, because test user 1 was deregistered
        assertNull(myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID));
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());

        //re-register test user 1
        myEventRegistry.registerUser(DomainFactory.USER_SPECIFIC_DOMAIN, TEST_USER_ID, null);

        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(myEventRegistry.isUserRegistered(TEST_USER_ID_2));

        startAddEvent(TEST_USER_ID, 100);
        joinThreads();

        //no events received, because test user 1 was deregistered
        assertEquals(1, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID).size());
        assertEquals(0, myEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2).size());
    }

    @Test
    public void testAddUserSpecificEvent_Isolation() throws Exception {
        EventServiceConfiguration theEventServiceConfiguration = createConfiguration(0, 2000, 9999);
        tearDownEventServiceConfiguration();
        setUp(theEventServiceConfiguration);

        myEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        setUp(myEventRegistry);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        startAddEvent(TEST_USER_ID, 250);

        joinThreads();

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

    @Test
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

        theEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        //It is waiting for events and will cause a timeout, because the max. waiting time is configured longer than the timeout time.
        //The result is a UnlistenEvent, because the timeout doesn't effect that method, but the next call.
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);

        //wait for the UserActivityScheduler-Thread
        Thread.yield();
        Thread.sleep(theNewEventServiceConfiguration.getTimeoutTime() + 100);

        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));

        UnlistenEvent theUnlistenEvent = (UnlistenEvent)theEvents.get(0).getEvent();
        assertTrue(theUnlistenEvent instanceof DefaultUnlistenEvent);
        assertEquals(TEST_USER_ID, theUnlistenEvent.getUserId());
        assertTrue(theUnlistenEvent.isTimeout());
        assertEquals(2, theUnlistenEvent.getDomains().size()); //a timeout is for all domains
        assertTrue(theUnlistenEvent.getDomains().contains(TEST_DOMAIN));
        assertTrue(theUnlistenEvent.getDomains().contains(DomainFactory.UNLISTEN_DOMAIN));

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNull(theEvents);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));
    }

    @Test
    public void testTimeOut_CustomUnlistenEvent() throws Exception {
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

        theEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, new TestUnlistenEvent("testuser", "123"));

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        //It is waiting for events and will cause a timeout, because the max. waiting time is configured longer than the timeout time.
        //The result is a UnlistenEvent, because the timeout doesn't effect that method, but the next call.
        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);

        //wait for the UserActivityScheduler-Thread
        Thread.yield();
        Thread.sleep(theNewEventServiceConfiguration.getTimeoutTime() + 100);

        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));

        assertTrue(theEvents.get(0).getEvent() instanceof TestUnlistenEvent);
        TestUnlistenEvent theUnlistenEvent = (TestUnlistenEvent)theEvents.get(0).getEvent();
        assertEquals(TEST_USER_ID, theUnlistenEvent.getUserId());
        assertTrue(theUnlistenEvent.isTimeout());
        assertEquals(2, theUnlistenEvent.getDomains().size()); //a timeout is for all domains
        assertTrue(theUnlistenEvent.getDomains().contains(TEST_DOMAIN));
        assertTrue(theUnlistenEvent.getDomains().contains(DomainFactory.UNLISTEN_DOMAIN));
        //custom data which is send to all users with a registered unlisten listener
        assertEquals("testuser", theUnlistenEvent.getUserName());
        assertEquals("123", theUnlistenEvent.getTelephoneNumber());

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNull(theEvents);
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID));
    }

    @Test
    public void testTimeOut_ImportantDomain() throws Exception {
        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();

        theEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);

        //register first user to TEST_DOMAIN
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theEventRegistry.addEvent(TEST_DOMAIN, new DummyEvent());
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        //register second user to TEST_DOMAIN
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID_2, null);
        theEventRegistry.addEvent(TEST_DOMAIN, new DummyEvent());
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_DOMAIN, TEST_USER_ID_2);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID_2));

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        Set<Domain> theUnlistenedDomains = ((UnlistenEvent)theEvents.get(0).getEvent()).getDomains();
        assertEquals(1, theUnlistenedDomains.size());
        assertTrue(theUnlistenedDomains.contains(TEST_DOMAIN));
        assertFalse(theUnlistenedDomains.contains(TEST_DOMAIN_2));
        assertEquals(TEST_USER_ID_2, ((UnlistenEvent)theEvents.get(0).getEvent()).getUserId());
    }

    @Test
    public void testTimeOut_UnimportantDomain() throws Exception {
        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();

        theEventRegistry.registerUnlistenEvent(TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN, null);

        //register first user to TEST_DOMAIN
        theEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        theEventRegistry.addEvent(TEST_DOMAIN, new DummyEvent());
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        List<DomainEvent> theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        //register second user to TEST_DOMAIN_2
        theEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID_2, null);
        theEventRegistry.addEvent(TEST_DOMAIN_2, new DummyEvent());
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID_2);
        assertNotNull(theEvents);
        assertEquals(1, theEvents.size());
        assertFalse(theEvents.get(0).getEvent() instanceof UnlistenEvent);

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));
        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID_2));

        myEventRegistry.unlisten(TEST_DOMAIN_2, TEST_USER_ID_2);

        assertTrue(theEventRegistry.isUserRegistered(TEST_USER_ID));
        assertFalse(theEventRegistry.isUserRegistered(TEST_USER_ID_2));

        theEvents = theEventRegistry.listen(getLongPollingListener(), TEST_USER_ID);
        assertNotNull(theEvents);
        assertTrue(theEvents.isEmpty());
    }

    @Test
    public void testChangeEventFilter() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "test_user_id: EventFilter changed for domain \"test_domain\".");
        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(3, "test_user_id: EventFilter removed from domain \"test_domain\".");
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
    }

    @Test
    public void testChangeEventFilter_2() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        assertNotNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(3, "test_user_id: EventFilter removed from domain \"test_domain\".");
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, TEST_USER_ID));
    }

    @Test
    public void testChangeEventFilter_Error() {
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, "noKnownUser"));
        myEventRegistry.setEventFilter(TEST_DOMAIN, "noKnownUser", new EventFilterTestMode());
        checkLog(0);
        assertNull(myEventRegistry.getEventFilter(TEST_DOMAIN, "noKnownUser"));
    }

    @Test
    public void testRemoveEventFilter() {
        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, new EventFilterTestMode());
        checkLog(2, "User \"test_user_id\" registered for domain \"test_domain\".",
                "test_user_id: EventFilter changed for domain \"test_domain\".");

        myEventRegistry.registerUser(TEST_DOMAIN_2, TEST_USER_ID, new EventFilterTestMode());
        checkLog(4, "User \"test_user_id\" registered for domain \"test_domain_2\".",
                "test_user_id: EventFilter changed for domain \"test_domain_2\".");

        myEventRegistry.removeEventFilter(TEST_DOMAIN_2, TEST_USER_ID);
        checkLog(5, "test_user_id: EventFilter removed from domain \"test_domain_2\".");

        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(6, "test_user_id: EventFilter removed from domain \"test_domain\".");
    }

    @Test
    public void testRemoveEventFilter_2() {
        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        checkLog(0);
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(0);

        myEventRegistry.registerUser(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1, "User \"test_user_id\" registered for domain \"test_domain\".");

        myEventRegistry.removeEventFilter(TEST_DOMAIN, TEST_USER_ID);
        checkLog(1);
        myEventRegistry.setEventFilter(TEST_DOMAIN, TEST_USER_ID, null);
        checkLog(1);
    }

    private void checkLog(int anExpectedLogSize, String... anExpectedLogMessageList) {
        assertEquals(anExpectedLogSize, myTestLoggingHandler.getLogMessageSize());
        assertTrue(myTestLoggingHandler.containsMessage(anExpectedLogMessageList));
    }

    private class EmptyEventFilter implements EventFilter
    {
        public boolean match(Event anEvent) {
            return false;
        }
    }

    private class TestUnlistenEvent extends DefaultUnlistenEvent
    {
        private String myUserName;
        private String myTelephoneNumber;

        private TestUnlistenEvent(String aUserName, String aTelephoneNumber) {
            myUserName = aUserName;
            myTelephoneNumber = aTelephoneNumber;
        }

        public String getUserName() {
            return myUserName;
        }

        public String getTelephoneNumber() {
            return myTelephoneNumber;
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
            List<DomainEvent> theEvents = myEventRegistry.listen(getLongPollingListener(), myUser);
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
        private Queue<String> myMessages;

        private TestLoggingHandler() {
            myMessages = new ConcurrentLinkedQueue<String>();
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
