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
package de.novanic.eventservice.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.tools.GWTTestSuite;

import de.novanic.eventservice.GWTEventServiceTestSuite;
import de.novanic.eventservice.client.event.RemoteEventServiceTest;

/**
 * @author sstrohschein
 * Date: 28.07.2008   
 * Time: 21:41:04
 */
public class GWTEventServiceGWTTestSuite extends GWTTestCase
{
    public String getModuleName() {
    	return null;
    }

    public static GWTTestSuite suite() {
        GWTTestSuite theGWTEventServiceTestSuite = new GWTTestSuite("GWTEventService - GWT-Tests");

        //Mock-Tests
        theGWTEventServiceTestSuite.addTest(GWTEventServiceTestSuite.suite());

        //Event
        theGWTEventServiceTestSuite.addTestSuite(RemoteEventServiceTest.class);

        return theGWTEventServiceTestSuite;
    }
}