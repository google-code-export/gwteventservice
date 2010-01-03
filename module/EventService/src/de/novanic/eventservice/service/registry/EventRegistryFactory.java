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
package de.novanic.eventservice.service.registry;

import de.novanic.eventservice.config.EventServiceConfigurationFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * The EventRegistryFactory is used to create the EventRegistry and to ensure that only one instance of
 * EventRegistryFactory and EventRegistry exists (singleton).
 * @see EventRegistry
 *
 * @author sstrohschein
 * <br>Date: 09.06.2008
 * <br>Time: 22:46:51
 */
public class EventRegistryFactory
{
    private static EventRegistryFactory myInstance;
    private EventRegistry myEventRegistry;

    /**
     * The EventRegistryFactory should be created via the getInstance method.
     * @see EventRegistryFactory#getInstance()
     */
    private EventRegistryFactory() {}

    /**
     * This method should be used to create an instance of EventRegistryFactory.
     * EventRegistryFactory is a singleton, so this method returns always the same instance of EventRegistryFactory.
     * @return EventRegistryFactory (singleton)
     */
    public static synchronized EventRegistryFactory getInstance() {
        if(myInstance == null) {
            myInstance = new EventRegistryFactory();
        }
        return myInstance;
    }

    /**
     * This method should be used to create an instance of EventRegistry.
     * EventRegistry is a singleton, so this method returns always the same instance of EventRegistry.
     * @return EventRegistry (singleton)
     */
    public synchronized EventRegistry getEventRegistry() {
        if(myEventRegistry == null) {
            EventServiceConfiguration theConfiguration = getEventServiceConfiguration();
            myEventRegistry = new DefaultEventRegistry(theConfiguration);
        }
        return myEventRegistry;
    }

    /**
     * This method should only be used in TestCases, because it resets the factory and the factory can't ensure
     * anymore that only one instance exists!
     */
    public static synchronized void reset() {
        myInstance = null;
    }

    private EventServiceConfiguration getEventServiceConfiguration() {
        EventServiceConfigurationFactory theConfigurationFactory = EventServiceConfigurationFactory.getInstance();
        return theConfigurationFactory.loadEventServiceConfiguration();
    }
}