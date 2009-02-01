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
package de.novanic.eventservice.service.registry.user;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.testhelper.DummyEvent;
import de.novanic.eventservice.service.testhelper.TestEventFilter;

/**
 * @author sstrohschein
 *         <br>Date: 20.01.2009
 *         <br>Time: 22:04:44
 */
public class UserInfoTest extends TestCase
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");

    private UserInfo myUserInfo;

    public void setUp() {
        myUserInfo = new UserInfo("test_user_id");
    }

    public void testGetUserId() {
        String theUserId = myUserInfo.getUserId();
        assertEquals("test_user_id", theUserId);
        assertEquals(theUserId, myUserInfo.toString());
    }

    public void testAddEvent() {
        assertTrue(myUserInfo.getEvents().isEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.getEvents().size());
    }

    public void testClearEvent() {
        assertTrue(myUserInfo.getEvents().isEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.getEvents().size());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(2, myUserInfo.getEvents().size());

        myUserInfo.clearEvents();
        assertTrue(myUserInfo.getEvents().isEmpty());
    }

    public void testSetEventFilter() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));

        final TestEventFilter theTestEventFilter = new TestEventFilter();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));
        
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));
    }

    public void testSetEventFilter_2() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));

        final TestEventFilter theTestEventFilter = new TestEventFilter();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));

        final TestEventFilter theTestEventFilter_2 = new TestEventFilter();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter_2);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter_2, myUserInfo.getEventFilter(TEST_DOMAIN));
        assertFalse(theTestEventFilter.equals(myUserInfo.getEventFilter(TEST_DOMAIN)));

        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));
    }

    public void testSetEventFilter_Error() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        myUserInfo.setEventFilter(TEST_DOMAIN, null);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    public void testRemoveEventFilter() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));

        final TestEventFilter theTestEventFilter = new TestEventFilter();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));

        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));

        //second time to try removing an not existing EventFilter
        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    public void testRemoveEventFilter_Error() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        myUserInfo.removeEventFilter(null);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(null));

        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    public void testEquals() {
        UserInfo theOtherUserInfo = new UserInfo("test_user_id");
        assertEquals(myUserInfo, myUserInfo);
        assertEquals(myUserInfo.hashCode(), myUserInfo.hashCode());

        assertNotSame(myUserInfo, theOtherUserInfo);
        assertEquals(myUserInfo, theOtherUserInfo);
        assertEquals(myUserInfo.hashCode(), theOtherUserInfo.hashCode());

        theOtherUserInfo = new UserInfo("test_user_id_2");
        assertFalse(myUserInfo.equals(theOtherUserInfo));

        theOtherUserInfo = null;
        assertFalse(myUserInfo.equals(theOtherUserInfo));
    }
}