package de.novanic.gwteventservice.demo.rapidevents.client.event.control;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 *         <br>Date: 27.07.12
 *         <br>Time: 18:23
 */
public interface ControlEvent extends Event
{
    public static final Domain RAPID_EVENT_CONTROL_DOMAIN = DomainFactory.getDomain("rapid_event_control_domain");
}