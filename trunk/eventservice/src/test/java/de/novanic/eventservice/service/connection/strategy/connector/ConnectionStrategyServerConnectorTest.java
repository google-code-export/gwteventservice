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
package de.novanic.eventservice.service.connection.strategy.connector;

import de.novanic.eventservice.EventServiceTestCase;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.registry.user.UserInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 16.03.2010
 *         <br>Time: 23:21:05
 */
public abstract class ConnectionStrategyServerConnectorTest extends EventServiceTestCase
{
    public void testGetEncoding(Class<? extends ConnectionStrategyServerConnectorAdapter> aConnectorClass) throws Exception {
        Constructor<? extends ConnectionStrategyServerConnectorAdapter> theConstructor = aConnectorClass.getConstructor(EventServiceConfiguration.class);
        theConstructor.newInstance(createConfiguration(0, 30000, 90000));
        assertEquals("utf-8", ConnectionStrategyServerConnectorAdapter.getEncoding());
    }

    public void testGetEncoding_Error(Class<? extends ConnectionStrategyServerConnectorAdapter> aConnectorClass) throws Exception {
        final EventServiceConfiguration theConfiguration = createConfiguration(0, 30000, 90000);
        theConfiguration.getConfigMap().put(ConfigParameter.CONNECTION_STRATEGY_ENCODING, null);
        try {
            Constructor<? extends ConnectionStrategyServerConnectorAdapter> theConstructor = aConnectorClass.getConstructor(EventServiceConfiguration.class);
            theConstructor.newInstance(theConfiguration);
            ConnectionStrategyServerConnectorAdapter.getEncoding();
            fail("Exception expected, because the encoding isn't defined!");
        } catch(InvocationTargetException e) {
            assertTrue(e.getCause() instanceof EventServiceException);
            assertNotNull(e.getCause().getMessage());
        } catch(EventServiceException e) {
            assertNotNull(e.getMessage());
        }
    }

    protected class ListenRunnable implements Runnable
    {
        private ConnectionStrategyServerConnector myServerEventListener;
        private UserInfo myUserInfo;
        private ListenResult myListenResult;
        private EventServiceException myOccurredException;

        public ListenRunnable(ConnectionStrategyServerConnector aServerEventListener, UserInfo aUserInfo) {
            myServerEventListener = aServerEventListener;
            myUserInfo = aUserInfo;
        }

        public void run() {
            try {
                final long theStartTime = System.currentTimeMillis();
                List<DomainEvent> theEvents = myServerEventListener.listen(myUserInfo);
                final int theDuration = ((int)(System.currentTimeMillis() - theStartTime));
                myListenResult = new ListenResult(theEvents, theDuration);
            } catch(EventServiceException e) {
                myOccurredException = e;
            }
        }

        public ListenResult getListenResult() {
            return myListenResult;
        }

        public EventServiceException getOccurredException() {
            return myOccurredException;
        }
    }

    protected class ListenResult
    {
        private List<DomainEvent> myEvents;
        private int myDuration;

        private ListenResult(List<DomainEvent> aEvents, int aDuration) {
            myEvents = aEvents;
            myDuration = aDuration;
        }

        public List<DomainEvent> getEvents() {
            return myEvents;
        }

        public int getDuration() {
            return myDuration;
        }
    }
}
