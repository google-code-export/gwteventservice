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
package de.novanic.eventservice.clientmock.connection.strategy.connector.streaming.specific;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;
import de.novanic.eventservice.client.connection.strategy.connector.streaming.specific.GWTStreamingClientConnectorGecko;
import de.novanic.eventservice.test.testhelper.PrivateMethodExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

/**
 * @author sstrohschein
 *         <br>Date: 24.10.2010
 *         <br>Time: 23:39:12
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("de.novanic.eventservice.client.connection.strategy.connector.streaming.specific.GWTStreamingClientConnectorGecko")
@PrepareForTest({GWTStreamingClientConnectorGecko.class, Element.class})
public class GWTStreamingClientConnectorGeckoInitializationTest
{
    @Test
    public void testCreateFrameElement() throws Exception {
        PowerMockito.mockStatic(Element.class);
        Element theElementMock = PowerMockito.mock(Element.class);

        Frame theFrameMock = mockInitFrame();
        when(theFrameMock.getElement()).thenReturn(theElementMock);

        PrivateMethodExecutor<GWTStreamingClientConnectorGecko> thePrivateMethodExecutor = new PrivateMethodExecutor<GWTStreamingClientConnectorGecko>(GWTStreamingClientConnectorGecko.class);
        thePrivateMethodExecutor.executePrivateMethod("createFrameElement");
    }

    private static Frame mockInitFrame() throws Exception {
        GWTMockUtilities.disarm();

        Frame theFrameMock = mock(Frame.class);
        PowerMockito.whenNew(Frame.class).withNoArguments().thenReturn(theFrameMock);

        GWTMockUtilities.restore();

        return theFrameMock;
    }
}