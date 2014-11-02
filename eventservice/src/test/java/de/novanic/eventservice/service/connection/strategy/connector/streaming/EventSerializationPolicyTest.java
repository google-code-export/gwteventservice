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
package de.novanic.eventservice.service.connection.strategy.connector.streaming;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.client.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * @author sstrohschein
 *         <br>Date: 26.04.2010
 *         <br>Time: 20:00:07
 */
@RunWith(JUnit4.class)
public class EventSerializationPolicyTest
{
    @Test
    public void testIsValid() {
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(Event.class));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(Event.class));
    }

    @Test
    public void testIsValid_2() {
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(DomainEvent.class));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(DomainEvent.class));
    }

    @Test
    public void testIsValid_3() {
        DomainEvent[] theArray = new DomainEvent[0];

        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(theArray.getClass()));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(theArray.getClass()));
    }

    @Test
    public void testIsValid_Serializable() {
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(SerializableClass.class));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(SerializableClass.class));
    }

    @Test
    public void testIsValid_Serializable_2() {
        SerializableClass[] theArray = new SerializableClass[0];

        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(theArray.getClass()));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(theArray.getClass()));
    }

    @Test
    public void testIsValid_IsSerializable() {
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(IsSerializableClass.class));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(IsSerializableClass.class));
    }

    @Test
    public void testIsValid_IsSerializable_2() {
        IsSerializableClass[] theArray = new IsSerializableClass[0];

        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertTrue(theEventSerializationPolicy.shouldSerializeFields(theArray.getClass()));
        assertTrue(theEventSerializationPolicy.shouldDeserializeFields(theArray.getClass()));
    }

    @Test
    public void testIsValid_NotSerializable() {
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertFalse(theEventSerializationPolicy.shouldSerializeFields(NotSerializableClass.class));
        assertFalse(theEventSerializationPolicy.shouldDeserializeFields(NotSerializableClass.class));
    }

    @Test
    public void testIsValid_NotSerializable_2() {
        NotSerializableClass[] theArray = new NotSerializableClass[0];
        
        final EventSerializationPolicy theEventSerializationPolicy = new EventSerializationPolicy();
        assertFalse(theEventSerializationPolicy.shouldSerializeFields(theArray.getClass()));
        assertFalse(theEventSerializationPolicy.shouldDeserializeFields(theArray.getClass()));
    }

    @Test
    public void testValidateSerialize() {
        try {
            new EventSerializationPolicy().validateSerialize(Event.class);
            new EventSerializationPolicy().validateSerialize(DomainEvent.class);
        } catch(SerializationException e) {
            fail("No exception was expected! " + e.getMessage());
        }
    }

    @Test
    public void testValidateDeserialize() {
        try {
            new EventSerializationPolicy().validateDeserialize(Event.class);
            new EventSerializationPolicy().validateDeserialize(DomainEvent.class);
        } catch(SerializationException e) {
            fail("No exception was expected! " + e.getMessage());
        }
    }

    private class SerializableClass implements Serializable {}

    private class IsSerializableClass implements IsSerializable {}

    private class NotSerializableClass {}
}