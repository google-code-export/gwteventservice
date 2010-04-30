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
package de.novanic.eventservice.service.connection.strategy.connector.streaming;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.impl.SerializabilityUtil;

/**
 * The {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy} is required
 * to serialize events (could implement IsSerializable or Serializable).
 *
 * @author sstrohschein
 *         <br>Date: 16.03.2010
 *         <br>Time: 23:14:40
 */
public class EventSerializationPolicy extends SerializationPolicy
{
    /**
     * Returns true when the class is serializable (see {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy#isValid(Class)}).
     * @param aClass class to check
     * @return true when the class is serializable (see {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy#isValid(Class)}).
     */
	public boolean shouldDeserializeFields(Class<?> aClass) {
		return isValid(aClass);
	}

    /**
     * Returns true when the class is serializable (see {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy#isValid(Class)}).
     * @param aClass class to check
     * @return true when the class is serializable (see {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy#isValid(Class)}).
     */
	public boolean shouldSerializeFields(Class<?> aClass) {
		return isValid(aClass);
	}

    /**
     * There is no implementation of that method for {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy}
     * @param aClass class to check
     * @throws SerializationException
     */
	public void validateDeserialize(Class<?> aClass) throws SerializationException {}

    /**
     * There is no implementation of that method for {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.EventSerializationPolicy}
     * @param aClass class to check
     * @throws SerializationException
     */
	public void validateSerialize(Class<?> aClass) throws SerializationException {}

    /**
     * Checks if the class is serializable (when the class extends from IsSerializable or Serializable, has a custom field serializer,
     * is a primitive or is an array which fulfills these conditions).
     * @param aClass class to check
     * @return true when the class is serializable
     */
	private boolean isValid(Class<?> aClass) {
        if(aClass.isArray()) {
			return isValid(aClass.getComponentType());
		}
        return aClass.isPrimitive() ||
                Serializable.class.isAssignableFrom(aClass) || IsSerializable.class.isAssignableFrom(aClass) ||
                SerializabilityUtil.hasCustomFieldSerializer(aClass) != null;
    }
}
