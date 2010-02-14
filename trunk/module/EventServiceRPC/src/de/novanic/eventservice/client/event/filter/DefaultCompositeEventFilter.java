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

import de.novanic.eventservice.client.event.Event;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * The DefaultCompositeEventFilter can handle various attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
 * The match method calls all attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances to check if the
 * {@link de.novanic.eventservice.client.event.Event} has to be filtered.
 *
 * @author sstrohschein
 *         <br>Date: 04.03.2009
 *         <br>Time: 20:56:07
 */
public class DefaultCompositeEventFilter implements CompositeEventFilter
{
    private List<EventFilter> myEventFilters;

    /**
     * Creates a new DefaultCompositeEventFilter. {@link de.novanic.eventservice.client.event.filter.EventFilter} instances
     * must be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)} or
     * the constructor {@link de.novanic.eventservice.client.event.filter.DefaultCompositeEventFilter#DefaultCompositeEventFilter(EventFilter...)} must
     * be used to register {@link de.novanic.eventservice.client.event.filter.EventFilter} instances for the match method.
     */
    public DefaultCompositeEventFilter() {
        myEventFilters = new ArrayList<EventFilter>();
    }

    /**
     * Creates a new DefaultCompositeEventFilter. The {@link de.novanic.eventservice.client.event.filter.EventFilter} instances
     * are used by the match method to filter the events. More {@link de.novanic.eventservice.client.event.filter.EventFilter} instances
     * can be attached with the attach method ({@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}).
     * @param anEventFilters {@link de.novanic.eventservice.client.event.filter.EventFilter} which are used by the match method
     */
    public DefaultCompositeEventFilter(EventFilter... anEventFilters) {
        myEventFilters = new ArrayList<EventFilter>(Arrays.asList(anEventFilters));
    }

    /**
     * If the match method returns true for an event, the event should be ignored, because the EventFilter recognizes
     * the event to filter it. The DefaultCompositeEventFilter uses the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     * instances to filter the events. See {@link de.novanic.eventservice.client.event.filter.DefaultCompositeEventFilter#DefaultCompositeEventFilter(EventFilter...)}
     * or {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)} to append
     * {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * @param anEvent event to check
     * @return true when the event should be filtered, otherwise false
     */
    public boolean match(Event anEvent) {
        for(EventFilter theEventFilter: myEventFilters) {
            if(theEventFilter.match(anEvent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends another {@link de.novanic.eventservice.client.event.filter.EventFilter} which is used/called from the match method
     * ({@link de.novanic.eventservice.client.event.filter.EventFilter#match(de.novanic.eventservice.client.event.Event)}).
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to attach
     * @return the created AppendableEventFilter (to attach more {@link de.novanic.eventservice.client.event.filter.EventFilter} instances)
     */
    public AppendableEventFilter attach(EventFilter anEventFilter) {
        myEventFilters.add(anEventFilter);
        return this;
    }

    /**
     * Detaches the attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return true if the EventFilter was removed with that call, otherwise false
     */
    public boolean detach(EventFilter anEventFilter) {
        return myEventFilters.remove(anEventFilter);
    }

    /**
     * Detaches all attached {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return true if at least one EventFilter was removed with that call, otherwise false
     */
    public boolean detach() {
        boolean isEmpty = myEventFilters.isEmpty();
        myEventFilters.clear();
        return !isEmpty;
    }

    /**
     * Returns the attached EventFilters.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    public List<EventFilter> getAttachedEventFilters() {
        return myEventFilters;
    }
}