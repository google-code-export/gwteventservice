/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.test.testhelper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.config.RemoteEventServiceConfigurationTransferable;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sstrohschein
 *         <br>Date: 20.10.2010
 *         <br>Time: 20:38:51
 */
public class EventServiceAsyncSuccessDummy implements EventServiceAsync, ServiceDefTarget
{
    private String myServiceURL = "dummyurl";

    public void initEventService(AsyncCallback<EventServiceConfigurationTransferable> aCallback) {
        aCallback.onSuccess(new RemoteEventServiceConfigurationTransferable(0, 0, 99999, "12345", null));
    }

    public void register(Domain aDomain, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void register(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void register(Set<Domain> aDomains, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void register(Set<Domain> aDomains, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void registerUnlistenEvent(UnlistenEventListener.Scope anUnlistenScope, UnlistenEvent anUnlistenEvent, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void registerEventFilter(Domain aDomain, EventFilter anEventFilter, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void deregisterEventFilter(Domain aDomain, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void getEventFilter(Domain aDomain, AsyncCallback<EventFilter> aCallback) {
        aCallback.onSuccess(new EventFilter() {
            public boolean match(Event anEvent) {
                return false;
            }
        });
    }

    public void listen(AsyncCallback<List<DomainEvent>> aCallback) {
        aCallback.onSuccess(new ArrayList<DomainEvent>());
    }

    public void unlisten(AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void unlisten(Domain aDomain, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void unlisten(Set<Domain> aDomains, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void isUserRegistered(Domain aDomain, AsyncCallback<Boolean> aCallback) {
        aCallback.onSuccess(Boolean.FALSE);
    }

    public void addEvent(Domain aDomain, Event anEvent, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void addEventUserSpecific(Event anEvent, AsyncCallback<Void> aCallback) {
        aCallback.onSuccess(null);
    }

    public void getActiveListenDomains(AsyncCallback<Set<Domain>> aCallback) {
        aCallback.onSuccess(new HashSet<Domain>());
    }

    public String getServiceEntryPoint() {
        return myServiceURL;
    }

    public void setServiceEntryPoint(String aServiceEntryPoint) {
        myServiceURL = aServiceEntryPoint;
    }

    public void setRpcRequestBuilder(RpcRequestBuilder aRpcRequestBuilder) {
    }

    public String getSerializationPolicyName() {
        return null;
    }
}