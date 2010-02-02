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
package de.novanic.eventservice.client.event.filter;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.Event;

/**
 * @author sstrohschein
 *         <br>Date: 04.03.2009
 *         <br>Time: 23:36:47
 */
public class EventFilterFactoryTest extends TestCase
{
    public void testGetInstance() {
        EventFilterFactory theEventRegistryFactory = EventFilterFactory.getInstance();
        assertSame(theEventRegistryFactory, EventFilterFactory.getInstance());
        assertSame(theEventRegistryFactory, EventFilterFactory.getInstance());
    }

    public void testConnect() {
        EventFilter theEventFilter_1 = new EventFilter() {
            public boolean match(Event anEvent) {
                return 0 == ((TestEvent)anEvent).getId();
            }
        };
        EventFilter theEventFilter_2 = new EventFilter() {
            public boolean match(Event anEvent) {
                return 1 == ((TestEvent)anEvent).getId();
            }
        };
        EventFilter theEventFilter_3 = new EventFilter() {
            public boolean match(Event anEvent) {
                return 2 == ((TestEvent)anEvent).getId();
            }
        };

        EventFilterFactory theEventFilterFactory = EventFilterFactory.getInstance();
        AppendableEventFilter theAppendableEventFilter = theEventFilterFactory.connect(theEventFilter_1, theEventFilter_2, theEventFilter_3);
        
        assertFalse(theAppendableEventFilter.match(new TestEvent(-1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(0)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(2)));
        assertFalse(theAppendableEventFilter.match(new TestEvent(3)));
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