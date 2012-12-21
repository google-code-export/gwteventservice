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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.service.connection.id.SessionConnectionIdGeneratorTest;
import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.test.testhelper.factory.FactoryResetService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 01.02.2009
 *         <br>Time: 21:48:37
 */
@RunWith(JUnit4.class)
public class UserManagerFactoryTest
{
    private UserManagerFactory myUserManagerFactory;

    @Before
    public void setUp() {
        FactoryResetService.resetFactory(UserManagerFactory.class);
        myUserManagerFactory = UserManagerFactory.getInstance();
    }

    @After
    public void tearDown() {
        FactoryResetService.resetFactory(UserManagerFactory.class);
    }

    @Test
    public void testGetInstance() {
        UserManagerFactory theEventRegistryFactory = UserManagerFactory.getInstance();
        assertSame(theEventRegistryFactory, UserManagerFactory.getInstance());
        assertSame(theEventRegistryFactory, UserManagerFactory.getInstance());
        
        FactoryResetService.resetFactory(UserManagerFactory.class);

        UserManagerFactory theEventRegistryFactory_2 = UserManagerFactory.getInstance();
        assertNotSame(theEventRegistryFactory, theEventRegistryFactory_2);
        assertSame(theEventRegistryFactory_2, UserManagerFactory.getInstance());
    }

    @Test
    public void testGetUserManager() {
        try {
            myUserManagerFactory.getUserManager();
            fail("ConfigurationException expected!");
        } catch(ConfigurationException e) {}
    }

    @Test
    public void testGetUserManager_2() {
        final int theTimeoutInterval = 99999;

        UserManager theUserManager = myUserManagerFactory.getUserManager(theTimeoutInterval);
        assertNotNull(theUserManager);
        assertSame(theUserManager, myUserManagerFactory.getUserManager(theTimeoutInterval));

        checkUserManager(theTimeoutInterval);
    }

    @Test
    public void testGetUserManager_3() {
        final int theTimeoutInterval = 77777;

        UserManager theUserManager = myUserManagerFactory.getUserManager(new TestEventServiceConfiguration(theTimeoutInterval));
        assertNotNull(theUserManager);
        assertSame(theUserManager, myUserManagerFactory.getUserManager(theTimeoutInterval));

        checkUserManager(theTimeoutInterval);
    }

    private void checkUserManager(long aTimeoutTime) {
        UserManager theUserManager = UserManagerFactory.getInstance().getUserManager();

        assertEquals(0, theUserManager.getUserCount());
        Collection<UserInfo> theUserInfoCollection = theUserManager.getUsers();
        assertNotNull(theUserInfoCollection);
        assertTrue(theUserInfoCollection.isEmpty());

        UserActivityScheduler theUserActivityScheduler = theUserManager.getUserActivityScheduler();
        assertNotNull(theUserActivityScheduler);
        assertEquals(aTimeoutTime, theUserActivityScheduler.getTimeoutInterval());
    }

    private class TestEventServiceConfiguration implements EventServiceConfiguration
    {
        private final int myTimeoutTime;

        private TestEventServiceConfiguration(int aTimeoutTime) {
            myTimeoutTime = aTimeoutTime;
        }

        public String getConfigDescription() {
            return TestEventServiceConfiguration.class.getName();
        }

        public Integer getMinWaitingTime() {
            return 0;
        }

        public Integer getMaxWaitingTime() {
            return 0;
        }

        public Integer getTimeoutTime() {
            return myTimeoutTime;
        }

        public Integer getReconnectAttemptCount() {
            return 0;
        }

        public String getConnectionIdGeneratorClassName() {
            return SessionConnectionIdGeneratorTest.class.getName();
        }

        public String getConnectionStrategyClientConnectorClassName() {
            return null;
        }

        public String getConnectionStrategyServerConnectorClassName() {
            return null;
        }

        public String getConnectionStrategyEncoding() {
            return "utf-8";
        }

        public Integer getMaxEvents() {
            return 1000;
        }

        public Map<ConfigParameter, Object> getConfigMap() {
            return new HashMap<ConfigParameter, Object>();
        }
    }
}
