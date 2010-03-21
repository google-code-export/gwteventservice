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
package de.novanic.eventservice.client.event.filter;

/**
 * AppendableEventFilter ensures that the {@link de.novanic.eventservice.client.event.filter.EventFilter} can handle more
 * attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances. Depending on the implementation
 * of the AppendableEventFilter, the method
 * {@link de.novanic.eventservice.client.event.filter.EventFilter#match(de.novanic.eventservice.client.event.Event)} of the
 * super class must be called first, when the method is overwritten.
 *
 * @author sstrohschein
 *         <br>Date: 04.03.2009
 *         <br>Time: 20:18:01
 */
public interface AppendableEventFilter extends EventFilter
{
    /**
     * Appends another {@link de.novanic.eventservice.client.event.filter.EventFilter} which should also be used/called from the match method
     * ({@link de.novanic.eventservice.client.event.filter.EventFilter#match(de.novanic.eventservice.client.event.Event)}).
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to attach
     * @return the created AppendableEventFilter (to attach more {@link de.novanic.eventservice.client.event.filter.EventFilter} instances)
     */
    AppendableEventFilter attach(EventFilter anEventFilter);
}