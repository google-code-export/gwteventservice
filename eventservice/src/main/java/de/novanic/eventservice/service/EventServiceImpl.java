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
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;
import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.config.level.ConfigLevelFactory;
import de.novanic.eventservice.config.loader.WebDescriptorConfigurationLoader;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

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
    private EventRegistry myEventRegistry;

    /**
     * The init method should be called automatically before the servlet can be used and should called only one time.
     * That method initialized the {@link de.novanic.eventservice.service.registry.EventRegistry}.
     * @param aConfig servlet configuration
     * @throws ServletException
     */
    public void init(ServletConfig aConfig) throws ServletException {
        super.init(aConfig);
        myEventRegistry = initEventRegistry(aConfig);
    }

    /**
     * Initializes the {@link de.novanic.eventservice.client.event.service.EventService}.
     */
    public void initEventService() {
        final String theClientId = getClientId(true);
        LOG.debug("Client \"" + theClientId + "\" initialized.");
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
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which is triggered on a
     * timeout or when a user/client leaves a {@link de.novanic.eventservice.client.event.domain.Domain}. An
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} is hold at the server side and can
     * contain custom data. Other users/clients can use the custom data when the event is for example triggered by a timeout.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which should
     * be transfered to other users/clients when a timeout occurs or a domain is leaved.
     */
    public void registerUnlistenEvent(UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent) {
        final String theClientId = getClientId();
        myEventRegistry.registerUnlistenEvent(theClientId, anUnlistenScope, anUnlistenEvent);
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
     * Registers the {@link de.novanic.eventservice.config.loader.WebDescriptorConfigurationLoader},
     * loads the first available configuration (with {@link de.novanic.eventservice.config.EventServiceConfigurationFactory})
     * and initializes the {@link de.novanic.eventservice.service.registry.EventRegistry}.
     * @param aConfig servlet configuration
     * @return initialized {@link de.novanic.eventservice.service.registry.EventRegistry}
     */
    private EventRegistry initEventRegistry(ServletConfig aConfig) {
        final WebDescriptorConfigurationLoader theWebDescriptorConfigurationLoader = new WebDescriptorConfigurationLoader(aConfig);
        final EventServiceConfigurationFactory theEventServiceConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        theEventServiceConfigurationFactory.addConfigurationLoader(ConfigLevelFactory.DEFAULT, theWebDescriptorConfigurationLoader);

        final EventRegistryFactory theEventRegistryFactory = EventRegistryFactory.getInstance();
        EventRegistry theEventRegistry = theEventRegistryFactory.getEventRegistry();

        if(theWebDescriptorConfigurationLoader.isAvailable()) {
            theEventServiceConfigurationFactory.loadEventServiceConfiguration();
        }
        return theEventRegistry;
    }

    /**
     * Returns the client id.
     * @return client id
     */
    private String getClientId() {
        return getClientId(false);
    }

    /**
     * Returns the client id.
     * @param isInitSession initializes the session (if not already initialized).
     * @return client id
     */
    protected String getClientId(boolean isInitSession) {
        return getThreadLocalRequest().getSession(isInitSession).getId();
    }
}
