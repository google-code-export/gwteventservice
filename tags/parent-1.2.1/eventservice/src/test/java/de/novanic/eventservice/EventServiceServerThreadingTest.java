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
package de.novanic.eventservice;

import de.novanic.eventservice.test.testhelper.*;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;
import de.novanic.eventservice.service.registry.user.UserManagerFactory;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.client.event.service.EventService;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.util.PlatformUtil;
import org.junit.After;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 23:35:43
 */
public abstract class EventServiceServerThreadingTest extends EventServiceTestCase
{
    private static final Logger LOG = Logger.getLogger(EventServiceServerThreadingTest.class.getName());

    private EventService myEventService;
    private EventRegistry myEventRegistry;
    private Collection<RunningUnit> myRunningUnits; 
    private Collection<ListenStartResult> myListenStartResults;
    private long myStartTime;

    public void setUp(EventService anEventService) {
        myStartTime = PlatformUtil.getCurrentTime();
        myEventService = anEventService;
        myRunningUnits = new ArrayList<RunningUnit>();
        myListenStartResults = new ArrayList<ListenStartResult>();
        FactoryResetService.resetFactory(AutoIncrementFactory.class);
        if(anEventService != null) {
            anEventService.initEventService();
        }
    }

    public void setUp(EventRegistry anEventRegistry) {
        setUp((EventService)null);
        myEventRegistry = anEventRegistry;
    }

    @After
    public void tearDown() throws Exception {
        //Join all threads to ensure that the next test doesn't collidate with other threads.
        joinThreads();
        joinListenThreads();

        if(LOG.isLoggable(Level.INFO) && myStartTime > 0L) {
            long theExecutionTime = PlatformUtil.getCurrentTime() - myStartTime;
            LOG.log(Level.INFO, "Execution time: " + theExecutionTime + "ms (" + theExecutionTime / 1000L + " second(s))");
        }
        myStartTime = 0L;

        //remove all users (memory optimizations for recursive tests)
        Collection<UserInfo> theUserInfoCollection = UserManagerFactory.getInstance().getUserManager(0L).getUsers();
        for(UserInfo theUserInfo: theUserInfoCollection) {
            myEventRegistry.unlisten(theUserInfo.getUserId());
        }
        myEventRegistry = null;
        FactoryResetService.resetFactory(UserManagerFactory.class);
        FactoryResetService.resetFactory(EventRegistryFactory.class);
        FactoryResetService.resetFactory(AutoIncrementFactory.class);
    }

    /**
     * Ensures that all accuired threads are started and joined. To join the listen threads the method joinListenThreads() must
     * be used, because the ordering is important (the listen threads must be joined at last).
     * @throws EventServiceServerThreadingTestException
     */
    public void joinThreads() throws EventServiceServerThreadingTestException {
        waitForStarts(myRunningUnits);
        try {
            for(RunningUnit theRunningUnit: myRunningUnits) {
                theRunningUnit.getThread().join();
            }
        } catch(InterruptedException e) {
            throw new EventServiceServerThreadingTestException("Error on joining threads!", e);
        }
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
        waitForStart(aListenRunnable);

        return theStartResult;
    }

    public Thread startAddEvent(Domain aDomain, long aWaitingTime) {
        return startRunningUnit(new AddEventRunnable(aDomain, aWaitingTime)).getThread();
    }

    public Thread startAddEvent(String aUser, long aWaitingTime) {
        return startRunningUnit(new AddEventRunnable(aUser, aWaitingTime)).getThread();
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

        AddEventRunnable theAddEventRunnable;
        if(isUserSpecific) {
            theAddEventRunnable = new AddEventRunnable(aUserId, aWaitingTime);
        } else {
            theAddEventRunnable = new AddEventRunnable(aDomain, aWaitingTime);
        }
        return startRunningUnit(theAddEventRunnable).getThread();
    }

    public FinishObservable startRegisterUser(Domain aDomain, String aUserId) {
        RegisterUserRunnable theRegisterUserRunnable = new RegisterUserRunnable(aDomain, aUserId);
        startRunningUnit(theRegisterUserRunnable);
        return theRegisterUserRunnable;
    }

    public void startDeregisterUser(String aUserId) {
        DeregisterUserRunnable theDeregisterUserRunnable = new DeregisterUserRunnable(aUserId);
        startRunningUnit(theDeregisterUserRunnable);
    }

    public void startDeregisterUser(Domain aDomain, String aUserId) {
        DeregisterUserRunnable theDeregisterUserRunnable = new DeregisterUserRunnable(aDomain, aUserId);
        startRunningUnit(theDeregisterUserRunnable);
    }

    public void startDeregisterUser(Domain aDomain, String aUserId, FinishObservable aFinishObservable) {
        DeregisterUserRunnable theDeregisterUserRunnable = new DeregisterUserRunnable(aDomain, aUserId);
        addRunningUnit(theDeregisterUserRunnable);
        waitForFinish(aFinishObservable);
        startRunningUnit(theDeregisterUserRunnable);
    }

    private RunningUnit startRunningUnit(StartObservable aStartObservable) {
        RunningUnit theRunningUnit = addRunningUnit(aStartObservable);
        return startRunningUnit(theRunningUnit);
    }

    private RunningUnit startRunningUnit(RunningUnit aRunningUnit) {
        aRunningUnit.getThread().start();
        return aRunningUnit;
    }

    private RunningUnit addRunningUnit(StartObservable aStartObservable) {
        Thread theThread = new Thread(aStartObservable);

        final RunningUnit theRunningUnit = new RunningUnit(aStartObservable, theThread);
        myRunningUnits.add(theRunningUnit);
        return theRunningUnit;
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

    public void checkEventSequence() {
        for(ListenStartResult theListenStartResult: myListenStartResults) {
            checkEventSequence(theListenStartResult);
        }
    }

    public void checkEventSequence(ListenStartResult aListenStartResult) {
        Map<Domain, List<Event>> theAllDomainEvents = aListenStartResult.getListenResult().getDomainEvents();
        for(List<Event> theDomainSpecificEvents: theAllDomainEvents.values()) {
            checkEventSequence(theDomainSpecificEvents);
        }

        Map<String, List<Event>> theAllUserEvents = aListenStartResult.getListenResult().getUserEvents();
        for(List<Event> theUserSpecificEvents: theAllUserEvents.values()) {
            checkEventSequence(theUserSpecificEvents);
        }
    }

    private void checkEventSequence(List<Event> anEventList) {
        int theLastEventId = -1;
        for(Event theEvent: anEventList) {
            if(theEvent instanceof UniqueIdEvent) {
                final int theCurrentEventId = ((UniqueIdEvent)theEvent).getId();
                assertTrue("Last event: " + theLastEventId + "; Current event: " + theCurrentEventId, theLastEventId < theCurrentEventId);
                theLastEventId = theCurrentEventId;
            }
        }

        if(theLastEventId == -1 && !(anEventList.isEmpty())) {
            fail("There are no unique events available! To check the event sequence there must be events available which implement \"" + UniqueIdEvent.class.getName() + "\".");
        }
    }

    private void waitForStarts(Collection<RunningUnit> aRunningUnits) {
        for(RunningUnit theRunningUnit: aRunningUnits) {
            waitForStart(theRunningUnit.getStartObservable());
        }
    }

    private void waitForStart(StartObservable aStartObservable) {
        while(!aStartObservable.isStarted()) {
            try {
                Thread.sleep(1);
                Thread.yield();
            } catch(InterruptedException e) {
                throw new RuntimeException("Error on waiting for starting listen threads.");
            }
        }
    }

    private void waitForFinish(FinishObservable aFinishObservable) {
        while(!aFinishObservable.isFinished()) {
            try {
                Thread.sleep(1);
                Thread.yield();
            } catch(InterruptedException e) {
                throw new RuntimeException("Error on waiting for finishing listen threads.");
            }
        }
    }

    private class RunningUnit
    {
        private StartObservable myStartObservable;
        private Thread myThread;

        private RunningUnit(StartObservable aStartObservable, Thread aThread) {
            myStartObservable = aStartObservable;
            myThread = aThread;
        }

        public StartObservable getStartObservable() {
            return myStartObservable;
        }

        public Thread getThread() {
            return myThread;
        }
    }
}
