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
package de.novanic.eventservice.client.logger;

/**
 * ClientLogger can be used to log at the client side. {@link de.novanic.eventservice.client.logger.ClientLoggerFactory} should
 * be used to create the ClientLogger.
 *
 * @author sstrohschein
 * <br>Date: 14.08.2008
 * <br>Time: 21:19:01
 */
public interface ClientLogger
{
    /**
     * Logs messages at the info level.
     * @param aMessage message to log
     */
    void log(String aMessage);

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

    void attach(ClientLogger aClientLogger);

    void detach(ClientLogger aClientLogger);
}