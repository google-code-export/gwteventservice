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
package de.novanic.eventservice.clientmock;

import de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 14:11:25
 */
@RunWith(JUnit4.class)
public class RemoteEventServiceRuntimeExceptionTest
{
    @Test
    public void testInit() {
        final String theErrorMessage = "testMessage";
        try {
            throwTestException(theErrorMessage);
            fail("Exception expected!");
        } catch(RemoteEventServiceRuntimeException e) {
            assertEquals(theErrorMessage, e.getMessage());
        }
    }

    @Test
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