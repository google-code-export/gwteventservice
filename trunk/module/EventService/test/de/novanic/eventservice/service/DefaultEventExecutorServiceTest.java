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
package de.novanic.eventservice.service;

import de.novanic.eventservice.service.exception.NoSessionAvailableException;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.filter.EventFilter;

/**
 * @author sstrohschein
 * Date: 05.08.2008
 * <br>Time: 11:15:02
 */
public class DefaultEventExecutorServiceTest extends EventExecutorServiceTest_A
{
    public EventExecutorService initEventExecutorService() {
        return new DefaultEventExecutorService(TEST_USER_ID);
    }

    public void testAddEvent_SessionLess() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.addEvent(DomainFactory.getDomain("X"), new TestEvent());
        } catch(NoSessionAvailableException e) {
            fail("No Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        }
    }

    public void testIsUserRegistered_Error() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.isUserRegistered();
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void testIsUserRegistered_2_Error() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.isUserRegistered(DomainFactory.getDomain("X"));
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void testAddEventUserSpecific_Error() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.addEventUserSpecific(new TestEvent());
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void testSetEventFilter_Error() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.setEventFilter(DomainFactory.getDomain("X"), new TestEventFilter());
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    public void testRemoveEventFilter_Error() {
        EventExecutorService theEventExecutorService = new DefaultEventExecutorService(null);
        try {
            theEventExecutorService.removeEventFilter(DomainFactory.getDomain("X"));
            fail("Exception \"" + NoSessionAvailableException.class.getName() + "\" expected!");
        } catch(NoSessionAvailableException e) {
            assertEquals("There is no session / client information available!", e.getMessage());
            assertNull(e.getCause());
        }
    }

    private class TestEvent implements Event {}

    private class TestEventFilter implements EventFilter
    {
        public boolean match(Event anEvent) {
            return false;
        }
    }
}