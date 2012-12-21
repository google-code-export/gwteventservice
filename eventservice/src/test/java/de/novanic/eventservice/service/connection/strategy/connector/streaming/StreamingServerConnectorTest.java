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
package de.novanic.eventservice.service.connection.strategy.connector.streaming;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.config.ConfigParameter;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnectorTest;
import de.novanic.eventservice.service.registry.user.UserInfo;
import de.novanic.eventservice.test.testhelper.DummyEvent;
import de.novanic.eventservice.test.testhelper.DummyServletOutputStream;
import org.easymock.EasyMock;

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
public class StreamingServerConnectorTest extends ConnectionStrategyServerConnectorTest
{
    public void testPrepare() throws Exception {
        HttpServletResponse theResponseMock = EasyMock.createMock(HttpServletResponse.class);

        theResponseMock.setContentType(EasyMock.<String>anyObject());

        theResponseMock.setHeader(EasyMock.<String>anyObject(), EasyMock.<String>anyObject());
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(theResponseMock.getOutputStream()).andReturn(new DummyServletOutputStream(new ByteArrayOutputStream()));

        EasyMock.replay(theResponseMock);
            StreamingServerConnector theStreamingServerConnector = new StreamingServerConnector(createConfiguration(0, 700, 90000));
            theStreamingServerConnector.prepare(theResponseMock);
        EasyMock.verify(theResponseMock);
        EasyMock.reset(theResponseMock);
    }

    public void testPrepare_Error() throws Exception {
        HttpServletResponse theResponseMock = EasyMock.createMock(HttpServletResponse.class);

        EasyMock.expect(theResponseMock.getOutputStream()).andThrow(new IOException("Test-Exception"));

        EasyMock.replay(theResponseMock);
            StreamingServerConnector theStreamingServerConnector = new StreamingServerConnector(createConfiguration(0, 700, 90000));
            try {
                theStreamingServerConnector.prepare(theResponseMock);
                fail("EventServiceException expected!");
            } catch(EventServiceException e) {}
        EasyMock.verify(theResponseMock);
        EasyMock.reset(theResponseMock);
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

        final String theOutput = theByteArrayOutputStream.toString();
        assertContainsScriptReceivedEvent(theOutput);
        assertContainsScriptCycle(theOutput);
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

        final EventServiceConfiguration theConfiguration = createConfiguration(0, 700, 90000);
        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(theByteArrayOutputStream, new DummyEventSerializationPolicy(), theConfiguration);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        EventServiceException theOccurredException = theListenRunnable.getOccurredException();
        assertNotNull(theOccurredException);
        assertTrue(theOccurredException.getCause() instanceof SerializationException);
    }

    public void testListen_Error_5() throws Exception {
        final Domain theDomain = DomainFactory.getDomain("test_domain");
        final UserInfo theUserInfo = new UserInfo("test_user");

        ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();

        final EventServiceConfiguration theConfiguration = createConfiguration(0, 700, 90000);
        theConfiguration.getConfigMap().put(ConfigParameter.CONNECTION_STRATEGY_ENCODING, "XYZ");
        theConfiguration.getConfigMap().put(ConfigParameter.FQ_CONNECTION_STRATEGY_ENCODING, "XYZ");
        StreamingServerConnector theStreamingServerConnector = createStreamingServerConnector(theByteArrayOutputStream, new EventSerializationPolicy(), theConfiguration);

        ListenRunnable theListenRunnable = new ListenRunnable(theStreamingServerConnector, theUserInfo);
        Thread theListenThread = new Thread(theListenRunnable);
        theListenThread.start();

        theUserInfo.addEvent(theDomain, new DummyEvent());

        theListenThread.join();

        EventServiceException theOccurredException = theListenRunnable.getOccurredException();
        assertNotNull(theOccurredException);
        assertTrue(theOccurredException.getCause() instanceof UnsupportedEncodingException);
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

        final String theOutput = theByteArrayOutputStream.toString();
        assertContainsScriptReceivedEvent(theOutput);
        assertContainsScriptCycle(theOutput);
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
        assertEquals(1, theListenResult.getEvents().size());
        assertTrue(theListenResult.getDuration() >= 500 && theListenResult.getDuration() < 8000);

        final String theOutput = theByteArrayOutputStream.toString();
        assertContainsScriptReceivedEvent(theOutput);
        assertContainsScriptCycle(theOutput);
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

        assertContainsScriptCycle(theByteArrayOutputStream.toString());
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

    public void testGetEncoding() throws Exception {
        testGetEncoding(StreamingServerConnector.class);
    }

    public void testGetEncoding_Error() throws Exception {
        testGetEncoding_Error(StreamingServerConnector.class);
    }

    private StreamingServerConnector createStreamingServerConnector(int aMaxWaitingTime, OutputStream anOutputStream) throws EventServiceException, IOException {
        final EventServiceConfiguration theConfiguration = createConfiguration(0, aMaxWaitingTime, 90000);
        return createStreamingServerConnector(anOutputStream, null, theConfiguration);
    }

    private StreamingServerConnector createStreamingServerConnector(OutputStream anOutputStream, SerializationPolicy aSerializationPolicy, EventServiceConfiguration aConfiguration) throws EventServiceException, IOException {
        HttpServletResponse theResponseMock = EasyMock.createMock(HttpServletResponse.class);

        theResponseMock.setContentType(EasyMock.<String>anyObject());

        theResponseMock.setHeader(EasyMock.<String>anyObject(), EasyMock.<String>anyObject());
        EasyMock.expectLastCall().anyTimes();

        EasyMock.expect(theResponseMock.getOutputStream()).andReturn(new DummyServletOutputStream(anOutputStream));

        EasyMock.replay(theResponseMock);
            final StreamingServerConnector theStreamingServerConnector;
            if(aSerializationPolicy != null) {
                theStreamingServerConnector = new StreamingServerConnector(aConfiguration, aSerializationPolicy);
            } else {
                theStreamingServerConnector = new StreamingServerConnector(aConfiguration);
            }
            theStreamingServerConnector.prepare(theResponseMock);
        EasyMock.verify(theResponseMock);
        EasyMock.reset(theResponseMock);

        return theStreamingServerConnector;
    }

    private static void assertContainsScriptReceivedEvent(String aContent) {
        assertNotNull(aContent);
        //check script start for received events
        assertTrue(aContent.contains("<script type='text/javascript'>window.parent.receiveEvent('"));
        //check received event class
        assertTrue(aContent.contains("\"de.novanic.eventservice.client.event.DefaultDomainEvent/"));
        //check received domain class
        assertTrue(aContent.contains("\"de.novanic.eventservice.client.event.domain.DefaultDomain/"));
        //check script end for received events
        assertTrue(aContent.contains("');</script>"));
    }

    private static void assertContainsScriptCycle(String aContent) {
        assertNotNull(aContent);
        assertTrue(aContent.contains("<script type='text/javascript'>window.parent.receiveEvent('cycle');</script>"));
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