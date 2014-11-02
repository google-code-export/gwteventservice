/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * Other licensing for GWTEventService may also be possible on request.
 * Please view the license.txt of the project for more information.
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
package de.novanic.eventservice.client.connection.strategy.connector;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Set;

/**
 * The RemoteEventConnector should handle the connection between the client and the server side.
 *
 * @author sstrohschein
 *         <br>Date: 12.10.2008
 *         <br>Time: 11:16:14
 */
public interface RemoteEventConnector
{
    /**
     * That method is called to execute the first server call (for initialization).
     * @param aCallback callback
     */
    void init(AsyncCallback<EventServiceConfigurationTransferable> aCallback);

    /**
     * Initializes the listen method implementation with a {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} from the configuration.
     * That is required to specify the listen / connection strategy.
     * @param aConfiguration configuration
     */
    ConnectionStrategyClientConnector initListen(EventServiceConfigurationTransferable aConfiguration);

    /**
     * Activates the connector for the domain. An {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * to filter events on the server side is optional.
     * @param aDomain domain to activate
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param anEventNotification supports the notification about incoming events
     * @param aCallback callback
     */
    void activate(Domain aDomain, EventFilter anEventFilter, EventNotification anEventNotification, AsyncCallback<Void> aCallback);

    /**
     * Deactivates the connector for all domains (no events can be got from the domains).
     */
    void deactivate();

    /**
     * Deactivates the connector for the domains (no events can be got from the domains).
     * @param aDomains domains to deactivate
     * @param aCallback callback
     */
    void deactivate(Set<Domain> aDomains, AsyncCallback<Void> aCallback);

    /**
     * Deactivates the connector for the domain (no events can be got from the domain).
     * @param aDomain domain to deactivate
     * @param aCallback callback
     */
    void deactivate(Domain aDomain, AsyncCallback<Void> aCallback);

    /**
     * Checks if the connector is active (listening).
     * @return true when active/listening, otherwise false
     */
    boolean isActive();

    /**
     * Sends an event to a domain. The event will be received from all clients which are registered to that domain.
     * User-specific events can be sent with the usage of this domain: {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN}.
     * @param aDomain domain
     * @param anEvent event
     * @param aCallback callback
     */
    void sendEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback);

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} to the server side which
     * will be triggered  when a timeout or unlisten/deactivation for a domain occurs.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can contain custom data
     * @param aCallback callback
     */
    void registerUnlistenEvent(UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback);

    /**
     * Registers an {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain. That can be used when
     * the domain is already activated and an {@link de.novanic.eventservice.client.event.filter.EventFilter} is
     * needed later or isn't available when the domain becomes active.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param aCallback callback
     */
    void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback);

    /**
     * Deregisters the {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain.
     * @param aDomain domain to remove the EventFilter from
     * @param aCallback callback
     */
    void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback);
}