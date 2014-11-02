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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 23:23:43
 */
public class EventListenerTestMode implements RemoteEventListener
{
    private List<Event> myEvents;
    private Map<Class, Integer> myEventCountMap;

    public EventListenerTestMode() {
        myEvents = new ArrayList<Event>();
        myEventCountMap = new HashMap<Class, Integer>();
    }

    public void apply(Event anEvent) {
        addEvent(anEvent);
    }

    public List<Event> getEvents() {
        return myEvents;
    }

    private Integer getEventCountInternal(Class anEventType) {
        return myEventCountMap.get(anEventType);
    }

    public int getEventCount() {
        return myEventCountMap.size();
    }

    public int getEventCount(Class anEventType) {
        Integer theCount = getEventCountInternal(anEventType);
        if(theCount == null) {
            theCount = 0;
        }
        return theCount;
    }

    protected void addEvent(Event anEvent) {
        myEvents.add(anEvent);
        Integer theEventCount = getEventCountInternal(anEvent.getClass());
        if(theEventCount == null || theEventCount == 0) {
            myEventCountMap.put(anEvent.getClass(), 1);
        } else {
            myEventCountMap.put(anEvent.getClass(), theEventCount + 1);
        }
    }
}