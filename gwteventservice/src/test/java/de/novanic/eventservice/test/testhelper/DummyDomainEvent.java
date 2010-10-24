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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.*;
import de.novanic.eventservice.client.event.DummyEvent;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * @author sstrohschein
 * Date: 05.08.2008
 * Time: 17:50:16
 */
public class DummyDomainEvent extends DefaultDomainEvent
{
    /**
     * User-specific event
     */
    public DummyDomainEvent() {
        super(new de.novanic.eventservice.client.event.DummyEvent());
    }

    public DummyDomainEvent(Domain aDomain) {
        super(new DummyEvent(), aDomain);
    }

    public DummyDomainEvent(Event aEvent, Domain aDomain) {
        super(aEvent, aDomain);
    }

    /**
     * User-specific event
     * @param aEvent a user-specific event
     */
    public DummyDomainEvent(Event aEvent) {
        super(aEvent);
    }
}