/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.event.listener.unlisten;

import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.service.registry.domain.ListenDomainAccessor;

import java.util.Set;

/**
 * The UnlistenEventFilter filters all {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} instances
 * by default, which doesn't match the registered domains of the user/client for report reasons.
 *
 * @author sstrohschein
 *         <br>Date: 16.08.2009
 *         <br>Time: 17:59:36
 */
public class UnlistenEventFilter implements EventFilter
{
    private final String myUserId;
    private final ListenDomainAccessor myListenDomainAccessor;
    private final UnlistenEventListener.Scope myUnlistenScope;

    public UnlistenEventFilter(ListenDomainAccessor aListenDomainAccessor, String aUserId, UnlistenEventListener.Scope anUnlistenScope) {
        myUserId = aUserId;
        myListenDomainAccessor = aListenDomainAccessor;
        myUnlistenScope = anUnlistenScope;
    }

    /**
     * Filters all {@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent} instances which doesn't
     * match the registered domains for the current user/client or which are sent on a other scope ({@link UnlistenEventListener.Scope}.
     * @param anEvent event to check
     * @return true when the event should be filtered, otherwise false
     */
    public boolean match(Event anEvent) {
        if(anEvent instanceof UnlistenEvent) {
            final UnlistenEvent theUnlistenEvent = (UnlistenEvent)anEvent;
            if(UnlistenEventListener.Scope.UNLISTEN == myUnlistenScope ||
                    (UnlistenEventListener.Scope.TIMEOUT == myUnlistenScope && theUnlistenEvent.isTimeout())) {
                final Set<Domain> theUnlistenedDomains = theUnlistenEvent.getDomains();
                if(theUnlistenedDomains != null && !theUnlistenedDomains.isEmpty()) {
                    final Set<Domain> theRegisteredListenDomains = myListenDomainAccessor.getListenDomains(myUserId);
                    for(Domain theUnlistenedDomain: theUnlistenedDomains) {
                        if(theRegisteredListenDomains.contains(theUnlistenedDomain)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}