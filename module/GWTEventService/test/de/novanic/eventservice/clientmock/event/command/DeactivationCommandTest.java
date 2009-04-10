package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.event.command.DeactivationCommand;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

import java.util.Set;
import java.util.HashSet;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 19:58:30
 */
public class DeactivationCommandTest extends ClientCommandTestCase
{
    public void testExecute() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");

        getRemoteEventConnectorMock().deactivate(theTestDomain, getCommandCallback());
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new DeactivationCommand(getRemoteEventConnectorMock(), theTestDomain, getCommandCallback()));
    }

    public void testExecute_2() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");
        final Set<Domain> theDomains = new HashSet<Domain>(1);
        theDomains.add(theTestDomain);

        getRemoteEventConnectorMock().deactivate(theDomains, getCommandCallback());
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new DeactivationCommand(getRemoteEventConnectorMock(), theDomains, getCommandCallback()));
    }

    public void testExecute_3() {
        getRemoteEventConnectorMock().deactivate();
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new DeactivationCommand(getRemoteEventConnectorMock()));
    }
}