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
package de.novanic.eventservice.client.event;

/**
 * {@link de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException} is an general exception for GWTEventService
 * that should primarily be used when there is no way to throw a checked exception for example within a callback.
 *
 * @author sstrohschein
 *         <br>Date: 16.04.2010
 *         <br>Time: 23:31:04
 */
public class RemoteEventServiceRuntimeException extends RuntimeException
{
    /**
     * Creates a new {@link de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException} with a message.
     * @param aMessage message
     */
    public RemoteEventServiceRuntimeException(String aMessage) {
        super(aMessage);
    }

    /**
     * Creates a new {@link de.novanic.eventservice.client.event.RemoteEventServiceRuntimeException} with a message and a cause throwable.
     * @param aMessage message
     * @param aThrowable throwable
     */
    public RemoteEventServiceRuntimeException(String aMessage, Throwable aThrowable) {
        super(aMessage, aThrowable);
    }
}