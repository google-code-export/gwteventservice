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
package de.novanic.eventservice;

import com.google.gwt.junit.tools.GWTTestSuite;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.GwtTestGWTStreamingClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.specific.GwtTestGWTStreamingClientConnectorGecko;
import de.novanic.eventservice.client.event.GwtTestRemoteEventService;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author sstrohschein
 *         <br>Date: 14.08.2010
 *         <br>Time: 12:05:58
 */
public class GWTEventServiceGWTTestSuite extends TestSuite
{
    public static Test suite() {
        GWTTestSuite theGWTEventServiceGWTTestSuite = new GWTTestSuite("GWTEventService - GWT-Tests");

        // --- GWT-Tests ---

        // Event
        theGWTEventServiceGWTTestSuite.addTestSuite(GwtTestRemoteEventService.class);

        // Connection
        theGWTEventServiceGWTTestSuite.addTestSuite(GwtTestGWTStreamingClientConnector.class);
        theGWTEventServiceGWTTestSuite.addTestSuite(GwtTestGWTStreamingClientConnectorGecko.class);

        return theGWTEventServiceGWTTestSuite;
    }
}