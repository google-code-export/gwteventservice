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
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
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
    private UnlistenEvent myUnlistenEvent;
    private volatile long myLastActivityTime;

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

    /**
     * Checks if events are available.
     * @return true when no events recognized, otherwise false
     */
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
     * Returns the registered {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}. That can be
     * a custom {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can be set with
     * {@link de.novanic.eventservice.service.registry.user.UserInfo#setUnlistenEvent(de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent)}
     * or a default/generic {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} when no custom
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is registered.
     * @return {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} to report a timeout or
     * a user/client which left a domain.
     */
    public UnlistenEvent getUnlistenEvent() {
        if(myUnlistenEvent == null) {
            return new DefaultUnlistenEvent();//create here on request/unlisten to save memory
        }
        return myUnlistenEvent;
    }

    /**
     * A custom {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} can be set which is transferred
     * to all registered {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener} instances when
     * an unlisten occurred (for example by a timeout or when a user/client leaves a domain). When no custom
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is registered, a default/generic
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} will be processed and reported.
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which should be
     * triggered.
     */
    public void setUnlistenEvent(UnlistenEvent anUnlistenEvent) {
        myUnlistenEvent = anUnlistenEvent;
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

    /**
     * That method must be called to report a user activity and protects the user from a timeout for the time of the
     * timeout interval ({@link de.novanic.eventservice.config.EventServiceConfiguration#getTimeoutTime()}).
     */
    public void reportUserActivity() {
        setLastActivityTime(PlatformUtil.getCurrentTime());
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
