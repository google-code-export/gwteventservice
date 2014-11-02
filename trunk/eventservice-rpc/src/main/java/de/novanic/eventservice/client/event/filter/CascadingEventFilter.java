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
package de.novanic.eventservice.client.event.filter;

/**
 * A CascadingEventFilter is based on cascading filter sequences. An CascadingEventFilter can only hold one next
 * EventFilter. When the next EventFilter is also a CascadingEventFilter, it can handle another EventFilter...
 * That technique can be used to build filter sequences (cascading).
 * A other solution to build filter sequences is available with {@link de.novanic.eventservice.client.event.filter.CompositeEventFilter}
 * or rather {@link de.novanic.eventservice.client.event.filter.EventFilterFactory#connect(EventFilter...)}.
 *
 * @author sstrohschein
 *         <br>Date: 05.03.2009
 *         <br>Time: 12:04:03
 */
public interface CascadingEventFilter extends AppendableEventFilter
{
    /**
     * Returns the attached EventFilter. A CascadingEventFilter can only hold one {@link de.novanic.eventservice.client.event.filter.EventFilter}.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}
     */
    EventFilter getAttachedEventFilter();

    /**
     * Detaches the attached {@link de.novanic.eventservice.client.event.filter.EventFilter}. A CascadingEventFilter can
     * only hold one {@link de.novanic.eventservice.client.event.filter.EventFilter}.
     * EventFilter instances can be attached with {@link de.novanic.eventservice.client.event.filter.AppendableEventFilter#attach(EventFilter)}.
     * @return true if an EventFilter was registered, otherwise false
     */
    boolean detach();
}