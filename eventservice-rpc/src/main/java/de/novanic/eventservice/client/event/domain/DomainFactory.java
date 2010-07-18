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
package de.novanic.eventservice.client.event.domain;

/**
 * The DomainFactory creates new Domains ({@link de.novanic.eventservice.client.event.domain.Domain}) with a name. The
 * name should be unique to avoid 'collision' of events.
 * @see de.novanic.eventservice.client.event.domain.Domain
 *
 * @author sstrohschein
 * <br>Date: 15.08.2008
 * <br>Time: 22:45:28
 */
public final class DomainFactory
{
    /**
     * All unlisten events ({@link de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent}) will be registered to
     * the UNLISTEN_DOMAIN.
     */
    public static final Domain UNLISTEN_DOMAIN = getDomain("service_unlisten_domain");

    /**
     * The {@link de.novanic.eventservice.client.event.domain.DomainFactory#USER_SPECIFIC_DOMAIN} is a special domain for
     * user-specific events. User-specific events will be registered to that domain. This domain holds only events for
     * the specified client / user and doesn't provide events for all users like all other domains.
     * Technically the user-specific domain is implemented as a / the NULL-domain.
     */
    public static final Domain USER_SPECIFIC_DOMAIN = null;

    private DomainFactory() {}

    /**
     * Creates a new {@link de.novanic.eventservice.client.event.domain.Domain} with a name. The name should be unique
     * to avoid 'collision' of events.
     * @param aDomainName unique domain name
     * @return {@link de.novanic.eventservice.client.event.domain.Domain}
     */
    public static Domain getDomain(String aDomainName) {
        return new DefaultDomain(aDomainName);
    }
}