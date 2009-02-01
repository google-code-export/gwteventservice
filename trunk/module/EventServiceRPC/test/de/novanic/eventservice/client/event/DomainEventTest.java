package de.novanic.eventservice.client.event;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 * Date: 05.08.2008
 * Time: 18:15:20
 */
public class DomainEventTest extends TestCase
{
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");

    public void testInit_1() {
        final Event theEmptyEvent = new EmptyEvent("key_1");
        DomainEvent theDomainEvent = new DefaultDomainEvent(theEmptyEvent, TEST_DOMAIN);
        assertNotNull(theDomainEvent.getDomain());
        assertSame(TEST_DOMAIN, theDomainEvent.getDomain());
        assertNotNull(theDomainEvent.getEvent());
        assertSame(theEmptyEvent, theDomainEvent.getEvent());

        assertFalse(theDomainEvent.isUserSpecific());
    }

    public void testInit_2() {
        final Event theEmptyEvent = new EmptyEvent("key_1"){};
        DomainEvent theDomainEvent = new DefaultDomainEvent(theEmptyEvent);
        assertNull(theDomainEvent.getDomain());
        assertNotNull(theDomainEvent.getEvent());
        assertSame(theEmptyEvent, theDomainEvent.getEvent());

        assertTrue(theDomainEvent.isUserSpecific());
    }

    public void testInit_Error() {
        DomainEvent theDomainEvent = new DefaultDomainEvent(null);
        assertNull(theDomainEvent.getDomain());
        assertNull(theDomainEvent.getEvent());
    }

    public void testInit_Error_2() {
        DomainEvent theDomainEvent = new DefaultDomainEvent();
        assertNull(theDomainEvent.getDomain());
        assertNull(theDomainEvent.getEvent());
    }

    public void testEquals() {
        final Event theEmptyEvent = new EmptyEvent("key_1"){};
        DomainEvent theDomainEvent = new DefaultDomainEvent(theEmptyEvent, TEST_DOMAIN);
        DomainEvent theDomainEvent_2 = new DefaultDomainEvent(theEmptyEvent, TEST_DOMAIN);
        assertEquals(theDomainEvent, theDomainEvent);
        assertEquals(theDomainEvent, theDomainEvent_2);
        assertEquals(theDomainEvent.hashCode(), theDomainEvent_2.hashCode());
        assertNotSame(theDomainEvent, theDomainEvent_2);

        //same Event, other domain
        Domain theOtherDomain = DomainFactory.getDomain("otherDomain");
        DomainEvent theDomainEvent_3 = new DefaultDomainEvent(theEmptyEvent, theOtherDomain);
        assertFalse(theDomainEvent.equals(theDomainEvent_3));

        //another Event, same domain
        final Event theEmptyEvent_2 = new EmptyEvent("key_2"){};
        DomainEvent theDomainEvent_4 = new DefaultDomainEvent(theEmptyEvent_2, TEST_DOMAIN);
        assertFalse(theDomainEvent.equals(theDomainEvent_4));

        DomainEvent theDomainEventNull = null;
        assertFalse(theDomainEvent.equals(theDomainEventNull));
    }

    public void testToString() {
        assertEquals("DomainEvent ()", new DefaultDomainEvent(null).toString());
        assertEquals("DomainEvent (test_domain)", new DefaultDomainEvent(null, TEST_DOMAIN).toString());
        assertEquals("DomainEvent (Event: TestEvent)", new DefaultDomainEvent(new EmptyEvent("TestEvent")).toString());
        assertEquals("DomainEvent (test_domain - Event: TestEvent)", new DefaultDomainEvent(new EmptyEvent("TestEvent"), TEST_DOMAIN).toString());
    }

    private class EmptyEvent implements Event
    {
        private String myKey;

        public EmptyEvent(String aKey) {
            myKey = aKey;
        }

        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            EmptyEvent theEvent = (EmptyEvent)o;
            return !(myKey != null ? !myKey.equals(theEvent.myKey) : theEvent.myKey != null);
        }

        public int hashCode() {
            return myKey != null ? myKey.hashCode() : 0;
        }

        public String toString() {
            return "Event: " + myKey;
        }
    }
}