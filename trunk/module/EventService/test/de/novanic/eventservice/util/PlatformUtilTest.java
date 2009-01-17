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
package de.novanic.eventservice.util;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 *         <br>Date: 11.01.2009
 *         <br>Time: 15:33:06
 */
public class PlatformUtilTest extends TestCase
{
    private static final String LINE_SEPARATOR_PROPERTY = "line.separator";

    public void testGetNewLine() {
        final String theNewLineChar = System.getProperty(LINE_SEPARATOR_PROPERTY);
        if(theNewLineChar != null) {
            assertEquals(theNewLineChar, PlatformUtil.getNewLine());
        }
    }
}
