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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.service.UserTimeoutListener;
import de.novanic.eventservice.util.PlatformUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * UserActivityScheduler observes the activities of the users/clients and can report timeouts.
 * To observe the users/clients, the UserActivityScheduler must be started with the start method
 * ({@link UserActivityScheduler#start(boolean)}).
 *
 * @author sstrohschein
 *         <br>Date: 20.01.2009
 *         <br>Time: 23:30:47
 */
public class UserActivityScheduler
{
    private final Collection<UserInfo> myUserInfoCollection;
    private final Queue<UserTimeoutListener> myTimeoutListeners;
    private final long myTimeoutInterval;
    private Timer myTimer;
    private TimeoutTimerTask myTimeoutTimerTask;
    private boolean myIsAutoClean;
    private boolean isActive;

    /**
     * Creates a new UserActivityScheduler with a reference to a Collection of {@link UserInfo}.
     * @param aUserInfoCollection all UserInfo instances to observe
     * @param aTimeoutInterval milliseconds to the timeout
     */
    protected UserActivityScheduler(Collection<UserInfo> aUserInfoCollection, long aTimeoutInterval) {
        myUserInfoCollection = aUserInfoCollection;
        myTimeoutInterval = aTimeoutInterval;
        myTimeoutListeners = new ConcurrentLinkedQueue<UserTimeoutListener>();
    }

    /**
     * Adds a {@link de.novanic.eventservice.service.UserTimeoutListener} to the {@link de.novanic.eventservice.service.registry.user.UserActivityScheduler}.
     * @param aTimeoutListener listener to get reported timeouts
     */
    public void addTimeoutListener(UserTimeoutListener aTimeoutListener) {
        myTimeoutListeners.add(aTimeoutListener);
    }

    /**
     * Removes a {@link de.novanic.eventservice.service.UserTimeoutListener} from the {@link de.novanic.eventservice.service.registry.user.UserActivityScheduler}.
     * @param aTimeoutListener listener to get reported timeouts
     */
    public void removeTimeoutListener(UserTimeoutListener aTimeoutListener) {
        myTimeoutListeners.remove(aTimeoutListener);
    }

    /**
     * Removes all {@link de.novanic.eventservice.service.UserTimeoutListener} from the {@link de.novanic.eventservice.service.registry.user.UserActivityScheduler}.
     */
    public void removeTimeoutListeners() {
        myTimeoutListeners.clear();
    }

    /**
     * That method starts the UserActivityScheduler to observe the users/clients. The activities will be checked like
     * the timeout interval is configured ({@link de.novanic.eventservice.config.EventServiceConfiguration#getTimeoutTime()}).
     * @param isAutoClean when set to true, the users/clients are removed automatically on timeout
     */
    public void start(boolean isAutoClean) {
        if(!isActive) {
            myIsAutoClean = isAutoClean;
            myTimer = new Timer("GWTEventService-UserActivityScheduler", true);
            myTimeoutTimerTask = new TimeoutTimerTask();
            isActive = true;
            schedule(myTimer, myTimeoutTimerTask, myTimeoutInterval);
        }
    }

    /**
     * Stops the UserActivityScheduler. At that point, the users/clients won't be observed anymore.
     */
    public void stop() {
        if(isActive) {
            isActive = false;

            if(myTimer != null) {
                myTimer.cancel();
            }
            if(myTimeoutTimerTask != null) {
                myTimeoutTimerTask.cancel();
            }
            if(myTimer != null) {
                myTimer.purge();
            }
        }
    }

    /**
     * Returns the state of the UserActivityScheduler. Returns true when the UserActivityScheduler is started
     * ({@link UserActivityScheduler#start(boolean)}) and false when the UserActivityScheduler isn't started or stopped
     * ({@link de.novanic.eventservice.service.registry.user.UserActivityScheduler#stop()}).
     * @return true when running/started, false when running (not started or stopped)
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns the configured timeout time/interval. The user activity is checked in that interval.
     * @return timeout time/interval
     */
    public long getTimeoutInterval() {
        return myTimeoutInterval;
    }

    /**
     * That method must be called to report a user activity and protects the user from a timeout for the time of the
     * timeout interval ({@link de.novanic.eventservice.config.EventServiceConfiguration#getTimeoutTime()}).
     * @param aUserInfo user/client to refresh
     */
    public void reportUserActivity(UserInfo aUserInfo) {
        aUserInfo.reportUserActivity();
    }

    /**
     * That method is used to measure the timeout and it will remove the user automatically, if configured with the start method
     * ({@link de.novanic.eventservice.service.registry.user.UserActivityScheduler#start(boolean)}).
     * @param aTimer timer to schedule
     * @param aTimeoutTimerTask TimeoutTimerTask
     * @param aTimeoutInterval interval to check for timeouts
     */
    private void schedule(Timer aTimer, TimeoutTimerTask aTimeoutTimerTask, long aTimeoutInterval) {
        aTimer.schedule(aTimeoutTimerTask, 0L, aTimeoutInterval);
    }

    /**
     * The TimeoutTimerTask runs in the interval which is configured with {@link de.novanic.eventservice.config.EventServiceConfiguration#getTimeoutTime()}.
     * All added {@link de.novanic.eventservice.service.UserTimeoutListener} get informed about the occurred timeout
     * with the execution of that {@link java.util.TimerTask}. The TimeoutTimerTask starts when the UserActivityScheduler
     * is started ({@link de.novanic.eventservice.service.registry.user.UserActivityScheduler#start(boolean)}) and stops when
     * the UserActivityScheduler is stopped ({@link de.novanic.eventservice.service.registry.user.UserActivityScheduler#stop()}).
     */
    private class TimeoutTimerTask extends TimerTask
    {
        public void run() {
            final long theTimeoutCriteriaTime = PlatformUtil.getCurrentTime() - myTimeoutInterval;

            Iterator<UserInfo> theUserInfoIterator = myUserInfoCollection.iterator();
            while(theUserInfoIterator.hasNext()) {
                UserInfo theUserInfo = theUserInfoIterator.next();
                if(isTimeout(theUserInfo, theTimeoutCriteriaTime)) {
                    //report about user timeout
                    for(UserTimeoutListener theTimeoutListener: myTimeoutListeners) {
                        theTimeoutListener.onTimeout(theUserInfo);
                    }
                    //remove the user/client automatically if auto-clean is switched on
                    if(myIsAutoClean) {
                        theUserInfoIterator.remove();
                    }
                }
            }
        }

        /**
         * Reports a timeout when {@link UserInfo#getLastActivityTime()} is greater than the max. timeout time (the
         * current time + {@link de.novanic.eventservice.config.EventServiceConfiguration#getTimeoutTime()}).  
         * @param aUserInfo user/client to check for a timeout
         * @param aTimeoutCriteriaTime timeout time to compare {@link UserInfo#getLastActivityTime()}
         * @return True if the {@link UserInfo#getLastActivityTime()} is too long ago, otherwise true
         */
        private boolean isTimeout(UserInfo aUserInfo, long aTimeoutCriteriaTime) {
            return (aUserInfo.getLastActivityTime() <= aTimeoutCriteriaTime);
        }
    }
}
