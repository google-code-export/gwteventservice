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

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.connection.strategy.connector.ServerEventConnectorTest;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.DummyServletOutputStream;
import org.easymock.MockControl;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author sstrohschein
 *         <br>Date: 25.04.2010
 *         <br>Time: 15:56:39
 */
public class StreamingServerConnectorTest extends ServerEventConnectorTest
{
    public void testPrepare() throws Exception {
        MockControl<HttpServletResponse> theResponseMockControl = MockControl.createControl(HttpServletResponse.class);
        HttpServletResponse theResponseMock = theResponseMockControl.getMock();

        theResponseMock.setContentType(null);
        theResponseMockControl.setDefaultVoidCallable();

        theResponseMock.setHeader(null, null);
        theResponseMockControl.setDefaultVoidCallable();

        theResponseMock.getOutputStream();
        theResponseMockControl.setReturnValue(new DummyServletOutputStream(new ByteArrayOutputStream()));

        theResponseMockControl.replay();
            StreamingServerConnector theStreamingServerConnector = new StreamingServerConnector(createConfiguration(0, 700, 90000));
            theStreamingServerConnector.prepare(theResponseMock);
        theResponseMockControl.verify();
        theResponseMockControl.reset();
    }

    public void testPrepare_Error() throws Exception {
        MockControl<HttpServletResponse> theResponseMockControl = MockControl.createControl(HttpServletResponse.class);
        HttpServletResponse theResponseMock = theResponseMockControl.getMock();

        theResponseMock.getOutputStream();
        try {
            throw new IOException("Test-Exception");
        } catch(IOException e) {
            theResponseMockControl.setThrowable(e);
        }

        theResponseMockControl.replay();
            StreamingServerConnector theStreamingServerConnector = new StreamingServerConnector(createConfiguration(0, 700, 90000));
            try {
                theStreamingServerConnector.prepare(theResponseMock);
                fail("EventServiceException expected!");
            } catch(EventServiceException e) {}
        theResponseMockControl.verify();
        theResponseMockControl.reset();
    }

    public void testListen() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 600);

        assertEquals(theDomain, theListenResult.getEvents().get(0).getDomain());

        assertTrue(theByteArrayOutputStream.toString().contains("DummyEvent"));
        assertTrue(theByteArrayOutputStream.toString().contains("test_domain"));
        assertFalse(theByteArrayOutputStream.toString().contains("test_domain_2"));
        assertEquals("<script type='text/javascript'>window.parent.receiveEvent('[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",\"de.novanic.eventservice.test.testhelper.DummyEvent/2679388400\"],0,5]');</script><script type='text/javascript'>window.parent.receiveEvent('cycle');</script>", theByteArrayOutputStream.toString());
    }

    public void testListen_2() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final Domain theDomain_2 = DomainFactory.getDomain("test_domain_2");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());
        Thread.sleep(200);
        theUserInfo.addEvent(theDomain_2, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(2, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 600);

        assertEquals(theDomain, theListenResult.getEvents().get(0).getDomain());
        assertEquals(theDomain_2, theListenResult.getEvents().get(1).getDomain());

        assertTrue(theByteArrayOutputStream.toString().contains("DummyEvent"));
        assertTrue(theByteArrayOutputStream.toString().contains("test_domain"));
        assertTrue(theByteArrayOutputStream.toString().contains("test_domain_2"));
    }

    public void testListen_Error() throws Exception {
        final UserInfo theUserInfo = new UserInfo("test_user");

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(0, new DummyServletOutputStreamNotClosable());

        TestLoggingHandler theTestLoggingHandler = new TestLoggingHandler();

        Logger theLogger = Logger.getLogger(StreamingServerConnector.class.getName());
        final Level theOldLevel = theLogger.getLevel();
        try {
            theLogger.setLevel(Level.FINEST);
            theLogger.addHandler(theTestLoggingHandler);

            ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
            Thread theListenThread = new Thread(theListenRunnable);
            theListenThread.start();
            theListenThread.join();

            assertNotNull(theTestLoggingHandler.getLastMessage());
            assertTrue(theTestLoggingHandler.getLastMessage().contains("close") || theTestLoggingHandler.getLastMessage().contains("closing"));
        } finally {
            theLogger.setLevel(theOldLevel);
            theLogger.removeHandler(theTestLoggingHandler);
        }
    }

    public void testListen_Error_2() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(0, new DummyServletOutputStreamNotFlushable());

        TestLoggingHandler theTestLoggingHandler = new TestLoggingHandler();

        Logger theLogger = Logger.getLogger(StreamingServerConnector.class.getName());
        final Level theOldLevel = theLogger.getLevel();
        try {
            theLogger.setLevel(Level.FINEST);
            theLogger.addHandler(theTestLoggingHandler);

            ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
            Thread theListenThread = new Thread(theListenRunnable);
            theListenThread.start();

            theUserInfo.addEvent(theDomain, new DummyEvent());

            theListenThread.join();

            assertNotNull(theTestLoggingHandler.getLastMessage());
            assertTrue(theTestLoggingHandler.getLastMessage().contains("Flush") || theTestLoggingHandler.getLastMessage().contains("flush"));
        } finally {
            theLogger.setLevel(theOldLevel);
            theLogger.removeHandler(theTestLoggingHandler);
        }
    }

    public void testListen_Error_3() throws Exception {
        final UserInfo theUserInfo = new UserInfo("test_user");

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(0, new DummyServletOutputStreamNotWritable());

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();
        theListenThread.join();

        EventServiceException theOccurredException = theListenRunnable.getOccurredException();
        assertNotNull(theOccurredException);
        assertTrue(theOccurredException.getCause() instanceof IOException);
    }

    public void testListen_Error_4() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream, new DummyEventSerializationPolicy());

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        EventServiceException theOccurredException = theListenRunnable.getOccurredException();
        assertNotNull(theOccurredException);
        assertTrue(theOccurredException.getCause() instanceof SerializationException);
    }

    /**
     * Min. waiting has no effect for streaming, because the connection is always still opened for the max. waiting time.
     * @throws Exception exception
     */
    public void testListen_Min_Waiting() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 600);

        assertEquals("<script type='text/javascript'>window.parent.receiveEvent('[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",\"de.novanic.eventservice.test.testhelper.DummyEvent/2679388400\"],0,5]');</script><script type='text/javascript'>window.parent.receiveEvent('cycle');</script>", theByteArrayOutputStream.toString());
    }

    /**
     * Min. waiting has no effect for streaming, because the connection is always still opened for the max. waiting time.
     * @throws Exception exception
     */
    public void testListen_Min_Waiting_2() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        System.out.println(theListenResult.getDuration());
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 500 && theListenResult.getDuration() < 8000);

        assertEquals("<script type='text/javascript'>window.parent.receiveEvent('[4,3,2,1,[\"de.novanic.eventservice.client.event.DefaultDomainEvent/3924906731\",\"de.novanic.eventservice.client.event.domain.DefaultDomain/240262385\",\"test_domain\",\"de.novanic.eventservice.test.testhelper.DummyEvent/2679388400\"],0,5]');</script><script type='text/javascript'>window.parent.receiveEvent('cycle');</script>", theByteArrayOutputStream.toString());
    }

    public void testListen_Max_Waiting() throws Exception {
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theListenThread.join();

        ListenResult theListenResult = theListenRunnable.getListenResult();
        assertEquals(0, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 600);

        assertEquals("<script type='text/javascript'>window.parent.receiveEvent('cycle');</script>", theByteArrayOutputStream.toString());
    }

    public void testListen_Max_Waiting_Concurrency() throws Exception {
        final UserInfo theUserInfo = new UserInfo("test_user") {
            private boolean isFirstCall = true;
            private boolean isSecondCall = false;

            public boolean isEventsEmpty() {
                if(isFirstCall) {
                    isFirstCall = false;
                    isSecondCall = true;
                    return true;
                } else if(isSecondCall) {
                    isSecondCall = false;
                    return false;
                } else {
                    return true;
                }
            }
        };

        long theLastActivationTime = theUserInfo.getLastActivityTime();
        long theStartTime = System.currentTimeMillis();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, new ByteArrayOutputStream());
        theStreamingServerConnector.listen(theUserInfo);

        long theEndTime = System.currentTimeMillis();
        assertTrue(theEndTime - theStartTime >= 600); //one cycle is expected...
        assertEquals(theLastActivationTime, theUserInfo.getLastActivityTime()); //..., but no user activity was reported, caused by the failed double-check
    }

    public void testClone() throws Exception {
        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(700, theByteArrayOutputStream);

        StreamingServerConnector theClone = (StreamingServerConnector)theStreamingServerConnector.clone();
        assertNotNull(theClone);
        assertNotSame(theStreamingServerConnector, theClone);
        
        StreamingServerConnector theClone_2 = (StreamingServerConnector)theStreamingServerConnector.clone();
        assertNotNull(theClone_2);
        assertNotSame(theClone, theClone_2);
        assertNotSame(theStreamingServerConnector, theClone_2);
    }

    private StreamingServerConnector createStreamingServerConnector(int aMaxWaitingTime, OutputStream anOutputStream) throws EventServiceException, IOException {
        return createStreamingServerConnector(aMaxWaitingTime, anOutputStream, null);
    }

    private StreamingServerConnector createStreamingServerConnector(int aMaxWaitingTime, OutputStream anOutputStream, SerializationPolicy aSerializationPolicy) throws EventServiceException, IOException {
        MockControl<HttpServletResponse> theResponseMockControl = MockControl.createControl(HttpServletResponse.class);
        HttpServletResponse theResponseMock = theResponseMockControl.getMock();

        theResponseMock.setContentType(null);
        theResponseMockControl.setDefaultVoidCallable();

        theResponseMock.setHeader(null, null);
        theResponseMockControl.setDefaultVoidCallable();

        theResponseMock.getOutputStream();
        theResponseMockControl.setReturnValue(new DummyServletOutputStream(anOutputStream));

        theResponseMockControl.replay();
            final EventServiceConfiguration theConfiguration = createConfiguration(0, aMaxWaitingTime, 90000);
            final StreamingServerConnector theStreamingServerConnector;
            if(aSerializationPolicy != null) {
                theStreamingServerConnector = new StreamingServerConnector(theConfiguration, aSerializationPolicy);
            } else {
                theStreamingServerConnector = new StreamingServerConnector(theConfiguration);
            }
            theStreamingServerConnector.prepare(theResponseMock);
        theResponseMockControl.verify();
        theResponseMockControl.reset();

        return theStreamingServerConnector;
    }

    private class DummyServletOutputStreamNotWritable extends DummyServletOutputStream
    {
        private DummyServletOutputStreamNotWritable() {
            super(new ByteArrayOutputStream());
        }

        public void write(byte[] b) throws IOException {
            throw new IOException("Test-Exception!");
        }

        public void write(byte[] b, int off, int len) throws IOException {
            throw new IOException("Test-Exception!");
        }
    }

    private class DummyServletOutputStreamNotClosable extends DummyServletOutputStream
    {
        private DummyServletOutputStreamNotClosable() {
            super(new ByteArrayOutputStream());
        }

        public void close() throws IOException {
            throw new IOException("Test-Exception!");
        }
    }

    private class DummyServletOutputStreamNotFlushable extends DummyServletOutputStream
    {
        private DummyServletOutputStreamNotFlushable() {
            super(new ByteArrayOutputStream());
        }

        public void flush() throws IOException {
            throw new IOException("Test-Exception!");
        }
    }

    private class DummyEventSerializationPolicy extends EventSerializationPolicy
    {
        public void validateSerialize(Class<?> aClass) throws SerializationException {
            throw new SerializationException("Test-Exception");
        }
    }

    private class TestLoggingHandler extends Handler
    {
        private String myLastMessage;

        public void publish(LogRecord aRecord) {
            myLastMessage = aRecord.getMessage();
        }

        public void flush() {}

        public void close() throws SecurityException {}

        public String getLastMessage() {
            return myLastMessage;
        }
    }
}