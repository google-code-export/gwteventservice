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
 * ClientLoggerFactory should be used to create instances of {@link de.novanic.eventservice.client.logger.ClientLogger}.
 * To create a new {@link de.novanic.eventservice.client.logger.ClientLogger} see the following method:
 * {@link ClientLoggerFactory#getClientLogger()}.
 *
 * @author sstrohschein
 * <br>Date: 14.08.2008
 * <br>Time: 21:19:11
 */
public class ClientLoggerFactory
{
    private ClientLoggerFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class ClientLoggerHolder {
        private static final ClientLogger CLIENT_LOGGER_INSTANCE = new GWTClientLogger();
    }

    /**
     * Creates a new {@link de.novanic.eventservice.client.logger.ClientLogger} and holds it as a singleton (only one
     * instance can exist).
     * @return {@link de.novanic.eventservice.client.logger.ClientLogger}
     */
    public static ClientLogger getClientLogger() {
        return ClientLoggerHolder.CLIENT_LOGGER_INSTANCE;
    }
}