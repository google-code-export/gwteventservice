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
package de.novanic.eventservice.service.exception;

/**
 * NoSessionAvailableException is an exception that should be thrown when no session is available, but needed.
 * For example when user/client information must be available.
 *
 * @author sstrohschein
 *         <br>Date: 23.11.2008
 *         <br>Time: 22:25:23
 */
public class NoSessionAvailableException extends RuntimeException
{
    private static final String DEFAULT_ERROR_MESSAGE = "There is no session / client information available!";

    public NoSessionAvailableException() {
        this(DEFAULT_ERROR_MESSAGE);
    }

    public NoSessionAvailableException(String aMessage) {
        super(aMessage);
    }

    public NoSessionAvailableException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }
}