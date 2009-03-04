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
package de.novanic.eventservice.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;
import java.util.Set;

import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;

/**
 * {@link de.novanic.eventservice.client.event.service.EventService} is the server side interface to register listen
 * requests for domains and to add events.
 *
 * @author sstrohschein
 * <br>Date: 05.06.2008
 * <br>Time: 19:12:17
 */
public class EventServiceImpl extends RemoteServiceServlet implements EventService
{
    private static final ServerLogger LOG = ServerLoggerFactory.getServerLogger(EventServiceImpl.class.getName());
    private final EventRegistry myEventRegistry;

    public EventServiceImpl() {
        final EventRegistryFactory theEventRegistryFactory = EventRegistryFactory.getInstance();
        myEventRegistry = theEventRegistryFactory.getEventRegistry();
    }

    /**
     * Register listen for a domain.
     * @param aDomain domain to listen to
     */
    public void register(Domain aDomain) {
        register(aDomain, null);
    }

    /**
     * Register listen for a domain.
     * @param aDomain domain to listen to
     * @param anEventFilter EventFilter to filter events
     */
    public void register(Domain aDomain, EventFilter anEventFilter) {
        final String theClientId = getClientId();
        myEventRegistry.registerUser(aDomain, theClientId, anEventFilter);
    }

    /**
     * Register listen for a domain.
     * @param aDomains domains to listen to
     */
    public void register(Set<Domain> aDomains) {
        for(Domain aDomain : aDomains) {
            register(aDomain);
        }
    }

    /**
     * Register listen for domains.
     * @param aDomains domains to listen to
     * @param anEventFilter EventFilter to filter events (applied to all domains)
     */
    public void register(Set<Domain> aDomains, EventFilter anEventFilter) {
        for(Domain aDomain : aDomains) {
            register(aDomain, anEventFilter);
        }
    }

    /**
     * Registers an {@link EventFilter} for the domain.
     * @param aDomain domain to register the EventFilter to
     * @param anEventFilter EventFilter to filter events for the domain
     */
    public void registerEventFilter(Domain aDomain, EventFilter anEventFilter) {
        final String theClientId = getClientId();
        myEventRegistry.setEventFilter(aDomain, theClientId, anEventFilter);
    }

    /**
     * Deregisters the {@link EventFilter} of the domain.
     * @param aDomain domain to drop the EventFilters from
     */
    public void deregisterEventFilter(Domain aDomain) {
        final String theClientId = getClientId();
        myEventRegistry.removeEventFilter(aDomain, theClientId);
    }

    /**
     * Returns the EventFilter for the user domain combination.
     * @param aDomain domain
     * @return EventFilter for the domain
     */
    public EventFilter getEventFilter(Domain aDomain) {
        final String theClientId = getClientId();
        return myEventRegistry.getEventFilter(aDomain, theClientId);
    }

    /**
     * The listen method returns all events for the user (events for all domains where the user is registered and user
     * specific events). If no events are available, the method waits a defined time before the events are returned.
     * The client side calls the method with a defined interval to receive all events. If the client don't call the
     * method in the interval, the user will be removed from the EventRegistry. The timeout time and the waiting time
     * can be configured with {@link de.novanic.eventservice.config.EventServiceConfiguration}.
     * @return list of events
     */
    public List<DomainEvent> listen() {
        final String theClientId = getClientId();
        LOG.debug("Listen (client id \"" + theClientId + "\").");
        return myEventRegistry.listen(theClientId);
    }

    /**
     * Unlisten for events (for the current user) in all domains (deregisters the user from all domains).
     */
    public void unlisten() {
        final String theClientId = getClientId();
        LOG.debug("Unlisten (client id \"" + theClientId + "\").");
        myEventRegistry.unlisten(theClientId);
        LOG.debug("Unlisten finished (client id \"" + theClientId + "\").");
    }

    /**
     * Unlisten for events (for the current user) in the domain and deregisters the user from the domain.
     * @param aDomain domain to unlisten
     */
    public void unlisten(Domain aDomain) {
        final String theClientId = getClientId();
        LOG.debug("Unlisten (client id \"" + theClientId + "\").");
        myEventRegistry.unlisten(aDomain, theClientId);
        LOG.debug("Unlisten finished (client id \"" + theClientId + "\").");
    }

    /**
     * Unlisten for events (for the current user) in the domains and deregisters the user from the domains.
     * @param aDomains set of domains to unlisten
     */
    public void unlisten(Set<Domain> aDomains) {
        for(Domain theDomain: aDomains) {
            unlisten(theDomain);
        }
    }

    /**
     * Checks if the user is registered for event listening.
     * @param aDomain domain to check
     * @return true when the user is registered for listening, otherwise false
     */
    public boolean isUserRegistered(Domain aDomain) {
        final String theClientId = getClientId();
        return myEventRegistry.isUserRegistered(aDomain, theClientId);
    }

    /**
     * Adds an event for all users in the domain.
     * @param aDomain domain to add the event
     * @param anEvent event to add
     */
    public void addEvent(Domain aDomain, Event anEvent) {
        myEventRegistry.addEvent(aDomain, anEvent);
    }

    /**
     * Adds an event only for the current user.
     * @param anEvent event to add to the user
     */
    public void addEventUserSpecific(Event anEvent) {
        final String theClientId = getClientId();
        myEventRegistry.addEventUserSpecific(theClientId, anEvent);
    }

    /**
     * Returns the domain names, where the user is listening to
     * @return collection of domain names
     */
    public Set<Domain> getActiveListenDomains() {
        final String theClientId = getClientId();
        return myEventRegistry.getListenDomains(theClientId);
    }

    /**
     * Returns the client id.
     * @return client id
     */
    protected String getClientId() {
        return getThreadLocalRequest().getSession().getId();
    }
}