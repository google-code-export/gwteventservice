/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
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
package de.novanic.eventservice.config.level;

import junit.framework.TestCase;
import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;

/**
 * @author sstrohschein
 *         <br>Date: 01.07.2009
 *         <br>Time: 22:00:43
 */
public class ConfigLevelFactoryTest extends TestCase
{
    public void testConstructor() {
        PrivateMethodExecutor<ConfigLevelFactory> thePrivateMethodExecutor = new PrivateMethodExecutor<ConfigLevelFactory>(ConfigLevelFactory.class);
        thePrivateMethodExecutor.executePrivateConstructor();
    }

    public void testCompareTo() {
        ConfigLevel theNullableConfigLevel = null;
        assertFalse(ConfigLevelFactory.LOWEST.equals(theNullableConfigLevel));

        assertEquals(ConfigLevelFactory.LOWEST, ConfigLevelFactory.LOWEST);
        assertEquals(ConfigLevelFactory.LOWEST.hashCode(), ConfigLevelFactory.LOWEST.hashCode());
        assertEquals(0, ConfigLevelFactory.LOWEST.compareTo(ConfigLevelFactory.LOWEST));
        assertFalse(ConfigLevelFactory.LOWEST.equals(ConfigLevelFactory.LOW));
        assertEquals(-1, ConfigLevelFactory.LOWEST.compareTo(ConfigLevelFactory.LOW));
        assertEquals(1, ConfigLevelFactory.LOW.compareTo(ConfigLevelFactory.LOWEST));

        assertEquals(ConfigLevelFactory.LOW, ConfigLevelFactory.LOW);
        assertEquals(ConfigLevelFactory.LOW.hashCode(), ConfigLevelFactory.LOW.hashCode());
        assertEquals(0, ConfigLevelFactory.LOW.compareTo(ConfigLevelFactory.LOW));
        assertFalse(ConfigLevelFactory.LOW.equals(ConfigLevelFactory.DEFAULT));
        assertEquals(-1, ConfigLevelFactory.LOW.compareTo(ConfigLevelFactory.DEFAULT));
        assertEquals(1, ConfigLevelFactory.DEFAULT.compareTo(ConfigLevelFactory.LOW));

        assertEquals(ConfigLevelFactory.DEFAULT, ConfigLevelFactory.DEFAULT);
        assertEquals(ConfigLevelFactory.DEFAULT.hashCode(), ConfigLevelFactory.DEFAULT.hashCode());
        assertEquals(0, ConfigLevelFactory.DEFAULT.compareTo(ConfigLevelFactory.DEFAULT));
        assertFalse(ConfigLevelFactory.DEFAULT.equals(ConfigLevelFactory.HIGH));
        assertEquals(-1, ConfigLevelFactory.DEFAULT.compareTo(ConfigLevelFactory.HIGH));
        assertEquals(1, ConfigLevelFactory.HIGH.compareTo(ConfigLevelFactory.DEFAULT));

        assertEquals(ConfigLevelFactory.HIGH, ConfigLevelFactory.HIGH);
        assertEquals(ConfigLevelFactory.HIGH.hashCode(), ConfigLevelFactory.HIGH.hashCode());
        assertEquals(0, ConfigLevelFactory.HIGH.compareTo(ConfigLevelFactory.HIGH));
        assertFalse(ConfigLevelFactory.HIGH.equals(ConfigLevelFactory.HIGHEST));
        assertEquals(-1, ConfigLevelFactory.HIGH.compareTo(ConfigLevelFactory.HIGHEST));
        assertEquals(1, ConfigLevelFactory.HIGHEST.compareTo(ConfigLevelFactory.HIGH));

        assertEquals(ConfigLevelFactory.HIGHEST, ConfigLevelFactory.HIGHEST);
        assertEquals(ConfigLevelFactory.HIGHEST.hashCode(), ConfigLevelFactory.HIGHEST.hashCode());
        assertEquals(0, ConfigLevelFactory.HIGHEST.compareTo(ConfigLevelFactory.HIGHEST));
        assertFalse(ConfigLevelFactory.LOWEST.equals(ConfigLevelFactory.HIGHEST));
        assertEquals(1, ConfigLevelFactory.HIGHEST.compareTo(ConfigLevelFactory.LOWEST));
        assertEquals(-1, ConfigLevelFactory.LOWEST.compareTo(ConfigLevelFactory.HIGHEST));
    }

    public void testCompareTo_toString() {
        assertEquals(ConfigLevelFactory.LOWEST.toString(), ConfigLevelFactory.LOWEST.toString());
        assertEquals(ConfigLevelFactory.LOWEST.toString().hashCode(), ConfigLevelFactory.LOWEST.toString().hashCode());
        assertEquals(0, ConfigLevelFactory.LOWEST.toString().compareTo(ConfigLevelFactory.LOWEST.toString()));
        assertFalse(ConfigLevelFactory.LOWEST.toString().equals(ConfigLevelFactory.LOW.toString()));

        assertEquals(ConfigLevelFactory.LOW.toString(), ConfigLevelFactory.LOW.toString());
        assertEquals(ConfigLevelFactory.LOW.toString().hashCode(), ConfigLevelFactory.LOW.toString().hashCode());
        assertEquals(0, ConfigLevelFactory.LOW.toString().compareTo(ConfigLevelFactory.LOW.toString()));
        assertFalse(ConfigLevelFactory.LOW.toString().equals(ConfigLevelFactory.DEFAULT.toString()));

        assertEquals(ConfigLevelFactory.DEFAULT.toString(), ConfigLevelFactory.DEFAULT.toString());
        assertEquals(ConfigLevelFactory.DEFAULT.toString().hashCode(), ConfigLevelFactory.DEFAULT.toString().hashCode());
        assertEquals(0, ConfigLevelFactory.DEFAULT.toString().compareTo(ConfigLevelFactory.DEFAULT.toString()));
        assertFalse(ConfigLevelFactory.DEFAULT.toString().equals(ConfigLevelFactory.HIGH.toString()));

        assertEquals(ConfigLevelFactory.HIGH.toString(), ConfigLevelFactory.HIGH.toString());
        assertEquals(ConfigLevelFactory.HIGH.toString().hashCode(), ConfigLevelFactory.HIGH.toString().hashCode());
        assertEquals(0, ConfigLevelFactory.HIGH.toString().compareTo(ConfigLevelFactory.HIGH.toString()));
        assertFalse(ConfigLevelFactory.HIGH.toString().equals(ConfigLevelFactory.HIGHEST.toString()));

        assertEquals(ConfigLevelFactory.HIGHEST.toString(), ConfigLevelFactory.HIGHEST.toString());
        assertEquals(ConfigLevelFactory.HIGHEST.toString().hashCode(), ConfigLevelFactory.HIGHEST.toString().hashCode());
        assertEquals(0, ConfigLevelFactory.HIGHEST.toString().compareTo(ConfigLevelFactory.HIGHEST.toString()));
        assertFalse(ConfigLevelFactory.LOWEST.toString().equals(ConfigLevelFactory.HIGHEST.toString()));
    }
}