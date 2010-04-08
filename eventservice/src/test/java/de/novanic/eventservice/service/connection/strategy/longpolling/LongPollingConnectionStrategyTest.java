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
package de.novanic.eventservice.service.connection.strategy.longpolling;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 07.04.2010
 *         <br>Time: 23:11:00
 */
public class LongPollingConnectionStrategyTest extends TestCase
{
    public void testGetServerConnector() {
        assertNull(new LongPollingConnectionStrategy().getServerConnector());
    }

    public void testGetClientConnector() {
        assertNull(new LongPollingConnectionStrategy().getClientConnector());
    }
}