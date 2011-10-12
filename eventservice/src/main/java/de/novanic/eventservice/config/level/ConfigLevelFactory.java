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
package de.novanic.eventservice.config.level;

/**
 * {@link de.novanic.eventservice.config.loader.ConfigurationLoader} instances can be registered at a {@link de.novanic.eventservice.config.level.ConfigLevel}.
 * There are five pre-defined configuration levels ({@link de.novanic.eventservice.config.level.ConfigLevelFactory#LOWEST},
 * {@link de.novanic.eventservice.config.level.ConfigLevelFactory#LOW}, {@link de.novanic.eventservice.config.level.ConfigLevelFactory#DEFAULT},
 * {@link de.novanic.eventservice.config.level.ConfigLevelFactory#HIGH} and {@link de.novanic.eventservice.config.level.ConfigLevelFactory#HIGHEST}).
 * Finer levels can be defined with a custom configuration level with {@link de.novanic.eventservice.config.level.ConfigLevelFactory#createConfigLevel(int)}.
 *
 * The loading process starts with the ConfigurationLoaders which are registered at the lower levels and check the availability. When the loader or rather the
 * source is available the loader is used to load the {@link de.novanic.eventservice.config.EventServiceConfiguration}. Otherwise the next loader in the
 * configuration level queue is checked.
 *
 * The ConfigLevelFactory is used by the {@link de.novanic.eventservice.config.EventServiceConfigurationFactory} and there are some pre-registered
 * {@link de.novanic.eventservice.config.loader.ConfigurationLoader} instances. See {@link de.novanic.eventservice.config.EventServiceConfigurationFactory}
 * for more information.
 *
 * @author sstrohschein
 *         <br>Date: 20.03.2009
 *         <br>Time: 19:44:33
 */
public final class ConfigLevelFactory
{
    /**
     * LOWEST level = 1 - 2499
     */
    public static final ConfigLevel LOWEST = createConfigLevel(1);

    /**
     * LOW level = 2500 - 4999
     */
    public static final ConfigLevel LOW = createConfigLevel(2500);

    /**
     * DEFAULT level = 5000 - 7499
     */
    public static final ConfigLevel DEFAULT = createConfigLevel(5000);

    /**
     * HIGH level = 7500 - 9999
     */
    public static final ConfigLevel HIGH = createConfigLevel(7500);

    /**
     * HIGHEST level = >= 10000
     */
    public static final ConfigLevel HIGHEST = createConfigLevel(10000);

    private ConfigLevelFactory() {}

    /**
     * Creates a custom configuration level for finer steps than the pre-defined configuration levels
     * ({@link de.novanic.eventservice.config.level.ConfigLevelFactory#LOWEST},
     * {@link de.novanic.eventservice.config.level.ConfigLevelFactory#LOW}, {@link de.novanic.eventservice.config.level.ConfigLevelFactory#DEFAULT},
     * {@link de.novanic.eventservice.config.level.ConfigLevelFactory#HIGH}, {@link de.novanic.eventservice.config.level.ConfigLevelFactory#HIGHEST}).
     * @param aLevelIdent queue number of the level (configuration loading is started from the lesser level)
     * @return created {@link de.novanic.eventservice.config.level.ConfigLevel}
     */
    public static ConfigLevel createConfigLevel(final int aLevelIdent) {
        return new DefaultConfigLevel(aLevelIdent);
    }

    /**
     * Default implementation of {@link de.novanic.eventservice.config.level.ConfigLevel}
     */
    private static class DefaultConfigLevel implements ConfigLevel
    {
        private final int myLevel;

        /**
         * Creates a {@link de.novanic.eventservice.config.level.ConfigLevel} with the specified level / queue number of the level
         * @param aLevel level / queue number of the level
         */
        private DefaultConfigLevel(final int aLevel) {
            myLevel = aLevel;
        }

        /**
         * Returns the specified level / queue number of the level
         * @return specified level / queue number of the level
         */
        public int getLevel() {
            return myLevel;
        }

        public int compareTo(ConfigLevel aConfigLevel) {
            final int theOtherLevel = aConfigLevel.getLevel();
            if(myLevel > theOtherLevel) {
                return 1;
            } else if(myLevel < theOtherLevel) {
                return -1;
            }
            return 0;
        }

        public boolean equals(Object anObject) {
            if(this == anObject) {
                return true;
            }
            if(anObject == null || getClass() != anObject.getClass()) {
                return false;
            }
            DefaultConfigLevel theOtherConfigLevel = (DefaultConfigLevel)anObject;
            return myLevel == theOtherConfigLevel.myLevel;
        }

        public int hashCode() {
            return myLevel;
        }

        public String toString() {
            return "DefaultConfigLevel: " + myLevel;
        }
    }
}
