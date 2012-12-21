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

import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 * Date: 16.08.2008
 * Time: 18:40:33
 */
@RunWith(JUnit4.class)
public class DomainFactoryTest
{
    @Test
    public void testConstructor() {
        PrivateMethodExecutor<DomainFactory> thePrivateMethodExecutor = new PrivateMethodExecutor<DomainFactory>(DomainFactory.class);
        thePrivateMethodExecutor.executePrivateConstructor();
    }

    @Test
    public void testFactory() {
        final String TEST_DOMAIN = "testDomain";
        Domain theDomain = DomainFactory.getDomain(TEST_DOMAIN);
        Domain theDomain_2 = DomainFactory.getDomain(TEST_DOMAIN);
        assertNotSame(theDomain, theDomain_2);
        assertEquals(theDomain, theDomain_2);
        assertEquals(theDomain.hashCode(), theDomain_2.hashCode());
    }

    @Test
    public void testFactory_2() {
        final String TEST_DOMAIN = "testDomain";
        final String TEST_DOMAIN_2 = "testDomain2";
        Domain theDomain = DomainFactory.getDomain(TEST_DOMAIN);
        Domain theDomain_2 = DomainFactory.getDomain(TEST_DOMAIN_2);
        assertNotSame(theDomain, theDomain_2);
        assertFalse(theDomain.equals(theDomain_2));
        assertFalse(theDomain.hashCode() == theDomain_2.hashCode());
    }
}