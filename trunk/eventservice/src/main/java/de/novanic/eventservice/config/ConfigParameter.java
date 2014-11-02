/*
 * GWTEventService
 * Copyright (c) 2014 and beyond, GWTEventService Committers
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
package de.novanic.eventservice.config;

/**
 * Constants for the configuration options.
 *
 * @author sstrohschein
 *         <br>Date: 06.03.2009
 *         <br>Time: 00:13:13
 */
public enum ConfigParameter
{
    /**
     * Max waiting time - Listening shouldn't hold longer than max waiting time.
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_MAX_WAITING_TIME}
     */
    MAX_WAITING_TIME_TAG("time.waiting.max"),

    /**
     * Min waiting time - Listening should hold at least for min waiting time.
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_MIN_WAITING_TIME}
     */
    MIN_WAITING_TIME_TAG("time.waiting.min"),

    /**
     * Timeout time - Max time for a listen cycle. If the timeout time is exceeded, the client will be deregistered.
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_TIME_OUT}
     */
    TIMEOUT_TIME_TAG("time.timeout"),

    /**
     * Reconnect attempts count - Number of reconnect attempts to execute
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_RECONNECT_ATTEMPTS}
     */
    RECONNECT_ATTEMPT_COUNT_TAG("reconnect.attempt.count"),

    /**
     * Connection id generator - Generates unique ids to identify the clients.
     * <br>Default value: {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_CONNECTION_ID_GENERATOR_CLASS_NAME}
     */
    CONNECTION_ID_GENERATOR("connection.id.generator"),

    /**
     * Connection strategy (client side part / connector) - Connection strategies are used to define the communication between the client and the server side
     * <br>Default value: {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_CONNECTION_STRATEGY_CLIENT_CONNECTOR}
     */
    CONNECTION_STRATEGY_CLIENT_CONNECTOR("connection.strategy.client.connector"),

    /**
     * Connection strategy (server side part / connector) - Connection strategies are used to define the communication between the client and the server side
     * <br>Default value: {@link de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_CONNECTION_STRATEGY_SERVER_CONNECTOR}
     */
    CONNECTION_STRATEGY_SERVER_CONNECTOR("connection.strategy.server.connector"),

    /**
     * Connection strategy encoding - Encoding / charset for the connection strategy
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_CONNECTION_STRATEGY_ENCODING}
     */
    CONNECTION_STRATEGY_ENCODING("connection.strategy.encoding"),

    /**
     * Maximum amount of events which should be transferred to the client at once.
     * The maximum amount of events prevents the listening logic from endless seeking of events (for example when more events are concurrently added than the listen thread can process).
     * <br>Default value: {@value de.novanic.eventservice.config.loader.DefaultConfigurationLoader#DEFAULT_MAX_EVENTS}
     */
    MAX_EVENTS("events.max");

    // --- Constants ---

    /**
     * Full qualified prefix
     */
    public static final String FULLY_QUALIFIED_TAG_PREFIX = "eventservice.";

    private String myDeclaration;
    private String myDeclarationFQ;

    /**
     * Creates a new {@link de.novanic.eventservice.config.ConfigParameter} with a declaration.
     * When the flag isFQ / isFullQualified is set, the full-qualified prefix is attached to the declaration.
     * @param aDeclaration declaration of the configuration parameter (name of the configuration entry)
     */
    private ConfigParameter(String aDeclaration) {
        myDeclaration = aDeclaration;
        myDeclarationFQ = FULLY_QUALIFIED_TAG_PREFIX + aDeclaration;
    }

    /**
     * Returns the declaration (name of the configuration entry) of the {@link de.novanic.eventservice.config.ConfigParameter}. 
     * @return declaration (name of the configuration entry) of the {@link de.novanic.eventservice.config.ConfigParameter}
     */
    public String declaration() {
        return myDeclaration;
    }

    /**
     * Returns the full-qualified declaration (name of the configuration entry) of the {@link de.novanic.eventservice.config.ConfigParameter}.
     * @return full-qualified declaration (name of the configuration entry) of the {@link de.novanic.eventservice.config.ConfigParameter}
     */
    public String declarationFQ() {
        return myDeclarationFQ;
    }
}