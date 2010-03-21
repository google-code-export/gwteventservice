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
 * The EventFilterFactory can be used to connect various {@link de.novanic.eventservice.client.event.filter.EventFilter}
 * instances by creating a {@link de.novanic.eventservice.client.event.filter.CompositeEventFilter}.
 * A other solution to build filter sequences is available with {@link CascadingEventFilter} (cascading filter sequences).
 *
 * @author sstrohschein
 *         <br>Date: 04.03.2009
 *         <br>Time: 19:21:21
 */
public class EventFilterFactory
{
    /**
     * The EventFilterFactory should be created via the getInstance method.
     * @see EventFilterFactory#getInstance()
     */
    private EventFilterFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class EventFilterFactoryHolder {
        private static final EventFilterFactory INSTANCE = new EventFilterFactory();
    }

    /**
     * This method should be used to create an instance of EventFilterFactory.
     * EventFilterFactory is a singleton, so this method returns always the same instance of
     * EventFilterFactory.
     * @return EventFilterFactory (singleton)
     */
    public static EventFilterFactory getInstance() {
        return EventFilterFactoryHolder.INSTANCE;
    }

    /**
     * That method can connect various {@link de.novanic.eventservice.client.event.filter.EventFilter} instances by
     * building a {@link de.novanic.eventservice.client.event.filter.CompositeEventFilter}.
     * @param anEventFilters {@link de.novanic.eventservice.client.event.filter.EventFilter} instances to build a
     * {@link de.novanic.eventservice.client.event.filter.CompositeEventFilter}.
     * @return {@link de.novanic.eventservice.client.event.filter.CompositeEventFilter} which contains the connectable
     * {@link de.novanic.eventservice.client.event.filter.EventFilter} instances.
     */
    public CompositeEventFilter connect(EventFilter... anEventFilters) {
        return new DefaultCompositeEventFilter(anEventFilters);
    }
}