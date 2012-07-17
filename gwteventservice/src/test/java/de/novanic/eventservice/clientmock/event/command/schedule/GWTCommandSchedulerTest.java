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
package de.novanic.eventservice.clientmock.event.command.schedule;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.command.ClientCommand;
import de.novanic.eventservice.client.event.command.schedule.GWTCommandScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 25.10.2010
 *         <br>Time: 00:04:48
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.google.gwt.user.client.Timer")
@PrepareForTest({GWTCommandScheduler.class, GWTCommandScheduler.GWTCommandTimer.class})
public class GWTCommandSchedulerTest
{
    @Test
    public void testSchedule() throws Exception {
        GWTCommandScheduler theGWTCommandScheduler = new GWTCommandScheduler();

        final ClientCommandDummy theClientCommand = new ClientCommandDummy();

        final GWTCommandScheduler.GWTCommandTimer theTimerDummy = new GWTCommandTimerDummy(theClientCommand);
        PowerMock.expectNew(GWTCommandScheduler.GWTCommandTimer.class, theClientCommand).andReturn(theTimerDummy);

        assertFalse(theClientCommand.isExecuted);

        PowerMock.replay(GWTCommandScheduler.GWTCommandTimer.class);

            theGWTCommandScheduler.schedule(theClientCommand);

        PowerMock.verify(GWTCommandScheduler.GWTCommandTimer.class);
        PowerMock.reset(GWTCommandScheduler.GWTCommandTimer.class);

        assertTrue(theClientCommand.isExecuted);
    }

    private class ClientCommandDummy implements ClientCommand<Void>
    {
        private boolean isExecuted;

        public void execute() {
            isExecuted = true;
        }

        public AsyncCallback<Void> getCommandCallback() {
            return null;
        }
    }

    private class GWTCommandTimerDummy extends GWTCommandScheduler.GWTCommandTimer
    {
        public GWTCommandTimerDummy(ClientCommand<?> aClientCommand) {
            super(aClientCommand);
        }

        public void schedule(int delayMillis) {
            run();
        }
    }
}