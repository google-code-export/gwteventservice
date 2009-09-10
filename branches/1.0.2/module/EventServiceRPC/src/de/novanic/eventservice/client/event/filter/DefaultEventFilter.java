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
package de.novanic.eventservice.client.event.filter;

import de.novanic.eventservice.client.event.Event;

/**
 * Default implementation of the {@link de.novanic.eventservice.client.event.filter.EventFilter} interface.
 * The DefaultEventFilter doesn't filter any events, because the match method returns false.
 *
 * @author sstrohschein
 * <br>Date: 20.07.2008
 * <br>Time: 15:33:39
 */
public class DefaultEventFilter implements EventFilter
{
    /**
     * No events should be filtered, because this method returns false.
     * @param anEvent event
     * @return false
     */
    public boolean match(Event anEvent) {
        return false;
    }
}