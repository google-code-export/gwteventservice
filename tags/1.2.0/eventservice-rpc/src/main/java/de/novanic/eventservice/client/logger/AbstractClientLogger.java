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
package de.novanic.eventservice.client.logger;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author sstrohschein
 *         <br>Date: 21.10.2008
 *         <br>Time: 23:13:12
 */
public abstract class AbstractClientLogger implements ClientLogger
{
    private final Collection<ClientLogger> myAttachedLoggers;

    protected AbstractClientLogger() {
        myAttachedLoggers = new ArrayList<ClientLogger>();
    }

    public void log(String aMessage) {
        log_internal(aMessage);
        for(ClientLogger theLogger: myAttachedLoggers) {
            theLogger.log(aMessage);
        }
    }

    public void error(String aMessage) {
        error_internal(aMessage);
        for(ClientLogger theLogger: myAttachedLoggers) {
            theLogger.error(aMessage);
        }
    }

    public void error(String aMessage, Throwable aThrowable) {
        error_internal(aMessage, aThrowable);
        for(ClientLogger theLogger: myAttachedLoggers) {
            theLogger.error(aMessage, aThrowable);
        }
    }

    public void attach(ClientLogger aClientLogger) {
        myAttachedLoggers.add(aClientLogger);
    }

    public void detach(ClientLogger aClientLogger) {
        myAttachedLoggers.remove(aClientLogger);
    }

    protected abstract void log_internal(String aMessage);

    protected abstract void error_internal(String aMessage);

    protected abstract void error_internal(String aMessage, Throwable aThrowable);
}