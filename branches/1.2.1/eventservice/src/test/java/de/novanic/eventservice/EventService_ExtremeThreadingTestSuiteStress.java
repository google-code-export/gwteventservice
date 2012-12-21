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
import de.novanic.eventservice.util.LoggingConfiguratorTestMode;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author sstrohschein
 * <br>Date: 23.08.2008
 * <br>Time: 10:21:30
 */
@Suite.SuiteClasses({
    EventRegistry_ExtremeThreadingTestStress.class,
    EventServiceImpl_ExtremeThreadingTestStress.class
})
@RunWith(Suite.class)
public class EventService_ExtremeThreadingTestSuiteStress
{
    @Before
    public void configureLogging() throws Exception {
        LoggingConfiguratorTestMode.configureLogging();
    }
}