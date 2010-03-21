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
        final Event theEmptyEvent = new EmptyEvent_1("key_1");
        DomainEvent theDomainEvent = new DefaultDomainEvent(theEmptyEvent, TEST_DOMAIN);
        assertNotNull(theDomainEvent.getDomain());
        assertSame(TEST_DOMAIN, theDomainEvent.getDomain());
        assertNotNull(theDomainEvent.getEvent());
        assertSame(theEmptyEvent, theDomainEvent.getEvent());

        assertFalse(theDomainEvent.isUserSpecific());
    }

    public void testInit_2() {
        final Event theEmptyEvent = new EmptyEvent_1("key_1"){};
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
        DomainEvent theDomainEvent = createDefaultDomainEvent();
        assertNull(theDomainEvent.getDomain());
        assertNull(theDomainEvent.getEvent());
    }

    public void testEquals() {
        final Event theEmptyEvent = new EmptyEvent_1("key_1"){};
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
        final Event theEmptyEvent_2 = new EmptyEvent_1("key_2"){};
        DomainEvent theDomainEvent_4 = new DefaultDomainEvent(theEmptyEvent_2, TEST_DOMAIN);
        assertFalse(theDomainEvent.equals(theDomainEvent_4));

        DomainEvent theDomainEventNull = null;
        assertFalse(theDomainEvent.equals(theDomainEventNull));

        assertEquals(createDefaultDomainEvent(), createDefaultDomainEvent());
        assertEquals(createDefaultDomainEvent().hashCode(), createDefaultDomainEvent().hashCode());
        assertEquals(0, createDefaultDomainEvent().hashCode());
    }

    public void testCompareTo() {
        final Comparable<DomainEvent> theDomainEvent = createDefaultDomainEvent();

        assertEquals(0, theDomainEvent.compareTo(createDefaultDomainEvent()));
        assertEquals(-1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"))));
        assertEquals(-1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"), TEST_DOMAIN)));
    }

    public void testCompareTo_2() {
        final DefaultDomainEvent theDomainEvent = new DefaultDomainEvent(new EmptyEvent_1("key"));

        assertEquals(0, theDomainEvent.compareTo(theDomainEvent));
        assertEquals(0, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"))));
        assertEquals(-1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"), TEST_DOMAIN)));
        //the event class is compared, because there is no compareTo method (EmptyEvent_1 = EmptyEvent_1)
        assertEquals(0, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("x"))));
        //the event class is compared, because there is no compareTo method (EmptyEvent_1 < EmptyEvent_2)
        assertEquals(-1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_2("key"))));
        assertEquals(1, new DefaultDomainEvent(new EmptyEvent_2("key")).compareTo(theDomainEvent));
        assertEquals(-1, new DefaultDomainEvent(null).compareTo(theDomainEvent));
        assertEquals(1, theDomainEvent.compareTo(new DefaultDomainEvent(null)));
    }

    public void testCompareTo_3() {
        final DefaultDomainEvent theDomainEvent = new DefaultDomainEvent(new EmptyEvent_1("key"), TEST_DOMAIN);

        assertEquals(0, theDomainEvent.compareTo(theDomainEvent));
        assertEquals(0, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"), TEST_DOMAIN)));
        assertEquals(1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("key"))));
        //the event class is compared, because there is no compareTo method (EmptyEvent_1 = EmptyEvent_1)
        assertEquals(0, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_1("x"), TEST_DOMAIN)));
        //the event class is compared, because there is no compareTo method (EmptyEvent_1 < EmptyEvent_2)
        assertEquals(-1, theDomainEvent.compareTo(new DefaultDomainEvent(new EmptyEvent_2("key"), TEST_DOMAIN)));
        assertEquals(1, new DefaultDomainEvent(new EmptyEvent_2("key"), TEST_DOMAIN).compareTo(theDomainEvent));

        //the domain is compared
        assertEquals(0, new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1"))));
        assertEquals(-1, new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_2"))));
        assertEquals(1, new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_2")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1"))));
        
        //the domain is compared first, the event is compared at first (when both domains are equal or when both domains are NULL)
        assertEquals(-1, new DefaultDomainEvent(new EmptyEvent_1("key"), DomainFactory.getDomain("test_1")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1"))));
        assertEquals(-1, new DefaultDomainEvent(new EmptyEvent_1("key"), null).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), null)));
        assertEquals(1, new DefaultDomainEvent(new EmptyEvent_1("key"), DomainFactory.getDomain("test_2")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_1"))));
        assertEquals(-1, new DefaultDomainEvent(new EmptyEvent_1("key"), DomainFactory.getDomain("test_1")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_2("key"), DomainFactory.getDomain("test_2"))));
        assertEquals(1, new DefaultDomainEvent(new EmptyEvent_1("key"), DomainFactory.getDomain("test_2")).compareTo(
                new DefaultDomainEvent(new EmptyEvent_1("key"), DomainFactory.getDomain("test_1"))));
    }

    public void testToString() {
        assertEquals("DomainEvent ()", new DefaultDomainEvent(null).toString());
        assertEquals("DomainEvent (test_domain)", new DefaultDomainEvent(null, TEST_DOMAIN).toString());
        assertEquals("DomainEvent (Event: TestEvent)", new DefaultDomainEvent(new EmptyEvent_1("TestEvent")).toString());
        assertEquals("DomainEvent (test_domain - Event: TestEvent)", new DefaultDomainEvent(new EmptyEvent_1("TestEvent"), TEST_DOMAIN).toString());
    }

    private DefaultDomainEvent createDefaultDomainEvent() {
        return new DefaultDomainEvent();
    }

    private class EmptyEvent_1 implements Event
    {
        private String myKey;

        public EmptyEvent_1(String aKey) {
            myKey = aKey;
        }

        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            EmptyEvent_1 theEvent = (EmptyEvent_1)o;
            return !(myKey != null ? !myKey.equals(theEvent.myKey) : theEvent.myKey != null);
        }

        public int hashCode() {
            return myKey != null ? myKey.hashCode() : 0;
        }

        public String toString() {
            return "Event: " + myKey;
        }
    }

    private class EmptyEvent_2 implements Event
    {
        private String myKey;

        public EmptyEvent_2(String aKey) {
            myKey = aKey;
        }

        public boolean equals(Object anObject) {
            if(this == anObject) {
                return true;
            }
            if(anObject == null || getClass() != anObject.getClass()) {
                return false;
            }

            EmptyEvent_2 theEvent = (EmptyEvent_2)anObject;
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