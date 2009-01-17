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

import java.util.logging.Level;

/**
 * A ServerLogger can be used to log at the server side. {@link de.novanic.eventservice.logger.ServerLoggerFactory} should
 * be used to create the ServerLogger.
 * 
 * @author sstrohschein
 * <br>Date: 15.08.2008
 * <br>Time: 00:13:49
 */
public interface ServerLogger
{
    /**
     * Logs messages at the debug level.
     * @param aMessage message to log
     */
    void debug(String aMessage);

    /**
     * Logs messages at the info level.
     * @param aMessage message to log
     */
    void info(String aMessage);

    /**
     * Logs messages at the error level.
     * @param aMessage message to log
     */
    void error(String aMessage);

    /**
     * Logs messages at the error level.
     * @param aMessage message to log
     * @param aThrowable throwable to log
     */
    void error(String aMessage, Throwable aThrowable);

    /**
     * Logs messages at a specified level.
     * @param aLevel logging level
     * @param aMessage message to log
     */
    void log(Level aLevel, String aMessage);
}