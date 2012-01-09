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
package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.filter.EventFilter;

/**
 * @author sstrohschein
 *         <br>Date: 03.03.2009
 *         <br>Time: 18:40:12
 */
public class TypeEventFilterTestMode implements EventFilter
{
    private String myIgnoredEventClass;

    public TypeEventFilterTestMode() {}

    public TypeEventFilterTestMode(Class anIgnoredEventClass) {
        myIgnoredEventClass = anIgnoredEventClass.getName();
    }

    public boolean match(Event anEvent) {
        return anEvent.getClass().getName().equals(myIgnoredEventClass);
    }
}