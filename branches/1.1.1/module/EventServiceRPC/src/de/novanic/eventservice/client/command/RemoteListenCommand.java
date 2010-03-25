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
package de.novanic.eventservice.client.command;

import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;

import java.util.List;

/**
 * The RemoteListenCommand calls the listen method of {@link de.novanic.eventservice.client.event.service.EventService} ({@link de.novanic.eventservice.client.event.service.EventService#listen()}).
 * This command is used by the EventService itself, to loop the listen cycle. The EventService calls the listen method
 * everytime again on callback of the listen method, as long as the user is registered for a domain.
 *
 * @author sstrohschein
 * <br>Date: 06.06.2008
 * <br>Time: 22:39:27
 */
public final class RemoteListenCommand extends DefaultRemoteCommand<List<DomainEvent>>
{
    /**
     * When the RemoteListenCommand is executed, the listen method of the EventService is called.
     * @param anEventService EventService ({@link de.novanic.eventservice.client.event.service.EventService})
     * @see de.novanic.eventservice.client.event.service.EventService#listen()
     */
    public void execute(EventServiceAsync anEventService) {
        anEventService.listen(getCallback());
    }
}