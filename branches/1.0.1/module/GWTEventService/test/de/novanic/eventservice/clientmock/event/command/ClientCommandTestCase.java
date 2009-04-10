package de.novanic.eventservice.clientmock.event.command;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.command.ClientCommand;
import de.novanic.eventservice.client.event.RemoteEventConnector;
import org.easymock.MockControl;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 20:50:05
 */
public abstract class ClientCommandTestCase extends TestCase
{
    private RemoteEventConnector myRemoteEventConnectorMock;
    private MockControl myRemoteEventConnectorMockControl;
    private TestAsyncCallback myTestAsyncCallback;

    public void setUp() {
        myRemoteEventConnectorMockControl = MockControl.createControl(RemoteEventConnector.class);
        myRemoteEventConnectorMock = (RemoteEventConnector)myRemoteEventConnectorMockControl.getMock();
        myTestAsyncCallback = null;
    }

    public void tearDown() {
        myRemoteEventConnectorMockControl.reset();
    }

    public void testExecute(ClientCommand aClientCommand) {
        checkInit(aClientCommand);

        myRemoteEventConnectorMockControl.replay();

        aClientCommand.execute();

        myRemoteEventConnectorMockControl.verify();
        myRemoteEventConnectorMockControl.reset();
    }

    private void checkInit(ClientCommand aClientCommand) {
        if(myTestAsyncCallback != null) {
            assertNotNull(aClientCommand.getCommandCallback());
            assertEquals(myTestAsyncCallback, getCommandCallback());
        }
    }

    public RemoteEventConnector getRemoteEventConnectorMock() {
        return myRemoteEventConnectorMock;
    }

    public MockControl getRemoteEventConnectorMockControl() {
        return myRemoteEventConnectorMockControl;
    }

    public TestAsyncCallback getCommandCallback() {
        if(myTestAsyncCallback == null) {
            myTestAsyncCallback = new TestAsyncCallback();
        }
        return myTestAsyncCallback;
    }

    private static class TestAsyncCallback implements AsyncCallback<Void>
    {
        public void onFailure(Throwable aThrowable) {}

        public void onSuccess(Void aResult) {}
    }
}