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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The UserManager is a container for {@link de.novanic.eventservice.service.registry.user.UserInfo} and provides various
 * methods to manage users. To activate the user timeout recognition, the method {@link UserManager#activateUserActivityScheduler()})
 * must be called. The UserManager can be created with {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(long)})
 * as a singleton.
 *
 * @author sstrohschein
 *         <br>Date: 27.01.2009
 *         <br>Time: 22:05:42
 */
public class DefaultUserManager implements UserManager
{
    private Map<String, UserInfo> myUserMap;
    private UserActivityScheduler myUserActivityScheduler;

    /**
     * Creates a new UserManager. To create the UserManager as a singleton (default), the UserManager can be created with
     * {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(long)})
     * @param aTimeoutInterval timeout interval (is only required if the {@link de.novanic.eventservice.service.registry.user.UserActivityScheduler}
     * needs to be started.
     */
    public DefaultUserManager(long aTimeoutInterval) {
        myUserMap = new ConcurrentHashMap<String, UserInfo>();
        myUserActivityScheduler = new UserActivityScheduler(myUserMap.values(), aTimeoutInterval);
    }

    /**
     * Creates and adds the {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id.
     * @param aUserId id of the user to add
     * @return created {@link de.novanic.eventservice.service.registry.user.UserInfo}
     */
    public UserInfo addUser(String aUserId) {
        UserInfo theUserInfo = getUser(aUserId);
        if(theUserInfo == null) {
            theUserInfo = new UserInfo(aUserId);
        }
        addUser(theUserInfo);
        return theUserInfo;
    }

    /**
     * Adds the {@link de.novanic.eventservice.service.registry.user.UserInfo} to the UserManager.
     * @param aUserInfo {@link de.novanic.eventservice.service.registry.user.UserInfo} to add
     */
    public void addUser(UserInfo aUserInfo) {
        myUserMap.put(aUserInfo.getUserId(), aUserInfo);
    }

    /**
     * Removes the {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id.
     * @param aUserId user id of the {@link de.novanic.eventservice.service.registry.user.UserInfo} to remove
     * @return removed {@link de.novanic.eventservice.service.registry.user.UserInfo}
     */
    public UserInfo removeUser(String aUserId) {
        return myUserMap.remove(aUserId);
    }

    /**
     * Removes the {@link de.novanic.eventservice.service.registry.user.UserInfo}.
     * @param aUserInfo {@link de.novanic.eventservice.service.registry.user.UserInfo} to remove
     * @return true if it had an effect, otherwise false
     */
    public boolean removeUser(UserInfo aUserInfo) {
        return (removeUser(aUserInfo.getUserId()) != null);
    }

    /**
     * Removes all added {@link UserInfo} objects.
     */
    public void removeUsers() {
        myUserMap.clear();
    }

    /**
     * Returns the {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id. It returns NULL when no
     * {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id is added.
     * @param aUserId user id of the requested {@link de.novanic.eventservice.service.registry.user.UserInfo}
     * @return {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id. NULL when no
     * {@link de.novanic.eventservice.service.registry.user.UserInfo} for the user id is added.
     */
    public UserInfo getUser(String aUserId) {
        if(aUserId != null) {
            return myUserMap.get(aUserId);
        }
        return null;
    }

    /**
     * Returns the count of the added {@link de.novanic.eventservice.service.registry.user.UserInfo} objects.
     * @return count of the added {@link de.novanic.eventservice.service.registry.user.UserInfo} objects
     */
    public int getUserCount() {
        return myUserMap.size();
    }

    /**
     * Returns all added {@link de.novanic.eventservice.service.registry.user.UserInfo} objects. It returns an empty
     * {@link java.util.Collection} when no
     * {@link de.novanic.eventservice.service.registry.user.UserInfo} objects are added.
     * @return all added {@link de.novanic.eventservice.service.registry.user.UserInfo} objects
     */
    public Collection<UserInfo> getUsers() {
        return myUserMap.values();
    }

    /**
     * Activates the {@link UserActivityScheduler} to observe the user activities. When the users/clients should be
     * removed automatically, please use {@link de.novanic.eventservice.service.registry.user.UserManager#activateUserActivityScheduler(boolean)}.
     */
    public void activateUserActivityScheduler() {
        activateUserActivityScheduler(false);
    }

    /**
     * Activates the {@link UserActivityScheduler} to observe the user activities.
     * @param isAutoClean when set to true, the users/clients are removed automatically on timeout
     */
    public void activateUserActivityScheduler(boolean isAutoClean) {
        myUserActivityScheduler.start(isAutoClean);
    }

    /**
     * Deactivates the {@link UserActivityScheduler}. See {@link UserActivityScheduler} for more information.
     */
    public void deactivateUserActivityScheduler() {
        myUserActivityScheduler.stop();
    }

    /**
     * Returns the {@link UserActivityScheduler} which is instantiated with the UserManager. The method
     * {@link UserManager#activateUserActivityScheduler()} must be called to start the {@link UserActivityScheduler}.
     * @return the {@link UserActivityScheduler} which is instantiated with the UserManager
     */
    public UserActivityScheduler getUserActivityScheduler() {
        return myUserActivityScheduler;
    }
}