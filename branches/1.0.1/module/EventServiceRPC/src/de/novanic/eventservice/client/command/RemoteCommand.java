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

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.service.EventServiceAsync;

/**
 * A RemoteCommand can be used to execute a call from client to the server side. The possibility to queue RemoteCommands
 * is one of the advantages.
 *
 * @author sstrohschein
 * <br>Date: 06.06.2008
 * <br>Time: 22:38:48
 */
public interface RemoteCommand<T>
{
    /**
     * Initializes the RemoteCommand with a callback, which can also be used in the execute method
     * {@link de.novanic.eventservice.client.command.RemoteCommand#execute(de.novanic.eventservice.client.event.service.EventServiceAsync)}
     * @param aCallback callback
     */
    void init(AsyncCallback<T> aCallback);

    /**
     * Checks if the RemoteCommand is initialized with a callback.
     * @return true when the init method ({@link de.novanic.eventservice.client.command.RemoteCommand#init(com.google.gwt.user.client.rpc.AsyncCallback)})
     * was called with a callback, false when the RemoteCommand wasn't initialized with a callback.
     */
    boolean isInitialized();

    /**
     * This method executes the RemoteCommand. This method has access to {@link de.novanic.eventservice.client.event.service.EventService}
     * and to the initialized callback ({@link de.novanic.eventservice.client.command.RemoteCommand#getCallback()}).
     * @param anEventService {@link de.novanic.eventservice.client.event.service.EventService}
     */
    void execute(EventServiceAsync anEventService);

    /**
     * Returns the callback, which can be set with the link method
     * ({@link de.novanic.eventservice.client.command.RemoteCommand#init(com.google.gwt.user.client.rpc.AsyncCallback)})
     * @return callback
     */
    AsyncCallback<T> getCallback();
}