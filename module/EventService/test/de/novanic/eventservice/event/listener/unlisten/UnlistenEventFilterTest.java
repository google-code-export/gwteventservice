/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.event.listener.unlisten;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.Event;

import java.util.Arrays;

/**
 * @author sstrohschein
 *         <br>Date: 17.08.2009
 *         <br>Time: 23:21:17
 */
public class UnlistenEventFilterTest extends TestCase
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");
    private static final Domain TEST_DOMAIN_3 = DomainFactory.getDomain("test_domain_3");

    public void testMatch() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, TEST_USER_ID, false);

        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(Arrays.asList(TEST_DOMAIN));
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN_2, TEST_USER_ID, false);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
    }

    public void testMatch_2() {
        UnlistenEventFilter theUnlistenEventFilter = new UnlistenEventFilter(Arrays.asList(TEST_DOMAIN, TEST_DOMAIN_2));
        assertFalse(theUnlistenEventFilter.match(new Event() {}));

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN_2, TEST_USER_ID, false);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN, TEST_USER_ID, false);
        assertFalse(theUnlistenEventFilter.match(theUnlistenEvent));

        theUnlistenEvent = new DefaultUnlistenEvent(TEST_DOMAIN_3, TEST_USER_ID, false);
        assertTrue(theUnlistenEventFilter.match(theUnlistenEvent));
    }
}