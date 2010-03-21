/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.client.event.listener.unlisten;

import junit.framework.TestCase;
import de.novanic.eventservice.client.event.Event;

/**
 * @author sstrohschein
 *         <br>Date: 16.08.2009
 *         <br>Time: 01:49:33
 */
public class UnlistenEventListenerAdapterTest extends TestCase
{
    public void testApply() {
        DummyUnlistenEventListener theUnlistenEventListenerAdapter = new DummyUnlistenEventListener();

        theUnlistenEventListenerAdapter.apply(new Event() {});
        assertFalse(theUnlistenEventListenerAdapter.isOnUnlistenCalled);

        theUnlistenEventListenerAdapter.apply(new Event() {});
        assertFalse(theUnlistenEventListenerAdapter.isOnUnlistenCalled);

        theUnlistenEventListenerAdapter.apply(new DefaultUnlistenEvent());
        assertTrue(theUnlistenEventListenerAdapter.isOnUnlistenCalled);
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