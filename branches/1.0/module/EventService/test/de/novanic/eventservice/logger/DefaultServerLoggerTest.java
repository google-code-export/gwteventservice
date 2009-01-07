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
package de.novanic.eventservice.logger;

import junit.framework.TestCase;

import java.util.logging.Level;

/**
 * @author sstrohschein
 * Date: 15.08.2008
 * <br>Time: 22:12:59
 */
public class DefaultServerLoggerTest extends TestCase
{
    public void testLogging() {
        ServerLogger theServerLogger = new DefaultServerLogger("testLogger");
        theServerLogger.debug("testDebug");
        theServerLogger.log(Level.CONFIG, "testConfigMessage");
        theServerLogger.error("testError");
        try {
            throwException();
            fail("Exception asserted!");
        } catch(TestException e) {
            theServerLogger.error("testError", e);
        }
    }

    private void throwException() throws TestException {
        throw new TestException();
    }

    private class TestException extends Exception {}
}