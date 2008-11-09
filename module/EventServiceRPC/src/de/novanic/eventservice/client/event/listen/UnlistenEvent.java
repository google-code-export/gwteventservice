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

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * The UnlistenEvent is created by {@link de.novanic.eventservice.client.event.service.EventService} when unlisten is
 * called for a user. It will also be returned as an event (from the listen method).
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten()
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(Domain)
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(java.util.Set)
 *
 * @author sstrohschein
 * <br>Date: 05.06.2008
 * <br>Time: 19:24:45
 */
public class UnlistenEvent implements Event
{
    private Domain myDomain;

    /**
     * Creates an UnlistenEvent for all domains (global). This is useful if the user exits
     * the browser/application.
     */
    public UnlistenEvent() {}

    /**
     * Creates an UnlistenEvent for the corresponding domain. This is useful if the user exits the domain/context,
     * but is still in the application or is listening for other domains.
     * @param aDomain domain to unlisten
     */
    public UnlistenEvent(Domain aDomain) {
        myDomain = aDomain;
    }

    /**
     * Returns the domain for which isn't listen anymore. If the UnlistenEvent is for all domains (global),
     * this method returns NULL.
     * @return domain for unlisten
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

        UnlistenEvent theEvent = (UnlistenEvent)anObject;
        return !(myDomain != null ? !myDomain.equals(theEvent.myDomain) : theEvent.myDomain != null);
    }

    public int hashCode() {
        return myDomain != null ? myDomain.hashCode() : 0;
    }

    public String toString() {
        String theString = "Event: Unlisten";

        final Domain theDomain = getDomain();
        if(theDomain != null) {
            theString += " (Domain \"" + getDomain() + "\")";
        }
        return theString;
    }
}