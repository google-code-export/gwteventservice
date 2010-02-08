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
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;
import de.novanic.eventservice.service.registry.user.*;
import de.novanic.eventservice.service.registry.domain.ListenDomainAccessor;
import de.novanic.eventservice.service.UserTimeoutListener;
import de.novanic.eventservice.event.listener.unlisten.UnlistenEventFilter;

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
public class DefaultEventRegistry implements EventRegistry, ListenDomainAccessor
{
    private static final ServerLogger LOG = ServerLoggerFactory.getServerLogger(DefaultEventRegistry.class.getName());

    private final EventServiceConfiguration myConfiguration;
    private final DomainUserMapping myDomainUserMapping;
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
        myDomainUserMapping = new DomainUserMapping();
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
        return aUserInfo != null && myUserManager.isUserContained(aUserInfo);
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
        return aDomain != null && aUserInfo != null && myDomainUserMapping.isUserContained(aDomain, aUserInfo);
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
        if(aDomain != null) {
            myDomainUserMapping.addUser(aDomain, theUserInfo);

            LOG.debug("User \"" + aUserId + "\" registered for domain \"" + aDomain + "\".");

            //set EventFilter
            setEventFilter(aDomain, theUserInfo, anEventFilter);
        } else {
            LOG.debug("User \"" + aUserId + "\" registered.");
        }
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
     * Returns the EventFilter for the user domain combination.
     * @param aDomain domain
     * @param aUserId user
     * @return EventFilter for the domain
     */
    public EventFilter getEventFilter(Domain aDomain, String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        if(theUserInfo != null) {
            return theUserInfo.getEventFilter(aDomain);
        }
        return null;
    }

    /**
     * EventFilters can be removed for a user domain combination with that method.
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
        if(theUserInfo != null) {
            try {
                final int theMinWaitingTime = myConfiguration.getMinWaitingTime();
                if(theMinWaitingTime > 0) {
                    Thread.sleep(theMinWaitingTime);
                }
                myUserActivityScheduler.reportUserActivity(theUserInfo);
                if(theUserInfo.isEventsEmpty()) {
                    //monitor for event notification and double checked
                    synchronized(theUserInfo) {
                        if(theUserInfo.isEventsEmpty()) {
                            theUserInfo.wait(myConfiguration.getMaxWaitingTime());
                        }
                    }
                }
                return theUserInfo.retrieveEvents();
            } catch(InterruptedException e) {
                LOG.error("Listen was interrupted (client id \"" + aUserId + "\")!", e);
            }
        }
        return null;
    }

    /**
     * This method causes a stop of listening for a domain ({@link DefaultEventRegistry#listen(String)}).
     * @param aDomain domain to stop listening
     * @param aUserId user
     */
    public void unlisten(Domain aDomain, String aUserId) {
        if(aDomain != null) {
            UserInfo theUserInfo = getUserInfo(aUserId);
            if(theUserInfo != null) {
                LOG.debug(aUserId + ": unlisten (domain \"" + aDomain + "\").");
                if(isUserRegistered(aDomain, theUserInfo)) {
                    Set<Domain> theDomains = new HashSet<Domain>(1);
                    theDomains.add(aDomain);
                    addEvent(DomainFactory.UNLISTEN_DOMAIN, produceUnlistenEvent(theUserInfo, theDomains, false));
                }
                removeUser(aDomain, theUserInfo);
            }
        } else {
            unlisten(aUserId);
        }
    }

    /**
     * This method causes a stop of listening for all domains ({@link DefaultEventRegistry#listen(String)}).
     * @param aUserId user
     */
    public void unlisten(String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        unlisten(theUserInfo, false);
    }

    /**
     * This method causes a stop of listening for all domains ({@link DefaultEventRegistry#listen(String)}).
     * @param aUserInfo user
     * @param isTimeout reason for the unlistening (timeout or leave of a specific domain)
     */
    private void unlisten(UserInfo aUserInfo, boolean isTimeout) {
        if(aUserInfo != null) {
            final String theUserId = aUserInfo.getUserId();
            LOG.debug(theUserId + ": unlisten.");
            Set<Domain> theDomains = myDomainUserMapping.getDomains(aUserInfo);
            addEvent(DomainFactory.UNLISTEN_DOMAIN, produceUnlistenEvent(aUserInfo, theDomains, isTimeout));
            removeUser(aUserInfo);
        }
    }

    /**
     * Removes the user from a domain.
     * @param aDomain domain
     * @param aUserInfo user
     * @return true when the user is removed from the domain, otherwise false
     */
    private boolean removeUser(Domain aDomain, UserInfo aUserInfo) {
        boolean isUserRemoved = myDomainUserMapping.removeUser(aDomain, aUserInfo);
        if(isUserRemoved) {
            LOG.debug("User \"" + aUserInfo + "\" removed from domain \"" + aDomain + "\".");
        }

        if(!myDomainUserMapping.isUserContained(aUserInfo)) {
            myUserManager.removeUser(aUserInfo.getUserId());
        } else {
            //remove the eventfilter if the user isn't removed completely
            aUserInfo.removeEventFilter(aDomain);
        }

        return isUserRemoved;
    }

    /**
     * Removes a user from all domains.
     * @param aUserInfo user
     */
    private void removeUser(UserInfo aUserInfo) {
        myDomainUserMapping.removeUser(aUserInfo);
        if(myUserManager.removeUser(aUserInfo.getUserId()) != null) {
            LOG.debug("User \"" + aUserInfo + "\" removed.");
        }
    }

    /**
     * Returns all domains where the user is registered to.
     * @param aUserId user id
     * @return domains where the user is registered to
     */
    public Set<Domain> getListenDomains(String aUserId) {
        UserInfo theUserInfo = getUserInfo(aUserId);
        return getListenDomains(theUserInfo);
    }

    /**
     * Returns all domains where the user is registered to.
     * @param aUserInfo user
     * @return domains where the user is registered to
     */
    private Set<Domain> getListenDomains(UserInfo aUserInfo) {
        return myDomainUserMapping.getDomains(aUserInfo);
    }

    /**
     * Returns all registered/activated domains.
     * @return all registered/activated domains
     */
    public Set<Domain> getListenDomains() {
        return myDomainUserMapping.getDomains();
    }

    /**
     * Returns all registered users/clients.
     * To get only the registered users/client of a specific {@link de.novanic.eventservice.client.event.domain.Domain},
     * the method {@link de.novanic.eventservice.service.registry.EventRegistry#getRegisteredUserIds(de.novanic.eventservice.client.event.domain.Domain)}
     * can be used instead.
     * @return registered users/clients
     */
    public Set<String> getRegisteredUserIds() {
        return getUserIds(myUserManager.getUsers());
    }

    /**
     * Returns all registered users/client of a specific {@link de.novanic.eventservice.client.event.domain.Domain}.
     * To get all the registered users/client (of all domains), the method {@link EventRegistry#getRegisteredUserIds()}
     * can be used instead.
     * @param aDomain domain
     * @return registered users/client of the specific domain
     */
    public Set<String> getRegisteredUserIds(Domain aDomain) {
        return getUserIds(myDomainUserMapping.getUsers(aDomain));
    }

    /**
     * Adds an event to a domain.
     * @param aDomain domain for the event
     * @param anEvent event to add
     */
    public void addEvent(Domain aDomain, Event anEvent) {
        LOG.debug("Event \"" + anEvent + "\" added to domain \"" + aDomain + "\".");
        final Set<UserInfo> theDomainUsers = myDomainUserMapping.getUsers(aDomain);
        //if the domain doesn't exist/no users assigned, no users must be notified for the event...
        if(theDomainUsers != null) {
            for(UserInfo theUserInfo: theDomainUsers) {
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
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which is triggered on a
     * timeout or when a user/client leaves a {@link de.novanic.eventservice.client.event.domain.Domain}. An
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is hold at the server side and can
     * contain custom data. Other users/clients can use the custom data when the event is for example triggered by a timeout.
     * @param aUserId user to register the {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} to
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which should
     * be transfered to other users/clients when a timeout occurs or a domain is leaved.
     */
    public void registerUnlistenEvent(String aUserId, UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent) {
        registerUser(DomainFactory.UNLISTEN_DOMAIN, aUserId, new UnlistenEventFilter(this, aUserId, anUnlistenScope));
        UserInfo theUserInfo = getUserInfo(aUserId);
        theUserInfo.setUnlistenEvent(anUnlistenEvent);
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
     * Checks if the EventFilter recognizes the event as valid. When no EventFilter is available (NULL),
     * the event is always valid.
     * @param anEvent event
     * @param anEventFilter EventFilter to check the event
     * @return true when the event is valid, false when the event isn't valid (filtered by the EventFilter)
     */
    private boolean isEventValid(Event anEvent, EventFilter anEventFilter) {
        return (anEventFilter == null || !(anEventFilter.match(anEvent)));
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
     * Initializes an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} with required information
     * like the unlistened domain, user id of the unlistened user/client and the reason for the {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}.
     * @param aUserInfo user
     * @param aDomains unlistened domains
     * @param isTimeout true when the unlisten event is caused by a timeout, false when the unlisten event is caused by a regular unlisten call
     * @return produced {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     */
    private UnlistenEvent produceUnlistenEvent(UserInfo aUserInfo, Set<Domain> aDomains, boolean isTimeout) {
        final UnlistenEvent theUnlistenEvent = aUserInfo.getUnlistenEvent();
        theUnlistenEvent.setUserId(aUserInfo.getUserId());
        theUnlistenEvent.setDomains(aDomains);
        theUnlistenEvent.setTimeout(isTimeout);
        return theUnlistenEvent;
    }

    /**
     * Generates a set of user ids on the basis of {@link de.novanic.eventservice.service.registry.user.UserInfo} objects
     * @param aUserInfoSet set of users ({@link de.novanic.eventservice.service.registry.user.UserInfo})
     * @return set of user ids
     */
    private Set<String> getUserIds(Collection<UserInfo> aUserInfoSet) {
        if(aUserInfoSet == null) {
            return new HashSet<String>(0);
        }
        
        Set<String> theUserIdSet = new HashSet<String>(aUserInfoSet.size());
        for(UserInfo theUserInfo: aUserInfoSet) {
            theUserIdSet.add(theUserInfo.getUserId());
        }
        return theUserIdSet;
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
            unlisten(aUserInfo, true);
        }
    }
}
