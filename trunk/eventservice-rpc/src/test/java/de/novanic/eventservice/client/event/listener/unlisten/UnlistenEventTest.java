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
package de.novanic.eventservice.client.event.listener.unlisten;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sstrohschein
 * Date: 28.07.2008
 * <br>Time: 21:49:29
 */
public class UnlistenEventTest extends TestCase
{
    private Domain myTestDomain;
    private Domain myTestDomain_2;

    public void setUp() {
        myTestDomain = DomainFactory.getDomain("testDomain");
        myTestDomain_2 = DomainFactory.getDomain("testDomain_2");
    }

    public void testInit() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        theUnlistenEvent.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        theUnlistenEvent.setUserId("XY");
        theUnlistenEvent.setTimeout(true);
        theUnlistenEvent.setLocal(true);
        assertEquals(1, theUnlistenEvent.getDomains().size());
        assertEquals(myTestDomain, theUnlistenEvent.getDomains().iterator().next());
        assertEquals("XY", theUnlistenEvent.getUserId());
        assertTrue(theUnlistenEvent.isTimeout());
        assertTrue(theUnlistenEvent.isLocal());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertTrue(theUnlistenEvent.toString().contains(myTestDomain.getName()));
        assertTrue(theUnlistenEvent.toString().contains("timeout"));
    }

    public void testInit_2() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(myTestDomain)), "XY", true);
        assertEquals(1, theUnlistenEvent.getDomains().size());
        assertEquals(myTestDomain, theUnlistenEvent.getDomains().iterator().next());
        assertEquals("XY", theUnlistenEvent.getUserId());
        assertTrue(theUnlistenEvent.isTimeout());
        assertFalse(theUnlistenEvent.isLocal());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertTrue(theUnlistenEvent.toString().contains(myTestDomain.getName()));
        assertTrue(theUnlistenEvent.toString().contains("timeout"));
    }

    public void testInit_3() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(new HashSet<Domain>(Arrays.asList(myTestDomain)), "XY", true, true);
        assertEquals(1, theUnlistenEvent.getDomains().size());
        assertEquals(myTestDomain, theUnlistenEvent.getDomains().iterator().next());
        assertEquals("XY", theUnlistenEvent.getUserId());
        assertTrue(theUnlistenEvent.isTimeout());
        assertTrue(theUnlistenEvent.isLocal());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertTrue(theUnlistenEvent.toString().contains(myTestDomain.getName()));
        assertTrue(theUnlistenEvent.toString().contains("timeout"));
    }

    public void testInit_4() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        assertNull(theUnlistenEvent.getDomains());
        assertNull(theUnlistenEvent.getUserId());
        assertFalse(theUnlistenEvent.isTimeout());
        assertFalse(theUnlistenEvent.isLocal());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertFalse(theUnlistenEvent.toString().contains("Null") || theUnlistenEvent.toString().contains("NULL") || theUnlistenEvent.toString().contains("null"));
        assertFalse(theUnlistenEvent.toString().contains("timeout"));
    }

    public void testEquals() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent();
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
        assertEquals(theUnlistenEvent.hashCode(), theUnlistenEvent_2.hashCode());
        
        UnlistenEvent theUnlistenEvent_3 = new DefaultUnlistenEvent();
        theUnlistenEvent_3.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        theUnlistenEvent_3.setUserId("XY");
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_3));
        assertFalse(theUnlistenEvent.toString().equals(theUnlistenEvent_3.toString()));

        assertEquals(theUnlistenEvent, theUnlistenEvent);
        UnlistenEvent theUnlistenEventNull = null;
        assertFalse(theUnlistenEvent.equals(theUnlistenEventNull));
    }

    public void testEquals_2() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent();
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
        assertEquals(theUnlistenEvent.hashCode(), theUnlistenEvent_2.hashCode());

        theUnlistenEvent.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        theUnlistenEvent.setUserId("XY");
        theUnlistenEvent.setTimeout(true);
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setUserId("XY");
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setTimeout(true);
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
    }

    public void testEquals_3() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent();
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
        assertEquals(theUnlistenEvent.hashCode(), theUnlistenEvent_2.hashCode());

        theUnlistenEvent.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        theUnlistenEvent.setUserId("XY");
        theUnlistenEvent.setTimeout(true);
        theUnlistenEvent.setLocal(true);
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setUserId("XY");
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setTimeout(true);
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setLocal(true);
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
    }

    public void testEquals_4() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent();
        UnlistenEvent theUnlistenEvent_2 = new DefaultUnlistenEvent();
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
        assertEquals(theUnlistenEvent.hashCode(), theUnlistenEvent_2.hashCode());

        theUnlistenEvent.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        theUnlistenEvent.setUserId("XY");
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setDomains(new HashSet<Domain>(Arrays.asList(myTestDomain)));
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_2));

        theUnlistenEvent_2.setUserId("XY");
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
    }

    public void testToString() {
        Set<Domain> theDomains = new HashSet<Domain>(1);
        theDomains.add(myTestDomain);

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(theDomains, "XY", false);
        assertEquals("Event: Unlisten (user \"XY\" for domain \"testDomain\")", theUnlistenEvent.toString());
    }

    public void testToString_MultiDomain() {
        Set<Domain> theDomains = new HashSet<Domain>(2);
        theDomains.add(myTestDomain);
        theDomains.add(myTestDomain_2);

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(theDomains, "XY", false);
        assertEquals("Event: Unlisten (user \"XY\" for 2 domains)", theUnlistenEvent.toString());
    }

    public void testToString_Timeout() {
        Set<Domain> theDomains = new HashSet<Domain>(1);
        theDomains.add(myTestDomain);

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(theDomains, "XY", true);
        assertEquals("Event: Unlisten(timeout) (user \"XY\" for domain \"testDomain\")", theUnlistenEvent.toString());
    }

    public void testToString_Local() {
        Set<Domain> theDomains = new HashSet<Domain>(1);
        theDomains.add(myTestDomain);

        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(theDomains, "XY", false, true);
        assertEquals("Event: Unlisten(local) (user \"XY\" for domain \"testDomain\")", theUnlistenEvent.toString());
    }

    public void testToString_DomainLess() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(null, "XY", false);
        assertEquals("Event: Unlisten (user \"XY\")", theUnlistenEvent.toString());
    }

    public void testToString_UserLess() {
        UnlistenEvent theUnlistenEvent = new DefaultUnlistenEvent(null, null, false);
        assertEquals("Event: Unlisten (user not available)", theUnlistenEvent.toString());
    }
}