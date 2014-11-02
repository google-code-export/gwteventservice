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
package de.novanic.eventservice.test.testhelper.factory;

import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;

import java.lang.reflect.Field;

/**
 * @author sstrohschein
 *         <br>Date: 28.02.2009
 *         <br>Time: 17:42:40
 */
public final class GenericFactoryResetService
{
    private static final String HOLDER_CLASS_SUFFIX = "Holder";
    private static final String INSTANCE_HOLDER_FIELD = "INSTANCE";

    public static <F> void resetFactory(final Class<F> aFactoryClass) throws FactoryResetException {
        final Class theHolderClass = getHolderClass(aFactoryClass);
        resetInstance(aFactoryClass, theHolderClass);
    }

    private static <F> Class getHolderClass(final Class<F> aFactoryClass) throws FactoryResetException {
        Class[] theClasses = aFactoryClass.getDeclaredClasses();

        Class theHolderClass = null;
        for(Class theInternalClass: theClasses) {
            if(theInternalClass.getName().endsWith(HOLDER_CLASS_SUFFIX)) {
                if(theHolderClass != null) {
                    throw new FactoryResetException("There is more than one Holder class for the factory \"" + aFactoryClass.getName()
                                                    + "\" defined! Only one inner class with the suffix 'Holder' is allowed for a factory.");
                }
                theHolderClass = theInternalClass;
            }
        }
        if(theHolderClass == null) {
            throw new FactoryResetException("No Holder class for the factory \"" + aFactoryClass.getName()
                                            + "\" could be found! The factory must contain an inner class with the suffix 'Holder'.");
        }
        return theHolderClass;
    }

    private static <F> void resetInstance(Class<F> aFactoryClass, Class aHolderClass) throws FactoryResetException {
        try {
            final Field theInstanceField = aHolderClass.getDeclaredField(INSTANCE_HOLDER_FIELD);
            theInstanceField.setAccessible(true);

            final PrivateMethodExecutor<F> thePrivateMethodExecutor = new PrivateMethodExecutor<F>(aFactoryClass);
            final Object theNewFactoryInstance = thePrivateMethodExecutor.executePrivateConstructor();
            theInstanceField.set(null, theNewFactoryInstance);
        } catch(IllegalAccessException e) {
            throw new FactoryResetException("Error on access the factory instance of \"" + aFactoryClass.getName() + "\"!");
        } catch(NoSuchFieldException e) {
            throw new FactoryResetException("The field \"" + INSTANCE_HOLDER_FIELD + "\" of the factory-holder \""
                    + aHolderClass.getName() + "\" could not be found!");
        }
    }
}