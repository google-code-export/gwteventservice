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
package de.novanic.eventservice.client.event.listener.unlisten;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;

/**
 * An UnlistenEvent will be triggered when a timeout or a domain specific unlisten/deregistration occurs. The UnlistenEvent is created by
 * {@link de.novanic.eventservice.client.event.service.EventService} when unlisten is called for a user. It will also be returned as an
 * event (from the listen method) and will be added to the UnlistenDomain {@link de.novanic.eventservice.client.event.domain.DomainFactory#UNLISTEN_DOMAIN}.
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten()
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(Domain)
 * @see de.novanic.eventservice.client.event.service.EventService#unlisten(java.util.Set)
 *
 * @author sstrohschein
 *         <br>Date: 16.08.2009
 *         <br>Time: 01:01:09
 */
public interface UnlistenEvent extends Event
{
    /**
     * A {@link de.novanic.eventservice.client.event.domain.Domain} can be set to the UnlistenEvent when the unlisten event
     * is domain specific.
     * @param aDomain domain for unlistening
     */
    void setDomain(Domain aDomain);

    /**
     * Returns the domain for which isn't listening anymore. If the UnlistenEvent is global (for example a timeout),
     * this method returns NULL.
     * @return domain for unlistening
     */
    Domain getDomain();

    /**
     * Sets the unlistened user id for the UnlistenEvent.
     * @param aUserId unlistened user id
     */
    void setUserId(String aUserId);

    /**
     * Returns the unlistened user id for the UnlistenEvent.
     * @return unlistened user id
     */
    String getUserId();

    /**
     * Returns true when the UnlistenEvent is a timeout, otherwise false (for example a domain specific UnlistenEvent).
     * @return true when timeout, otherwise false (for example a domain specific UnlistenEvent)
     */
    boolean isTimeout();

    /**
     * Sets the timeout flag. It should be set true when the UnlistenEvent marks a timeout, otherwise false (for example a domain specific UnlistenEvent).
     * @param aTimeout true when the UnlistenEvent marks a timeout, otherwise false (for example a domain specific UnlistenEvent)
     */
    void setTimeout(boolean aTimeout);

    /**
     * Returns true when the UnlistenEvent is triggered from the client side. That can for example occur on connection errors.
     * @return true when triggered from client side, otherwise false
     */
    boolean isLocal();

    /**
     * Sets the local flag. It should be set true when the UnlistenEvent is triggered from the client side.  That can for example
     * occur on connection errors.
     * @param isLocal true when triggered from client side, otherwise false
     */
    void setLocal(boolean isLocal);
}