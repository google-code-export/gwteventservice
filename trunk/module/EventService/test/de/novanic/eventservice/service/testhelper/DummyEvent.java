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
package de.novanic.eventservice.service.testhelper;

import de.novanic.eventservice.client.event.Event;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 21:54:44
 */
public class DummyEvent implements Event
{
    private static final String DUMMY_EVENT_KEY = DummyEvent.class.getName();

    private int myId;

    public DummyEvent() {
        myId = AutoIncrementFactory.getInstance().getNextValue(DUMMY_EVENT_KEY);
    }

    public int getId() {
        return myId;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }
        DummyEvent that = (DummyEvent)anObject;
        return myId == that.myId;
    }

    public int hashCode() {
        return myId;
    }

    public String toString() {
        return "Event: DummyEvent";
    }
}