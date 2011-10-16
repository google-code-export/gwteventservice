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
package de.novanic.gwteventservice.demo.hello.server;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.service.RemoteEventServiceServlet;
import de.novanic.gwteventservice.demo.hello.client.ServerMessageGeneratorService;
import de.novanic.gwteventservice.demo.hello.client.event.ServerGeneratedMessageEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author sstrohschein
 *         <br>Date: 19.02.2010
 *         <br>Time: 23:21:42
 */
public class ServerMessageGeneratorServiceImpl extends RemoteEventServiceServlet implements ServerMessageGeneratorService
{
    private static Timer myEventGeneratorTimer;
    
    public synchronized void start() {
        if(myEventGeneratorTimer == null) {
            myEventGeneratorTimer = new Timer();
            myEventGeneratorTimer.schedule(new ServerMessageGeneratorTimerTask(), 0, 5000);
        }
    }

    private class ServerMessageGeneratorTimerTask extends TimerTask
    {
        public void run() {
            final String theEventMessage = "GWTEventService is greeting everybody with \"Hello\" at " + getCurrentTimeFormatted() + " (and every five seconds again)! :-)";
            //create the event
            Event theEvent = new ServerGeneratedMessageEvent(theEventMessage);
            //add the event, so clients can receive it
            addEvent(ServerGeneratedMessageEvent.SERVER_MESSAGE_DOMAIN, theEvent);
        }
    }

    private static String getCurrentTimeFormatted() {
        SimpleDateFormat theDateFormat = new SimpleDateFormat("HH:mm:ss");
        return theDateFormat.format(Calendar.getInstance().getTime());
    }
}