/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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

import com.google.gwt.core.client.GWT;

/**
 * This {@link de.novanic.eventservice.client.logger.ClientLogger} uses {@link com.google.gwt.core.client.GWT#log(String, Throwable)}
 * to log client messages.
 * {@link de.novanic.eventservice.client.logger.ClientLogger} can be used to log at the server side.
 *
 * @author sstrohschein
 * <br>Date: 14.08.2008
 * <br>Time: 21:19:21
 */
public class GWTClientLogger extends AbstractClientLogger
{
    private static final String CLIENT_PREFIX = "Client: ";
    private static final String CLIENT_ERROR_PREFIX = "Client-Error: ";

    protected GWTClientLogger() {}

    /**
     * Logs messages at the info level.
     * @param aMessage message to log
     */
    public void log_internal(String aMessage) {
        log(aMessage, CLIENT_PREFIX, null);
    }

    /**
     * Logs messages at the error level.
     * @param aMessage message to log
     */
    public void error_internal(String aMessage) {
        log(aMessage, CLIENT_ERROR_PREFIX, null);
    }

    /**
     * Logs messages at the error level.
     * @param aMessage message to log
     * @param aThrowable throwable to log
     */
    public void error_internal(String aMessage, Throwable aThrowable) {
        log(aMessage, CLIENT_ERROR_PREFIX, aThrowable);
    }

    /**
     * Logs a message with a prefix and a throwable.
     * @param aMessage message to log
     * @param aPrefix prefix (the message will be appended to the prefix)
     * @param aThrowable throwable to log
     */
    private static void log(String aMessage, String aPrefix, Throwable aThrowable) {
        GWT.log(aPrefix + aMessage, aThrowable);
    }
}