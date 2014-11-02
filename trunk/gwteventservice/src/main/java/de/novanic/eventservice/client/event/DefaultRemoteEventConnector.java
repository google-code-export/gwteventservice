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
package de.novanic.eventservice.client.event;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

import com.google.gwt.user.client.rpc.StatusCodeException;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.callback.AsyncCallbackWrapper;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.event.listener.EventNotification;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.logger.ClientLogger;
import de.novanic.eventservice.client.logger.ClientLoggerFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.DefaultUnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;

/**
 * RemoteEventConnector should handle the connections between client- and the server side.
 *
 * @author sstrohschein
 *         <br>Date: 14.10.2008
 *         <br>Time: 11:34:45
 */
public abstract class DefaultRemoteEventConnector implements RemoteEventConnector
{
    private static final ClientLogger LOG = ClientLoggerFactory.getClientLogger();

    private boolean isActive;
    private EventServiceConfigurationTransferable myConfiguration;
    private ConnectionStrategyClientConnector myConnectionStrategyClientConnector;
    private UnlistenEvent myUnlistenEvent;
    private int myErrorCount;

    /**
     * Initializes the listen method implementation with a {@link de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector} from the configuration.
     * That is required to specify the listen / connection strategy. The connection strategy can't be changed, when the listening has already started / an listener was added.
     * @param aConfiguration configuration
     */
    public synchronized ConnectionStrategyClientConnector initListen(EventServiceConfigurationTransferable aConfiguration) {
        if(isActive) {
            throw new RemoteEventServiceRuntimeException("Invalid attempt to change the connection strategy after listening was started!");
        }
        if(aConfiguration == null) {
            throw new RemoteEventServiceRuntimeException("Invalid attempt to initialize the listening without a configuration!");
        }
        myConfiguration = aConfiguration;
        ConfigurationTransferableDependentFactory theConfigDependentFactory = ConfigurationTransferableDependentFactory.getInstance(aConfiguration);
        return (myConnectionStrategyClientConnector = theConfigDependentFactory.getConnectionStrategyClientConnector());
    }

    /**
     * Deactivates the connector for all domains (no events can be got from the domains).
     */
    public synchronized void deactivate() {
        if(isActive) {
            isActive = false;
            myConnectionStrategyClientConnector.deactivate();
            LOG.log("RemoteEventConnector deactivated.");
        }
    }

    /**
     * Checks if the connector is active (listening).
     * @return true when active/listening, otherwise false
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Activates the connector for the domain. An {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * to filter events on the server side is optional.
     * @param aDomain domain to activate
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param anEventNotification supports the notification about incoming events
     * @param aCallback callback
     */
    public void activate(Domain aDomain, EventFilter anEventFilter, EventNotification anEventNotification, AsyncCallback<Void> aCallback) {
        LOG.log("Activate RemoteEventConnector for domain \"" + aDomain + "\".");
        activateStart(aDomain, anEventFilter, new ActivationCallback<Void>(anEventNotification, aCallback));
    }

    /**
     * Registers an {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} to the server side which
     * will be triggered  when a timeout or unlisten/deactivation for a domain occurs.
     * The UnlistenEvent will also be hold at the client side to trigger on local timeouts (for e.g. connection errors).
     * @param anUnlistenScope scope of the unlisten events to receive
     * @param anUnlistenEvent {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} which can contain custom data
     * @param aCallback callback
     */
    public void registerUnlistenEvent(UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback) {
        myUnlistenEvent = anUnlistenEvent;
    }

    /**
     * Activates the connector for the domain. An {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * to filter events on the server side is optional.
     * @param aDomain domain to activate
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param aCallback callback
     */
    protected abstract void activateStart(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback);

    /**
     * Creates the {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} for local timeouts.
     * @param anEventNotification {@link de.novanic.eventservice.client.event.listener.EventNotification} for the triggered
     * {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}
     */
    private void fireUnlistenEvent(EventNotification anEventNotification) {
        if(myUnlistenEvent == null) {
            myUnlistenEvent = new DefaultUnlistenEvent();
        }
        myUnlistenEvent.setTimeout(false);
        myUnlistenEvent.setLocal(true);
        final DomainEvent theUnlistenDomainEvent = new DefaultDomainEvent(myUnlistenEvent, DomainFactory.UNLISTEN_DOMAIN);
        anEventNotification.onNotify(theUnlistenDomainEvent);
    }

    /**
     * Callback to activate listening of RemoteEventConnector.
     */
    private final class ActivationCallback<T> extends AsyncCallbackWrapper<T>
    {
        private final EventNotification myEventNotification;

        private ActivationCallback(EventNotification anEventNotification, AsyncCallback<T> aCallback) {
            super(aCallback);
            myEventNotification = anEventNotification;
        }

        /**
         * Starts listening to get events from the server side.
         * @param aResult unused
         * @see de.novanic.eventservice.client.event.GWTRemoteEventConnector.ListenEventCallback#callListen()
         */
        public void onSuccess(T aResult) {
            if(!isActive) {
                if(myConnectionStrategyClientConnector != null) {
                    LOG.log("RemoteEventConnector activated.");
                    isActive = true;
                    final ListenEventCallback theListenEventCallback = new ListenEventCallback(myEventNotification);
                    theListenEventCallback.callListen();
                } else {
                    throw new RemoteEventServiceRuntimeException("No connection strategy was set at the start of listening!");
                }
            }
            super.onSuccess(aResult);
        }

        public void onFailure(Throwable aThrowable) {
            LOG.error("Error on register client for domain!", aThrowable);
            fireUnlistenEvent(myEventNotification);
            super.onFailure(aThrowable);
        }
    }

    /**
     * The ListenEventCallback is used to produce the listen cycle. It is attached as callback for the listen server call.
     */
    private final class ListenEventCallback implements AsyncCallback<List<DomainEvent>>
    {
        private EventNotification myEventNotification;

        private ListenEventCallback(EventNotification anEventNotification) {
            myEventNotification = anEventNotification;
        }

        /**
         * When an error occurs while listening for events, reconnect attempts are started (when configured) and an unlisten event
         * will be processed when the connection error could not be solved (to clean-up the client side).
         * @param aThrowable
         */
        public void onFailure(Throwable aThrowable) {
            if((aThrowable instanceof StatusCodeException) && !isNotableStatusCode((StatusCodeException)aThrowable)) {
                //Status code 0 is not handled as an error. Some browsers send this status code when the user leaves the site/application.
                //The module is unloaded in this case and it has no negative effects to the application. Therefore it isn't a notable error.
                LOG.log("The current connection was terminated with status code " + ((StatusCodeException)aThrowable).getStatusCode() + '.');
                fireUnlistenEvent(myEventNotification); //client side clean-up
            } else {
                LOG.error("Error on processing event!", aThrowable);
                if(++myErrorCount > myConfiguration.getReconnectAttemptCount()) {
                    fireUnlistenEvent(myEventNotification); //client side clean-up
                } else {
                    LOG.log("Reconnecting after error...");
                    callListen();
                }
            }
        }

        /**
         * Calls listen on the server side and put itself as the callback to produces the listen cycle as long as the
         * RemoteEventConnector is active.
         * @param anEvents) events to process
         * @see de.novanic.eventservice.client.event.GWTRemoteEventConnector.ListenEventCallback#callListen()
         */
        public void onSuccess(List<DomainEvent> anEvents) {
            myErrorCount = 0;
            if(anEvents != null) {
                for(DomainEvent theEvent: anEvents) {
                    myEventNotification.onNotify(theEvent);
                }
                callListen();
            } else {
                //if the remote service doesn't know the client, all listeners will be removed and the connection becomes inactive
                deactivate();
                myEventNotification.onAbort();
            }
        }

        /**
         * Calls listen at the server side to receive events.
         */
        public synchronized void callListen() {
            if(isActive) {
                //after getting an event, register itself to listen for the next events
                myConnectionStrategyClientConnector.listen(myEventNotification, this);
            }
        }

        /**
         * Checks if the status code is a valid error code. For example status code 0 is rather an informational status code
         * instead of a notable error status code.
         * @param aStatusCodeException {@link StatusCodeException} which occurred with the status code
         * @return true when it is a notable error status code, otherwise false
         */
        private boolean isNotableStatusCode(StatusCodeException aStatusCodeException) {
            return aStatusCodeException.getStatusCode() != 0;
        }
    }
}