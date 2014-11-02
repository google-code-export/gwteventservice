/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A RemoteCommand can be used to execute a call from client to the server side. The possibility to queue RemoteCommands
 * is one of the advantages.
 *
 * @author sstrohschein
 * <br>Date: 06.06.2008
 * <br>Time: 22:40:48
 */
public abstract class DefaultRemoteCommand<T> implements RemoteCommand<T>
{
    private AsyncCallback<T> myCallback;

    /**
     * Initializes the RemoteCommand with a callback, which can also be used in the execute method
     * {@link de.novanic.eventservice.client.command.RemoteCommand#execute(de.novanic.eventservice.client.event.service.EventServiceAsync)}
     * @param aCallback callback
     */
    public void init(AsyncCallback<T> aCallback) {
        myCallback = aCallback;
    }

    /**
     * Checks if the RemoteCommand is initialized with a callback.
     * @return true when the init method ({@link de.novanic.eventservice.client.command.RemoteCommand#init(com.google.gwt.user.client.rpc.AsyncCallback)})
     * was called with a callback, false when the RemoteCommand wasn't initialized with a callback.
     */
    public boolean isInitialized() {
        return myCallback != null;
    }

    /**
     * Returns the callback, which can be set with the link method
     * ({@link de.novanic.eventservice.client.command.RemoteCommand#init(com.google.gwt.user.client.rpc.AsyncCallback)})
     * @return callback
     */
    public AsyncCallback<T> getCallback() {
        return myCallback;
    }
}