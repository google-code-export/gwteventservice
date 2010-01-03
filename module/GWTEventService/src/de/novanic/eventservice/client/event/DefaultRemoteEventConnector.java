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

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

import de.novanic.eventservice.client.logger.ClientLogger;
import de.novanic.eventservice.client.logger.ClientLoggerFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;

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

    /**
     * Deactivates the connector for all domains (no events can be got from the domains).
     */
    public synchronized void deactivate() {
        if(isActive) {
            isActive = false;
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
    public <T> void activate(Domain aDomain, EventFilter anEventFilter, EventNotification anEventNotification, AsyncCallback<T> aCallback) {
        LOG.log("Activate RemoteEventConnector for domain \"" + aDomain + "\".");
        activateStart(aDomain, anEventFilter, new ActivationCallback<T>(anEventNotification, aCallback));
    }

    /**
     * Starts listening for events (listen call to the server side).
     * @param aCallback callback
     */
    protected abstract void listen(AsyncCallback<List<DomainEvent>> aCallback);

    /**
     * Activates the connector for the domain. An {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * to filter events on the server side is optional.
     * @param aDomain domain to activate
     * @param anEventFilter EventFilter to filter the events on the server side (optional)
     * @param aCallback callback
     */
    protected abstract <T> void activateStart(Domain aDomain, EventFilter anEventFilter, AsyncCallback<T> aCallback);

    /**
     * Callback to activate listening of RemoteEventConnector.
     */
    private final class ActivationCallback<T> implements AsyncCallback<T>
    {
        private final EventNotification myEventNotification;
        private final AsyncCallback<T> myCallback;

        private ActivationCallback(EventNotification anEventNotification, AsyncCallback<T> aCallback) {
            myEventNotification = anEventNotification;
            myCallback = aCallback;
        }

        /**
         * Starts listening to get events from the server side.
         * @param aResult unused
         * @see de.novanic.eventservice.client.event.GWTRemoteEventConnector.ListenEventCallback#callListen()
         */
        public void onSuccess(T aResult) {
            if(!isActive) {
                LOG.log("RemoteEventConnector activated.");
                isActive = true;
                final ListenEventCallback theListenEventCallback = new ListenEventCallback(myEventNotification);
                theListenEventCallback.callListen();
            }
            if(myCallback != null) {
                myCallback.onSuccess(aResult);
            }
        }

        public void onFailure(Throwable aThrowable) {
            LOG.error("Error on register client for domain!", aThrowable);
            if(myCallback != null) {
                myCallback.onFailure(aThrowable);
            }
        }
    }

    /**
     * The ListenEventCallback is used to produce the listen cycle. It executes a {@link de.novanic.eventservice.client.command.RemoteListenCommand}
     * and is attached as callback for the listen server call.
     */
    private final class ListenEventCallback implements AsyncCallback<List<DomainEvent>>
    {
        private EventNotification myEventNotification;

        public ListenEventCallback(EventNotification anEventNotification) {
            myEventNotification = anEventNotification;
        }

        public void onFailure(Throwable aThrowable) {
            LOG.error("Error on processing event!", aThrowable);
        }

        /**
         * Calls listen on the server side and put itself as the callback to produces the listen cycle as long as the
         * RemoteEventConnector is active.
         * @param anEvents) events to process
         * @see de.novanic.eventservice.client.event.GWTRemoteEventConnector.ListenEventCallback#callListen()
         */
        public void onSuccess(List<DomainEvent> anEvents) {
            if(anEvents != null) {
                myEventNotification.onNotify(anEvents);
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
                listen(this);
            }
        }
    }
}