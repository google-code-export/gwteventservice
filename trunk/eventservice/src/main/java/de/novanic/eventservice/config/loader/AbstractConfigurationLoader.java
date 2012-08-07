package de.novanic.eventservice.config.loader;

import de.novanic.eventservice.client.config.ConfigurationException;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.config.RemoteEventServiceConfiguration;
import de.novanic.eventservice.util.ServiceUtilException;
import de.novanic.eventservice.util.StringUtil;

/**
 * @author sstrohschein
 *         <br>Date: 07.08.12
 *         <br>Time: 13:36
 */
public abstract class AbstractConfigurationLoader implements ConfigurationLoader
{
    /**
     * Checks if the configuration is available and can be loaded. If no configuration is available, the load method
     * {@link ConfigurationLoader#load()} shouldn't be called.
     * The configuration is available when at least one parameter is configured.
     * @return true when available, otherwise false
     */
    public boolean isAvailable() {
        //The configuration is available when at least one parameter is configured.
        for(ConfigParameter theConfigParameter: ConfigParameter.values()) {
            final String theParameterValue = readParameterValue(theConfigParameter);
            if(theParameterValue != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads the configuration with the loader.
     * @return {@link de.novanic.eventservice.config.EventServiceConfiguration} the loaded configuration
     * @throws de.novanic.eventservice.client.config.ConfigurationException occurs when an loading error occurs or if it contains unreadable values.
     */
    public EventServiceConfiguration load() {
        if(isAvailable()) {
            return new RemoteEventServiceConfiguration(getConfigDescription(),
                    readIntParameterValue(ConfigParameter.MIN_WAITING_TIME_TAG),
                    readIntParameterValue(ConfigParameter.MAX_WAITING_TIME_TAG),
                    readIntParameterValue(ConfigParameter.TIMEOUT_TIME_TAG),
                    readIntParameterValue(ConfigParameter.RECONNECT_ATTEMPT_COUNT_TAG),
                    readParameterValue(ConfigParameter.CONNECTION_ID_GENERATOR),
                    readParameterValue(ConfigParameter.CONNECTION_STRATEGY_CLIENT_CONNECTOR),
                    readParameterValue(ConfigParameter.CONNECTION_STRATEGY_SERVER_CONNECTOR),
                    readParameterValue(ConfigParameter.CONNECTION_STRATEGY_ENCODING),
                    readIntParameterValue(ConfigParameter.MAX_EVENTS)
            );
        }
        return null;
    }

    /**
     * Returns the name/description of the created configuration.
     * @return name/description of the configuration
     */
    protected abstract String getConfigDescription();

    /**
     * Returns the parameter value of the configuration parameter.
     * @param aConfigParameter config parameter
     * @return value of the config parameter, NULL when the parameter isn't configured
     */
    protected abstract String readParameterValue(ConfigParameter aConfigParameter);

    /**
     * Reads the numeric value of the parameter. When the value isn't numeric, an {@link de.novanic.eventservice.client.config.ConfigurationException} is thrown.
     * @param aConfigParameter parameter
     * @return numeric parameter value
     * @throws de.novanic.eventservice.client.config.ConfigurationException (when the value isn't numeric)
     */
    private Integer readIntParameterValue(ConfigParameter aConfigParameter) {
        final String theParameterValue = readParameterValue(aConfigParameter);
        if(theParameterValue != null) {
            try {
                return StringUtil.readInteger(theParameterValue);
            } catch(ServiceUtilException e) {
                throw new ConfigurationException("The value of the parameter \"" + aConfigParameter.declaration()
                        + "\" was expected to be numeric, but was \"" + theParameterValue + "\"!", e);
            }
        }
        return null;
    }

    /**
     * Checks if the parameter is available.
     * @param aParameterValue value to check
     * @return true when it is available, otherwise false
     */
    protected boolean isParameterAvailable(String aParameterValue) {
        return aParameterValue != null && aParameterValue.trim().length() > 0;
    }

    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject == null || getClass() != anObject.getClass()) {
            return false;
        }
        PropertyConfigurationLoader theOtherLoader = (PropertyConfigurationLoader)anObject;
        return getConfigDescription().equals(theOtherLoader.getConfigDescription());
    }

    public int hashCode() {
        return getConfigDescription().hashCode();
    }
}