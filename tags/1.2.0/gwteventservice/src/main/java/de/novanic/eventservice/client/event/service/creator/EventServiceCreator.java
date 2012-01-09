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
package de.novanic.eventservice.client.event.service.creator;

import de.novanic.eventservice.client.event.service.EventServiceAsync;

/**
 * The {@link de.novanic.eventservice.client.event.service.creator.EventServiceCreator} is a factory to
 * create a new {@link de.novanic.eventservice.client.event.service.EventServiceAsync} which is required
 * for the connection to the server side.
 *
 * @author sstrohschein
 *         <br>Date: 19.10.2010
 *         <br>Time: 22:46:59
 */
public interface EventServiceCreator
{
    /**
     * Creates a new {@link de.novanic.eventservice.client.event.service.EventServiceAsync} which is required
     * for the connection to the server side.
     * @return {@link de.novanic.eventservice.client.event.service.EventServiceAsync} interface for the server side service
     */
    EventServiceAsync createEventService();
}