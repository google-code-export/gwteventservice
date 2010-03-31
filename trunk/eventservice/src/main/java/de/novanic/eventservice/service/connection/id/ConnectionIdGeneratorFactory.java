/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
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
package de.novanic.eventservice.service.connection.id;

import de.novanic.eventservice.config.ConfigurationException;
import de.novanic.eventservice.config.EventServiceConfiguration;

/**
 * The {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory} creates and holds the configured
 * {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}. The {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}
 * generates unique ids which are used to identify the connected clients / users.
 *
 * @author sstrohschein
 *         <br>Date: 28.03.2010
 *         <br>Time: 23:41:16
 */
public final class ConnectionIdGeneratorFactory
{
    private static EventServiceConfiguration myConfiguration;
    private ConnectionIdGenerator myConnectionIdGenerator;

    /**
     * The ConnectionIdGeneratorFactory should be created via the getInstance method. The private constructor initializes
     * the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}.
     * @see ConnectionIdGeneratorFactory#getInstance(de.novanic.eventservice.config.EventServiceConfiguration)
     */
    private ConnectionIdGeneratorFactory() {
        init(myConfiguration);
    }

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class ConnectionIdGeneratorFactoryHolder {
        private static ConnectionIdGeneratorFactory INSTANCE = new ConnectionIdGeneratorFactory();
    }

    /**
     * This method should be used to create an instance of ConnectionIdGeneratorFactory.
     * ConnectionIdGeneratorFactory is a singleton, so this method returns always the same instance of ConnectionIdGeneratorFactory.
     * @return ConnectionIdGeneratorFactory (singleton)
     */
    public static ConnectionIdGeneratorFactory getInstance(EventServiceConfiguration aConfiguration) {
        if(myConfiguration == null) {
            myConfiguration = aConfiguration;
        }
        return ConnectionIdGeneratorFactoryHolder.INSTANCE;
    }

    /**
     * Creates and initializes the {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} with the configuration.
     * @param aConfiguration configuration to create the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator} 
     */
    private void init(EventServiceConfiguration aConfiguration) {
        if(aConfiguration == null) {
            throw new ConfigurationException(ConnectionIdGeneratorFactory.class.getName() + " was initialized without a configuration!");
        }
        
        String theConnectionIdGeneratorClassName = aConfiguration.getConnectionIdGeneratorClassName();
        try {
            final Class theConnectionIdGeneratorClass = Class.forName(theConnectionIdGeneratorClassName);
            final Object theConnectionIdGeneratorInstance = theConnectionIdGeneratorClass.newInstance();
            if(theConnectionIdGeneratorInstance instanceof ConnectionIdGenerator) {
                myConnectionIdGenerator = (ConnectionIdGenerator)theConnectionIdGeneratorInstance;
            } else {
                throw new ConfigurationException(theConnectionIdGeneratorClassName + " isn't a type of " + ConnectionIdGenerator.class.getName());
            }
        } catch(ClassNotFoundException e) {
            throw new ConfigurationException(theConnectionIdGeneratorClassName + " couldn't be instantiated!", e);
        } catch(InstantiationException e) {
            throw new ConfigurationException(theConnectionIdGeneratorClassName + " couldn't be instantiated!", e);
        } catch(IllegalAccessException e) {
            throw new ConfigurationException(theConnectionIdGeneratorClassName + " couldn't be instantiated!", e);
        }
    }

    /**
     * Returns the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}.
     * @return the configured {@link de.novanic.eventservice.service.connection.id.ConnectionIdGenerator}
     * @throws ConfigurationException occurs when the {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory}
     * isn't initialized with a configuration.
     */
    public ConnectionIdGenerator getConnectionIdGenerator() {
        if(myConnectionIdGenerator == null) {
            throw new ConfigurationException(ConnectionIdGeneratorFactory.class.getName() + " isn't initialized!");
        }
        return myConnectionIdGenerator;
    }

    /**
     * Returns the configuration which was specified with the initialization of the {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory}.
     * @see {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory#getInstance(de.novanic.eventservice.config.EventServiceConfiguration)}
     * @return configuration which was specified with the initialization of the {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory}
     */
    public static EventServiceConfiguration getConfiguration() {
        return myConfiguration;
    }

    /**
     * Resets the {@link de.novanic.eventservice.service.connection.id.ConnectionIdGeneratorFactory} and re-initializes it
     * with a new configuration.
     * @param aConfiguration new configuration
     */
    public void reset(EventServiceConfiguration aConfiguration) {
        myConnectionIdGenerator = null;
        myConfiguration = aConfiguration;
        init(aConfiguration);
    }
}