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
package de.novanic.eventservice.service.registry;

import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.DefaultDomainEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.client.event.listen.UnlistenEvent;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;

import java.util.*;

/**
 * The EventRegistry handles the users/clients and the events per domain. Users can be registered for a domain/context
 * to receive events for the according domain.
 * User specific events can be handled domainless, when the user is registered.
 * The EventRegistry is used by {@link de.novanic.eventservice.service.EventServiceImpl}.
 *
 * <br>The client id is required, because the connection to every client must be kept open.
 *
 * @see de.novanic.eventservice.service.EventServiceImpl
 *
 * @author sstrohschein
 * <br>Date: 05.06.2008
 * <br>Time: 19:12:35
 */
public class DefaultEventRegistry implements EventRegistry
{
    private final ServerLogger LOG = ServerLoggerFactory.getServerLogger(DefaultEventRegistry.class.getName());

    private EventServiceConfiguration myConfiguration;
    private final Map<Domain, Collection<UserInfo>> myDomainUserInfoMap;
    private final Map<String, UserInfo> myUserInfoMap;

    /**
     * Creates a new EventRegistry with * Initializes the EventRegistry with {@link de.novanic.eventservice.config.EventServiceConfiguration}.
     * The {@link EventRegistryFactory} should be used instead of calling this constructor directly.
     * @param aConfiguration configuration
     * @see de.novanic.eventservice.service.registry.EventRegistryFactory#getEventRegistry()
     */
    protected DefaultEventRegistry(EventServiceConfiguration aConfiguration) {
        myConfiguration = aConfiguration;
        myDomainUserInfoMap = new HashMap<Domain, Collection<UserInfo>>();
        myUserInfoMap = new HashMap<String, UserInfo>();

        LOG.info("Configuration changed - " + aConfiguration.toString());
    }

    /**
     * Checks if the user is registered for any domain.
     * @param aUserId the user to check
     * @return true if registered, false if not registered
     */
    public boolean isUserRegistered(String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        return isUserRegistered(theUserInfo);
    }

    /**
     * Checks if the user is registered for any domain.
     * @param aUserInfo the user to check
     * @return true if registered, false if not registered
     */
    private boolean isUserRegistered(UserInfo aUserInfo) {
        for(Domain theDomain : myDomainUserInfoMap.keySet()) {
            if(isUserRegistered(theDomain, aUserInfo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the user is registered for the corresponding domain.
     * @param aDomain the domain to check
     * @param aUserId the user to check
     * @return true if registered, false if not registered
     */
    public boolean isUserRegistered(Domain aDomain, String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        return isUserRegistered(aDomain, theUserInfo);
    }

    /**
     * Checks if the user is registered for the corresponding domain.
     * @param aDomain the domain to check
     * @param aUserInfo the user to check
     * @return true if registered, false if not registered
     */
    private boolean isUserRegistered(Domain aDomain, UserInfo aUserInfo) {
        Collection<UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        return (aUserInfo != null && theDomainUsers != null && theDomainUsers.contains(aUserInfo));
    }

    /**
     * Registers a user for listening for the corresponding domain. From now all events for the domain are recognized and
     * will be returned when listen ({@link DefaultEventRegistry#listen(String)}) is called. The {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * is optional and can be NULL.
     * @param aDomain the domain to listen
     * @param aUserId the user to register
     * @param anEventFilter EventFilter to filter the domain events (optional, can be NULL)
     */
    public synchronized void registerUser(final Domain aDomain, final String aUserId, EventFilter anEventFilter) {
        //get or create UserInfo
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo == null) {
            theUserInfo = new UserInfo(aUserId);
            myUserInfoMap.put(aUserId, theUserInfo);
        }

        //register UserInfo for the Domain
        Collection<UserInfo> theUsers = myDomainUserInfoMap.get(aDomain);
        if(theUsers == null) {
            theUsers = new HashSet<UserInfo>();
            myDomainUserInfoMap.put(aDomain, theUsers);
        }
        if(!theUsers.contains(theUserInfo)) {
            theUsers.add(theUserInfo);
        }
        LOG.debug("User \"" + aUserId + "\" registered for domain \"" + aDomain + "\".");

        //set EventFilter
        setEventFilter(aDomain, theUserInfo, anEventFilter);
    }

    /**
     * The EventFilter for a user domain combination can be set or changed with that method.
     * @param aDomain domain
     * @param aUserId user
     * @param anEventFilter new EventFilter
     */
    public void setEventFilter(final Domain aDomain, final String aUserId, EventFilter anEventFilter) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        setEventFilter(aDomain, theUserInfo, anEventFilter);
    }

    /**
     * The EventFilter for a user domain combination can be set or changed with that method.
     * @param aDomain domain
     * @param aUserInfo user
     * @param anEventFilter new EventFilter
     */
    private void setEventFilter(final Domain aDomain, final UserInfo aUserInfo, EventFilter anEventFilter) {
        if(aUserInfo != null) {
            if(anEventFilter != null) {
                LOG.debug(aUserInfo.getUserId() + ": EventFilter changed for domain \"" + aDomain + "\".");
                aUserInfo.addEventFilter(aDomain, anEventFilter);
            } else {
                aUserInfo.removeEventFilter(aDomain);
            }
        }
    }

    /**
     * EventFilters can be removed with that method.
     * @param aDomain domain
     * @param aUserId user
     */
    public void removeEventFilter(final Domain aDomain, final String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo != null) {
            theUserInfo.removeEventFilter(aDomain);
        }
    }

    /**
     * The listen method returns all events for the user (events for all domains where the user is registered and user
     * specific events). If no events are available, the method waits a defined time before the events are returned.
     * The listen method is designed for the EventService functionality. The client side calls the method with a defined
     * interval to receive all events. If the client don't call the method in the interval, the user will be removed
     * from the EventRegistry. The timeout time and the min and max waiting time can be configured by
     * {@link de.novanic.eventservice.config.EventServiceConfiguration}.
     * @param aUserId user
     * @return list of events
     */
    public List<DomainEvent> listen(String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        LOG.debug(aUserId + ": listen (UserInfo " + theUserInfo + ").");
        try {
            EventServiceConfiguration theConfiguration = getConfiguration();
            Thread.sleep(theConfiguration.getMinWaitingTime());
            if(theUserInfo != null) {
                synchronized(theUserInfo) {
                    theUserInfo.schedule(theConfiguration.getTimeoutTime());
                    if(theUserInfo.getEvents().isEmpty()) {
                        theUserInfo.wait(theConfiguration.getMaxWaitingTime());
                    }

                    final List<DomainEvent> theEvents = theUserInfo.getEvents();
                    theUserInfo.clearEvents();
                    return theEvents;
                }
            }
        } catch(InterruptedException e) {
            LOG.error("Listen was interrupted (client id \"" + aUserId + "\")!", e);
        }
        return null;
    }

    /**
     * This method causes a stop of listening for a domain ({@link DefaultEventRegistry#listen(String)}).
     * @param aDomain domain to stop listening
     * @param aUserId user
     */
    public void unlisten(Domain aDomain, String aUserId) {
        LOG.debug(aUserId + ": unlisten (domain \"" + aDomain + "\").");
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo != null) {
            synchronized(theUserInfo) {
                theUserInfo.cancelSchedule();

                addEventUserSpecific(theUserInfo, new UnlistenEvent(aDomain));
                removeUser(aDomain, theUserInfo);
            }
        }
    }

    /**
     * This method causes a stop of listening for all domains ({@link DefaultEventRegistry#listen(String)}).
     * @param aUserId user
     */
    public void unlisten(String aUserId) {
        LOG.debug(aUserId + ": unlisten.");
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo != null) {
            synchronized(theUserInfo) {
                theUserInfo.cancelSchedule();
                addEventUserSpecific(theUserInfo, new UnlistenEvent());
                removeUser(theUserInfo);
            }
        }
    }

    /**
     * Removes the user from a domain.
     * @param aDomain domain
     * @param aUserInfo user
     */
    private void removeUser(Domain aDomain, UserInfo aUserInfo) {
        Collection<UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        if(theDomainUsers != null) {
            if(theDomainUsers.remove(aUserInfo)) {
                LOG.debug("User \"" + aUserInfo + "\" removed from domain \"" + aDomain + "\".");
            }
        }
        if(!isUserRegistered(aUserInfo)) {
            myUserInfoMap.remove(aUserInfo.getUserId());
        } else {
            //remove the eventfilter if the user isn't removed completely
            aUserInfo.removeEventFilter(aDomain);
        }
    }

    /**
     * Removes a user from all domains.
     * @param aUserInfo user
     */
    private void removeUser(UserInfo aUserInfo) {
        for(Collection<UserInfo> theDomainUsers : myDomainUserInfoMap.values()) {
            theDomainUsers.remove(aUserInfo);
        }
        if(myUserInfoMap.remove(aUserInfo.getUserId()) != null) {
            LOG.debug("User \"" + aUserInfo + "\" removed.");
        }
    }

    /**
     * Returns all domains where the user is registered to.
     * @param aUserId user
     * @return domains where the user is registered to
     */
    public Set<Domain> getListenDomains(String aUserId) {
        Set<Domain> theDomains = new HashSet<Domain>(myDomainUserInfoMap.size());
        UserInfo theUserInfo = getUserInfo(aUserId);

        for(Map.Entry<Domain, Collection<UserInfo>> theDomainUserEntry : myDomainUserInfoMap.entrySet()) {
            Collection<UserInfo> theDomainUsers = theDomainUserEntry.getValue();
            if(theDomainUsers.contains(theUserInfo)) {
                theDomains.add(theDomainUserEntry.getKey());
            }
        }
        return theDomains;
    }

    /**
     * Adds an event to a domain.
     * @param aDomain domain for the event
     * @param anEvent event to add
     */
    public synchronized void addEvent(Domain aDomain, Event anEvent) {
        LOG.debug("Event \"" + anEvent + "\" added to domain \"" + aDomain + "\".");
        final Collection<UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        //if the domain doesn't exist/no users assigned, no users must be notified for the event...
        if(theDomainUsers != null) {
            for(UserInfo theUserInfo : theDomainUsers) {
                addEvent(aDomain, theUserInfo, anEvent);
            }
        }
    }

    /**
     * Adds an event directly to a user. The user must be registered to any domain.
     * @param aUserId user
     * @param anEvent event
     */
    public synchronized void addEventUserSpecific(String aUserId, Event anEvent) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        addEventUserSpecific(theUserInfo, anEvent);
    }

    /**
     * Adds an event directly to a user. The user must be registered to any domain.
     * @param aUserInfo user
     * @param anEvent event
     */
    private synchronized void addEventUserSpecific(UserInfo aUserInfo, Event anEvent) {
        if(aUserInfo != null) {
            LOG.debug("User specific event \"" + anEvent + "\" added to client id \"" + aUserInfo + "\".");
            addEvent(null, aUserInfo, anEvent);
        }
    }

    /**
     * Returns the initialized {@link de.novanic.eventservice.config.EventServiceConfiguration}
     * @return configuration {@link de.novanic.eventservice.config.EventServiceConfiguration}
     */
    public EventServiceConfiguration getConfiguration() {
        return myConfiguration;
    }

    /**
     * Adds an event to a user in a domain.
     * @param aDomain domain for the event
     * @param aUserInfo user
     * @param anEvent event to add
     */
    private void addEvent(Domain aDomain, UserInfo aUserInfo, Event anEvent) {
        if(isEventValid(anEvent, aUserInfo.getEventFilter(aDomain))) {
            aUserInfo.addEvent(aDomain, anEvent);
            LOG.debug(anEvent + " for user \"" + aUserInfo + "\".");
        }
    }

    /**
     * Checks if the EventFilter recognizes the event as valid. When no EventFilter is available (NULL), the event is
     * ever valid.
     * @param anEvent event
     * @param anEventFilter EventFilter to check the event
     * @return true when the event is valid, false when the event isn't valid (filtered by the EventFilter)
     */
    private boolean isEventValid(Event anEvent, EventFilter anEventFilter) {
        return anEventFilter == null || !(anEventFilter.match(anEvent));
    }

    /**
     * Determines the UserInfo with the user id.
     * @param aUserId user
     * @return UserInfo according to the user id
     */
    private UserInfo getUserInfo(final String aUserId) {
        return myUserInfoMap.get(aUserId);
    }

    /**
     * UserInfo is a internal class of EventRegistry to manage all information for the current user.
     * It holds the events and the EventFilters for the user.
     */
    private class UserInfo
    {
        private final String myUserId;
        private List<DomainEvent> myEvents;
        private Timer myTimer;
        private TimerTask myInactivityTask;
        private final Map<Domain, EventFilter> myDomainEventFilters;

        /**
         * Creates a new UserInfo for the user id.
         * @param aUserId user
         */
        private UserInfo(String aUserId) {
            myUserId = aUserId;
            myTimer = new Timer();
            myEvents = new ArrayList<DomainEvent>();
            myDomainEventFilters = new HashMap<Domain, EventFilter>();
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
         * doNotifyAll informs all waiting Threads for events.
         */
        private synchronized void doNotifyAll() {
            notifyAll();
        }

        /**
         * Removes all events from the user.
         */
        public void clearEvents() {
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
         * This method is used to measure the timeout and it will remove the user automatically.
         * @param aMillis milliseconds to the timeout
         */
        public void schedule(long aMillis) {
            cancelSchedule();
            myInactivityTask = new ScheduleTimer();
            myTimer.schedule(myInactivityTask, aMillis);
        }

        /**
         * Resets the timeout timer and is automatically called by schedule.
         * @see DefaultEventRegistry.UserInfo#schedule(long)
         */
        public void cancelSchedule() {
            if(myTimer != null) {
                myTimer.cancel();
            }
            if(myInactivityTask != null) {
                myInactivityTask.cancel();
            }
            if(myTimer != null) {
                myTimer.purge();
            }
            myTimer = new Timer();
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
         * Sets the an EventFilter to a domain.
         * @param aDomain domain where the EventFilter should be applied.
         * @param anEventFilter EventFilter to filter the events for the domain
         */
        public void addEventFilter(final Domain aDomain, EventFilter anEventFilter) {
            myDomainEventFilters.put(aDomain, anEventFilter);
        }

        /**
         * Removes the EventFilter for a domain.
         * @param aDomain domain where the EventFilter to remove is applied.
         */
        public void removeEventFilter(final Domain aDomain) {
            if(myDomainEventFilters.remove(aDomain) != null) {
                LOG.debug(getUserId() + ": EventFilter removed from domain \"" + aDomain + "\".");
            }
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

        public String toString() {
            return getUserId();
        }

        private class ScheduleTimer extends TimerTask
        {
            public void run() {
                String theUserId = myUserId;
                LOG.debug(theUserId + ": timeout.");
                unlisten(theUserId); //remove the user
            }
        }
    }
}