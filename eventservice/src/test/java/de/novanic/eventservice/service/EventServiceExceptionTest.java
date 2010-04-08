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
package de.novanic.eventservice.service;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 07.04.2010
 *         <br>Time: 23:17:55
 */
public class EventServiceExceptionTest extends TestCase
{
    public void testInit() {
        final String theErrorMessage = "testMessage";
        try {
            throwTestException(theErrorMessage);
            fail("Exception expected!");
        } catch(EventServiceException e) {
            assertEquals(theErrorMessage, e.getMessage());
        }
    }

    public void testInit_2() {
        final String theErrorMessage = "testMessage";
        try {
            throwTestException(theErrorMessage);
            fail("Exception expected!");
        } catch(EventServiceException e) {
            assertEquals(theErrorMessage, e.getMessage());

            final String theErrorMessage_2 = "testMessage2";
            try {
                throwTestException(theErrorMessage_2, e);
            } catch(EventServiceException e1) {
                assertEquals(theErrorMessage_2, e1.getMessage());
                assertEquals(e, e1.getCause());
            }
        }
    }

    private void throwTestException(String aMessage) throws EventServiceException {
        throw new EventServiceException(aMessage);
    }

    private void throwTestException(String aMessage, Throwable aThrowable) throws EventServiceException {
        throw new EventServiceException(aMessage, aThrowable);
    }
}