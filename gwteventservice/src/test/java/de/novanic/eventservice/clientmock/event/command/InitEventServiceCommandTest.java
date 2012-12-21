/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschr�nkt)
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
package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.event.command.InitEventServiceCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author sstrohschein
 *         <br>Date: 04.04.2009
 *         <br>Time: 20:15:58
 */
@RunWith(JUnit4.class)
public class InitEventServiceCommandTest extends ClientCommandTestCase
{
    @Test
    public void testExecute() {
        getRemoteEventConnectorMock().init(getCommandCallback(EventServiceConfigurationTransferable.class));

        testExecute(new InitEventServiceCommand(getRemoteEventConnectorMock(), getCommandCallback(EventServiceConfigurationTransferable.class)));
    }
}