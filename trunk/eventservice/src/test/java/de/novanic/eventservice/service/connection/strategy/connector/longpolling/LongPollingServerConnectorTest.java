/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.service.connection.strategy.connector.longpolling;

import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnector;
import de.novanic.eventservice.service.connection.strategy.connector.ServerEventConnectorTest;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.test.testhelper.DummyEvent;

import java.util.Date;

/**
 * @author sstrohschein
 *         <br>Date: 16.03.2010
 *         <br>Time: 20:40:34
 */
public class LongPollingServerConnectorTest extends ServerEventConnectorTest
{
    public void testListen() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ConnectionStrategyServerConnector theLongPollingListener = new LongPollingServerConnector(createConfiguration(0, 2000, 90000));

        ListenRunnable theListenRunnable = new ListenRunnable(theLongPollingListener, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() < 500);
    }

    public void testListen_Min_Waiting() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ConnectionStrategyServerConnector theLongPollingListener = new LongPollingServerConnector(createConfiguration(500, 2000, 90000));

        ListenRunnable theListenRunnable = new ListenRunnable(theLongPollingListener, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() > 400); //could be get a little bit lesser than the specified min. waiting time (sleep) caused by accuracies of the OS and JDK implementations. 
    }

    public void testListen_Min_Waiting_Interrupted() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");
        ConnectionStrategyServerConnector theLongPollingListener = new LongPollingServerConnector(createConfiguration(1000, 2000, 90000));

        ListenRunnable theListenRunnable = new ListenRunnable(theLongPollingListener, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);

        Date theStartTime = new Date();
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        //wait to ensure that the connector is waiting
        Thread.sleep(200);

        //interrupt min. waiting
        theListenThread.interrupt();

        theListenThread.join();

        EventServiceException theOccurredException = theListenRunnable.getOccurredException();
        assertNotNull(theOccurredException);
        assertTrue(theOccurredException.getCause() instanceof InterruptedException);

        Date theEndTime = new Date();
        assertTrue((theEndTime.getTime() - theStartTime.getTime()) < 1000);
    }

    public void testListen_Max_Waiting() throws Exception {
        final UserInfo theUserInfo = new UserInfo("test_user");

        ConnectionStrategyServerConnector theLongPollingListener = new LongPollingServerConnector(createConfiguration(0, 500, 90000));

        ListenRunnable theListenRunnable = new ListenRunnable(theLongPollingListener, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();
        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(0, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 400);
    }
}
