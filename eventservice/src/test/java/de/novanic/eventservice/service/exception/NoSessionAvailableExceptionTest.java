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
package de.novanic.eventservice.service.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 23.11.2008
 *         <br>Time: 22:34:40
 */
@RunWith(JUnit4.class)
public class NoSessionAvailableExceptionTest
{
    @Test
    public void testInit() {
        try {
            throwNoSessionAvailableException_DefaultMessage();
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void testInit_2() {
        final String theErrorMessage = "testNoSessionAvailableExceptionMessage";
        try {
            throwNoSessionAvailableException_Message(theErrorMessage);
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals(theErrorMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void testInit_3() {
        final String theErrorMessage = "testNoSessionAvailableExceptionMessage";
        try {
            throwNoSessionAvailableException_Cause(theErrorMessage);
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals(theErrorMessage, e.getMessage());
            assertNotNull(e.getCause());
            if(!(e.getCause() instanceof CauseException)) {
                fail("Exception \"" + CauseException.class.getName() + "\" expected!");
            }
        }
    }

    private void throwNoSessionAvailableException_DefaultMessage() {
        throw new NoSessionAvailableException();
    }

    private void throwNoSessionAvailableException_Message(String aMessage) {
        throw new NoSessionAvailableException(aMessage);
    }

    private void throwNoSessionAvailableException_Cause(String aMessage) {
        try {
            throwCauseException();
            fail("Exception \"" + CauseException.class.getName() + "\" expected!");
        } catch(CauseException e) {
            throw new NoSessionAvailableException(aMessage, e);
        }
    }

    private void throwCauseException() throws CauseException {
        throw new CauseException();
    }

    private class CauseException extends Exception {}
}