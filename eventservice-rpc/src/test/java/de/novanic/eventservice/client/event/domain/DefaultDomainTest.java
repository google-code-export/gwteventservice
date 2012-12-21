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
package de.novanic.eventservice.client.event.domain;

import junit.framework.TestCase;

/**
 * @author sstrohschein
 * Date: 16.08.2008
 * Time: 18:44:37
 */
public class DefaultDomainTest extends TestCase
{
    public void testInit() {
        final String theName = "testName";
        Domain theDomain = new DefaultDomain(theName);
        assertNotNull(theDomain.getName());
        assertEquals(theName, theDomain.getName());
        assertEquals(theName, theDomain.toString());
        
        Domain theDomain_2 = new DefaultDomain(theName);
        assertNotSame(theDomain, theDomain_2);
        assertEquals(theDomain.getName(), theDomain_2.getName());
        assertEquals(theDomain.toString(), theDomain_2.toString());
        assertEquals(theDomain.hashCode(), theDomain_2.hashCode());
        assertEquals(theDomain, theDomain_2);
        assertEquals(theDomain.hashCode(), theDomain_2.hashCode());
        assertEquals(0, theDomain.compareTo(theDomain_2));

        assertEquals(theDomain, theDomain);
        Domain theNullDomain = null;
        assertFalse(theDomain.equals(theNullDomain));

        Domain theDomain_3 = new DefaultDomain(theName + "2");
        assertNotSame(theDomain, theDomain_3);
        assertFalse(theDomain.equals(theDomain_3));
        assertFalse(theDomain.compareTo(theDomain_3) == 0);
    }

    public void testCompareTo() {
        final String theName = "testName1";
        Domain theDomain = new DefaultDomain(theName);
        
        assertEquals(0, theDomain.compareTo(theDomain));
        assertEquals(0, theDomain.compareTo(new DefaultDomain(theName)));
        assertEquals(-1, theDomain.compareTo(new DefaultDomain("testName2")));
        assertEquals(1, theDomain.compareTo(new DefaultDomain("testName0")));
        assertEquals(1, theDomain.compareTo(null));
    }

    public void testInit_Error() {
        Domain theDomain = new DefaultDomain();
        assertNull(theDomain.getName());
    }
}