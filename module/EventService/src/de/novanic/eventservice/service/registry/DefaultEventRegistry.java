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
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.client.event.listen.UnlistenEvent;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.service.registry.user.UserManager;
import de.novanic.eventservice.service.registry.user.UserActivityScheduler;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;
import de.novanic.eventservice.service.UserTimeoutListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private final EventServiceConfiguration myConfiguration;
    private final ConcurrentMap<Domain, ConcurrentMap<String, UserInfo>> myDomainUserInfoMap;
    private final UserManager myUserManager;
    private final UserActivityScheduler myUserActivityScheduler;

    /**
     * Creates a new EventRegistry with a configuration ({@link de.novanic.eventservice.config.EventServiceConfiguration}).
     * The {@link EventRegistryFactory} should be used instead of calling that constructor directly.
     * @param aConfiguration configuration
     * @see de.novanic.eventservice.service.registry.EventRegistryFactory#getEventRegistry()
     */
    protected DefaultEventRegistry(EventServiceConfiguration aConfiguration) {
        myConfiguration = aConfiguration;
        myDomainUserInfoMap = new ConcurrentHashMap<Domain, ConcurrentMap<String, UserInfo>>();
        myUserManager = UserManagerFactory.getInstance().getUserManager(aConfiguration);
        myUserActivityScheduler = myUserManager.getUserActivityScheduler();
        myUserActivityScheduler.addTimeoutListener(new TimeoutListener());
        myUserManager.activateUserActivityScheduler();

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
        if(aDomain != null && aUserInfo != null) {
            Map<String, UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
            if(theDomainUsers != null) {
                return theDomainUsers.containsValue(aUserInfo);
            }
        }
        return false;
    }

    /**
     * Registers a user for listening for the corresponding domain. From now all events for the domain are recognized and
     * will be returned when listen ({@link DefaultEventRegistry#listen(String)}) is called. The {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * is optional and can be NULL.
     * @param aDomain the domain to listen
     * @param aUserId the user to register
     * @param anEventFilter EventFilter to filter the domain events (optional, can be NULL)
     */
    public void registerUser(final Domain aDomain, final String aUserId, EventFilter anEventFilter) {
        //create UserInfo
        UserInfo theUserInfo = myUserManager.addUser(aUserId);

        //register UserInfo for the Domain
        myDomainUserInfoMap.putIfAbsent(aDomain, new ConcurrentHashMap<String, UserInfo>());
        ConcurrentMap<String, UserInfo> theUsers = myDomainUserInfoMap.get(aDomain);
        theUsers.putIfAbsent(aUserId, theUserInfo);

        LOG.debug("User \"" + aUserId + "\" registered for domain \"" + aDomain + "\".");

        //set EventFilter
        setEventFilter(aDomain, theUserInfo, anEventFilter);
    }

    /**
     * The {@link de.novanic.eventservice.client.event.filter.EventFilter} for a user domain combination can be set or
     * changed with that method. The {@link de.novanic.eventservice.client.event.filter.EventFilter} can be removed
     * with the method {@link de.novanic.eventservice.service.registry.EventRegistry#removeEventFilter(de.novanic.eventservice.client.event.domain.Domain, String)}
     * or when that method is called with NULL as the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * parameter value.
     * @param aDomain domain
     * @param aUserId user
     * @param anEventFilter new EventFilter
     */
    public void setEventFilter(final Domain aDomain, final String aUserId, EventFilter anEventFilter) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        setEventFilter(aDomain, theUserInfo, anEventFilter);
    }

    /**
     * The {@link de.novanic.eventservice.client.event.filter.EventFilter} for a user domain combination can be set or
     * changed with that method. The {@link de.novanic.eventservice.client.event.filter.EventFilter} can be removed
     * with the method {@link de.novanic.eventservice.service.registry.EventRegistry#removeEventFilter(de.novanic.eventservice.client.event.domain.Domain, String)}
     * or when that method is called with NULL as the {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * parameter value.
     * @param aDomain domain
     * @param aUserInfo user
     * @param anEventFilter new {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    private void setEventFilter(final Domain aDomain, final UserInfo aUserInfo, EventFilter anEventFilter) {
        if(aUserInfo != null) {
            if(anEventFilter != null) {
                LOG.debug(aUserInfo.getUserId() + ": EventFilter changed for domain \"" + aDomain + "\".");
                aUserInfo.setEventFilter(aDomain, anEventFilter);
            } else {
                if(aUserInfo.removeEventFilter(aDomain)) {
                    LOG.debug(aUserInfo.getUserId() + ": EventFilter removed from domain \"" + aDomain + "\".");
                }
            }
        }
    }

    /**
     * EventFilters can be removed with that method.
     * @param aDomain domain
     * @param aUserId user
     */
    public void removeEventFilter(final Domain aDomain, final String aUserId) {
        setEventFilter(aDomain, aUserId, null);
    }

    /**
     * The listen method returns all events for the user (events for all domains where the user is registered and user
     * specific events). If no events are available, the method waits a defined time before the events are returned.
     * The listen method is designed for the EventService functionality. The client side calls the method with a defined
     * interval to receive all events. If the client don't call the method in the interval, the user will be removed
     * from the EventRegistry. The timeout time and the min. and max. waiting time can be configured by
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
                myUserActivityScheduler.reportUserActivity(theUserInfo);
                if(theUserInfo.isEventsEmpty()) {
                    //monitor for event notification and double checked
                    synchronized(theUserInfo) {
                        if(theUserInfo.isEventsEmpty()) {
                            theUserInfo.wait(theConfiguration.getMaxWaitingTime());
                        }
                    }
                }
                return theUserInfo.retrieveEvents();
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
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo != null) {
            LOG.debug(aUserId + ": unlisten (domain \"" + aDomain + "\").");
            addEventUserSpecific(theUserInfo, new UnlistenEvent(aDomain));
            removeUser(aDomain, theUserInfo);
        }
    }

    /**
     * This method causes a stop of listening for all domains ({@link DefaultEventRegistry#listen(String)}).
     * @param aUserId user
     */
    public void unlisten(String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        unlisten(theUserInfo);
    }

    /**
     * This method causes a stop of listening for all domains ({@link DefaultEventRegistry#listen(String)}).
     * @param aUserInfo user
     */
    public void unlisten(UserInfo aUserInfo) {
        if(aUserInfo != null) {
            LOG.debug(aUserInfo.getUserId() + ": unlisten.");
            addEventUserSpecific(aUserInfo, new UnlistenEvent());
            removeUser(aUserInfo);
        }
    }

    /**
     * Removes the user from a domain.
     * @param aDomain domain
     * @param aUserInfo user
     */
    private void removeUser(Domain aDomain, UserInfo aUserInfo) {
        Map<String, UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        if(theDomainUsers != null) {
            if(theDomainUsers.remove(aUserInfo.getUserId()) != null) {
                LOG.debug("User \"" + aUserInfo + "\" removed from domain \"" + aDomain + "\".");
            }
        }
        if(!isUserRegistered(aUserInfo)) {
            myUserManager.removeUser(aUserInfo.getUserId());
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
        final String theUserId = aUserInfo.getUserId();
        for(Map<String, UserInfo> theDomainUsers : myDomainUserInfoMap.values()) {
            theDomainUsers.remove(theUserId);
        }
        if(myUserManager.removeUser(aUserInfo.getUserId()) != null) {
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

        for(Map.Entry<Domain, ConcurrentMap<String, UserInfo>> theDomainUserEntry : myDomainUserInfoMap.entrySet()) {
            Map<String, UserInfo> theDomainUsers = theDomainUserEntry.getValue();
            if(theDomainUsers.containsKey(aUserId)) {
                theDomains.add(theDomainUserEntry.getKey());
            }
        }
        return theDomains;
    }

    /**
     * Returns all registered/activated domains.
     * @return all registered/activated domains
     */
    public Set<Domain> getListenDomains() {
        return myDomainUserInfoMap.keySet();
    }

    /**
     * Adds an event to a domain.
     * @param aDomain domain for the event
     * @param anEvent event to add
     */
    public void addEvent(Domain aDomain, Event anEvent) {
        LOG.debug("Event \"" + anEvent + "\" added to domain \"" + aDomain + "\".");
        final Map<String, UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        //if the domain doesn't exist/no users assigned, no users must be notified for the event...
        if(theDomainUsers != null) {
            for(UserInfo theUserInfo : theDomainUsers.values()) {
                addEvent(aDomain, theUserInfo, anEvent);
            }
        }
    }

    /**
     * Adds an event directly to a user. The user must be registered to any domain.
     * @param aUserId user
     * @param anEvent event
     */
    public void addEventUserSpecific(String aUserId, Event anEvent) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        addEventUserSpecific(theUserInfo, anEvent);
    }

    /**
     * Adds an event directly to a user. The user must be registered to any domain.
     * @param aUserInfo user
     * @param anEvent event
     */
    private void addEventUserSpecific(UserInfo aUserInfo, Event anEvent) {
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
        return myUserManager.getUser(aUserId);
    }

    /**
     * TimeoutListener to clean up inactive users/clients. The timeout is checked with
     * {@link de.novanic.eventservice.service.registry.user.UserActivityScheduler}.
     */
    private class TimeoutListener implements UserTimeoutListener
    {
        /**
         * The method onTimeout is called when a timeout is recognized for the user.
         * It caueses a unlisten call ({@link de.novanic.eventservice.service.registry.DefaultEventRegistry#unlisten(String)})
         * to clean up the inactive user/client.
         * @param aUserInfo the inactive user
         */
        public void onTimeout(UserInfo aUserInfo) {
            LOG.debug(aUserInfo.getUserId() + ": timeout.");
            unlisten(aUserInfo);
        }
    }
}
