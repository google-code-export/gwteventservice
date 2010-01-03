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

import de.novanic.eventservice.client.event.listener.RemoteEventListener;

import java.util.Map;
import java.util.HashMap;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 23:23:43
 */
public class TestEventListener implements RemoteEventListener
{
    private Map<String, Integer> myEventMap;
    private RemoteEventListener myListener;

    public TestEventListener() {
        myEventMap = new HashMap<String, Integer>();
    }

    public void apply(Event anEvent) {
        addEvent(anEvent);
    }

    private Integer getEventCountInternal(String anEventType) {
        return myEventMap.get(anEventType);
    }

    public int getEventCount(String anEventType) {
        Integer theCount = getEventCountInternal(anEventType);
        if(theCount == null) {
            theCount = 0;
        }
        return theCount;
    }

    public void setListener(RemoteEventListener aListener) {
        myListener = aListener;
    }

    private void addEvent(Event anEvent) {
        Integer theEventCount = getEventCountInternal(anEvent.getClass().getName());
        if(theEventCount == null || theEventCount == 0) {
            myEventMap.put(anEvent.getClass().getName(), 1);
        } else {
            myEventMap.put(anEvent.getClass().getName(), theEventCount + 1);
        }
        if(myListener != null) {
            myListener.apply(anEvent);
        }
    }
}