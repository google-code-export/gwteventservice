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
package de.novanic.eventservice.client.logger;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * @author sstrohschein
 * Date: 15.08.2008
 * Time: 22:29:52
 */
public class GWTClientLoggerTest extends TestCase
{
    private ClientLogger myClientLogger;
    private DummyClientLogger myAttachedDummyClientLogger;

    public void setUp() {
        myClientLogger = new GWTClientLogger();
        myAttachedDummyClientLogger = new DummyClientLogger();
        myClientLogger.attach(myAttachedDummyClientLogger);
    }

    public void testLogging() {
        myClientLogger.log("testLog");
        List<String> theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(1, theLogMessages.size());
        assertEquals("Log: testLog", theLogMessages.get(0));

        myClientLogger.error("testError");
        theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(2, theLogMessages.size());
        assertEquals("Error: testError", theLogMessages.get(1));

        try {
            throwException();
            fail("Exception asserted!");
        } catch(TestException e) {
            myClientLogger.error("testError2", e);
        }
        theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(3, theLogMessages.size());
        assertEquals("Error-Exception: testError2", theLogMessages.get(2));
    }

    public void testLogging_Detached() {
        myClientLogger.detach(myAttachedDummyClientLogger);

        myClientLogger.log("testLog");
        List<String> theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(0, theLogMessages.size());

        myClientLogger.error("testError");
        theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(0, theLogMessages.size());

        try {
            throwException();
            fail("Exception asserted!");
        } catch(TestException e) {
            myClientLogger.error("testError2", e);
        }
        theLogMessages = myAttachedDummyClientLogger.getLogMessages();
        assertEquals(0, theLogMessages.size());
    }

    private void throwException() throws TestException {
        throw new TestException();
    }

    private class TestException extends Exception {}

    private class DummyClientLogger extends AbstractClientLogger
    {
        private static final String MESSAGE_LOG_PREFIX = "Log: ";
        private static final String ERROR_LOG_PREFIX = "Error: ";
        private static final String ERROR_EXCEPTION_LOG_PREFIX = "Error-Exception: ";

        private List<String> myLogMessages;

        public DummyClientLogger() {
            myLogMessages = new ArrayList<String>();
        }

        public void log_internal(String aMessage) {
            myLogMessages.add(MESSAGE_LOG_PREFIX + aMessage);
        }

        public void error_internal(String aMessage) {
            myLogMessages.add(ERROR_LOG_PREFIX + aMessage);
        }

        public void error_internal(String aMessage, Throwable aThrowable) {
            myLogMessages.add(ERROR_EXCEPTION_LOG_PREFIX + aMessage);
        }

        public List<String> getLogMessages() {
            return myLogMessages;
        }

        public void clearLogMessages() {
            myLogMessages.clear();
        }
    }
}