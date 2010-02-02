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
package de.novanic.eventservice.clientmock.event.command.schedule;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler;
import de.novanic.eventservice.client.event.command.ClientCommand;

/**
 * @author sstrohschein
 *         <br>Date: 04.04.2009
 *         <br>Time: 23:01:17
 */
public class ClientCommandSchedulerFactoryTest extends TestCase
{
    public void testGetClientCommandScheduler() {
        ClientCommandSchedulerFactory theClientCommandSchedulerFactory = ClientCommandSchedulerFactory.getInstance();
        ClientCommandScheduler theClientCommandScheduler = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler);

        ClientCommandScheduler theClientCommandScheduler_2 = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler_2);
        assertSame(theClientCommandScheduler, theClientCommandScheduler_2);
    }

    public void testGetClientCommandScheduler_2() {
        ClientCommandSchedulerFactory theClientCommandSchedulerFactory = ClientCommandSchedulerFactory.getInstance();
        ClientCommandScheduler theClientCommandScheduler = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler);

        theClientCommandSchedulerFactory.setClientCommandSchedulerInstance(new ClientCommandScheduler() {
            public void schedule(ClientCommand aCommand) {}

            public void schedule(ClientCommand aCommand, int aDelay) {}
        });

        ClientCommandScheduler theClientCommandScheduler_2 = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler_2);
        assertNotSame(theClientCommandScheduler, theClientCommandScheduler_2);

        ClientCommandScheduler theClientCommandScheduler_3 = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler_3);
        assertSame(theClientCommandScheduler_2, theClientCommandScheduler_3);
    }

    public void testReset() {
        ClientCommandSchedulerFactory theClientCommandSchedulerFactory = ClientCommandSchedulerFactory.getInstance();
        ClientCommandScheduler theClientCommandScheduler = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler);

        theClientCommandSchedulerFactory.reset();

        ClientCommandScheduler theClientCommandScheduler_2 = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler_2);
        assertNotSame(theClientCommandScheduler, theClientCommandScheduler_2);

        ClientCommandScheduler theClientCommandScheduler_3 = theClientCommandSchedulerFactory.getClientCommandScheduler();
        assertNotNull(theClientCommandScheduler_3);
        assertSame(theClientCommandScheduler_2, theClientCommandScheduler_3);
    }
}