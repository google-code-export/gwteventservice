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
package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.event.command.DeactivationCommand;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;
import java.util.HashSet;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 19:58:30
 */
@RunWith(JUnit4.class)
public class DeactivationCommandTest extends ClientCommandTestCase
{
    @Test
    public void testExecute() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");

        getRemoteEventConnectorMock().deactivate(theTestDomain, getCommandCallback());

        testExecute(new DeactivationCommand(getRemoteEventConnectorMock(), theTestDomain, getCommandCallback()));
    }

    @Test
    public void testExecute_2() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");
        final Set<Domain> theDomains = new HashSet<Domain>(1);
        theDomains.add(theTestDomain);

        getRemoteEventConnectorMock().deactivate(theDomains, getCommandCallback());

        testExecute(new DeactivationCommand(getRemoteEventConnectorMock(), theDomains, getCommandCallback()));
    }
}