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
package de.novanic.eventservice;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.novanic.eventservice.service.registry.EventRegistry_ExtremeThreadingTest;
import de.novanic.eventservice.service.EventServiceImpl_ExtremeThreadingTest;
import de.novanic.eventservice.util.TestLoggingConfigurator;

/**
 * @author sstrohschein
 * <br>Date: 23.08.2008
 * <br>Time: 10:21:30
 */
public class EventService_ExtremeThreadingTestSuite
{
    private EventService_ExtremeThreadingTestSuite() {}

    public static Test suite() throws Exception {
        TestSuite theSuite = new TestSuite("EventService - ExtremeThreading-Tests");

        TestLoggingConfigurator.configureLogging();

        theSuite.addTestSuite(EventRegistry_ExtremeThreadingTest.class);
        theSuite.addTestSuite(EventServiceImpl_ExtremeThreadingTest.class);

        return theSuite;
    }
}
