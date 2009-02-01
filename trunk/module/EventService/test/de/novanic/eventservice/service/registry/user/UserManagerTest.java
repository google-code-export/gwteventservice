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

import junit.framework.TestCase;

import java.util.Iterator;

import de.novanic.eventservice.service.UserTimeoutListener;

/**
 * @author sstrohschein
 *         <br>Date: 29.01.2009
 *         <br>Time: 20:15:01
 */
public class UserManagerTest extends TestCase
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USER_ID_2 = "test_user_id_2";
    private static final UserInfo TEST_USER_INFO = new UserInfo(TEST_USER_ID);
    private static final UserInfo TEST_USER_INFO_2 = new UserInfo(TEST_USER_ID_2);

    private UserManager myUserManager;

    public void setUp() {
        myUserManager = new UserManager(99999);
    }

    public void tearDown() {
        myUserManager.getUserActivityScheduler().stop();
        myUserManager.removeUsers();
    }

    public void testAddUser() {
        assertEquals(0, myUserManager.getUserCount());

        UserInfo theUserInfo = myUserManager.addUser(TEST_USER_ID);
        assertEquals(TEST_USER_ID, theUserInfo.getUserId());
        assertEquals(TEST_USER_INFO, theUserInfo);

        assertEquals(1, myUserManager.getUserCount());
    }

    public void testAddUser_2() {
        assertEquals(0, myUserManager.getUserCount());
        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertEquals(0, myUserManager.getUserCount());

        myUserManager.addUser(TEST_USER_INFO);
        assertEquals(1, myUserManager.getUserCount());

        final UserInfo theUserInfo = myUserManager.getUser(TEST_USER_ID);
        assertNotNull(theUserInfo);
        assertEquals(TEST_USER_INFO, theUserInfo);
        assertEquals(1, myUserManager.getUserCount());
    }

    public void testAddUser_3() {
        assertEquals(0, myUserManager.getUserCount());
        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertEquals(0, myUserManager.getUserCount());

        myUserManager.addUser(TEST_USER_INFO);
        assertEquals(1, myUserManager.getUserCount());

        //to add the same user shouldn't effect the number of users
        myUserManager.addUser(TEST_USER_INFO);
        assertEquals(1, myUserManager.getUserCount());

        myUserManager.addUser(TEST_USER_INFO_2);
        assertEquals(2, myUserManager.getUserCount());

        final UserInfo theUserInfo = myUserManager.getUser(TEST_USER_ID);
        assertNotNull(theUserInfo);
        assertEquals(TEST_USER_INFO, theUserInfo);

        final UserInfo theUserInfo_2 = myUserManager.getUser(TEST_USER_ID_2);
        assertNotNull(theUserInfo_2);
        assertEquals(TEST_USER_INFO_2, theUserInfo_2);
        assertFalse(theUserInfo.equals(theUserInfo_2));

        assertEquals(2, myUserManager.getUserCount());
    }

    public void testRemoveUser() {
        //test remove without users
        assertEquals(0, myUserManager.getUserCount());
        assertNull(myUserManager.getUser(TEST_USER_ID));

        assertNull(myUserManager.removeUser(TEST_USER_ID));
        assertNull(myUserManager.removeUser(TEST_USER_ID));

        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertEquals(0, myUserManager.getUserCount());

        //add two users
        UserInfo theUserInfo = myUserManager.addUser(TEST_USER_ID);
        myUserManager.addUser(TEST_USER_ID_2);
        assertEquals(2, myUserManager.getUserCount());

        //check added users
        assertEquals(theUserInfo, myUserManager.getUser(TEST_USER_ID));
        assertFalse(theUserInfo.equals(myUserManager.getUser(TEST_USER_ID_2)));
        assertEquals(2, myUserManager.getUserCount());

        //remove one user
        UserInfo theRemovedUser = myUserManager.removeUser(TEST_USER_ID);
        assertEquals(theUserInfo, theRemovedUser);
        assertEquals(1, myUserManager.getUserCount());

        //check the existing users
        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertNotNull(myUserManager.getUser(TEST_USER_ID_2));
        assertEquals(1, myUserManager.getUserCount());

        //remove the user a second time (should have no effect)
        assertNull(myUserManager.removeUser(TEST_USER_ID));
        assertEquals(1, myUserManager.getUserCount());

        //remove and check the other user
        UserInfo theRemovedUser_2 = myUserManager.removeUser(TEST_USER_ID_2);
        assertNotNull(theRemovedUser_2);
        assertFalse(theRemovedUser.equals(theRemovedUser_2));
        assertEquals(TEST_USER_INFO_2, theRemovedUser_2);
        assertEquals(0, myUserManager.getUserCount());
    }

    public void testRemoveUser_2() {
        //test remove without users
        assertEquals(0, myUserManager.getUserCount());
        assertNull(myUserManager.getUser(TEST_USER_ID));

        assertFalse(myUserManager.removeUser(TEST_USER_INFO));
        assertFalse(myUserManager.removeUser(TEST_USER_INFO));

        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertEquals(0, myUserManager.getUserCount());

        //add two users
        myUserManager.addUser(TEST_USER_INFO);
        myUserManager.addUser(TEST_USER_INFO_2);
        assertEquals(2, myUserManager.getUserCount());

        //check added users
        assertEquals(TEST_USER_INFO, myUserManager.getUser(TEST_USER_ID));
        assertFalse(TEST_USER_INFO.equals(myUserManager.getUser(TEST_USER_ID_2)));
        assertEquals(2, myUserManager.getUserCount());

        //remove one user
        assertTrue(myUserManager.removeUser(TEST_USER_INFO));
        assertEquals(1, myUserManager.getUserCount());

        //check the existing users
        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertNotNull(myUserManager.getUser(TEST_USER_ID_2));
        assertEquals(1, myUserManager.getUserCount());

        //remove the user a second time (should have no effect)
        assertFalse(myUserManager.removeUser(TEST_USER_INFO));
        assertEquals(1, myUserManager.getUserCount());

        //remove the other user
        assertTrue(myUserManager.removeUser(TEST_USER_INFO_2));
        assertEquals(0, myUserManager.getUserCount());
    }

    public void testRemoveUsers() {
        assertEquals(0, myUserManager.getUserCount());
        myUserManager.removeUsers();
        assertEquals(0, myUserManager.getUserCount());

        myUserManager.addUser(TEST_USER_INFO);
        assertEquals(1, myUserManager.getUserCount());
        myUserManager.addUser(TEST_USER_INFO_2);
        assertEquals(2, myUserManager.getUserCount());

        myUserManager.removeUsers();
        assertEquals(0, myUserManager.getUserCount());
    }

    public void testGetUsers() {
        assertNotNull(myUserManager.getUsers());
        assertEquals(0, myUserManager.getUsers().size());

        assertEquals(0, myUserManager.getUserCount());
        assertNull(myUserManager.getUser(TEST_USER_ID));
        assertEquals(0, myUserManager.getUserCount());
        assertEquals(0, myUserManager.getUsers().size());

        myUserManager.addUser(TEST_USER_INFO);
        assertEquals(1, myUserManager.getUserCount());
        assertEquals(1, myUserManager.getUsers().size());

        myUserManager.addUser(TEST_USER_INFO_2);
        assertEquals(2, myUserManager.getUserCount());
        assertEquals(2, myUserManager.getUsers().size());

        Iterator<UserInfo> theUserIterator = myUserManager.getUsers().iterator();
        assertTrue(theUserIterator.hasNext());
        UserInfo theUserInfo_1 = theUserIterator.next();
        assertTrue(theUserIterator.hasNext());
        UserInfo theUserInfo_2 = theUserIterator.next();

        //the sorting of the users is not guaranteed
        assertTrue(TEST_USER_INFO.equals(theUserInfo_1) || TEST_USER_INFO.equals(theUserInfo_2));
        assertTrue(TEST_USER_INFO_2.equals(theUserInfo_1) || TEST_USER_INFO_2.equals(theUserInfo_2));
        assertFalse(theUserInfo_1.equals(theUserInfo_2));
    }

    public void testGetUserActivityScheduler() {
        UserActivityScheduler theUserActivityScheduler = myUserManager.getUserActivityScheduler();
        assertNotNull(theUserActivityScheduler);
        assertEquals(theUserActivityScheduler, myUserManager.getUserActivityScheduler());
        assertFalse(theUserActivityScheduler.equals(new UserManager(99999).getUserActivityScheduler()));
    }

    public void testActivateUserActivityScheduler() throws Exception {
        myUserManager = new UserManager(400);

        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserManager.getUserActivityScheduler().addTimeoutListener(theTimeoutListener);

        final UserInfo theUserInfo = myUserManager.addUser(TEST_USER_ID);

        myUserManager.activateUserActivityScheduler();
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        //Reactivate User
        myUserManager.getUserActivityScheduler().reportUserActivity(theUserInfo);

        Thread.sleep(300);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(700);
        assertEquals(1, theTimeoutListener.getTimeoutCount());

        //re-add the user
        myUserManager.addUser(TEST_USER_ID);
        Thread.sleep(700);
        //second timeout recognized
        assertEquals(2, theTimeoutListener.getTimeoutCount());

        myUserManager.deactivateUserActivityScheduler();

        myUserManager.activateUserActivityScheduler();
        myUserManager.deactivateUserActivityScheduler();
    }

    private class TestUserTimeoutListener implements UserTimeoutListener
    {
        private int myTimeoutCount;

        public void onTimeout(UserInfo aUserInfo) {
            myUserManager.removeUser(aUserInfo);
            myTimeoutCount++;
        }

        public int getTimeoutCount() {
            return myTimeoutCount;
        }
    }
}