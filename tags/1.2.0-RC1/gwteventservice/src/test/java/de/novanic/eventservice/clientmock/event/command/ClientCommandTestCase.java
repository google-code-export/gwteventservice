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
package de.novanic.eventservice.clientmock.event.command;

import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import junit.framework.TestCase;
import de.novanic.eventservice.client.event.command.ClientCommand;
import org.easymock.EasyMock;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 31.03.2009
 *         <br>Time: 20:50:05
 */
public abstract class ClientCommandTestCase extends TestCase
{
    private RemoteEventConnector myRemoteEventConnectorMock;
    private TestAsyncCallback myTestAsyncCallback;

    public void setUp() {
        myRemoteEventConnectorMock = EasyMock.createMock(RemoteEventConnector.class);
        myTestAsyncCallback = null;
    }

    public void tearDown() {
        EasyMock.reset(myRemoteEventConnectorMock);
    }

    public void testExecute(ClientCommand aClientCommand) {
        checkInit(aClientCommand);

        EasyMock.replay(myRemoteEventConnectorMock);

        aClientCommand.execute();

        EasyMock.verify(myRemoteEventConnectorMock);
        EasyMock.reset(myRemoteEventConnectorMock);
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