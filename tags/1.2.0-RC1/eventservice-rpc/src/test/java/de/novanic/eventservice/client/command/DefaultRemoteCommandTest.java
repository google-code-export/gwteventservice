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
package de.novanic.eventservice.client.command;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.service.EventServiceAsync;
import de.novanic.eventservice.client.event.DomainEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author sstrohschein
 * Date: 03.08.2008
 * Time: 20:25:02
 */
public class DefaultRemoteCommandTest extends TestCase
{
    public void testInit() {
        final RemoteCommand<List<DomainEvent>> theRemoteCommand = new DefaultRemoteCommand<List<DomainEvent>>() {
            public void execute(EventServiceAsync anEventServiceAsync) {}
        };

        final AsyncCallback<List<DomainEvent>> theAsyncCallback = new AsyncCallback<List<DomainEvent>>() {
            public void onFailure(Throwable aThrowable) {}

            public void onSuccess(List<DomainEvent> aResult) {}
        };

        assertFalse(theRemoteCommand.isInitialized());
        theRemoteCommand.init(theAsyncCallback);
        assertTrue(theRemoteCommand.isInitialized());

        assertSame(theAsyncCallback, theRemoteCommand.getCallback());
    }
}