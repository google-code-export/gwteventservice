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
package de.novanic.eventservice.util;

import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 02.07.2009
 *         <br>Time: 21:46:57
 */
@RunWith(JUnit4.class)
public class StringUtilTest
{
    @Test
    public void testConstructor() {
        PrivateMethodExecutor<StringUtil> thePrivateMethodExecutor = new PrivateMethodExecutor<StringUtil>(StringUtil.class);
        thePrivateMethodExecutor.executePrivateConstructor();
    }

    @Test
    public void testReadInteger() throws ServiceUtilException {
        assertEquals(Integer.valueOf(0), StringUtil.readInteger("0"));
        assertEquals(Integer.valueOf(12), StringUtil.readInteger("12"));
        assertEquals(Integer.valueOf(1234567890), StringUtil.readInteger("1234567890"));
    }

    @Test
    public void testReadInteger_Error() {
        try {
            StringUtil.readInteger("XYZ");
            fail(ServiceUtilException.class.getName() + " expected!");
        } catch(ServiceUtilException e) {
            assertTrue(e.getMessage().contains("XYZ"));
        }
    }

    @Test
    public void testServiceUtilException() {
        try {
            throw new ServiceUtilException("aMessage");
        } catch(ServiceUtilException e) {
            assertEquals("aMessage", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            try {
                StringUtil.readInteger("XYZ");
            } catch(ServiceUtilException e) {
                throw new ServiceUtilException("aMessage", e);
            }
        } catch(ServiceUtilException e) {
            assertEquals("aMessage", e.getMessage());
            assertTrue(e.getCause().getMessage().contains("XYZ"));
            assertNull(e.getCause().getCause());
        }
    }
}