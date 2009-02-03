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

import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.loader.ConfigurationException;

/**
 * The UserManagerFactory is used to create the UserManager and to ensure that only one instance of
 * UserManagerFactory and UserManager exists (singleton).
 * @see de.novanic.eventservice.service.registry.user.UserManagerFactory
 *
 * @author sstrohschein
 *         <br>Date: 01.02.2009
 *         <br>Time: 18:17:58
 */
public class UserManagerFactory
{
    private static UserManagerFactory myInstance;
    private UserManager myUserManager;

    private UserManagerFactory() {}

    /**
     * This method should be used to create an instance of UserManagerFactory.
     * UserManagerFactory is a singleton, so this method returns always the same instance of UserManagerFactory.
     * @return UserManagerFactory (singleton)
     */
    public static synchronized UserManagerFactory getInstance() {
        if(myInstance == null) {
            myInstance = new UserManagerFactory();
        }
        return myInstance;
    }

    /**
     * Returns the {@link de.novanic.eventservice.service.registry.user.UserManager} as a singleton. It is important that
     * the {@link de.novanic.eventservice.service.registry.user.UserManager} must be initiated first. That can be done by
     * creating a {@link de.novanic.eventservice.service.registry.user.UserManager} with a configuration:
     * {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(de.novanic.eventservice.config.EventServiceConfiguration)}
     * or {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(long)}. When the
     * {@link de.novanic.eventservice.service.registry.user.UserManager} isn't initiated the method throws a
     * {@link de.novanic.eventservice.config.loader.ConfigurationException}.
     * @return {@link de.novanic.eventservice.service.registry.user.UserManager} (singleton)
     * @throws ConfigurationException thrown when the {@link de.novanic.eventservice.service.registry.user.UserManager} isn't
     * initiated first with {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(de.novanic.eventservice.config.EventServiceConfiguration)}
     * or {@link de.novanic.eventservice.service.registry.user.UserManagerFactory#getUserManager(long)}
     */
    public synchronized UserManager getUserManager() {
        if(myUserManager != null) {
            return myUserManager;
        }
        throw new ConfigurationException("The UserManager isn't configured! It is necessary to create an instance of " +
                "UserManager first. That can be done by calling a factory method of UserManagerFactory with a configuration.");
    }

    /**
     * Returns the {@link de.novanic.eventservice.service.registry.user.UserManager} as a singleton.
     * @param aConfiguration {@link EventServiceConfiguration} used to read the timeout time/interval.
     * @return {@link de.novanic.eventservice.service.registry.user.UserManager} (singleton)
     */
    public synchronized UserManager getUserManager(EventServiceConfiguration aConfiguration) {
        return getUserManager(aConfiguration.getTimeoutTime());
    }

    /**
     * Returns the {@link de.novanic.eventservice.service.registry.user.UserManager} as a singleton.
     * @param aTimeoutInterval
     * @return {@link de.novanic.eventservice.service.registry.user.UserManager} (singleton)
     */
    public synchronized UserManager getUserManager(long aTimeoutInterval) {
        if(myUserManager == null) {
            myUserManager = new DefaultUserManager(aTimeoutInterval);
        }
        return myUserManager;
    }

    /**
     * This method should only be used in TestCases, because it resets the factory and the factory can't ensure
     * anymore that only one instance exists!
     */
    public static synchronized void reset() {
        myInstance = null;
    }
}