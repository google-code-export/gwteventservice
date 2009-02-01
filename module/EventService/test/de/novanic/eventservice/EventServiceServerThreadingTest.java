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
package de.novanic.eventservice;

import de.novanic.eventservice.service.testhelper.ListenStartResult;
import de.novanic.eventservice.service.testhelper.ListenRunnable;
import de.novanic.eventservice.service.testhelper.AddEventRunnable;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.domain.Domain;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 23:35:43
 */
public abstract class EventServiceServerThreadingTest extends EventServiceTestCase
{
    private EventService myEventService;
    private EventRegistry myEventRegistry;
    private Collection<EventThread> myEventThreads;
    private Collection<ListenStartResult> myListenStartResults;

    public void setUp(EventService anEventService) {
        myEventService = anEventService;
        myEventThreads = new ArrayList<EventThread>();
        myListenStartResults = new ArrayList<ListenStartResult>();
    }

    public void setUp(EventRegistry anEventRegistry) {
        myEventRegistry = anEventRegistry;
        myEventThreads = new ArrayList<EventThread>();
        myListenStartResults = new ArrayList<ListenStartResult>();
    }

    public void tearDown() throws Exception {
        //Join all threads to ensure that the next test doesn't collidate with other threads.
        joinEventThreads();
        joinListenThreads();
    }

    public void joinEventThreads() throws EventServiceServerThreadingTestException {
        try {
            for(Thread theEventThread : myEventThreads) {
                theEventThread.join();
            }
        } catch(InterruptedException e) {
            throw new EventServiceServerThreadingTestException("Error on joining threads!", e);
        }
    }

    private void checkInit(EventService anEventService) throws EventServiceServerThreadingTestException {
        if(anEventService == null) {
            throw new EventServiceServerThreadingTestException("The test isn't initialized with an EventService!");
        }
    }

    private void checkInit(EventRegistry anEventRegistry) throws EventServiceServerThreadingTestException {
        if(anEventRegistry == null) {
            throw new EventServiceServerThreadingTestException("The test isn't initialized with an EventRegistry!");
        }
    }

    public ListenStartResult startListen() throws EventServiceServerThreadingTestException {
        checkInit(myEventService);
        final ListenRunnable theListenRunnable = new ListenRunnable(myEventService);
        return startListen(theListenRunnable);
    }

    public ListenStartResult startListen(String aUserId) throws EventServiceServerThreadingTestException {
        checkInit(myEventRegistry);
        final ListenRunnable theListenRunnable = new ListenRunnable(myEventRegistry, aUserId);
        return startListen(theListenRunnable);
    }

    private ListenStartResult startListen(ListenRunnable aListenRunnable) {
        Thread theListenThread = new Thread(aListenRunnable);

        final ListenStartResult theStartResult = new ListenStartResult(theListenThread, aListenRunnable);
        myListenStartResults.add(theStartResult);

        theListenThread.start();
        waitForListenStart(aListenRunnable);

        return theStartResult;
    }

    public Thread startAddEvent(Domain aDomain, long aWaitingTime) {
        EventThread theEventThread = new EventThread(new AddEventRunnable(aDomain, aWaitingTime));
        return startAddEvent(theEventThread);
    }

    public Thread startAddEvent(String aUser, long aWaitingTime) {
        EventThread theEventThread = new EventThread(new AddEventRunnable(aUser, aWaitingTime));
        return startAddEvent(theEventThread);
    }

    private Thread startAddEvent(EventThread anEventThread) {
        myEventThreads.add(anEventThread);
        anEventThread.start();
        return anEventThread;
    }

    public int listen() throws EventServiceServerThreadingTestException {
        checkInit(myEventService);
        ListenStartResult theListenStartResult = startListen();
        return joinListen(theListenStartResult);
    }

    public int listen(String aUserId) throws EventServiceServerThreadingTestException {
        checkInit(myEventRegistry);
        ListenStartResult theListenStartResult = startListen(aUserId);
        return joinListen(theListenStartResult);
    }

    public int getEventCount() {
        int theListenEventCount = 0;
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            theListenEventCount += theListenStartResult.getListenResult().getEventCount();
        }
        return theListenEventCount;
    }

    public int getEventCount(Domain aDomain) {
        int theListenEventCount = 0;
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            theListenEventCount += theListenStartResult.getListenResult().getEventCount(aDomain);
        }
        return theListenEventCount;
    }

    public int getEventCount(String aUser) {
        int theListenEventCount = 0;
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            theListenEventCount += theListenStartResult.getListenResult().getEventCount(aUser);
        }
        return theListenEventCount;
    }

    public void joinListenThreads() throws EventServiceServerThreadingTestException {
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            joinListen(theListenStartResult);
        }
    }

    public int joinListen(ListenStartResult aListenResult) throws EventServiceServerThreadingTestException {
        try {
            aListenResult.getThread().join();
            return aListenResult.getListenResult().getEventCount();
        } catch(InterruptedException e) {
            throw new EventServiceServerThreadingTestException("Listen thread interrupted!", e);
        }
    }

    private void waitForListenStart(ListenRunnable aListenRunnable) {
        while(!aListenRunnable.isStarted()) {}
    }

    private class EventThread<AddEventRunnable> extends Thread
    {
        public EventThread(AddEventRunnable aRunnable) {
            super((Runnable)aRunnable);
        }
    }
}