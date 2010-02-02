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
package de.novanic.eventservice.config;

/**
 * Constants for the configuration options.
 *
 * @author sstrohschein
 *         <br>Date: 06.03.2009
 *         <br>Time: 00:13:13
 */
public interface ConfigParameter
{
    /**
     * Full qualified prefix
     */
    public static final String FULLY_QUALIFIED_TAG_PREFIX = "eventservice.";

    /**
     * Max waiting time - Listening shouldn't hold longer than max waiting time.
     */
    public static final String MAX_WAITING_TIME_TAG = "time.waiting.max";
    /**
     * Min waiting time - Listening should hold at least for min waiting time.
     */
    public static final String MIN_WAITING_TIME_TAG = "time.waiting.min";
    /**
     * Timeout time - Max time for a listen cycle. If the timeout time is overlapsed, the client will be deregistered.
     */
    public static final String TIMEOUT_TIME_TAG = "time.timeout";

    /**
     * Max waiting time - Listening shouldn't hold longer than max waiting time.
     */
    public static final String FQ_MAX_WAITING_TIME_TAG = FULLY_QUALIFIED_TAG_PREFIX + MAX_WAITING_TIME_TAG;
    /**
     * Min waiting time - Listening should hold at least for min waiting time.
     */
    public static final String FQ_MIN_WAITING_TIME_TAG = FULLY_QUALIFIED_TAG_PREFIX + MIN_WAITING_TIME_TAG;
    /**
     * Timeout time - Max time for a listen cycle. If the timeout time is overlapsed, the client will be deregistered.
     */
    public static final String FQ_TIMEOUT_TIME_TAG = FULLY_QUALIFIED_TAG_PREFIX + TIMEOUT_TIME_TAG;
}