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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.client.event.domain.Domain;

/**
 * @author sstrohschein
 *         <br>Date: 24.02.2009
 *         <br>Time: 22:21:58
 */
public class RegisterUserRunnable implements Runnable, StartObservable, FinishObservable
{
    private final Domain myDomain;
    private final String myUserId;
    private boolean isStarted;
    private boolean isFinished;

    public RegisterUserRunnable(Domain aDomain, String aUserId) {
        myDomain = aDomain;
        myUserId = aUserId;
    }

    public void run() {
        isStarted = true;

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        theEventRegistry.registerUser(myDomain, myUserId, null);

        isFinished = true;
    }

    public Domain getDomain() {
        return myDomain;
    }

    public String getUserId() {
        return myUserId;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isFinished() {
        return isFinished;
    }
}