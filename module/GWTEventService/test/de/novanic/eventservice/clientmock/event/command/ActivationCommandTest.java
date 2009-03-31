package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.TestTypeEventFilter;
import de.novanic.eventservice.client.event.EventNotification;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.command.ActivationCommand;
import de.novanic.eventservice.client.event.filter.EventFilter;

import java.util.List;

/**
 * @author sstrohschein
 *         <br>Date: 30.03.2009
 *         <br>Time: 23:24:18
 */
public class ActivationCommandTest extends ClientCommandTestCase
{
    public void testExecute() {
        final Domain theTestDomain = DomainFactory.getDomain("test_domain");
        final EventFilter theEventFilter = new TestTypeEventFilter();
        final TestEventNotification theTestEventNotification = new TestEventNotification();

        getRemoteEventConnectorMock().activate(theTestDomain, theEventFilter, theTestEventNotification, getCommandCallback());
        getRemoteEventConnectorMockControl().setVoidCallable();

        testExecute(new ActivationCommand(getRemoteEventConnectorMock(), theTestDomain, theEventFilter, theTestEventNotification, getCommandCallback()));
    }

//    public void testExecute() {
//        final Domain theTestDomain = DomainFactory.getDomain("test_domain");
//        final TestAsyncCallback theAsyncCallback = new TestAsyncCallback();
//        final EventFilter theEventFilter = new TestTypeEventFilter();
//        final TestEventNotification theTestEventNotification = new TestEventNotification();
//
//        MockControl theRemoteEventConnectorMockControl = MockControl.createControl(RemoteEventConnector.class);
//        RemoteEventConnector theRemoteEventConnectorMock = (RemoteEventConnector)theRemoteEventConnectorMockControl.getMock();
//
//        theRemoteEventConnectorMock.activate(theTestDomain, theEventFilter, theTestEventNotification, theAsyncCallback);
//        theRemoteEventConnectorMockControl.setVoidCallable();
//
//        theRemoteEventConnectorMockControl.replay();
//
//        ActivationCommand theActivationCommand = new ActivationCommand(theRemoteEventConnectorMock, theTestDomain,
//                theEventFilter, theTestEventNotification, theAsyncCallback);
//
//        Test init
//        assertNotNull(theActivationCommand.getRemoteEventConnector());
//        assertEquals(theRemoteEventConnectorMock, theActivationCommand.getRemoteEventConnector());
//        assertNotNull(theActivationCommand.getCommandCallback());
//        assertEquals(theAsyncCallback, theActivationCommand.getCommandCallback());
//
//        Test execute
//        theActivationCommand.execute();
//
//        theRemoteEventConnectorMockControl.verify();
//        theRemoteEventConnectorMockControl.reset();
//    }

    private static class TestEventNotification implements EventNotification
    {
        public void onNotify(List<DomainEvent> anEvents) {}

        public void onAbort() {}
    }
}