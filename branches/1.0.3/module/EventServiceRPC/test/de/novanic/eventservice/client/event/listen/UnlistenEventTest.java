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
package de.novanic.eventservice.client.event.listen;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 * Date: 28.07.2008
 * <br>Time: 21:49:29
 */
public class UnlistenEventTest extends TestCase
{
    private Domain myTestDomain;

    public void setUp() {
        myTestDomain = DomainFactory.getDomain("testDomain");
    }

    public void testInit() {
        UnlistenEvent theUnlistenEvent = new UnlistenEvent(myTestDomain);
        assertEquals(myTestDomain, theUnlistenEvent.getDomain());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertTrue(theUnlistenEvent.toString().contains(myTestDomain.getName()));
    }

    public void testInit_2() {
        UnlistenEvent theUnlistenEvent = new UnlistenEvent();
        assertNull(theUnlistenEvent.getDomain());

        //test must-haves for toString
        assertTrue(theUnlistenEvent.toString().contains("Unlisten") || theUnlistenEvent.toString().contains("unlisten"));
        assertFalse(theUnlistenEvent.toString().contains("Null") || theUnlistenEvent.toString().contains("NULL") || theUnlistenEvent.toString().contains("null"));
    }

    public void testEquals() {
        UnlistenEvent theUnlistenEvent = new UnlistenEvent();
        UnlistenEvent theUnlistenEvent_2 = new UnlistenEvent();
        assertEquals(theUnlistenEvent, theUnlistenEvent_2);
        assertEquals(theUnlistenEvent.hashCode(), theUnlistenEvent_2.hashCode());
        
        UnlistenEvent theUnlistenEvent_3 = new de.novanic.eventservice.client.event.listen.UnlistenEvent(myTestDomain);
        assertFalse(theUnlistenEvent.equals(theUnlistenEvent_3));
        assertFalse(theUnlistenEvent.toString().equals(theUnlistenEvent_3.toString()));

        assertEquals(theUnlistenEvent, theUnlistenEvent);
        UnlistenEvent theUnlistenEventNull = null;
        assertFalse(theUnlistenEvent.equals(theUnlistenEventNull));
    }
}