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

import java.util.List;

/**
 * A CompositeEventFilter can handle various attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
 * The match method calls all attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances to check if the
 * {@link de.novanic.eventservice.client.event.Event} has to be filtered.
 * A other solution to build filter sequences is available with {@link CascadingEventFilter} (cascading filter sequences).
 *
 * @author sstrohschein
 *         <br>Date: 05.03.2009
 *         <br>Time: 12:06:49
 */
public interface CompositeEventFilter extends AppendableEventFilter
{
    /**
     * Returns the attached EventFilters.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    List<EventFilter> getAttachedEventFilters();

    /**
     * Detaches the attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to detach
     * @return true if the EventFilter was removed with that call, otherwise false
     */
    boolean detach(EventFilter anEventFilter);

    /**
     * Detaches all attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return true if at least one EventFilter was removed with that call, otherwise false
     */
    boolean detach();
}