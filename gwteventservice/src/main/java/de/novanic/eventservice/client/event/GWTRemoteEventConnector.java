/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschränkt)
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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.callback.AsyncCallbackWrapper;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.event.service.creator.EventServiceCreator;

import java.util.Set;

/**
 * RemoteEventConnector should handle the connections between client- and the server side.
 * GWTRemoteEventConnector uses the GWT-RPC mechanism to communicate with the server side.
 *
 * @author sstrohschein
 *         <br>Date: 12.10.2008
 *         <br>Time: 11:16:23
 */
public class GWTRemoteEventConnector extends DefaultRemoteEventConnector
{
    private EventServiceCreator myEventServiceCreator;
    private EventServiceAsync myEventService;

    /**
     * Creates a new RemoteEventConnector with a connection to the corresponding EventService.
     * @param aGWTEventServiceCreator factory to create the EventService for the connection
     */
    protected GWTRemoteEventConnector(EventServiceCreator aGWTEventServiceCreator) {
        myEventServiceCreator = aGWTEventServiceCreator;
        myEventService = aGWTEventServiceCreator.createEventService();
    }

    /**
     * That method is called to execute the first server call (for initialization).
     * @param aCallback callback
     */
    public void init(final AsyncCallback<EventServiceConfigurationTransferable> aCallback) {
        myEventService.initEventService(new AsyncCallbackWrapper<EventServiceConfigurationTransferable>(aCallback) {
            public void onSuccess(EventServiceConfigurationTransferable anEventServiceConfigurationTransferable) {
                myEventService = refreshEventService(anEventServiceConfigurationTransferable.getConnectionId());
                refreshConnectionStrategyClientConnector(anEventServiceConfigurationTransferable, myEventService);
                super.onSuccess(anEventServiceConfigurationTransferable);
            }
        });
    }

    /**
     * Initializes the listen method implementation with a {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} from the configuration.
     * That is required to specify the listen / connection strategy. The connection strategy can't be changed, when the listening has already started / an listener was added.
     * That implementation initializes the connection strategy with the {@link de.novanic.eventservice.client.event.service.EventService}.
     * @param aConfiguration configuration
     * @return initialized connection strategy connector
     */
    public ConnectionStrategyClientConnector initListen(EventServiceConfigurationTransferable aConfiguration) {
        return initListen(aConfiguration, myEventService, false);
    }

    /**
     * Initializes the listen method implementation with a {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} from the configuration.
     * That is required to specify the listen / connection strategy. The connection strategy can't be changed, when the listening has already started / an listener was added.
     * That implementation initializes the connection strategy with the {@link de.novanic.eventservice.client.event.service.EventService}.
     * @param aConfiguration configuration
     * @param anEventService event service (required for the connection strategy)
     * @param isReinitialize decides if the initialization (init method) of the connection strategy connector is also executed when the connection strategy connector is already initialized
     * (required for refreshes).
     * @return initialized connection strategy connector
     */
    private ConnectionStrategyClientConnector initListen(EventServiceConfigurationTransferable aConfiguration, EventServiceAsync anEventService, boolean isReinitialize) {
        ConnectionStrategyClientConnector theConnectionStrategyClientConnector = super.initListen(aConfiguration);
        if(theConnectionStrategyClientConnector != null && (isReinitialize || !theConnectionStrategyClientConnector.isInitialized())) {
            theConnectionStrategyClientConnector.init(anEventService);
        }
        return theConnectionStrategyClientConnector;
    }

    /**
     * Activates the connector for the domain. An {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * to filter events on the server side is optional.
     * @param aDomain domain to activate
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param aCallback callback
     */
    public void activateStart(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        myEventService.register(aDomain, anEventFilter, aCallback);
    }

    /**
     * Deactivates the connector for the domains (no events can be got from the domains).
     * @param aDomains domains to deactivate
     * @param aCallback callback
     */
    public void deactivate(Set<Domain> aDomains, AsyncCallback<Void> aCallback) {
        myEventService.unlisten(aDomains, aCallback);
    }

    /**
     * Deactivates the connector for the domain (no events can be got from the domain).
     * @param aDomain domain to deactivate
     * @param aCallback callback
     */
    public void deactivate(Domain aDomain, AsyncCallback<Void> aCallback) {
        myEventService.unlisten(aDomain, aCallback);
    }

    /**
     * Sends an event to a domain. The event will be received from all clients which are registered to that domain.
     * User-specific events can be sent with the usage of this domain: {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN}.
     * @param aDomain domain
     * @param anEvent event
     * @param aCallback callback
     */
    public void sendEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback) {
        myEventService.addEvent(aDomain, anEvent, aCallback);
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} to the server side which
     * will be triggered  when a timeout or unlisten/deactivation for a domain occurs.
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can contain custom data
     * @param aCallback callback
     */
    public void registerUnlistenEvent(UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback) {
        super.registerUnlistenEvent(anUnlistenScope, anUnlistenEvent, aCallback);
        //The UnlistenEvent mustn't be registered to the server side, when the scope is local.
        if(UnlistenEventListener.Scope.LOCAL != anUnlistenScope) {
            myEventService.registerUnlistenEvent(anUnlistenScope, anUnlistenEvent, aCallback);
        }
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain. That can be used when
     * the domain is already activated and an {@link de.novanic.eventservice.client.event.filter.EventFilter} is
     * needed later or isn't available when the domain becomes active.
     * @param aDomain domain
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param aCallback callback
     */
    public void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        myEventService.registerEventFilter(aDomain, anEventFilter, aCallback);
    }

    /**
     * Deregisters the {@link de.novanic.eventservice.client.event.filter.EventFilter} for a domain.
     * @param aDomain domain to remove the EventFilter from
     * @param aCallback callback
     */
    public void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback) {
        myEventService.deregisterEventFilter(aDomain, aCallback);
    }

    /**
     * Refreshes / re-initializes the {@link de.novanic.eventservice.client.event.service.EventService} when an explicit
     * connection id is assigned for the client. When the connection id is NULL (no explicit connection id assigned), the
     * {@link de.novanic.eventservice.client.event.service.EventService} will not be refreshed, because no separate connection
     * id has to be transferred back to the server.
     * @param aConnectionId connection id
     * @return refreshed / re-initialized {@link de.novanic.eventservice.client.event.service.EventService}
     */
    private EventServiceAsync refreshEventService(String aConnectionId) {
        if(aConnectionId != null) {
            EventServiceAsync theEventService = myEventServiceCreator.createEventService();

            final ServiceDefTarget theServiceDefTarget = (ServiceDefTarget)theEventService;
            theServiceDefTarget.setServiceEntryPoint(theServiceDefTarget.getServiceEntryPoint() + "?id=" + aConnectionId);

            return theEventService;
        }
        return myEventService;
    }

    /**
     * Refreshes or creates the connection strategy connector.
     * @param aConfiguration configuration
     * @param anEventService event service (required for the connection strategy)
     * @return initialized connection strategy connector
     */
    private ConnectionStrategyClientConnector refreshConnectionStrategyClientConnector(EventServiceConfigurationTransferable aConfiguration, EventServiceAsync anEventService) {
        return initListen(aConfiguration, anEventService, true);
    }
}