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

/**
 * @author sstrohschein
 *         <br>Date: 31.05.2010
 *         <br>Time: 00:00:52
 */
public class AsyncListenCallbackMatcher extends AsyncCallbackMatcher
{
    private int myLoops;
    private Throwable myThrowable;

    public AsyncListenCallbackMatcher(Object aCallbackResult, int aLoops, Throwable aThrowable) {
        super(aCallbackResult);
        myLoops = aLoops;
        myThrowable = aThrowable;
    }

    public boolean matches(Object anObject) {
        if(myLoops == 1) {
            myCallbackThrowable = myThrowable;
        } else if(myLoops <= 0) {
            isCall = false;
        }
        myLoops--;
        return super.matches(anObject);
    }
}