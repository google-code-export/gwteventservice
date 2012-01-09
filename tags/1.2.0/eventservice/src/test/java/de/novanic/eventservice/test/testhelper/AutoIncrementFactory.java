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
package de.novanic.eventservice.test.testhelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AutoIncrementFactory can be used to create unique idents for various contexts.
 *
 * @author sstrohschein
 *         <br>Date: 07.02.2009
 *         <br>Time: 17:24:55
 */
public class AutoIncrementFactory
{
    private final ConcurrentMap<String, AtomicInteger> myAutoIncrementMap;

    private AutoIncrementFactory() {
        myAutoIncrementMap = new ConcurrentHashMap<String, AtomicInteger>();
    }

    private static class AutoIncrementFactoryHolder {
        private static AutoIncrementFactory INSTANCE = new AutoIncrementFactory();
    }

    public static AutoIncrementFactory getInstance() {
        return AutoIncrementFactoryHolder.INSTANCE;
    }

    public int getCurrentValue(String aKey) {
        return getAtomic(aKey).intValue();
    }

    public int getNextValue(String aKey) {
        return getAtomic(aKey).incrementAndGet();
    }

    public void reset() {
        myAutoIncrementMap.clear(); 
    }

    private AtomicInteger getAtomic(String aKey) {
        AtomicInteger theAtomicInteger = myAutoIncrementMap.get(aKey);
        if(theAtomicInteger == null) {
            AtomicInteger theNewAtomicInteger = new AtomicInteger();
            theAtomicInteger = myAutoIncrementMap.putIfAbsent(aKey, theNewAtomicInteger);
            if(theAtomicInteger == null) {
                return theNewAtomicInteger;
            }
        }
        return theAtomicInteger;
    }
}