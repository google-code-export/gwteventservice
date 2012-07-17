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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.EventFilterTestMode;
import de.novanic.eventservice.util.PlatformUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 20.01.2009
 *         <br>Time: 22:04:44
 */
@RunWith(JUnit4.class)
public class UserInfoTest
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");

    private UserInfo myUserInfo;

    @Before
    public void setUp() {
        myUserInfo = new UserInfo("test_user_id");
    }

    @Test
    public void testGetUserId() {
        String theUserId = myUserInfo.getUserId();
        assertEquals("test_user_id", theUserId);
        assertEquals(theUserId, myUserInfo.toString());
    }

    @Test
    public void testAddEvent() {
        assertTrue(myUserInfo.retrieveEvents().isEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.retrieveEvents().size());
    }

    @Test
    public void testAddEvent_2() {
        assertTrue(myUserInfo.retrieveEvents().isEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.retrieveEvents().size());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.retrieveEvents().size());

        assertTrue(myUserInfo.retrieveEvents().isEmpty());
    }

    @Test
    public void testAddEvent_3() {
        assertTrue(myUserInfo.retrieveEvents().isEmpty());
        for(int i = 0; i < 100; i++) {
            myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
            myUserInfo.addEvent(TEST_DOMAIN_2, new DummyEvent());
            myUserInfo.addEvent(TEST_DOMAIN_2, new DummyEvent());
            myUserInfo.addEvent(null, new DummyEvent());
            myUserInfo.addEvent(null, new DummyEvent());
            myUserInfo.addEvent(null, new DummyEvent());
        }

        List<DomainEvent> theEvents = myUserInfo.retrieveEvents();
        assertEquals(600, theEvents.size());
        assertTrue(myUserInfo.retrieveEvents().isEmpty()); //all events got

        int theDomainEventCount_1 = 0;
        int theDomainEventCount_2 = 0;
        int theUserSpecificEventCount = 0;
        for(DomainEvent theDomainEvent: theEvents) {
            if(theDomainEvent.getDomain() == null) {
                theUserSpecificEventCount++;
            } else if(TEST_DOMAIN.equals(theDomainEvent.getDomain())) {
                theDomainEventCount_1++;
            } else if(TEST_DOMAIN_2.equals(theDomainEvent.getDomain())) {
                theDomainEventCount_2++;
            }
        }
        assertEquals(100, theDomainEventCount_1);
        assertEquals(200, theDomainEventCount_2);
        assertEquals(300, theUserSpecificEventCount);

        assertTrue(myUserInfo.retrieveEvents().isEmpty()); //all events got
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertEquals(1, myUserInfo.retrieveEvents().size());
        assertTrue(myUserInfo.retrieveEvents().isEmpty()); //all events got
    }

    @Test
    public void testIsEventsEmpty() {
        assertTrue(myUserInfo.isEventsEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertFalse(myUserInfo.isEventsEmpty());
        assertFalse(myUserInfo.isEventsEmpty());
        
        assertEquals(1, myUserInfo.retrieveEvents().size());
        assertTrue(myUserInfo.retrieveEvents().isEmpty()); //all events got
        assertTrue(myUserInfo.isEventsEmpty());
    }

    @Test
    public void testIsEventsEmpty_2() {
        assertTrue(myUserInfo.isEventsEmpty());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        myUserInfo.addEvent(TEST_DOMAIN, new DummyEvent());
        assertFalse(myUserInfo.isEventsEmpty());
        assertFalse(myUserInfo.isEventsEmpty());

        assertEquals(3, myUserInfo.retrieveEvents().size());
        assertTrue(myUserInfo.isEventsEmpty());
        assertTrue(myUserInfo.retrieveEvents().isEmpty()); //all events got
    }

    @Test
    public void testSetEventFilter() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));

        final EventFilterTestMode theTestEventFilter = new EventFilterTestMode();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));
        
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));
    }

    @Test
    public void testSetEventFilter_2() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));

        final EventFilterTestMode theTestEventFilter = new EventFilterTestMode();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));

        final EventFilterTestMode theTestEventFilter_2 = new EventFilterTestMode();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter_2);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter_2, myUserInfo.getEventFilter(TEST_DOMAIN));
        assertFalse(theTestEventFilter.equals(myUserInfo.getEventFilter(TEST_DOMAIN)));

        assertNull(myUserInfo.getEventFilter(DomainFactory.getDomain("otherDomain")));
    }

    @Test
    public void testSetEventFilter_Error() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        myUserInfo.setEventFilter(TEST_DOMAIN, null);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    @Test
    public void testGetEventFilter_Error() {
        assertNull(myUserInfo.getEventFilter(null));
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    @Test
    public void testRemoveEventFilter() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));

        final EventFilterTestMode theTestEventFilter = new EventFilterTestMode();
        myUserInfo.setEventFilter(TEST_DOMAIN, theTestEventFilter);
        assertNotNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertEquals(theTestEventFilter, myUserInfo.getEventFilter(TEST_DOMAIN));

        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));

        //second time to try removing an not existing EventFilter
        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    @Test
    public void testRemoveEventFilter_Error() {
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        myUserInfo.removeEventFilter(null);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
        assertNull(myUserInfo.getEventFilter(null));

        myUserInfo.removeEventFilter(TEST_DOMAIN);
        assertNull(myUserInfo.getEventFilter(TEST_DOMAIN));
    }

    @Test
    public void testSetGetLastActivityTime() {
        long theLastActivityTime = myUserInfo.getLastActivityTime();
        assertTrue(theLastActivityTime > 0);
        assertTrue(theLastActivityTime <= PlatformUtil.getCurrentTime());

        myUserInfo.setLastActivityTime(0);
        assertEquals(0, myUserInfo.getLastActivityTime());
    }

    @Test
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

    @Test
    public void testCompareTo() {
        UserInfo theUserInfo = new UserInfo("1");
        UserInfo theUserInfo_2 = new UserInfo("2");
        assertEquals(-1, theUserInfo.compareTo(theUserInfo_2));
        assertEquals(1, theUserInfo_2.compareTo(theUserInfo));
        assertEquals(0, theUserInfo.compareTo(new UserInfo("1")));

        Comparable<UserInfo> theComparableUserInfo = new UserInfo("1");
        assertEquals(0, theComparableUserInfo.compareTo(new UserInfo("1")));
    }
}