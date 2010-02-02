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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.domain.Domain;

import java.io.Serializable;

/**
 * A {@link de.novanic.eventservice.client.event.DomainEvent} is a container and contains an event and the domain where the event has occurred.
 *
 * @author sstrohschein
 *         <br>Date: 05.10.2008
 *         <br>Time: 15:17:40
 */
public interface DomainEvent extends Serializable
{
    /**
     * An event/DomainEvent is user specific when it is only for one user and not for the complete domain.
     * This flag depends on the constructor. If the DomainEvent is created with a domain, the DomainEvent isn't user
     * specific.
     * @return true when the event is user specific, otherwise false
     */
    boolean isUserSpecific();

    /**
     * Returns the event.
     * @return event
     */
    Event getEvent();

    /**
     * Returns the domain where the event has occured.
     * @return domain
     */
    Domain getDomain();
}