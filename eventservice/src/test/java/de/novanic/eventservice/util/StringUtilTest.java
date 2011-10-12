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

import junit.framework.TestCase;
import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;

/**
 * @author sstrohschein
 *         <br>Date: 02.07.2009
 *         <br>Time: 21:46:57
 */
public class StringUtilTest extends TestCase
{
    public void testConstructor() {
        PrivateMethodExecutor<StringUtil> thePrivateMethodExecutor = new PrivateMethodExecutor<StringUtil>(StringUtil.class);
        thePrivateMethodExecutor.executePrivateConstructor();
    }

    public void testIsNumeric() {
        assertTrue(StringUtil.isNumeric("1234567890"));
        assertFalse(StringUtil.isNumeric("1234567890XY"));
        assertFalse(StringUtil.isNumeric("X"));

        assertTrue(StringUtil.isNumeric("12"));
        assertFalse(StringUtil.isNumeric("12 X"));
        assertFalse(StringUtil.isNumeric("12 1"));
        assertFalse(StringUtil.isNumeric("12X"));

        assertTrue(StringUtil.isNumeric("0"));
        assertTrue(StringUtil.isNumeric("000"));
        assertTrue(StringUtil.isNumeric("012"));
        assertFalse(StringUtil.isNumeric(""));
        assertFalse(StringUtil.isNumeric(null));
    }

    public void testReadIntegerChecked() throws ServiceUtilException {
        assertEquals(0, StringUtil.readIntegerChecked("0"));
        assertEquals(12, StringUtil.readIntegerChecked("12"));
        assertEquals(1234567890, StringUtil.readIntegerChecked("1234567890"));
    }

    public void testReadIntegerChecked_Error() {
        try {
            StringUtil.readIntegerChecked("XYZ");
            fail(ServiceUtilException.class.getName() + " expected!");
        } catch(ServiceUtilException e) {
            assertTrue(e.getMessage().contains("XYZ"));
        }
    }

    public void testServiceUtilException() {
        try {
            throw new ServiceUtilException("aMessage");
        } catch(ServiceUtilException e) {
            assertEquals("aMessage", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            try {
                StringUtil.readIntegerChecked("XYZ");
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