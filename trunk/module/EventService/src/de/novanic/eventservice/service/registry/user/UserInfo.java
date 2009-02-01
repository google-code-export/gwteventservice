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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DefaultDomainEvent;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.util.PlatformUtil;

import java.util.*;

/**
 * UserInfo is a class to represent all users/clients and to manage all their information.
 * It holds the events, the EventFilters ({@link de.novanic.eventservice.client.event.filter.EventFilter}) and the last
 * activity time for the user.
 *
 * @author sstrohschein
 *         <br>Date: 19.01.2009
 *         <br>Time: 23:59:58
 */
public class UserInfo
{
    private final String myUserId;
    private List<DomainEvent> myEvents;
    private final Map<Domain, EventFilter> myDomainEventFilters;
    private long myLastActivityTime;

    /**
     * Creates a new UserInfo for the user id.
     * @param aUserId user
     */
    public UserInfo(String aUserId) {
        myUserId = aUserId;
        myEvents = new ArrayList<DomainEvent>();
        myDomainEventFilters = new HashMap<Domain, EventFilter>();
        myLastActivityTime = PlatformUtil.getCurrentTime();
    }

    /**
     * Returns the user id.
     * @return user / user id
     */
    public String getUserId() {
        return myUserId;
    }

    /**
     * Adds an event for a domain to the user.
     * @param aDomain domain
     * @param anEvent event
     */
    public synchronized void addEvent(Domain aDomain, Event anEvent) {
        DomainEvent theDomainEvent = new DefaultDomainEvent(anEvent, aDomain);
        myEvents.add(theDomainEvent);
        doNotifyAll();
    }

    /**
     * doNotifyAll informs all waiting Threads for new events.
     */
    private synchronized void doNotifyAll() {
        notifyAll();
    }

    /**
     * Removes all events from the user.
     */
    public synchronized void clearEvents() {
        myEvents = new ArrayList<DomainEvent>();
    }

    /**
     * Returns all recorded events.
     * @return all events according to the user
     */
    public List<DomainEvent> getEvents() {
        return myEvents;
    }

    /**
     * Sets an EventFilter to a domain.
     * @param aDomain domain where the EventFilter should be applied.
     * @param anEventFilter EventFilter to filter the events for the domain
     */
    public void setEventFilter(final Domain aDomain, EventFilter anEventFilter) {
        myDomainEventFilters.put(aDomain, anEventFilter);
    }

    /**
     * Removes the EventFilter for a domain.
     * @param aDomain domain where the EventFilter to remove is applied.
     */
    public boolean removeEventFilter(final Domain aDomain) {
        return myDomainEventFilters.remove(aDomain) != null;
    }

    /**
     * Returns the EventFilter for the domain.
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    public EventFilter getEventFilter(Domain aDomain) {
        return myDomainEventFilters.get(aDomain);
    }

    /**
     * Sets the last activity time. The last activity is used to recognize timeouts.
     * @param aLastActivityTime last activity time
     */
    public void setLastActivityTime(long aLastActivityTime) {
        myLastActivityTime = aLastActivityTime;
    }

    /**
     *  Returns the last activity time. The last activity is used to recognize timeouts.
     * @return last activity time
     */
    public long getLastActivityTime() {
        return myLastActivityTime;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }
        UserInfo theOtherUserInfo = (UserInfo)anObject;
        return myUserId.equals(theOtherUserInfo.myUserId);
    }

    public int hashCode() {
        return myUserId.hashCode();
    }

    /**
     * The user id is used to represent the UserInfo.
     * @return String representation (user id) of the UserInfo.
     */
    public String toString() {
        return getUserId();
    }
}