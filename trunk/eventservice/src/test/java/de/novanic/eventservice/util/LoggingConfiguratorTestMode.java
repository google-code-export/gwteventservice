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
package de.novanic.eventservice.util;

import de.novanic.eventservice.client.config.ConfigurationException;

import java.net.URL;
import java.util.logging.LogManager;
import java.io.IOException;

/**
 * @author sstrohschein
 *         <br>Date: 14.01.2009
 *         <br>Time: 23:38:11
 */
public final class LoggingConfiguratorTestMode
{
    private static final String LOGGING_FILE_PROPERTY = "java.util.logging.config.file";
    private static final String TEST_LOGGING_PROPERTIES = "testlogging.properties";
    private static final String EMPTY_LOGGING_PROPERTIES = "empty.properties";

    private static String myLoggingFilePropertiesBackup;
    private static String myTestLoggingProperties;
    private static String myEmptyLoggingProperties;

    private LoggingConfiguratorTestMode() {}

    public static void configureLogging() throws Exception {
        if(myTestLoggingProperties == null) {
            myTestLoggingProperties = getConfiguration(TEST_LOGGING_PROPERTIES);
        }
        loadConfiguration(myTestLoggingProperties);
    }

    public static void logOn() throws IOException {
        if(myLoggingFilePropertiesBackup != null) {
            System.setProperty(LOGGING_FILE_PROPERTY, myLoggingFilePropertiesBackup);
        } else {
            System.clearProperty(LOGGING_FILE_PROPERTY);
        }
        LogManager.getLogManager().readConfiguration();
    }

    public static void logOff() throws IOException {
        if(myEmptyLoggingProperties == null) {
            myLoggingFilePropertiesBackup = System.getProperty(LOGGING_FILE_PROPERTY);
            myEmptyLoggingProperties = getConfiguration(EMPTY_LOGGING_PROPERTIES);
        }
        loadConfiguration(myEmptyLoggingProperties);
    }

    public static void reset() {
        myTestLoggingProperties = null;
        myEmptyLoggingProperties = null;
    }

    private static void loadConfiguration(String aLoggingProperties) throws IOException {
        System.setProperty(LOGGING_FILE_PROPERTY, aLoggingProperties);
        LogManager.getLogManager().readConfiguration();
    }

    private static String getConfiguration(String aConfigProperties) {
        URL theResource = Thread.currentThread().getContextClassLoader().getResource(aConfigProperties);
        if(theResource != null) {
            return theResource.getFile();
        } else {
            throw new ConfigurationException("Configuration file \"" + aConfigProperties + "\" could not be found on classpath!");
        }
    }
}