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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;

import java.util.Map;
import java.util.Collection;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 21:45:40
 */
public class ListenStartResult
{
    private Thread myThread;
    private ListenRunnable myListenRunnable;

    public ListenStartResult(Thread aThread, ListenRunnable aListenRunnable) {
        myThread = aThread;
        myListenRunnable = aListenRunnable;
    }

    public Thread getThread() {
        return myThread;
    }

    public ListenRunnable getListenRunnable() {
        return myListenRunnable;
    }

    public ListenResult getListenResult() {
        Map<Domain, Collection<Event>> theDomainEvents = myListenRunnable.getDomainEvents();
        Map<String, Collection<Event>> theUserEvents = myListenRunnable.getUserEvents();
        //the listen call is finished when the events are initialized
        if(theDomainEvents != null && theUserEvents != null) {
            return new ListenResult(theDomainEvents, theUserEvents);
        }
        return null;
    }
}