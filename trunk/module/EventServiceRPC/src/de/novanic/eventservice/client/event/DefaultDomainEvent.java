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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.domain.Domain;

/**
 * A {@link de.novanic.eventservice.client.event.DomainEvent} is a container and contains an event and the domain where the event has occurred.
 *
 * @author sstrohschein
 * <br>Date: 05.08.2008
 * <br>Time: 17:25:12
 */
public class DefaultDomainEvent implements DomainEvent
{
    private Event myEvent;
    private Domain myDomain;

    /**
     * @deprecated That constructor is only for serialization! Please use
     * {@link de.novanic.eventservice.client.event.DefaultDomainEvent#DefaultDomainEvent(Event)} or
     * {@link de.novanic.eventservice.client.event.DefaultDomainEvent#DefaultDomainEvent(Event, de.novanic.eventservice.client.event.domain.Domain)} instead.
     * @see de.novanic.eventservice.client.event.DefaultDomainEvent#DefaultDomainEvent(Event)
     * @see de.novanic.eventservice.client.event.DefaultDomainEvent#DefaultDomainEvent(Event, de.novanic.eventservice.client.event.domain.Domain)
     */
    @Deprecated
    public DefaultDomainEvent() {}

    /**
     * Creates a new DomainEvent with an event and a domain.
     * @param aEvent event
     * @param aDomain domain where the event has occurred
     */
    public DefaultDomainEvent(Event aEvent, Domain aDomain) {
        myEvent = aEvent;
        myDomain = aDomain;
    }

    /**
     * Creates a new DomainEvent without a domain. This can be used when the event is user specific.
     * When the event has occurred for a domain, the constructor {@link de.novanic.eventservice.client.event.DefaultDomainEvent#DefaultDomainEvent(Event, Domain)} should be used.
     * @param aEvent event
     */
    public DefaultDomainEvent(Event aEvent) {
        myEvent = aEvent;
    }

    /**
     * An event/DomainEvent is user specific when it is only for one user and not for the complete domain.
     * This flag depends on the constructor. If the DomainEvent is created with a domain, the DomainEvent isn't user
     * specific.
     * @return true when the event is user specific, otherwise false
     */
    public boolean isUserSpecific() {
        return myDomain == null;
    }

    /**
     * Returns the event.
     * @return event
     */
    public Event getEvent() {
        return myEvent;
    }

    /**
     * Returns the domain where the event has occured.
     * @return domain
     */
    public Domain getDomain() {
        return myDomain;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        DomainEvent theDomainEvent = (DomainEvent)anObject;
        return !(myDomain != null ? !myDomain.equals(theDomainEvent.getDomain()) : theDomainEvent.getDomain() != null)
                && !(myEvent != null ? !myEvent.equals(theDomainEvent.getEvent()) : theDomainEvent.getEvent() != null);
    }

    public int hashCode() {
        int theResult = myEvent != null ? myEvent.hashCode() : 0;
        theResult = 31 * theResult + (myDomain != null ? myDomain.hashCode() : 0);
        return theResult;
    }

    public String toString() {
        StringBuilder theStringBuilder = new StringBuilder(80);
        theStringBuilder.append("DomainEvent (");
        if(myDomain != null) {
            theStringBuilder.append(myDomain.getName());
        }
        if(myDomain != null && myEvent != null) {
            theStringBuilder.append(" - ");
        }
        if(myEvent != null) {
            theStringBuilder.append(myEvent);
        }
        theStringBuilder.append(')');
        return theStringBuilder.toString();
    }
}