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

import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;

/**
 * @author sstrohschein
 *         <br>Date: 11.01.2009
 *         <br>Time: 15:33:06
 */
public class PlatformUtilTest extends TestCase
{
    private static final String LINE_SEPARATOR_PROPERTY = "line.separator";

    public void testPrivateConstructor() {
        assertNotNull(new PrivateMethodExecutor<PlatformUtil>(PlatformUtil.class).executePrivateConstructor());
    }

    public void testGetNewLine() {
        final String theNewLineChar = System.getProperty(LINE_SEPARATOR_PROPERTY);
        if(theNewLineChar != null) {
            assertEquals(theNewLineChar, PlatformUtil.getNewLine());
        }
    }

    public void testGetNewLine_2() {
        final String theCreateNewLineCharMethodName = "createNewLineChar";
        final String theOldNewLineChar = System.getProperty(LINE_SEPARATOR_PROPERTY);

        try {
            System.clearProperty(LINE_SEPARATOR_PROPERTY);

            String theCreatedNewLineChar = (String)new PrivateMethodExecutor<PlatformUtil>(PlatformUtil.class).executePrivateMethod(theCreateNewLineCharMethodName);
            assertEquals("\n", theCreatedNewLineChar);
        } finally {
            System.setProperty(LINE_SEPARATOR_PROPERTY, theOldNewLineChar);
        }
    }

    public void testGetCurrentTime() throws Exception {
        long theCurrentTime = PlatformUtil.getCurrentTime();
        assertTrue(theCurrentTime > 0);

        Thread.sleep(50);
        long theCurrentTime_2 = PlatformUtil.getCurrentTime();
        assertTrue(theCurrentTime_2 > 0);
        assertTrue(theCurrentTime_2 > theCurrentTime);
    }
}
