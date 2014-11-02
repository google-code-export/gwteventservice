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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.service.UserTimeoutListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 25.01.2009
 *         <br>Time: 17:09:43
 */
@RunWith(JUnit4.class)
public class UserActivitySchedulerTest
{
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USER_ID_2 = "test_user_id_2";
    private UserInfo TEST_USER_INFO;
    private UserInfo TEST_USER_INFO_2;

    private Collection<UserInfo> myUserInfoCollection;
    private UserActivityScheduler myUserActivityScheduler;

    @Before
    public void setUp() {
        myUserInfoCollection = createUserInfoCollection();
        myUserActivityScheduler = createUserActivityScheduler(myUserInfoCollection);
    }

    @After
    public void tearDown() {
        myUserActivityScheduler.removeTimeoutListeners();
        myUserActivityScheduler.stop();
    }

    @Test
    public void testSchedule() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);
        
        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(400);
        assertEquals(2, theTimeoutListener.getTimeoutCount());

        Thread.sleep(400);
        assertEquals(2, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_2() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);

        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        myUserActivityScheduler.removeTimeoutListener(theTimeoutListener);

        Thread.sleep(400);
        //no timeouts, because the timeout listener was removed before
        assertEquals(0, theTimeoutListener.getTimeoutCount());
        
        Thread.sleep(400);
        //no timeouts, because the timeout listener was removed before
        assertEquals(0, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_3() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);

        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        myUserActivityScheduler.removeTimeoutListeners();

        Thread.sleep(400);
        //no timeouts, because the timeout listener was removed before
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(400);
        //no timeouts, because the timeout listener was removed before
        assertEquals(0, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_4() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);

        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        myUserActivityScheduler.stop();

        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(400);
        //no timeouts, because scheduler is stopped
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(400);
        //no timeouts, because scheduler is stopped
        assertEquals(0, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_5() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);

        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        //Reactivate User_1
        assertTrue(myUserInfoCollection.iterator().hasNext());
        myUserActivityScheduler.reportUserActivity(myUserInfoCollection.iterator().next());

        Thread.sleep(300);
        //User_2 gets a timeout, because it isn't reactivated
        assertEquals(1, theTimeoutListener.getTimeoutCount());

        Thread.sleep(700);
        //User_1 gets a timeout, because it isn't reactivated and the last reactivation is too long ago
        assertEquals(2, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_6() throws Exception {
        final TestUserTimeoutListener theTimeoutListener = new TestUserTimeoutListener();
        myUserActivityScheduler.addTimeoutListener(theTimeoutListener);

        myUserActivityScheduler.start(false);
        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        //Reactivate User_1 and User_2
        Iterator<UserInfo> theUserInfoIterator = myUserInfoCollection.iterator();
        assertTrue(theUserInfoIterator.hasNext());
        final UserInfo theUserInfo_1 = theUserInfoIterator.next();
        assertTrue(theUserInfoIterator.hasNext());
        final UserInfo theUserInfo_2 = theUserInfoIterator.next();
        assertFalse(theUserInfoIterator.hasNext());

        myUserActivityScheduler.reportUserActivity(theUserInfo_1);
        myUserActivityScheduler.reportUserActivity(theUserInfo_2);

        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        //Reactivate User_1 and User_2
        myUserActivityScheduler.reportUserActivity(theUserInfo_1);
        myUserActivityScheduler.reportUserActivity(theUserInfo_2);

        Thread.sleep(200);
        assertEquals(0, theTimeoutListener.getTimeoutCount());

        Thread.sleep(700);
        //User_1 and User_2 get a timeout, because they aren't reactivated
        assertEquals(2, theTimeoutListener.getTimeoutCount());
    }

    @Test
    public void testSchedule_WithAutoClean() throws Exception {
        myUserActivityScheduler.start(true);
        Thread.sleep(200);
        assertEquals(2, myUserInfoCollection.size());
        assertTrue(myUserInfoCollection.contains(TEST_USER_INFO));
        assertTrue(myUserInfoCollection.contains(TEST_USER_INFO_2));

        //Reactivate User_1
        assertTrue(myUserInfoCollection.iterator().hasNext());
        myUserActivityScheduler.reportUserActivity(myUserInfoCollection.iterator().next());

        Thread.sleep(300);
        //User_2 gets a timeout, because it isn't reactivated
        assertEquals(1, myUserInfoCollection.size());
        assertTrue(myUserInfoCollection.contains(TEST_USER_INFO));

        Thread.sleep(700);
        //User_1 gets a timeout, because it isn't reactivated and the last reactivation is too long ago
        assertEquals(0, myUserInfoCollection.size());
    }

    @Test
    public void testIsActive() {
        assertFalse(myUserActivityScheduler.isActive());
        assertFalse(myUserActivityScheduler.isActive());

        myUserActivityScheduler.start(false);
        assertTrue(myUserActivityScheduler.isActive());
        assertTrue(myUserActivityScheduler.isActive());

        myUserActivityScheduler.start(false);
        assertTrue(myUserActivityScheduler.isActive());
        assertTrue(myUserActivityScheduler.isActive());

        myUserActivityScheduler.stop();
        assertFalse(myUserActivityScheduler.isActive());
        assertFalse(myUserActivityScheduler.isActive());

        myUserActivityScheduler.stop();
        assertFalse(myUserActivityScheduler.isActive());
        assertFalse(myUserActivityScheduler.isActive());

        myUserActivityScheduler.start(false);
        assertTrue(myUserActivityScheduler.isActive());
        assertTrue(myUserActivityScheduler.isActive());
    }

    @Test
    public void testGetTimeoutInterval() {
        assertEquals(400, myUserActivityScheduler.getTimeoutInterval());
        assertEquals(500, new UserActivityScheduler(new ConcurrentLinkedQueue<UserInfo>(), 500).getTimeoutInterval());
    }

    private class TestUserTimeoutListener implements UserTimeoutListener
    {
        private int myTimeoutCount;

        public void onTimeout(UserInfo aUserInfo) {
            myTimeoutCount++;
            myUserInfoCollection.remove(aUserInfo);
        }

        public int getTimeoutCount() {
            return myTimeoutCount;
        }
    }

    private Collection<UserInfo> createUserInfoCollection() {
        TEST_USER_INFO = new UserInfo(TEST_USER_ID);
        TEST_USER_INFO_2 = new UserInfo(TEST_USER_ID_2);

        Collection<UserInfo> theUserInfoCollection = new ConcurrentLinkedQueue<UserInfo>();
        theUserInfoCollection.add(TEST_USER_INFO);
        theUserInfoCollection.add(TEST_USER_INFO_2);

        return theUserInfoCollection;
    }

    private UserActivityScheduler createUserActivityScheduler(Collection<UserInfo> aUserInfoCollection) {
        return createUserActivityScheduler(aUserInfoCollection, 400);
    }

    private UserActivityScheduler createUserActivityScheduler(Collection<UserInfo> aUserInfoCollection, long aTimeoutInterval) {
        return new UserActivityScheduler(aUserInfoCollection, aTimeoutInterval);
    }
}