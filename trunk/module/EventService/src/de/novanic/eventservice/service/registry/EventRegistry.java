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
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.config.EventServiceConfiguration;

import java.util.List;
import java.util.Set;

/**
 * The EventRegistry handles the users/clients and the events per domain. Users can be registered for a domain/context
 * to receive events for the corresponding domain.
 * User specific events can be handled domainless, when the user is registered.
 * The EventRegistry is used by {@link de.novanic.eventservice.service.EventServiceImpl}.
 *
 * <br>The client id is required, because the connection to every client must be kept open.
 *
 * @see de.novanic.eventservice.service.EventServiceImpl
 *
 * @author sstrohschein
 * <br>Date: 09.08.2008
 * <br>Time: 22:28:24
 */
public interface EventRegistry
{
    /**
     * Checks if the user is registered for any domain.
     * @param aUserId the user to check
     * @return true if registered, false if not registered
     */
    boolean isUserRegistered(String aUserId);

    /**
     * Checks if the user is registered for the corresponding domain.
     * @param aDomain the domain to check
     * @param aUserId the user to check
     * @return true if registered, false if not registered
     */
    boolean isUserRegistered(Domain aDomain, String aUserId);

    /**
     * Registers a user for listening for the corresponding domain. From now all events for the domain are recognized and
     * will be returned when listen ({@link EventRegistry#listen(String)}) is called. The {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * is optional and can be NULL.
     * @param aDomain the domain to listen
     * @param aUserId the user to register
     * @param anEventFilter EventFilter to filter the domain events (optional, can be NULL)
     */
    void registerUser(Domain aDomain, String aUserId, EventFilter anEventFilter);

    /**
     * The EventFilter for a user domain combination can be set or changed with that method.
     * @param aDomain domain
     * @param aUserId user
     * @param anEventFilter new EventFilter
     */
    void setEventFilter(Domain aDomain, String aUserId, EventFilter anEventFilter);

    /**
     * Returns the EventFilter for the user domain combination.
     * @param aDomain domain
     * @param aUserId user
     * @return EventFilter for the user domain combination
     */
    EventFilter getEventFilter(Domain aDomain, String aUserId);

    /**
     * EventFilters can be removed for a user domain combination with that method.
     * @param aUserId user
     * @param aDomain domain
     */
    void removeEventFilter(Domain aDomain, String aUserId);

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
    List<DomainEvent> listen(String aUserId);

    /**
     * This method causes a stop of listening for a domain ({@link EventRegistry#listen(String)}).
     * @param aDomain domain to stop listening
     * @param aUserId user
     */
    void unlisten(Domain aDomain, String aUserId);

    /**
     * This method causes a stop of listening for all domains ({@link EventRegistry#listen(String)}).
     * @param aUserId user
     */
    void unlisten(String aUserId);

    /**
     * Returns all domains where the user is registered to.
     * @param aUserId user
     * @return domains where the user is registered to
     */
    Set<Domain> getListenDomains(String aUserId);

    /**
     * Returns all registered/activated domains.
     * @return all registered/activated domains
     */
    Set<Domain> getListenDomains();

    /**
     * Returns all registered users/clients.
     * To get only the registered users/client of a specific {@link de.novanic.eventservice.client.event.domain.Domain},
     * the method {@link de.novanic.eventservice.service.registry.EventRegistry#getRegisteredUserIds(de.novanic.eventservice.client.event.domain.Domain)}
     * can be used instead.
     * @return registered users/clients
     */
    Set<String> getRegisteredUserIds();

    /**
     * Returns all registered users/client of a specific {@link de.novanic.eventservice.client.event.domain.Domain}.
     * To get all the registered users/client (of all domains), the method {@link EventRegistry#getRegisteredUserIds()}
     * can be used instead.
     * @param aDomain domain
     * @return registered users/client of the specific domain
     */
    Set<String> getRegisteredUserIds(Domain aDomain);

    /**
     * Adds an event to a domain.
     * @param aDomain domain for the event
     * @param anEvent event to add
     */
    void addEvent(Domain aDomain, Event anEvent);

    /**
     * Adds an event directly to a user. The user must be registered to any domain.
     * @param aUserId user
     * @param anEvent event
     */
    void addEventUserSpecific(String aUserId, Event anEvent);

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
    void registerUnlistenEvent(String aUserId, UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent);

    /**
     * Returns the initialized {@link de.novanic.eventservice.config.EventServiceConfiguration}
     * @return configuration {@link de.novanic.eventservice.config.EventServiceConfiguration}
     */
    EventServiceConfiguration getConfiguration();
}