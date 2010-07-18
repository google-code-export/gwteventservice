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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.core.client.GWT;

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
    private EventServiceAsync myEventService;

    /**
     * Creates a new RemoteEventConnector with a connection to {@link EventService}.
     */
    protected GWTRemoteEventConnector() {
        this(createEventService());
    }

    /**
     * Creates a new RemoteEventConnector with a connection to the corresponding EventService.
     * @param anEventServiceAsync EventService for the connection
     */
    protected GWTRemoteEventConnector(EventServiceAsync anEventServiceAsync) {
        myEventService = anEventServiceAsync;
    }

    /**
     * That method is called to execute the first server call (for initialization).
     * @param aCallback callback
     */
    public void init(final AsyncCallback<EventServiceConfigurationTransferable> aCallback) {
        myEventService.initEventService(new AsyncCallback<EventServiceConfigurationTransferable>() {
            public void onSuccess(EventServiceConfigurationTransferable anEventServiceConfigurationTransferable) {
                myEventService = refreshEventService(anEventServiceConfigurationTransferable.getConnectionId());
                aCallback.onSuccess(anEventServiceConfigurationTransferable);
            }

            public void onFailure(Throwable aThrowable) {
                aCallback.onFailure(aThrowable);
            }
        });
    }

    /**
     * Initializes the listen method implementation with a {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector}.
     * That is required to specify the listen / connection strategy. The connection strategy can't be changed, when the listening has already started / an listener was added.
     * That implementation initializes the connection strategy with the {@link de.novanic.eventservice.client.event.service.EventService}.
     * @param aConnectionStrategyClientConnector {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} which implements the listen method
     */
    public void initListen(ConnectionStrategyClientConnector aConnectionStrategyClientConnector) {
        super.initListen(aConnectionStrategyClientConnector);
        if(!aConnectionStrategyClientConnector.isInitialized()) {
            aConnectionStrategyClientConnector.init(myEventService);
        }
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
     * @param aDomain domain
     * @param anEvent event
     * @param aCallback callback
     */
    public void sendEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback) {
        myEventService.addEvent(aDomain, anEvent, aCallback);
    }

    /**
     * Sends an event to the calling user / client and get eventually filtered at the server side
     * (when an {@link de.novanic.eventservice.client.event.filter.EventFilter} is used).
     * @param anEvent event
     * @param aCallback callback
     */
    public void sendEventUserSpecific(Event anEvent, AsyncCallback<Void> aCallback) {
        myEventService.addEventUserSpecific(anEvent, aCallback);
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
     * Creates an instance of the EventService.
     * @return EventService
     */
    private static EventServiceAsync createEventService() {
        final String theServiceURL = GWT.getModuleBaseURL() + "gwteventservice";
        return createEventService(theServiceURL);
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
            final String theServiceURL = GWT.getModuleBaseURL() + "gwteventservice?id=" + aConnectionId;
            return createEventService(theServiceURL);
        }
        return myEventService;
    }

    private static EventServiceAsync createEventService(String aServiceURL) {
        ServiceDefTarget theServiceEndPoint = (ServiceDefTarget)GWT.create(EventService.class);
        theServiceEndPoint.setServiceEntryPoint(aServiceURL);
        return (EventServiceAsync)theServiceEndPoint;
    }
}