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

import java.util.Scanner;

/**
 * Utility class to provide String and parsing operations.
 *
 * @author sstrohschein
 *         <br>Date: 05.03.2009
 *         <br>Time: 23:54:01
 */
public final class StringUtil
{
    private StringUtil() {}

    /**
     * Checks if the String is numeric (integer / long and comma or point separated).
     * @param aString String to check
     * @return true when it is numeric (integer / long and comma or point separated), otherwise false
     */
    public static boolean isNumeric(String aString) {
        if(aString != null) {
            Scanner theScanner = new Scanner(aString.trim());
            if(theScanner.hasNextLong()) {
                theScanner.nextLong();
                return !theScanner.hasNext();
            }
        }
        return false;
    }

    /**
     * Returns the integer of the value and throws a {@link de.novanic.eventservice.util.ServiceUtilException} when it isn't numeric
     * (integer / long and comma or point separated).
     * @param aValue value to parse
     * @return parsed integer
     * @throws ServiceUtilException thrown when the value isn't numeric
     */
    public static int readIntegerChecked(String aValue) throws ServiceUtilException {
        if(aValue == null || !(isNumeric(aValue))) {
            throw new ServiceUtilException("The value \"" + aValue + "\" was expected to be numeric!");
        }
        return Integer.parseInt(aValue);
    }
}