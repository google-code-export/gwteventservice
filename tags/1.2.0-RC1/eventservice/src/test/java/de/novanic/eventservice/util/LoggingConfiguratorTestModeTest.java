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
package de.novanic.eventservice.util;

import de.novanic.eventservice.EventServiceTestCase;

import java.util.logging.LogManager;

/**
 * @author sstrohschein
 *         <br>Date: 14.01.2009
 *         <br>Time: 23:42:36
 */
public class LoggingConfiguratorTestModeTest extends EventServiceTestCase
{
    private static final String LOGGING_FILE_PROPERTY = "java.util.logging.config.file";

    private String myLoggingFilePropertyBackup;

    public void setUp() {
        myLoggingFilePropertyBackup = System.getProperty(LOGGING_FILE_PROPERTY);
    }

    public void tearDown() throws Exception {
        if(myLoggingFilePropertyBackup != null) {
            System.setProperty(LOGGING_FILE_PROPERTY, myLoggingFilePropertyBackup);
        } else {
            System.clearProperty(LOGGING_FILE_PROPERTY);
        }
        LogManager.getLogManager().readConfiguration();
        LoggingConfiguratorTestMode.reset();
    }

    public void testConfigureLogging() throws Exception {
        LoggingConfiguratorTestMode.configureLogging();
        assertTrue(System.getProperty(LOGGING_FILE_PROPERTY).contains("testlogging.properties"));

        LoggingConfiguratorTestMode.configureLogging();
        assertTrue(System.getProperty(LOGGING_FILE_PROPERTY).contains("testlogging.properties"));
    }

    public void testLogOff() throws Exception {
        LoggingConfiguratorTestMode.logOff();
        assertTrue(System.getProperty(LOGGING_FILE_PROPERTY).contains("empty.properties"));

        LoggingConfiguratorTestMode.logOff();
        assertTrue(System.getProperty(LOGGING_FILE_PROPERTY).contains("empty.properties"));
    }

    public void testLogOn() throws Exception {
        LoggingConfiguratorTestMode.logOn();
        assertEquals(myLoggingFilePropertyBackup, System.getProperty(LOGGING_FILE_PROPERTY));

        LoggingConfiguratorTestMode.logOn();
        assertEquals(myLoggingFilePropertyBackup, System.getProperty(LOGGING_FILE_PROPERTY));
    }    
}
