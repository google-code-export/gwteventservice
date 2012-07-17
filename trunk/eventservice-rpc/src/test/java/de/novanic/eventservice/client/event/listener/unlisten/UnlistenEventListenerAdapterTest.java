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
package de.novanic.eventservice.client.event.listener.unlisten;

import de.novanic.eventservice.client.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 16.08.2009
 *         <br>Time: 01:49:33
 */
@RunWith(JUnit4.class)
public class UnlistenEventListenerAdapterTest
{
    @Test
    public void testApply() {
        DummyUnlistenEventListener theUnlistenEventListenerAdapter = new DummyUnlistenEventListener();

        theUnlistenEventListenerAdapter.apply(new Event() {});
        assertFalse(theUnlistenEventListenerAdapter.isOnUnlistenCalled);

        theUnlistenEventListenerAdapter.apply(new Event() {});
        assertFalse(theUnlistenEventListenerAdapter.isOnUnlistenCalled);

        theUnlistenEventListenerAdapter.apply(new DefaultUnlistenEvent());
        assertTrue(theUnlistenEventListenerAdapter.isOnUnlistenCalled);
    }

    @Test
    public void testScopes() {
        assertEquals("LOCAL", UnlistenEventListener.Scope.LOCAL.name());
        assertEquals("LOCAL", UnlistenEventListener.Scope.LOCAL.toString());

        assertEquals("UNLISTEN", UnlistenEventListener.Scope.UNLISTEN.name());
        assertEquals("UNLISTEN", UnlistenEventListener.Scope.UNLISTEN.toString());

        assertEquals("TIMEOUT", UnlistenEventListener.Scope.TIMEOUT.name());
        assertEquals("TIMEOUT", UnlistenEventListener.Scope.TIMEOUT.toString());
    }

    private class DummyUnlistenEventListener extends UnlistenEventListenerAdapter
    {
        private boolean isOnUnlistenCalled;

        public void onUnlisten(UnlistenEvent anUnlistenEvent) {
            super.onUnlisten(anUnlistenEvent);
            isOnUnlistenCalled = true;
        }
    }
}