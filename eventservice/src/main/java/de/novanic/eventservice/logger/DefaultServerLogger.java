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

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This {@link de.novanic.eventservice.logger.ServerLogger} uses the Java-Logging-API to log server messages.
 * {@link de.novanic.eventservice.logger.ServerLogger} can be used to log at the server side.
 *
 * @author sstrohschein
 * <br>Date: 15.08.2008
 * <br>Time: 00:14:02
 */
public class DefaultServerLogger implements ServerLogger
{
    private static final String SERVER_LOG_PREFIX = "Server: ";

    private final Logger LOG;

    /**
     * Creates a new DefaultServerLogger with the corresponding name.
     * @param aLoggerName logger name
     */
    protected DefaultServerLogger(String aLoggerName) {
        LOG = Logger.getLogger(aLoggerName);
    }

    /**
     * Logs messages at the finest level.
     * @param aMessage message to log
     */
    public void debug(String aMessage) {
        log(Level.FINEST, aMessage);
    }

    /**
     * Logs messages at the info level.
     * @param aMessage message to log
     */
    public void info(String aMessage) {
        log(Level.INFO, aMessage);
    }

    /**
     * Logs messages at the severe level.
     * @param aMessage message to log
     */
    public void error(String aMessage) {
        log(Level.SEVERE, aMessage);
    }

    /**
     * Logs messages at the severe level.
     * @param aMessage message to log
     * @param aThrowable throwable to log
     */
    public void error(String aMessage, Throwable aThrowable) {
        if(LOG.isLoggable(Level.SEVERE)) {
            LOG.log(Level.SEVERE, SERVER_LOG_PREFIX + aMessage, aThrowable);
        }
    }

    /**
     * Logs messages at a specified level.
     * @param aLevel logging level
     * @param aMessage message to log
     */
    public void log(Level aLevel, String aMessage) {
        if(LOG.isLoggable(aLevel)) {
            LOG.log(aLevel, SERVER_LOG_PREFIX + aMessage);
        }
    }
}