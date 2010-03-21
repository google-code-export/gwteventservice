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

/**
 * ServerLoggerFactory should be used to create instances of {@link de.novanic.eventservice.logger.ServerLogger}.
 * To create a new {@link de.novanic.eventservice.logger.ServerLogger} see the following method:
 * {@link de.novanic.eventservice.logger.ServerLoggerFactory#getServerLogger(String)}.
 *
 * @author sstrohschein
 * <br>Date: 15.08.2008
 * <br>Time: 00:13:43
 */
public class ServerLoggerFactory
{
    private ServerLoggerFactory() {}

    /**
     * Creates a new {@link de.novanic.eventservice.logger.ServerLogger} with a specified name.
     * @param aName logger name
     * @return new {@link de.novanic.eventservice.logger.ServerLogger}
     */
    public static ServerLogger getServerLogger(String aName) {
        return new DefaultServerLogger(aName);
    }
}