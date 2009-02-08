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
package de.novanic.eventservice.test.testhelper;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @author sstrohschein
 * <br>Date: 17.08.2008
 * <br>Time: 23:09:11
 */
public class AddEventRunnable implements Runnable
{
    private EventExecutorService myEventExecutorService;
    private Domain myDomain;
    private long myWaitingTime;

    private AddEventRunnable(long aWaitingTime) {
        init("internalTestUser", aWaitingTime);
    }

    public AddEventRunnable(Domain aDomain, long aWaitingTime) {
        this(aWaitingTime);
        myDomain = aDomain;
    }

    public AddEventRunnable(String aUser, long aWaitingTime) {
        init(aUser, aWaitingTime);
    }

    private void init(String aUser, long aWaitingTime) {
        myWaitingTime = aWaitingTime;
        EventExecutorServiceFactory.reset();
        final EventExecutorServiceFactory theEventExecutorServiceFactory = EventExecutorServiceFactory.getInstance();
        myEventExecutorService = theEventExecutorServiceFactory.getEventExecutorService(new HttpSessionDummy(aUser));
    }

    public void run() {
        try {
            Thread.sleep(myWaitingTime);
        } catch(InterruptedException e) {
            throw new RuntimeException("Sleep of " + AddEventRunnable.class.getName() + " aborted!", e);
        }
        if(myDomain != null) {
            myEventExecutorService.addEvent(myDomain, new DummyEvent());
        } else {
            myEventExecutorService.addEventUserSpecific(new DummyEvent());
        }
    }

    private class HttpSessionDummy implements HttpSession
    {
        private String myUser;

        public HttpSessionDummy(String aUser) {
            myUser = aUser;
        }

        public long getCreationTime() {
            return 0;
        }

        public String getId() {
            return myUser;
        }

        public long getLastAccessedTime() {
            return 0;
        }

        public ServletContext getServletContext() {
            return null;
        }

        public void setMaxInactiveInterval(int i) {}

        public int getMaxInactiveInterval() {
            return 0;
        }

        public HttpSessionContext getSessionContext() {
            return null;
        }

        public Object getAttribute(String s) {
            return null;
        }

        public Object getValue(String s) {
            return null;
        }

        public Enumeration getAttributeNames() {
            return null;
        }

        public String[] getValueNames() {
            return new String[0];
        }

        public void setAttribute(String s, Object o) {}

        public void putValue(String s, Object o) {}

        public void removeAttribute(String s) {}

        public void removeValue(String s) {}

        public void invalidate() {}

        public boolean isNew() {
            return false;
        }
    }
}