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
package de.novanic.eventservice.service.registry.domain;

import de.novanic.eventservice.client.event.domain.Domain;

import java.util.Set;

/**
 * The ListenDomainAccessor provides methods to get all active domains where users are registered to
 * listen for events. That information can be get for all users or for a specific user.
 *
 * @author sstrohschein
 *         <br>Date: 29.10.2009
 *         <br>Time: 22:00:24
 */
public interface ListenDomainAccessor
{
    /**
     * Returns all domains where the user is registered to.
     * @param aUserId user
     * @return domains where the user is registered to
     */
    Set<Domain> getListenDomains(String aUserId);

    /**
     * Returns all registered/activated domains.
     * @return all registered/activated domains
     */
    Set<Domain> getListenDomains();
}