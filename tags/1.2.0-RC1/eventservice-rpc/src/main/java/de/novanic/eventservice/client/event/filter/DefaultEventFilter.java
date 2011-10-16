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

/**
 * Default implementation of the {@link de.novanic.eventservice.client.event.filter.EventFilter} interface.
 * When no {@link de.novanic.eventservice.client.event.filter.EventFilter} is attached to the DefaultEventFilter,
 * the DefaultEventFilter doesn't filter any events, because the match method returns false. An optional
 * {@link de.novanic.eventservice.client.event.filter.EventFilter} instance can be attached using
 * {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)} or the constructor
 * {@link de.novanic.eventservice.client.event.filter.DefaultEventFilter#DefaultEventFilter(EventFilter)} and is used/called
 * by the default implementation of the match method of DefaultEventFilter. The DefaultEventFilter implements the
 * {@link de.novanic.eventservice.client.event.filter.CascadingEventFilter} interface to allow to build cascading filter
 * sequences. A CascadingEventFilter can only hold one (the next) {@link de.novanic.eventservice.client.event.filter.EventFilter}.
 * See {@link de.novanic.eventservice.client.event.filter.CascadingEventFilter} for more information.
 *
 * @author sstrohschein
 * <br>Date: 20.07.2008
 * <br>Time: 15:33:39
 */
public class DefaultEventFilter implements CascadingEventFilter
{
    private EventFilter myNextEventFilter;

    /**
     * Creates a new DefaultEventFilter. An optional, appended {@link de.novanic.eventservice.client.event.filter.EventFilter} can
     * be set/attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)} or
     * the constructor {@link de.novanic.eventservice.client.event.filter.DefaultEventFilter#DefaultEventFilter(EventFilter)}.
     */
    public DefaultEventFilter() {}

    /**
     * Creates a new DefaultEventFilter. The {@link de.novanic.eventservice.client.event.filter.EventFilter} is used by
     * the default implementation of the match method (in DefaultEventFilter) to filter the events.
     * @param aNextEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to attach
     */
    public DefaultEventFilter(EventFilter aNextEventFilter) {
        attach(aNextEventFilter);
    }

    /**
     * When no {@link de.novanic.eventservice.client.event.filter.EventFilter} is attached to the DefaultEventFilter,
     * no events will be filtered, because that method implementation returns false. An optional
     * {@link de.novanic.eventservice.client.event.filter.EventFilter} instance can be attached using
     * {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)} or the constructor
     * {@link de.novanic.eventservice.client.event.filter.DefaultEventFilter#DefaultEventFilter(EventFilter)}.
     * @param anEvent event
     * @return false or the result of the optional, attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    public boolean match(Event anEvent) {
        return myNextEventFilter != null && myNextEventFilter.match(anEvent);
    }

    /**
     * Sets/Attaches another {@link de.novanic.eventservice.client.event.filter.EventFilter} which is used/called from the match method
     * ({@link de.novanic.eventservice.client.event.filter.EventFilter#match(de.novanic.eventservice.client.event.Event)}).
     * A CascadingEventFilter can only hold one {@link de.novanic.eventservice.client.event.filter.EventFilter}.
     * @param anEventFilter {@link de.novanic.eventservice.client.event.filter.EventFilter} to attach/set
     * @return itself/DefaultEventFilter (result of the attachment)
     */
    public AppendableEventFilter attach(EventFilter anEventFilter) {
        myNextEventFilter = anEventFilter;
        return this;
    }

    /**
     * Detaches the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}. A CascadingEventFilter can
     * only hold one {@link de.novanic.eventservice.client.event.filter.EventFilter}.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return true if an EventFilter was registered, otherwise false
     */
    public boolean detach() {
        if(myNextEventFilter != null) {
            myNextEventFilter = null;
            return true;
        }
        return false;
    }

    /**
     * Returns the attached EventFilter. A CascadingEventFilter can only hold one {@link de.novanic.eventservice.client.event.filter.EventFilter}.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    public EventFilter getAttachedEventFilter() {
        return myNextEventFilter;
    }
}