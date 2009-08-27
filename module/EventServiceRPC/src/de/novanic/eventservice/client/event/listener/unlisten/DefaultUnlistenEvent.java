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
package de.novanic.eventservice.client.event.listener.unlisten;

import de.novanic.eventservice.client.event.domain.Domain;

/**
 * An UnlistenEvent will be triggered when a timeout or a domain specific unlisten/deregistration occurs. The UnlistenEvent is created by
 * {@link de.novanic.eventservice.client.event.service.EventService} when unlisten is called for a user. It will also be returned as an
 * event (from the listen method) and will be added to the UnlistenDomain {@link de.novanic.eventservice.client.event.domain.DomainFactory#UNLISTEN_DOMAIN}.
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten()
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(Domain)
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(java.util.Set)
 *
 * @author sstrohschein
 * <br>Date: 05.06.2008
 * <br>Time: 19:24:45
 */
public class DefaultUnlistenEvent implements UnlistenEvent
{
    private Domain myDomain;
    private String myUserId;
    private boolean isTimeout;

    /**
     * Creates an UnlistenEvent for all domains (global). This is useful if the user exits
     * the browser/application and a timeout occurs.
     */
    public DefaultUnlistenEvent() {}

    /**
     * Creates an UnlistenEvent for a specific domain. That will be created when a user will be deregistered from a domain.
     * @param aDomain {@link de.novanic.eventservice.client.event.domain.Domain} which is unlistened (in combination with the user id)
     * @param aUserId user id which is unlistened (in combination with the domain)
     * @param isTimeout
     */
    public DefaultUnlistenEvent(Domain aDomain, String aUserId, boolean isTimeout) {
        myDomain = aDomain;
        myUserId = aUserId;
        this.isTimeout = isTimeout;
    }

    /**
     * A {@link de.novanic.eventservice.client.event.domain.Domain} can be set to the UnlistenEvent when the unlisten event
     * is domain specific.
     * @param aDomain domain for unlistening
     */
    public void setDomain(Domain aDomain) {
        myDomain = aDomain;
    }

    /**
     * Returns the domain for which isn't listening anymore. If the UnlistenEvent is global (for example a timeout),
     * this method returns NULL.
     * @return domain for unlistening
     */
    public Domain getDomain() {
        return myDomain;
    }

    /**
     * Sets the unlistened user id for the UnlistenEvent.
     * @param aUserId unlistened user id
     */
    public void setUserId(String aUserId) {
        myUserId = aUserId;
    }

    /**
     * Returns the unlistened user id for the UnlistenEvent.
     * @return unlistened user id
     */
    public String getUserId() {
        return myUserId;
    }

    /**
     * Returns true when the UnlistenEvent is a timeout, otherwise false (for example a domain specific UnlistenEvent).
     * @return true when timeout, otherwise false (for example a domain specific UnlistenEvent)
     */
    public boolean isTimeout() {
        return isTimeout;
    }

    /**
     * Sets the timeout flag. It should be set true when the UnlistenEvent marks a timeout, otherwise false (for example a domain specific UnlistenEvent).
     * @param aTimeout true when the UnlistenEvent marks a timeout, otherwise false (for example a domain specific UnlistenEvent)
     */
    public void setTimeout(boolean aTimeout) {
        isTimeout = aTimeout;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }

        UnlistenEvent theOtherObject = (UnlistenEvent)anObject;
        if(isTimeout != theOtherObject.isTimeout()) {
            return false;
        }
        if(myDomain != null ? !myDomain.equals(theOtherObject.getDomain()) : theOtherObject.getDomain() != null) {
            return false;
        }
        if(myUserId != null ? !myUserId.equals(theOtherObject.getUserId()) : theOtherObject.getUserId() != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int theResult = myDomain != null ? myDomain.hashCode() : 0;
        theResult = 31 * theResult + (myUserId != null ? myUserId.hashCode() : 0);
        theResult = 31 * theResult + (isTimeout ? 1 : 0);
        return theResult;
    }

    public String toString() {
        String theString = "Event: Unlisten";
        if(isTimeout) {
            theString += "(timeout)";
        }

        final Domain theDomain = getDomain();
        if(theDomain != null) {
            theString += " (Domain \"" + getDomain() + "\")";
        }
        return theString;
    }
}