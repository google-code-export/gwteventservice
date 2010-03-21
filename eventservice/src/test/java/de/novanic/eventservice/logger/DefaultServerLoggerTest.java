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

import java.util.logging.*;

import de.novanic.eventservice.EventServiceTestCase;

/**
 * @author sstrohschein
 * Date: 15.08.2008
 * <br>Time: 22:12:59
 */
public class DefaultServerLoggerTest extends EventServiceTestCase
{
    private static final String LOGGER_NAME = "testLogger";

    private TestLoggerHandler myLoggerHandler;
    private Logger myRealLogger;

    public void setUp() throws Exception {
        logOff();

        myRealLogger = Logger.getLogger(LOGGER_NAME);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        logOn();
        
        myRealLogger.removeHandler(myLoggerHandler);
    }

    public void testLogging_Debug() {
        final String theMessage = "testDebug";

        ServerLogger theServerLogger = setUpLogger(Level.FINEST, theMessage);
        theServerLogger.debug(theMessage);
    }

    public void testLogging_Info() {
        final String theMessage = "testInfo";

        ServerLogger theServerLogger = setUpLogger(Level.INFO, theMessage);
        theServerLogger.info(theMessage);
    }

    public void testLogging_OwnLevel() {
        final String theMessage = "testConfigMessage";

        ServerLogger theServerLogger = setUpLogger(Level.CONFIG, theMessage);
        theServerLogger.log(Level.CONFIG, theMessage);
    }

    public void testLogging_Error() {
        final String theMessage = "testError";

        ServerLogger theServerLogger = setUpLogger(Level.SEVERE, theMessage);
        theServerLogger.error(theMessage);
    }

    public void testLogging_ExceptionError() {
        final String theMessage = "testError";

        ServerLogger theServerLogger = setUpLogger(Level.SEVERE, theMessage);
        try {
            throwException();
            fail("Exception asserted!");
        } catch(TestException e) {
            theServerLogger.error(theMessage, e);
        }
    }

    private ServerLogger setUpLogger(Level aLevel, String aMessage) {
        ServerLogger theServerLogger = new DefaultServerLogger(LOGGER_NAME);

        myLoggerHandler = new TestLoggerHandler(aLevel, aMessage);
        myRealLogger.addHandler(myLoggerHandler);

        return theServerLogger;
    }

    private void throwException() throws TestException {
        throw new TestException();
    }

    private class TestException extends Exception {}

    private class TestLoggerHandler extends Handler
    {
        private static final String SERVER_MESSAGE_PREFIX = "Server: ";

        private Level myLevel;
        private String myMessage;

        private TestLoggerHandler(Level aLevel, String aMessage) {
            myLevel = aLevel;
            myMessage = aMessage;
        }

        public void publish(LogRecord aLogRecord) {
            assertEquals(myLevel, aLogRecord.getLevel());
            assertEquals(SERVER_MESSAGE_PREFIX + myMessage, aLogRecord.getMessage());
        }

        public void flush() {}

        public void close() throws SecurityException {}
    }
}
