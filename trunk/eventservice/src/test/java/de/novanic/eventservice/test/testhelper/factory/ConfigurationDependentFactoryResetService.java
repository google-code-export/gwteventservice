package de.novanic.eventservice.test.testhelper.factory;

import de.novanic.eventservice.config.ConfigurationDependentFactory;

/**
 * @author sstrohschein
 *         <br>Date: 24.11.12
 *         <br>Time: 01:30
 */
public final class ConfigurationDependentFactoryResetService
{
    private ConfigurationDependentFactoryResetService() {}

    public static void resetFactory() {
        ConfigurationDependentFactory.reset();
        GenericFactoryResetService.resetFactory(ConfigurationDependentFactory.class);
    }
}