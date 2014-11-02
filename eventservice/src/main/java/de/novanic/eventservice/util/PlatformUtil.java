/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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

/**
 * Utility class to ensure the platform independency.
 *
 * @author sstrohschein
 *         <br>Date: 11.01.2009
 *         <br>Time: 15:27:04
 */
public final class PlatformUtil
{
    private static final String NEW_LINE_CHAR;

    static {
        NEW_LINE_CHAR = createNewLineChar();
    }

    private PlatformUtil() {}

    /**
     * Returns the new line character for the corresponding platform.
     * @return new line character
     */
    public static String getNewLine() {
        return NEW_LINE_CHAR;
    }

    /**
     * Returns the current time in milliseconds.
     * @return current time in milliseconds
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Returns the new line character for the corresponding platform.
     * @return new line character
     */
    private static String createNewLineChar() {
        String theNewLineChar = System.getProperty("line.separator");
        if(theNewLineChar == null) {
            theNewLineChar = "\n";
        }
        return theNewLineChar;
    }
}
