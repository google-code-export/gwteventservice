package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.TestTypeEventFilter;
import de.novanic.eventservice.client.event.command.RegistrationEventFilterCommand;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 22:05:59
 */
public class RegistrationEventFilterCommandTest extends ClientCommandTestCase
{
    public void testExecute() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");
        final TestTypeEventFilter theTestEventFilter = new TestTypeEventFilter();

        getRemoteEventConnectorMock().registerEventFilter(theTestDomain, theTestEventFilter, getCommandCallback());
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new RegistrationEventFilterCommand(getRemoteEventConnectorMock(), theTestDomain, theTestEventFilter, getCommandCallback()));
    }
}