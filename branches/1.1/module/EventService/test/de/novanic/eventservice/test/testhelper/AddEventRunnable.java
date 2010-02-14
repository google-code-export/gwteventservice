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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import de.novanic.eventservice.service.DefaultEventExecutorService;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 23:09:11
 */
public class AddEventRunnable implements Runnable, StartObservable
{
    private EventExecutorService myEventExecutorService;
    private Domain myDomain;
    private long myWaitingTime;
    private boolean isStarted;

    private AddEventRunnable(long aWaitingTime) {
        init("internalTestUser", aWaitingTime);
    }

    public AddEventRunnable(Domain aDomain, long aWaitingTime) {
        this(aWaitingTime);
        myDomain = aDomain;
    }

    public AddEventRunnable(String aUser, long aWaitingTime) {
        init(aUser, aWaitingTime);
    }

    private void init(String aUser, long aWaitingTime) {
        myWaitingTime = aWaitingTime;
        FactoryResetService.resetFactory(DefaultEventExecutorService.class);
        final EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        myEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(aUser);
    }

    public void run() {
        isStarted = true;

        final DummyEvent theEvent = new DummyEvent();
        try {
            Thread.sleep(myWaitingTime);
        } catch(InterruptedException e) {
            throw new RuntimeException("Sleep of " + AddEventRunnable.class.getName() + " aborted!", e);
        }

        if(myDomain != null) {
            myEventExecutorService.addEvent(myDomain, theEvent);
        } else {
            myEventExecutorService.addEventUserSpecific(theEvent);
        }
    }

    public boolean isStarted() {
        return isStarted;
    }
}