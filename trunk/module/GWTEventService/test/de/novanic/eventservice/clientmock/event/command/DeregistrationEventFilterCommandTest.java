package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.command.DeregistrationEventFilterCommand;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 20:46:42
 */
public class DeregistrationEventFilterCommandTest extends ClientCommandTestCase
{
    public void testExecute() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");

        getRemoteEventConnectorMock().deregisterEventFilter(theTestDomain, getCommandCallback());
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new DeregistrationEventFilterCommand(getRemoteEventConnectorMock(), theTestDomain, getCommandCallback()));
    }
}