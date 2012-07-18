/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschränkt)
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

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGenerator;
import de.novanic.eventservice.service.connection.id.SessionExtendedConnectionIdGenerator;
import de.novanic.eventservice.service.connection.strategy.connector.longpolling.LongPollingServerConnector;
import de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector;
import de.novanic.eventservice.test.testhelper.*;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import de.novanic.eventservice.EventServiceServerThreadingTest;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.loader.PropertyConfigurationLoader;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 * Date: 05.06.2008
 * <br>Time: 23:26:19
 */
@RunWith(JUnit4.class)
public class EventServiceImplTest extends EventServiceServerThreadingTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    private EventServiceImpl myEventService;

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        tearDownEventServiceConfiguration();

        if(myEventService != null) {
            myEventService.unlisten();
        }
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
    }

    @Test
    public void testInit() throws Exception {
        initEventService();

        EventServiceImpl theEventService = new EventServiceImpl();
        try {
            theEventService.getActiveListenDomains();
            fail("Exception expected, because no client id can be found!");
        } catch(Exception e) {}

        theEventService = new DummyEventServiceImpl();

        assertEquals(0, theEventService.getActiveListenDomains().size());
    }

    @Test
    public void testInit_2() throws Exception {
        final EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        //remove the PropertyConfigurationLoaders, because there is a eventservice.properties in the classpath and that prevents the WebDescriptorConfigurationLoader from loading.
        theEventServiceConfigurationFactory.removeConfigurationLoader(new PropertyConfigurationLoader());

        myEventService = new DummyEventServiceImpl(new DummyServletConfig());
        super.setUp(myEventService);

        assertEquals(0, myEventService.getActiveListenDomains().size());

        //Configuration of WebDescriptorConfigurationLoader (see DummyServletConfig). That configuration and ConfigurationLoader
        //was initialized with the init-method of EventServiceImpl. 
        EventServiceConfiguration theConfiguration = theEventServiceConfigurationFactory.loadEventServiceConfiguration();
        assertEquals(Integer.valueOf(40000), theConfiguration.getMaxWaitingTime());
        assertEquals(Integer.valueOf(5000), theConfiguration.getMinWaitingTime());
        assertEquals(Integer.valueOf(120000), theConfiguration.getTimeoutTime());
    }

    @Test
    public void testInitEventService() throws Exception {
        final EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        //remove the PropertyConfigurationLoaders, because there is a eventservice.properties in the classpath and that prevents the WebDescriptorConfigurationLoader from loading.
        theEventServiceConfigurationFactory.removeConfigurationLoader(new PropertyConfigurationLoader());

        myEventService = new DummyEventServiceImpl(new DummyServletConfig(SessionConnectionIdGenerator.class.getName()));
        super.setUp(myEventService);

        EventServiceConfigurationTransferable theEventServiceConfigurationTransferable = myEventService.initEventService();
        assertNull(theEventServiceConfigurationTransferable.getConnectionId());
    }

    @Test
    public void testInitEventService_2() throws Exception {
        setUp(createConfiguration(0, 30000, 90000, SessionExtendedConnectionIdGenerator.class.getName(), LongPollingServerConnector.class.getName()));

        myEventService = new DummyEventServiceImpl(new DummyServletConfig(SessionExtendedConnectionIdGenerator.class.getName()));
        super.setUp(myEventService);

        EventServiceConfigurationTransferable theEventServiceConfigurationTransferable = myEventService.initEventService();
        assertEquals("test_user_id", theEventServiceConfigurationTransferable.getConnectionId());
    }

    @Test
    public void testInitEventService_Error() throws Exception {
        setUp(createConfiguration(0, 30000, 90000, SessionExtendedConnectionIdGenerator.class.getName(), LongPollingServerConnector.class.getName()));

        myEventService = new DummyEventServiceImpl_2();
        try {
            super.setUp(myEventService);
            myEventService.initEventService();
            fail("Exception expected, because no session / request is available!");
        } catch(Exception e) {}
    }

    @Test
    public void testListen() throws Exception {
        initEventService();

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

    @Test
    public void testListen_2() throws Exception {
        initEventService();

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

    @Test
    public void testListen_3() throws Exception {
        initEventService();

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

    @Test
    public void testListen_4() throws Exception {
        initEventService();

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

    @Test
    public void testListen_5() throws Exception {
        initEventService();

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

        //depend on the timing, it could be that the two events recognized with the first listen or that one get recognized with the first and one with the second listen.
        int theEventCount = joinListen(theListenResult);
        assertTrue(theEventCount == 1 || theEventCount == 2);
        if(theEventCount == 1) {
            theListenResult = startListen();
            assertEquals(1, joinListen(theListenResult));
        }
    }

    @Test
    public void testListen_UserSpecific() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
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

    @Test
    public void testRegisterMultiDomain() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
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
        assertEquals(0, myEventService.listen().size()); //Event is for a other domain
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

    @Test
    public void testRegisterMultiDomain_2() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();

        assertNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_3));

        Set<Domain> theDomains = new HashSet<Domain>();
        theDomains.add(TEST_DOMAIN);
        theDomains.add(TEST_DOMAIN_3);
        myEventService.register(theDomains, new EventFilterTestMode());

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(TEST_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN_3, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN_3));

        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN_3));

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
        assertEquals(0, myEventService.listen().size()); //Event is for a other domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for a other domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_2, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is for a other domain
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(0, myEventService.listen().size()); //Event is filtered by TestEventFilter,
        // because the three events above are for a other domain and the EventFilter wasn't triggered
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        myEventService.addEvent(TEST_DOMAIN_3, new ListenCycleCancelEvent());
        Thread.sleep(100);
        assertEquals(1, myEventService.listen().size());
        Thread.sleep(400);
        assertEquals(0, myEventService.listen().size());

        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN_3));
    }

    @Test
    public void testRegisterUnlistenEvent() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();
        myEventService.initEventService();

        myEventService.register(TEST_DOMAIN);
        final DefaultUnlistenEvent theCustomUnlistenEvent = new DefaultUnlistenEvent();
        myEventService.registerUnlistenEvent(UnlistenEventListener.Scope.UNLISTEN, theCustomUnlistenEvent);

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(DomainFactory.UNLISTEN_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_3));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        assertEquals(1, myEventService.listen().size());
        assertEquals(0, myEventService.listen().size());

        myEventService.unlisten(TEST_DOMAIN);
        final List<DomainEvent> theEvents = myEventService.listen();
        assertEquals(1, theEvents.size());
        assertEquals(DomainFactory.UNLISTEN_DOMAIN, theEvents.get(0).getDomain());
        assertTrue(theEvents.get(0).getEvent() instanceof UnlistenEvent);
        assertEquals(theCustomUnlistenEvent, theEvents.get(0).getEvent());

        UnlistenEvent theUnlistenEvent = (UnlistenEvent)theEvents.get(0).getEvent();
        assertEquals(1, theUnlistenEvent.getDomains().size());
        assertEquals(TEST_DOMAIN, theUnlistenEvent.getDomains().iterator().next());
        assertEquals(TEST_USER_ID, theUnlistenEvent.getUserId());
        assertFalse(theUnlistenEvent.isTimeout());
    }

    @Test
    public void testRegisterUnlistenEvent_FalseDomain() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
        myEventService = new DummyEventServiceImpl();
        myEventService.initEventService();

        myEventService.register(TEST_DOMAIN);
        final DefaultUnlistenEvent theCustomUnlistenEvent = new DefaultUnlistenEvent();
        myEventService.registerUnlistenEvent(UnlistenEventListener.Scope.UNLISTEN, theCustomUnlistenEvent);

        final List<Domain> theActiveDomains = new ArrayList<Domain>(myEventService.getActiveListenDomains());
        Collections.sort(theActiveDomains);

        assertEquals(2, theActiveDomains.size());
        assertEquals(DomainFactory.UNLISTEN_DOMAIN, theActiveDomains.get(0));
        assertEquals(TEST_DOMAIN, theActiveDomains.get(1));
        assertTrue(myEventService.isUserRegistered(TEST_DOMAIN));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_2));
        assertFalse(myEventService.isUserRegistered(TEST_DOMAIN_3));

        myEventService.addEvent(TEST_DOMAIN, new ListenCycleCancelEvent());
        assertEquals(1, myEventService.listen().size());
        assertEquals(0, myEventService.listen().size());

        myEventService.unlisten(TEST_DOMAIN_2); //false domain
        assertTrue(myEventService.listen().isEmpty());
    }

    @Test
    public void testRegisterEventFilter() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
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

        assertNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_3));

        myEventService.registerEventFilter(TEST_DOMAIN_2, new EventFilterTestMode());

        assertNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_3));

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

        myEventService.registerEventFilter(TEST_DOMAIN, new EventFilterTestMode());

        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_3));

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

        assertNull(myEventService.getEventFilter(TEST_DOMAIN));
        assertNotNull(myEventService.getEventFilter(TEST_DOMAIN_2));
        assertNull(myEventService.getEventFilter(TEST_DOMAIN_3));

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

    @Test
    public void testUnlisten() throws Exception {
        initEventService();

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

    @Test
    public void testUnlisten_2() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 300, 9999));
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

    @Test
    public void testDoGet() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 500, 90000, StreamingServerConnector.class.getName()));
        myEventService = new DummyEventServiceImpl();

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpServletResponse theResponseMock = mock(HttpServletResponse.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(false)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);

        ServletOutputStream theOutputStream = new DummyServletOutputStream(new ByteArrayOutputStream());
        when(theResponseMock.getOutputStream()).thenReturn(theOutputStream);

        final EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        //remove the PropertyConfigurationLoaders, because there is a eventservice.properties in the classpath and that prevents the WebDescriptorConfigurationLoader from loading.
        theEventServiceConfigurationFactory.removeConfigurationLoader(new PropertyConfigurationLoader());

        myEventService = new DummyEventServiceImpl(new DummyServletConfig());
        super.setUp(myEventService);

        myEventService.doGet(theRequestMock, theResponseMock);
    }

    @Test
    public void testDoGet_Streaming() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 500, 90000, StreamingServerConnector.class.getName()));
        myEventService = new DummyEventServiceImpl();

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpServletResponse theResponseMock = mock(HttpServletResponse.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(false)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);

        final ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream theOutputStream = new DummyServletOutputStream(theByteArrayOutputStream);

        when(theResponseMock.getOutputStream()).thenReturn(theOutputStream);

        myEventService.register(TEST_DOMAIN);
        myEventService.addEvent(TEST_DOMAIN, new DummyEvent());

        myEventService.doGet(theRequestMock, theResponseMock);

        myEventService.unlisten(TEST_DOMAIN);

        final String theStreamedEvents = theByteArrayOutputStream.toString();
        assertTrue(theStreamedEvents.contains("test_domain"));
        assertTrue(theStreamedEvents.contains("DummyEvent"));
    }

    @Test
    public void testDoGet_Streaming_Error() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 500, 90000, StreamingServerConnector.class.getName()));
        myEventService = new DummyEventServiceImpl();

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpServletResponse theResponseMock = mock(HttpServletResponse.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(false)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);

        when(theResponseMock.getOutputStream()).thenThrow(new IOException("Test-Exception"));

        try {
            myEventService.doGet(theRequestMock, theResponseMock);
            fail("Exception expected!");
        } catch(ServletException e) {
        } catch(IOException e) {}
    }

    @Test
    public void testDoGet_Streaming_Error_2() throws Exception {
        initEventService();

        tearDownEventServiceConfiguration();
        setUp(createConfiguration(0, 500, 90000, DummyStreamingConnectorNotCloneable.class.getName()));
        myEventService = new DummyEventServiceImpl();

        HttpServletRequest theRequestMock = mock(HttpServletRequest.class);
        HttpServletResponse theResponseMock = mock(HttpServletResponse.class);
        HttpSession theSessionMock = mock(HttpSession.class);

        when(theRequestMock.getSession(false)).thenReturn(theSessionMock);
        when(theSessionMock.getId()).thenReturn(TEST_USER_ID);
        when(theResponseMock.getOutputStream()).thenReturn(new DummyServletOutputStream(new ByteArrayOutputStream()));

        try {
            myEventService.doGet(theRequestMock, theResponseMock);
            fail("Exception expected!");
        } catch(ServletException e) {
            assertTrue(e.getCause() instanceof CloneNotSupportedException);
        } catch(IOException e) {
            assertTrue(e.getCause() instanceof CloneNotSupportedException);
        }
    }

    @Test
    public void testCheckPermutationStrongName() throws Exception {
        initEventService();
        assertNotNull(myEventService);

        boolean isSuccessful = false;
        try {
            myEventService.checkPermutationStrongName();
            isSuccessful = true;
        } finally {
            assertTrue("The execution of checkPermutationStrongName couldn't be completed successfully!", isSuccessful);
        }
    }

    private void initEventService() throws Exception {
        setUp(createConfiguration(0, 30000, 90000));

        myEventService = new DummyEventServiceImpl();
        super.setUp(myEventService);
    }

    private class DummyEventServiceImpl extends EventServiceImpl
    {
        private DummyEventServiceImpl() throws ServletException {
            this(null);
        }

        private DummyEventServiceImpl(ServletConfig aConfig) throws ServletException {
            init(aConfig);
        }

        protected String getClientId() {
            return TEST_USER_ID;
        }

        protected String generateClientId() {
            return TEST_USER_ID;
        }
    }

    private class DummyEventServiceImpl_2 extends EventServiceImpl
    {
        private DummyEventServiceImpl_2() throws ServletException {
            init(null);
        }

        protected String getClientId() {
            return TEST_USER_ID;
        }
    }

    private class DummyServletConfig implements ServletConfig
    {
        private ConcurrentHashMap<String, String> myParameterMap;

        public DummyServletConfig() {
            this(null);
        }

        public DummyServletConfig(String aConnectionIdGeneratorClassName) {
            myParameterMap = new ConcurrentHashMap<String, String>(4);
            myParameterMap.put(ConfigParameter.MAX_WAITING_TIME_TAG.declaration(), String.valueOf(40000));
            myParameterMap.put(ConfigParameter.MIN_WAITING_TIME_TAG.declaration(), String.valueOf(5000));
            myParameterMap.put(ConfigParameter.TIMEOUT_TIME_TAG.declaration(), String.valueOf(120000));
            myParameterMap.put(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG.declaration(), String.valueOf(2));
            myParameterMap.put(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR.declaration(), StreamingServerConnector.class.getName());
            if(aConnectionIdGeneratorClassName != null) {
                myParameterMap.put(ConfigParameter.CONNECTION_ID_GENERATOR.declaration(), aConnectionIdGeneratorClassName);
            }
        }

        public String getServletName() {
            return "DummyEventServiceImpl_TestServlet";
        }

        public ServletContext getServletContext() {
            return null;
        }

        public String getInitParameter(String aParameterName) {
            return myParameterMap.get(aParameterName);
        }

        public Enumeration getInitParameterNames() {
            return myParameterMap.keys();
        }
    }

    public static class DummyStreamingConnectorNotCloneable extends StreamingServerConnector
    {
        public DummyStreamingConnectorNotCloneable(EventServiceConfiguration aConfiguration) throws EventServiceException {
            super(aConfiguration);
        }

        public void prepare(HttpServletResponse aResponse) throws EventServiceException {}

        public Object clone() throws CloneNotSupportedException {
            super.clone();
            throw new CloneNotSupportedException("Test-Exception");
        }
    }
}
