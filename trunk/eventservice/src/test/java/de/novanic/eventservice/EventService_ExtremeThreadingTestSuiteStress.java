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
package de.novanic.eventservice;

import de.novanic.eventservice.service.EventServiceImpl_ExtremeThreadingTestStress;
import de.novanic.eventservice.service.registry.EventRegistry_ExtremeThreadingTestStress;
import junit.framework.Test;
import junit.framework.TestSuite;
import de.novanic.eventservice.util.LoggingConfiguratorTestMode;

/**
 * @author sstrohschein
 * <br>Date: 23.08.2008
 * <br>Time: 10:21:30
 */
public class EventService_ExtremeThreadingTestSuiteStress
{
    private EventService_ExtremeThreadingTestSuiteStress() {}

    public static Test suite() throws Exception {
        TestSuite theSuite = new TestSuite("EventService - ExtremeThreading-Tests");

        LoggingConfiguratorTestMode.configureLogging();

        theSuite.addTestSuite(EventRegistry_ExtremeThreadingTestStress.class);
        theSuite.addTestSuite(EventServiceImpl_ExtremeThreadingTestStress.class);

        return theSuite;
    }
}
