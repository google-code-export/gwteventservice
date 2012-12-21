/*
 * GWTEventService
 * Copyright (c) 2011 and beyond, strawbill UG (haftungsbeschr�nkt)
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
package de.novanic.eventservice.test.testhelper.factory;

import de.novanic.eventservice.service.DefaultEventExecutorService;

/**
 * @author sstrohschein
 *         <br>Date: 02.03.2009
 *         <br>Time: 12:28:45
 */
public final class FactoryResetService
{
    private FactoryResetService() {}

    public static <F> void resetFactory(final Class<F> aFactoryClass) throws FactoryResetException {
        if(DefaultEventExecutorService.class.equals(aFactoryClass)) {
            EventExecutorServiceFactoryResetService.resetFactory();
        } else {
            GenericFactoryResetService.resetFactory(aFactoryClass);
        }
    }
}