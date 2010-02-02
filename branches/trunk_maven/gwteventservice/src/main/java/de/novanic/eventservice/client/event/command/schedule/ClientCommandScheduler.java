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
package de.novanic.eventservice.client.event.command.schedule;

import de.novanic.eventservice.client.event.command.ClientCommand;

/**
 * The ClientCommandScheduler can schedule the execution of {@link de.novanic.eventservice.client.event.command.ClientCommand}
 * instances for a specified time/delay.
 *
 * @author sstrohschein
 *         <br>Date: 04.04.2009
 *         <br>Time: 22:24:47
 */
public interface ClientCommandScheduler
{
    /**
     * Creates a new thread for the execution of the {@link de.novanic.eventservice.client.event.command.ClientCommand}.
     * @param aCommand {@link de.novanic.eventservice.client.event.command.ClientCommand} to schedule
     */
    void schedule(ClientCommand aCommand);

    /**
     * Creates a new thread for the execution of the {@link de.novanic.eventservice.client.event.command.ClientCommand}.
     * The execution is started after the specified delay.
     * @param aCommand {@link de.novanic.eventservice.client.event.command.ClientCommand} to schedule
     * @param aDelay delay in milliseconds
     */
    void schedule(ClientCommand aCommand, int aDelay);
}