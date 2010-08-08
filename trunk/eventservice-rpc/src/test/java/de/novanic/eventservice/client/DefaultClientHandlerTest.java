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
package de.novanic.eventservice.client;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 02.08.2010
 *         <br>Time: 19:20:26
 */
public class DefaultClientHandlerTest extends TestCase
{
    public void testGetConnectionId() {
        ClientHandler theClientHandler = new DefaultClientHandler("XY1234");
        assertEquals("XY1234", theClientHandler.getConnectionId());

        theClientHandler = new DefaultClientHandler("XY12345");
        assertFalse("XY1234".equals(theClientHandler.getConnectionId()));
    }
}