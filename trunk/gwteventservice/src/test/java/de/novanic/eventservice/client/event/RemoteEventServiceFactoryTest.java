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
package de.novanic.eventservice.client.event;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 * Date: 16.08.2008
 * Time: 20:37:44
 */
public class RemoteEventServiceFactoryTest extends TestCase
{
    private static final String ERROR_MESSAGE = "Exception expected, because GWT can not be initialized in MockTest!";

    public void testReset() {
        try {
            RemoteEventServiceFactory.reset();
            fail(ERROR_MESSAGE);
        } catch(Throwable e) {}

        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        try {
            theRemoteEventServiceFactory.getRemoteEventService();
            fail(ERROR_MESSAGE);
        } catch(Throwable e) {}

        try {
            RemoteEventServiceFactory.reset();
            fail(ERROR_MESSAGE);
        } catch(Throwable e) {}
    }

    public void testFactory() {
        RemoteEventServiceFactory theRemoteEventServiceFactory = RemoteEventServiceFactory.getInstance();
        assertSame(theRemoteEventServiceFactory, RemoteEventServiceFactory.getInstance());

        try {
            theRemoteEventServiceFactory.getRemoteEventService();
            fail(ERROR_MESSAGE);
        } catch(Throwable e) {}
    }
}