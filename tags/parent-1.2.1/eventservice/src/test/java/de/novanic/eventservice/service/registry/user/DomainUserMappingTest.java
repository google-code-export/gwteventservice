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
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 09.09.2009
 *         <br>Time: 16:01:47
 */
@RunWith(JUnit4.class)
public class DomainUserMappingTest
{
    private static final UserInfo TEST_USER_INFO = new UserInfo("test_user_id");
    private static final UserInfo TEST_USER_INFO_2 = new UserInfo("test_user_id_2");
    private static final Domain TEST_DOMAIN = DomainFactory.getDomain("test_domain");
    private static final Domain TEST_DOMAIN_2 = DomainFactory.getDomain("test_domain_2");

    private DomainUserMapping myDomainUserMapping;

    @Before
    public void setUp() {
        myDomainUserMapping = new DomainUserMapping();
    }

    @Test
    public void testAddUser() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        //no effect, when the user is added again
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(2, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testAddUser_2() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO_2);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN_2).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testAddUser_3() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertEquals(2, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testAddUser_4() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        //no effect, when the user is added again
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(1, myDomainUserMapping.getUsers(TEST_DOMAIN).size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));

        //check if the second add had really no effect to the internal data structure
        myDomainUserMapping.removeUser(TEST_USER_INFO);

        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(0, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
    }

    @Test
    public void testRemoveUser() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
        
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));
    }

    @Test
    public void testRemoveUser_2() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO_2);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        //remove again (no effect)
        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN_2, TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testRemoveUser_3() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO_2);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testRemoveUser_4() {
        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO_2));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO_2);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_USER_INFO);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        //remove from unimportant domain (no effect)
        myDomainUserMapping.removeUser(TEST_DOMAIN, TEST_USER_INFO_2);

        assertEquals(1, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertTrue(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));

        myDomainUserMapping.removeUser(TEST_DOMAIN_2, TEST_USER_INFO_2);

        assertEquals(0, myDomainUserMapping.getDomains().size());
        assertFalse(myDomainUserMapping.isUserContained(TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN, TEST_USER_INFO_2));
        assertFalse(myDomainUserMapping.isUserContained(TEST_DOMAIN_2, TEST_USER_INFO_2));
    }

    @Test
    public void testGetDomains() {
        assertNull(myDomainUserMapping.getUsers(null));

        myDomainUserMapping.addUser(TEST_DOMAIN, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO);
        myDomainUserMapping.addUser(TEST_DOMAIN_2, TEST_USER_INFO_2);

        assertEquals(2, myDomainUserMapping.getDomains().size());
        assertEquals(2, myDomainUserMapping.getDomains(TEST_USER_INFO).size());
        assertEquals(1, myDomainUserMapping.getDomains(TEST_USER_INFO_2).size());
        assertEquals(0, myDomainUserMapping.getDomains(null).size());
        assertNull(myDomainUserMapping.getUsers(null));
    }
}