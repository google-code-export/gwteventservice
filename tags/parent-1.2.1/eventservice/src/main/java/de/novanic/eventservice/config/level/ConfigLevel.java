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
 * See {@link de.novanic.eventservice.config.level.ConfigLevelFactory} for more information.
 *
 * @author sstrohschein
 *         <br>Date: 20.03.2009
 *         <br>Time: 19:42:31
 */
public interface ConfigLevel extends Comparable<ConfigLevel>
{
    /**
     * Returns the specified level / queue number of the level
     * @return specified level / queue number of the level
     */
    int getLevel();
}
