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
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.util.PlatformUtil;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 23:35:43
 */
public abstract class EventServiceServerThreadingTest extends EventServiceTestCase
{
    private static Logger LOG = Logger.getLogger(EventServiceServerThreadingTest.class.getName());

    private EventService myEventService;
    private EventRegistry myEventRegistry;
    private Collection<EventThread> myEventThreads;
    private Collection<ListenStartResult> myListenStartResults;
    private long myStartTime;

    public void setUp(EventService anEventService) {
        myStartTime = PlatformUtil.getCurrentTime();
        myEventService = anEventService;
        myEventThreads = new ArrayList<EventThread>();
        myListenStartResults = new ArrayList<ListenStartResult>();
    }

    public void setUp(EventRegistry anEventRegistry) {
        myStartTime = PlatformUtil.getCurrentTime();
        myEventRegistry = anEventRegistry;
        myEventThreads = new ArrayList<EventThread>();
        myListenStartResults = new ArrayList<ListenStartResult>();
    }

    public void tearDown() throws Exception {
        //Join all threads to ensure that the next test doesn't collidate with other threads.
        joinEventThreads();
        joinListenThreads();

        if(LOG.isLoggable(Level.INFO) && myStartTime > 0) {
            long theExecutionTime = PlatformUtil.getCurrentTime() - myStartTime;
            LOG.log(Level.INFO, "Execution time: " + theExecutionTime + "ms (" + theExecutionTime / 1000 + " second(s))");
        }
        myStartTime = 0;
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
        EventThread theEventThread = new EventThread<AddEventRunnable>(new AddEventRunnable(aDomain, aWaitingTime));
        return startAddEvent(theEventThread);
    }

    public Thread startAddEvent(String aUser, long aWaitingTime) {
        EventThread theEventThread = new EventThread<AddEventRunnable>(new AddEventRunnable(aUser, aWaitingTime));
        return startAddEvent(theEventThread);
    }

    protected void startAddEvent(String[] aUserIds, Domain aDomain, long aWaitingTime, boolean isUserSpecific) {
        startAddEvent(aUserIds, aDomain, aWaitingTime, isUserSpecific, true);
    }

    protected void startAddEvent(String[] aUserIds, Domain aDomain, long aWaitingTime, boolean isUserSpecific, boolean isCheckUser) {
        if(isUserSpecific) {
            for(String theUserId: aUserIds) {
                startAddEvent(theUserId, aDomain, aWaitingTime, isUserSpecific, isCheckUser);
            }
        } else {
            startAddEvent((String)null, aDomain, aWaitingTime, isUserSpecific, false);
        }
    }

    protected Thread startAddEvent(String aUserId, Domain aDomain, long aWaitingTime, boolean isUserSpecific) {
        return startAddEvent(aUserId, aDomain, aWaitingTime, isUserSpecific, true);
    }

    protected Thread startAddEvent(String aUserId, Domain aDomain, long aWaitingTime, boolean isUserSpecific, boolean isCheckUser) {
        if(isCheckUser) {
            assertTrue("The user \"" + aUserId + "\" isn't registered for domain \"" + aDomain + "\", but expected in test case!",
                    myEventRegistry.getListenDomains(aUserId).contains(aDomain));
        }

        EventThread theEventThread;
        if(isUserSpecific) {
            theEventThread = new EventThread<AddEventRunnable>(new AddEventRunnable(aUserId, aWaitingTime));
        } else {
            theEventThread = new EventThread<AddEventRunnable>(new AddEventRunnable(aDomain, aWaitingTime));
        }
        return startAddEvent(theEventThread);
    }

    private Thread startAddEvent(EventThread anEventThread) {
        myEventThreads.add(anEventThread);
        anEventThread.start();
        return anEventThread;
    }

    protected void addEvent(String aUserId, Domain aDomain, Event anEvent, boolean isUserSpecific) {
        addEvent(aUserId, aDomain, anEvent, isUserSpecific, true);
    }

    protected void addEvent(String aUserId, Domain aDomain, Event anEvent, boolean isUserSpecific, boolean isCheckUser) {
        if(isCheckUser) {
            assertTrue("The user \"" + aUserId + "\" isn't registered for domain \"" + aDomain + "\", but expected in test case!",
                    myEventRegistry.getListenDomains(aUserId).contains(aDomain));
        }
        if(isUserSpecific) {
            myEventRegistry.addEventUserSpecific(aUserId, anEvent);
        } else {
            myEventRegistry.addEvent(aDomain, anEvent);
        }
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
        Set<Event> theEvents = new HashSet<Event>(myListenStartResults.size() * 3);
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            final Collection<Event> theNewEvents = theListenStartResult.getListenResult().getDomainEvents().get(aDomain);
            if(theNewEvents != null) {
                theEvents.addAll(theNewEvents);
            }
        }
        return theEvents.size();
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