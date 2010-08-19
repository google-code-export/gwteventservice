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
package de.novanic.eventservice.clientmock;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.easymock.IArgumentMatcher;

/**
 * @author sstrohschein
 *         <br>Date: 30.05.2010
 *         <br>Time: 22:05:53
 */
public class AsyncCallbackMatcher implements IArgumentMatcher
{
    private Object myCallbackResult;
    protected Throwable myCallbackThrowable;
    protected boolean isCall;

    public AsyncCallbackMatcher(Object aCallbackResult) {
        this(true);
        myCallbackResult = aCallbackResult;
    }

    public AsyncCallbackMatcher(Throwable aCallbackThrowable) {
        this(true);
        myCallbackThrowable = aCallbackThrowable;
    }

    public AsyncCallbackMatcher(boolean isCall) {
        this.isCall = isCall;
    }

    public boolean matches(Object anObject) {
        if(anObject instanceof AsyncCallback) {
            if(isCall) {
                if(myCallbackThrowable != null) {
                    try {
                        ((AsyncCallback)anObject).onFailure(myCallbackThrowable);
                    } catch(RuntimeException e) { /* do nothing, because the matcher wouldn't work, when the match is aborted by an exception */}
                } else if(isCall) {
                    ((AsyncCallback)anObject).onSuccess(myCallbackResult);
                }
            }
            return true;
        }
        return false;
    }

    public void appendTo(StringBuffer aStringBuffer) {
        aStringBuffer.append("AsyncCallback failed!");
    }
}