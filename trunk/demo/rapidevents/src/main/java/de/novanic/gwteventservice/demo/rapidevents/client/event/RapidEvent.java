package de.novanic.gwteventservice.demo.rapidevents.client.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 *         <br>Date: 26.07.12
 *         <br>Time: 00:01
 */
public class RapidEvent implements Event
{
    public static final Domain RAPID_EVENT_DOMAIN = DomainFactory.getDomain("rapid_event_domain");

    private int myId;

    /**
     * Required for serialization
     */
    public RapidEvent() {}

    public RapidEvent(int aId) {
        myId = aId;
    }

    public int getId() {
        return myId;
    }

    @Override
    public String toString() {
        return "Rapid event " + myId;
    }
}