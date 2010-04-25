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

import de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException;
import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 14:11:25
 */
public class RemoteEventServiceRuntimeExceptionTest extends TestCase
{
    public void testInit() {
        final String theErrorMessage = "testMessage";
        try {
            throwTestException(theErrorMessage);
            fail("Exception expected!");
        } catch(RemoteEventServiceRuntimeException e) {
            assertEquals(theErrorMessage, e.getMessage());
        }
    }

    public void testInit_2() {
        final String theErrorMessage = "testMessage";
        try {
            throwTestException(theErrorMessage);
            fail("Exception expected!");
        } catch(RemoteEventServiceRuntimeException e) {
            assertEquals(theErrorMessage, e.getMessage());

            final String theErrorMessage_2 = "testMessage2";
            try {
                throwTestException(theErrorMessage_2, e);
            } catch(RemoteEventServiceRuntimeException e1) {
                assertEquals(theErrorMessage_2, e1.getMessage());
                assertEquals(e, e1.getCause());
            }
        }
    }

    private void throwTestException(String aMessage) {
        throw new RemoteEventServiceRuntimeException(aMessage);
    }

    private void throwTestException(String aMessage, Throwable aThrowable) {
        throw new RemoteEventServiceRuntimeException(aMessage, aThrowable);
    }
}