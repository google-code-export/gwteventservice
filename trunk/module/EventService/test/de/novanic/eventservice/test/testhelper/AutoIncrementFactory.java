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
package de.novanic.eventservice.test.testhelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AutoIncrementFactory can be used to create unique idents for various contexts. 
 *
 * @author sstrohschein
 *         <br>Date: 07.02.2009
 *         <br>Time: 17:24:55
 */
public class AutoIncrementFactory
{
    private static AutoIncrementFactory myInstance;
    private final Map<String, AtomicInteger> myAutoIncrementMap;

    private AutoIncrementFactory() {
        myAutoIncrementMap = new ConcurrentHashMap<String, AtomicInteger>();
    }

    public static synchronized AutoIncrementFactory getInstance() {
        if(myInstance == null) {
            myInstance = new AutoIncrementFactory();
        }
        return myInstance;
    }

    public int getCurrentValue(String aKey) {
        return getAtomic(aKey).intValue();
    }

    public int getNextValue(String aKey) {
        return getAtomic(aKey).incrementAndGet();
    }

    public static synchronized void reset() {
        myInstance = null;
    }

    private AtomicInteger getAtomic(String aKey) {
        AtomicInteger theAtomicInteger = myAutoIncrementMap.get(aKey);
        if(theAtomicInteger == null) {
            theAtomicInteger = new AtomicInteger();
            myAutoIncrementMap.put(aKey, theAtomicInteger);
        }
        return theAtomicInteger;
    }
}