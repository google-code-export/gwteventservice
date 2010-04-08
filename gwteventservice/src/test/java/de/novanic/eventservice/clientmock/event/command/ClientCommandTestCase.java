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
package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.connector.RemoteEventConnector;
import junit.framework.TestCase;
import de.novanic.eventservice.client.event.command.ClientCommand;
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
            assertEquals(myTestAsyncCallback, getCommandCallback(EventServiceConfigurationTransferable.class));
        }
    }

    public RemoteEventConnector getRemoteEventConnectorMock() {
        return myRemoteEventConnectorMock;
    }

    public MockControl getRemoteEventConnectorMockControl() {
        return myRemoteEventConnectorMockControl;
    }

    public TestAsyncCallback<Void> getCommandCallback() {
        if(myTestAsyncCallback == null) {
            myTestAsyncCallback = new TestAsyncCallback<Void>();
        }
        return myTestAsyncCallback;
    }

    public <C> TestAsyncCallback<C> getCommandCallback(Class<C> aReturnType) {
        if(myTestAsyncCallback == null) {
            myTestAsyncCallback = new TestAsyncCallback<C>();
        }
        return myTestAsyncCallback;
    }

    private static class TestAsyncCallback<C> implements AsyncCallback<C>
    {
        public void onFailure(Throwable aThrowable) {}

        public void onSuccess(C aResult) {}
    }
}