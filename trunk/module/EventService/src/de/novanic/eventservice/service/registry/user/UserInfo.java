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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserInfo is a class to represent all users/clients and to manage all their information.
 * It holds the events, the EventFilters ({@link de.novanic.eventservice.client.event.filter.EventFilter}) and the last
 * activity time for the user.
 *
 * @author sstrohschein
 *         <br>Date: 19.01.2009
 *         <br>Time: 23:59:58
 */
public class UserInfo implements Comparable<UserInfo>
{
    private final String myUserId;
    private final Queue<DomainEvent> myEvents;
    private final Map<Domain, EventFilter> myDomainEventFilters;
    private long myLastActivityTime;

    /**
     * Creates a new UserInfo for the user id.
     * @param aUserId user
     */
    public UserInfo(String aUserId) {
        myUserId = aUserId;
        myEvents = new ConcurrentLinkedQueue<DomainEvent>();
        myDomainEventFilters = new ConcurrentHashMap<Domain, EventFilter>();
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
    public void addEvent(Domain aDomain, Event anEvent) {
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
     * Returns and removes all recorded events.
     * @return all events according to the user
     */
    public List<DomainEvent> retrieveEvents() {
        List<DomainEvent> theEventList = new ArrayList<DomainEvent>(myEvents.size());
        DomainEvent theEvent;
        while((theEvent = myEvents.poll()) != null) {
            theEventList.add(theEvent);
        }
        return theEventList;
    }

    public boolean isEventsEmpty() {
        return myEvents.isEmpty();
    }

    /**
     * Sets an EventFilter to a domain.
     * @param aDomain domain where the EventFilter should be applied.
     * @param anEventFilter EventFilter to filter the events for the domain
     */
    public void setEventFilter(final Domain aDomain, EventFilter anEventFilter) {
        if(anEventFilter != null) {
            myDomainEventFilters.put(aDomain, anEventFilter);
        }
    }

    /**
     * Removes the EventFilter for a domain.
     * @param aDomain domain where the EventFilter to remove is applied.
     * @return true when the {@link de.novanic.eventservice.client.event.filter.EventFilter} is removed, otherwise false
     * (for example the {@link de.novanic.eventservice.client.event.filter.EventFilter} was already removed before)
     */
    public boolean removeEventFilter(final Domain aDomain) {
        return aDomain != null && myDomainEventFilters.remove(aDomain) != null;
    }

    /**
     * Returns the EventFilter for the domain.
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    public EventFilter getEventFilter(Domain aDomain) {
        if(aDomain != null) {
            return myDomainEventFilters.get(aDomain);
        }
        return null;
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

    public int compareTo(UserInfo aUserInfo) {
        return myUserId.compareTo(aUserInfo.myUserId);
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
