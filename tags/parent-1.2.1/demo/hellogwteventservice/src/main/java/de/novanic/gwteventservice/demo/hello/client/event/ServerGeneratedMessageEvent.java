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
package de.novanic.gwteventservice.demo.hello.client.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author sstrohschein
 *         <br>Date: 19.02.2010
 *         <br>Time: 23:19:03
 */
public class ServerGeneratedMessageEvent implements Event
{
    public static final Domain SERVER_MESSAGE_DOMAIN = DomainFactory.getDomain("server_message_domain");

    private String myServerGeneratedMessage;

    /**
     * Needed for serialization
     */
    public ServerGeneratedMessageEvent() {}

    public ServerGeneratedMessageEvent(String aServerGeneratedMessage) {
        myServerGeneratedMessage = aServerGeneratedMessage;
    }

    public String getServerGeneratedMessage() {
        return myServerGeneratedMessage;
    }

    public String toString() {
        StringBuilder theStringBuilder = new StringBuilder(100);
        theStringBuilder.append(ServerGeneratedMessageEvent.class.getName());
        theStringBuilder.append(new char[] {' ', '('});
        theStringBuilder.append(myServerGeneratedMessage);
        theStringBuilder.append(')');
        return theStringBuilder.toString();        
    }
}