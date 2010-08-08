/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.client.connection.callback;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Simple wrapper class to wrap another callback. That makes it possible to execute custom actions before or after
 * the wrapped callback is called.
 *
 * @author sstrohschein
 *         <br>Date: 02.08.2010
 *         <br>Time: 21:23:09
 */
public class AsyncCallbackWrapper<T> implements AsyncCallback<T>
{
    private final AsyncCallback<T> myAsyncCallback;

    public AsyncCallbackWrapper(AsyncCallback<T> anAsyncCallback) {
        myAsyncCallback = anAsyncCallback;
    }

    public void onSuccess(T anObject) {
        if(myAsyncCallback != null) {
            myAsyncCallback.onSuccess(anObject);
        }
    }

    public void onFailure(Throwable aThrowable) {
        if(myAsyncCallback != null) {
            myAsyncCallback.onFailure(aThrowable);
        }
    }
}