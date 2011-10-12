/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschränkt)
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
package de.novanic.eventservice.client.event.filter;

import de.novanic.eventservice.client.event.Event;

import java.io.Serializable;

/**
 * An EventFilter can be used to filter events. When an event is filtered, the user/client shouldn't be informed about
 * the event. EventFilters are useful when the filter function for domains/contexts is to common.
 *
 * @author sstrohschein
 * <br>Date: 05.06.2008
 * <br>Time: 19:09:11
 */
public interface EventFilter extends Serializable
{
    /**
     * If the match method returns true for an event, the event should be ignored, because the EventFilter recognizes
     * the event to filter it.
     * @param anEvent event to check
     * @return true when the event should be filtered, otherwise false
     */
    boolean match(Event anEvent);
}