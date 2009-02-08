/*
 * GWTEventService
 * Copyright (c) 2008, GWTEventService Committers
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
package de.novanic.eventservice.test.testhelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Private methods and constructors can be called with PrivateMethodExecutor (<b>only for testing purposes</b>).
 *
 * @author sstrohschein
 *         <br>Date: 08.02.2009
 *         <br>Time: 15:58:54
 */
public final class PrivateMethodExecutor<T>
{
    private Class<T> myClass;

    /**
     * Creates a new PrivateMethodExecutor for access the related class (<b>only for testing purposes</b>).
     * @param aClass class with methods which can't be accessed in test cases
     */
    public PrivateMethodExecutor(Class<T> aClass) {
        myClass = aClass;
    }

    /**
     * Executes the no-arg private constructor (<b>only for testing purposes</b>).
     * @return the created object (via call to the no-arg private constructor)
     */
    public T executePrivateConstructor() {
        try {
            Constructor<T> theConstructor = myClass.getDeclaredConstructor();
            theConstructor.setAccessible(true);
            return theConstructor.newInstance();
        } catch(NoSuchMethodException e) {
            throw new AssertionError("The no-arg constructor of \"" + myClass.getName() + "\" could not be found (" + e.getMessage() + "\"!");
        } catch(InvocationTargetException e) {
            throw new AssertionError("The no-arg constructor of \"" + myClass.getName() + "\" could not be invoked (" + e.getMessage() + "\"!");
        } catch(IllegalAccessException e) {
            throw new AssertionError("The no-arg constructor of \"" + myClass.getName() + "\" could not be accessed (" + e.getMessage() + "\"!");
        } catch(InstantiationException e) {
            throw new AssertionError("The object could not be instatiated with the no-arg constructor of \"" + myClass.getName() + "\" (" + e.getMessage() + "\"!");
        }
    }

    /**
     * Executes a no-arg private method (<b>only for testing purposes</b>).
     * @param aMethodName name of the method
     * @return result object (return)
     */
    public Object executePrivateMethod(final String aMethodName) {
        return executePrivateMethod(aMethodName, null, null);
    }

    /**
     * Executes a private method (<b>only for testing purposes</b>).
     * @param aMethodName name of the method
     * @param aParameters parameter objects/values;
     * When the classes of the parameters differ from the parameter objects/values, please use {@link de.novanic.eventservice.test.testhelper.PrivateMethodExecutor#executePrivateMethod(String, Class[], Object[])}) instead.
     * That can be necessary for example when the method is overloaded and the parameter classes of the various methods
     * are in the same inheritance hierarchy.
     * @return result object (return)
     */
    public Object executePrivateMethod(final String aMethodName, Object[] aParameters) {
        return executePrivateMethod(aMethodName, buildClasses(aParameters), aParameters);
    }

    /**
     * Executes a private method (<b>only for testing purposes</b>).
     * @param aMethodName aMethodName name of the method
     * @param theParameterClasses classes of the parameter objects/values; When the classes of the parameters doesn't
     * differ from the parameter objects/values, the method {@link de.novanic.eventservice.test.testhelper.PrivateMethodExecutor#executePrivateMethod(String, Object[])})
     * can be used instead.
     * This method can be necessary for example when the method is overloaded and the parameter classes of the various methods
     * are in the same inheritance hierarchy.
     * @param aParameters parameter objects/values
     * @return result object (return)
     */
    public Object executePrivateMethod(final String aMethodName, Class[] theParameterClasses, Object[] aParameters) {
        try {
            Method theCreateNewLineCharMethod = myClass.getDeclaredMethod(aMethodName, theParameterClasses);
            theCreateNewLineCharMethod.setAccessible(true);
            return theCreateNewLineCharMethod.invoke(aParameters);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("The method \"" + aMethodName + "\" could not be found (" + e.getMessage() + ")!");
        } catch(IllegalAccessException e) {
            throw new AssertionError("The method \"" + aMethodName + "\" could not be accessed (" + e.getMessage() + ")!");
        } catch(InvocationTargetException e) {
            throw new AssertionError("The method \"" + aMethodName + "\" could not be invoked (" + e.getMessage() + ")!");
        }
    }

    /**
     * Builds a class array of the object array (same order).
     * @param anObjects objects to get the classes from 
     * @return classes of the objects
     */
    private Class[] buildClasses(Object[] anObjects) {
        Class[] theClasses = new Class[anObjects.length];
        for(int i = 0; i < anObjects.length; i++) {
            theClasses[i] = anObjects[i].getClass();
        }
        return theClasses;
    }
}