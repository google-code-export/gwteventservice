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
import com.google.gwt.user.client.Timer;

/**
 * The ClientCommandScheduler can schedule the execution of {@link de.novanic.eventservice.client.event.command.ClientCommand}
 * instances for a specified time/delay. GWTCommandScheduler is a implementation of {@link de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler}
 * for GWT.
 *
 * @author sstrohschein
 *         <br>Date: 04.04.2009
 *         <br>Time: 22:25:02
 */
public class GWTCommandScheduler implements ClientCommandScheduler
{
    /**
     * Creates a new thread for the execution of the {@link de.novanic.eventservice.client.event.command.ClientCommand}.
     * @param aCommand {@link de.novanic.eventservice.client.event.command.ClientCommand} to schedule
     */
    public void schedule(ClientCommand<?> aCommand) {
        schedule(aCommand, 1);
    }

    /**
     * Creates a new thread for the execution of the {@link de.novanic.eventservice.client.event.command.ClientCommand}.
     * The execution is started after the specified delay.
     * @param aCommand {@link de.novanic.eventservice.client.event.command.ClientCommand} to schedule
     * @param aDelay delay in milliseconds
     */
    public void schedule(final ClientCommand<?> aCommand, int aDelay) {
        Timer theTimer = new GWTCommandTimer(aCommand);
        theTimer.schedule(aDelay);
    }

    public static class GWTCommandTimer extends Timer
    {
        private ClientCommand<?> myClientCommand;

        public GWTCommandTimer(ClientCommand<?> aClientCommand) {
            myClientCommand = aClientCommand;
        }

        public void run() {
            myClientCommand.execute();
        }
    }
}