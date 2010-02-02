/*
 * GWTEventService
 * Copyright (c) 2008, GWTEventService Committers
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
package de.novanic.eventservice.client.command;

import junit.framework.TestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;

import org.easymock.MockControl;

import java.util.List;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 20:31:09
 */
public class RemoteListenCommandTest extends TestCase
{
    public void testExecute() {
        final AsyncCallback<List<DomainEvent>> theAsyncCallback = new AsyncCallback<List<DomainEvent>>() {
            public void onFailure(Throwable aThrowable) {}

            public void onSuccess(List<DomainEvent> aResult) {}
        };

        RemoteListenCommand theRemoteListenCommand = new RemoteListenCommand();
        theRemoteListenCommand.init(theAsyncCallback);

        MockControl theEventServiceAsyncMockControl = MockControl.createControl(EventServiceAsync.class);
        EventServiceAsync theEventServiceAsyncMock = (EventServiceAsync)theEventServiceAsyncMockControl.getMock();

        theEventServiceAsyncMock.listen(theAsyncCallback);
        theEventServiceAsyncMockControl.setVoidCallable();

        theEventServiceAsyncMockControl.replay();
            theRemoteListenCommand.execute(theEventServiceAsyncMock);
        theEventServiceAsyncMockControl.verify();
        theEventServiceAsyncMockControl.reset();
    }
}