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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;

/**
 * @author sstrohschein
 *         <br>Date: 10.09.2009
 *         <br>Time: 12:57:11
 */
public class DeregisterUserRunnable implements Runnable, StartObservable
{
    private Domain myDomain;
    private String myUserId;
    private boolean isStarted;

    public DeregisterUserRunnable(String aUserId) {
        myUserId = aUserId;
    }

    public DeregisterUserRunnable(Domain aDomain, String aUserId) {
        this(aUserId);
        myDomain = aDomain;
    }

    public void run() {
        isStarted = true;

        EventRegistry theEventRegistry = EventRegistryFactory.getInstance().getEventRegistry();
        if(myDomain != null) {
            theEventRegistry.unlisten(myDomain, myUserId);
        } else {
            theEventRegistry.unlisten(myUserId);
        }
    }

    public boolean isStarted() {
        return isStarted;
    }
}