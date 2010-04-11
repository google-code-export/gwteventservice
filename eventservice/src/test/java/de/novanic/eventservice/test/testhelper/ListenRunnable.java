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

import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.config.ConfigurationDependentFactory;
import de.novanic.eventservice.service.registry.EventRegistry;

import java.util.*;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 21:46:37
 */
public class ListenRunnable implements Runnable, StartObservable
{
    private EventService myEventService;
    private EventRegistry myEventRegistry;
    private String myUserId;
    private Map<Domain, List<Event>> myDomainEvents;
    private Map<String, List<Event>> myUserEvents;
    private boolean isStarted;

    public ListenRunnable(EventService anEventService) {
        myEventService = anEventService;
    }

    public ListenRunnable(EventRegistry anEventRegistry, String aUserId) {
        myEventRegistry = anEventRegistry;
        myUserId = aUserId;
    }

    public void run() {
        isStarted = true;
        
        final List<DomainEvent> theDomainEvents;
        if(myEventService != null) {
            theDomainEvents = myEventService.listen();
        } else {
            theDomainEvents = myEventRegistry.listen(ConfigurationDependentFactory.getInstance(myEventRegistry.getConfiguration()).getConnectionStrategyServerConnector(), myUserId);
        }

        Map<String, List<Event>> theUserEventMap = new HashMap<String, List<Event>>();
        Map<Domain, List<Event>> theDomainEventMap = new HashMap<Domain, List<Event>>();

        if(theDomainEvents != null) {
            for(DomainEvent theDomainEvent: theDomainEvents) {
                if(!(theDomainEvent.getEvent() instanceof ListenCycleCancelEvent)) {
                    if(theDomainEvent.isUserSpecific()) {
                        processUserEvent(theUserEventMap, theDomainEvent.getEvent());
                    } else {
                        processDomainEvent(theDomainEventMap, theDomainEvent);
                    }
                }
            }
        }
        myUserEvents = theUserEventMap;
        myDomainEvents = theDomainEventMap;
    }

    private void processUserEvent(Map<String, List<Event>> aUserEventMap, Event anEvent) {
        List<Event> theEvents = aUserEventMap.get(myUserId);
        if(theEvents == null) {
            theEvents = new ArrayList<Event>();
            aUserEventMap.put(myUserId, theEvents);
        }
        theEvents.add(anEvent);
    }

    private void processDomainEvent(Map<Domain, List<Event>> aDomainEventMap, DomainEvent aDomainEvent) {
        List<Event> theEvents = aDomainEventMap.get(aDomainEvent.getDomain());
        if(theEvents == null) {
            theEvents = new ArrayList<Event>();
            aDomainEventMap.put(aDomainEvent.getDomain(), theEvents);
        }
        theEvents.add(aDomainEvent.getEvent());
    }

    public Map<String, List<Event>> getUserEvents() {
        return myUserEvents;
    }

    public Map<Domain, List<Event>> getDomainEvents() {
        return myDomainEvents;
    }

    public boolean isStarted() {
        return isStarted;
    }
}