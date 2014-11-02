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
package de.novanic.eventservice.client.event.filter;

import de.novanic.eventservice.client.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 21:13:14
 */
@RunWith(JUnit4.class)
public class DefaultEventFilterTest
{
    @Test
    public void testMatch() {
        EventFilter theEventFilter = new DefaultEventFilter();
        assertFalse(theEventFilter.match(new Event() {}));
    }

    @Test
    public void testAppend() {
        AppendableEventFilter theEventFilter = new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 0 == ((TestEvent)anEvent).getId();
            }
        }.attach(new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 1 == ((TestEvent)anEvent).getId();
            }
        }.attach(new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 2 == ((TestEvent)anEvent).getId();
            }
        }));

        assertFalse(theEventFilter.match(new TestEvent(-1)));
        assertTrue(theEventFilter.match(new TestEvent(0)));
        assertTrue(theEventFilter.match(new TestEvent(1)));
        assertTrue(theEventFilter.match(new TestEvent(2)));
        assertFalse(theEventFilter.match(new TestEvent(3)));
    }

    @Test
    public void testAppend_2() {
        AppendableEventFilter theEventFilter = new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 1 == ((TestEvent)anEvent).getId();
            }
        }.attach(new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 2 == ((TestEvent)anEvent).getId();
            }
        }.attach(new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 0 == ((TestEvent)anEvent).getId();
            }
        }));

        assertFalse(theEventFilter.match(new TestEvent(-1)));
        assertTrue(theEventFilter.match(new TestEvent(0)));
        assertTrue(theEventFilter.match(new TestEvent(1)));
        assertTrue(theEventFilter.match(new TestEvent(2)));
        assertFalse(theEventFilter.match(new TestEvent(3)));
    }

    @Test
    public void testAppend_3() {
        AppendableEventFilter theEventFilter = new DefaultEventFilter(new DefaultEventFilter(new DefaultEventFilter() {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 0 == ((TestEvent)anEvent).getId();
            }
        }) {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 1 == ((TestEvent)anEvent).getId();
            }
        }) {
            public boolean match(Event anEvent) {
                return super.match(anEvent) || 2 == ((TestEvent)anEvent).getId();
            }
        };

        assertFalse(theEventFilter.match(new TestEvent(-1)));
        assertTrue(theEventFilter.match(new TestEvent(0)));
        assertTrue(theEventFilter.match(new TestEvent(1)));
        assertTrue(theEventFilter.match(new TestEvent(2)));
        assertFalse(theEventFilter.match(new TestEvent(3)));
    }

    @Test
    public void testDetach() {
        CascadingEventFilter theCascadingEventFilter = new DefaultEventFilter();
        assertNull(theCascadingEventFilter.getAttachedEventFilter());

        //shouldn't have an effect
        theCascadingEventFilter.detach();
        assertNull(theCascadingEventFilter.getAttachedEventFilter());

        theCascadingEventFilter.attach(new DefaultEventFilter());
        assertNotNull(theCascadingEventFilter.getAttachedEventFilter());

        theCascadingEventFilter.detach();
        assertNull(theCascadingEventFilter.getAttachedEventFilter());
    }

    private class TestEvent implements Event
    {
        private int myId;

        private TestEvent(int anId) {
            myId = anId;
        }

        public int getId() {
            return myId;
        }
    }
}