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

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 04.03.2009
 *         <br>Time: 23:22:17
 */
public class CompositeEventFilterTest extends TestCase
{
    public void testMatch() {
        EventFilter theEventFilter = new DefaultEventFilter();
        assertFalse(theEventFilter.match(new Event() {}));
    }

    public void testAppend() {
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

        AppendableEventFilter theAppendableEventFilter = new DefaultCompositeEventFilter();
        theAppendableEventFilter.attach(theEventFilter_1);
        theAppendableEventFilter.attach(theEventFilter_2);
        theAppendableEventFilter.attach(theEventFilter_3);

        assertFalse(theAppendableEventFilter.match(new TestEvent(-1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(0)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(2)));
        assertFalse(theAppendableEventFilter.match(new TestEvent(3)));
    }

    public void testAppend_2() {
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

        AppendableEventFilter theAppendableEventFilter = new DefaultCompositeEventFilter();
        theAppendableEventFilter.attach(theEventFilter_1).attach(theEventFilter_2).attach(theEventFilter_3);

        assertFalse(theAppendableEventFilter.match(new TestEvent(-1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(0)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(2)));
        assertFalse(theAppendableEventFilter.match(new TestEvent(3)));
    }

    public void testAppend_3() {
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

        AppendableEventFilter theAppendableEventFilter = new DefaultCompositeEventFilter(theEventFilter_1, theEventFilter_2, theEventFilter_3);

        assertFalse(theAppendableEventFilter.match(new TestEvent(-1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(0)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(1)));
        assertTrue(theAppendableEventFilter.match(new TestEvent(2)));
        assertFalse(theAppendableEventFilter.match(new TestEvent(3)));
    }

    public void testDetach() {
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

        CompositeEventFilter theCompositeEventFilter = new DefaultCompositeEventFilter(theEventFilter_1, theEventFilter_2);

        List<EventFilter> theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(2, theAttachedEventFilters.size());
        assertTrue(theAttachedEventFilters.contains(theEventFilter_1));
        assertTrue(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));

        assertTrue(theCompositeEventFilter.detach(theEventFilter_2));

        theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(1, theAttachedEventFilters.size());
        assertTrue(theAttachedEventFilters.contains(theEventFilter_1));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));

        //to remove the same EventFilter shouldn't have an effect
        assertFalse(theCompositeEventFilter.detach(theEventFilter_2));

        theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(1, theAttachedEventFilters.size());
        assertTrue(theAttachedEventFilters.contains(theEventFilter_1));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));

        assertTrue(theCompositeEventFilter.detach(theEventFilter_1));

        theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(0, theAttachedEventFilters.size());
        assertFalse(theAttachedEventFilters.contains(theEventFilter_1));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));
    }

    public void testDetach_Empty() {
        EventFilter theEventFilter_1 = new EventFilter() {
            public boolean match(Event anEvent) {
                return true;
            }
        };

        CompositeEventFilter theCompositeEventFilter = new DefaultCompositeEventFilter();
        assertFalse(theCompositeEventFilter.detach(theEventFilter_1));
    }

    public void testDetach_2() {
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

        CompositeEventFilter theCompositeEventFilter = new DefaultCompositeEventFilter(theEventFilter_1, theEventFilter_2);

        List<EventFilter> theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(2, theAttachedEventFilters.size());
        assertTrue(theAttachedEventFilters.contains(theEventFilter_1));
        assertTrue(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));

        assertTrue(theCompositeEventFilter.detach());

        theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(0, theAttachedEventFilters.size());
        assertFalse(theAttachedEventFilters.contains(theEventFilter_1));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));
        
        //to remove all EventFilter objects again shouldn't have an effect
        assertFalse(theCompositeEventFilter.detach());

        theAttachedEventFilters = theCompositeEventFilter.getAttachedEventFilters();
        assertEquals(0, theAttachedEventFilters.size());
        assertFalse(theAttachedEventFilters.contains(theEventFilter_1));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_2));
        assertFalse(theAttachedEventFilters.contains(theEventFilter_3));
    }

    public void testDetach_2_Empty() {
        CompositeEventFilter theCompositeEventFilter = new DefaultCompositeEventFilter();
        assertFalse(theCompositeEventFilter.detach());
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