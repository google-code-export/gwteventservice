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
package de.novanic.eventservice.event.listener.unlisten;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.service.registry.domain.ListenDomainAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 17.08.2009
 *         <br>Time: 23:21:17
 */
@RunWith(JUnit4.class)
public class UnlistenEventFilterTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    @Test
    public void testMatch() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), TEST_USER_ID, false);

        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(new DummyListenDomainAccessor(TEST_DOMAIN), TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN_2)), TEST_USER_ID, false);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
    }

    @Test
    public void testMatch_2() {
        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(new DummyListenDomainAccessor(TEST_DOMAIN, TEST_DOMAIN_2), TEST_USER_ID, UnlistenEventListener.Scope.UNLISTEN);
        assertFalse(theUnlistenEventFilter.match(new Event() {}));

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN_2)), TEST_USER_ID, false);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), TEST_USER_ID, false);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN_3)), TEST_USER_ID, false);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
    }

    @Test
    public void testMatch_Timeout() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), TEST_USER_ID, false);
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN_2)), TEST_USER_ID, false);

        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(new DummyListenDomainAccessor(TEST_DOMAIN), TEST_USER_ID, UnlistenEventListener.Scope.TIMEOUT);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent_2));

        theUnlistenEvent.setTimeout(true);
        theUnlistenEvent_2.setTimeout(true);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent_2));
    }

    @Test
    public void testMatch_Local() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN)), TEST_USER_ID, false);
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(TEST_DOMAIN_2)), TEST_USER_ID, false);

        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(new DummyListenDomainAccessor(TEST_DOMAIN), TEST_USER_ID, UnlistenEventListener.Scope.LOCAL);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent_2));

        theUnlistenEvent.setTimeout(true);
        theUnlistenEvent_2.setTimeout(true);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent_2));
    }

    private class DummyListenDomainAccessor implements ListenDomainAccessor
    {
        private Set<Domain> myDomains;

        private DummyListenDomainAccessor(Domain... aDomains) {
            myDomains = new HashSet<Domain>(Arrays.asList(aDomains));
        }

        public Set<Domain> getListenDomains(String aUserId) {
            return myDomains;
        }

        public Set<Domain> getListenDomains() {
            return myDomains;
        }
    }
}