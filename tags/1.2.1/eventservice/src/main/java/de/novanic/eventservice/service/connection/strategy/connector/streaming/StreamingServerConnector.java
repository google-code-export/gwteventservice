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
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;
import de.novanic.eventservice.client.event.DomainEvent;
import de.novanic.eventservice.config.EventServiceConfiguration;
import de.novanic.eventservice.logger.ServerLogger;
import de.novanic.eventservice.logger.ServerLoggerFactory;
import de.novanic.eventservice.service.EventServiceException;
import de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnectorAdapter;
import de.novanic.eventservice.service.registry.user.UserInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector} implements
 * the streaming event listen method. Streaming means that the connection is hold open for a specified time and when an event
 * occurs, the answer / event is streamed directly to the client without closing and re-open the connection. The connection is
 * closed and re-opened (by the client) when the configured max. waiting time is reached.
 *
 * @author sstrohschein
 *         <br>Date: 15.03.2010
 *         <br>Time: 23:00:34
 */
public class StreamingServerConnector extends ConnectionStrategyServerConnectorAdapter implements Cloneable
{
    private static byte[] SCRIPT_TAG_PREFIX;
    private static byte[] SCRIPT_TAG_SUFFIX;
    private static byte[] CYCLE_TAG;

    private static final ServerLogger LOG = ServerLoggerFactory.getServerLogger(StreamingServerConnector.class.getName());

    private HttpServletResponse myResponse;
    private OutputStream myOutputStream;
    private SerializationPolicy mySerializationPolicy;

    /**
     * Creates a new {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector}.
     * The {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector} implements
     * the streaming event listen method.
     * @param aConfiguration configuration
     */
    public StreamingServerConnector(EventServiceConfiguration aConfiguration) throws EventServiceException {
        this(aConfiguration, new EventSerializationPolicy());
        SCRIPT_TAG_PREFIX = encode("<script type='text/javascript'>window.parent.receiveEvent('");
        SCRIPT_TAG_SUFFIX = encode("');</script>");
        CYCLE_TAG = encode("cycle");
    }

    /**
     * Creates a new {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector}.
     * The {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector} implements
     * the streaming event listen method.
     * @param aConfiguration configuration
     * @param aSerializationPolicy serialization policy to define the serialization of event (preparation for the transfer of events)
     */
    protected StreamingServerConnector(EventServiceConfiguration aConfiguration, SerializationPolicy aSerializationPolicy) {
        super(aConfiguration);
        mySerializationPolicy = aSerializationPolicy;
    }

    /**
     * Prepares the {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector} with a response.
     * The response is required to stream the events to the client. Therefore that method must be called before the listening for events starts.
     * @param aResponse response
     * @throws EventServiceException
     */
    public void prepare(HttpServletResponse aResponse) throws EventServiceException {
        myResponse = aResponse;
        try {
            myOutputStream = aResponse.getOutputStream();
        } catch(IOException e) {
            throw new EventServiceException("Error on using output stream of the response!", e);
        }
        myResponse.setContentType("text/html;charset=" + getEncoding());
        myResponse.setHeader("expires", "0");
        myResponse.setHeader("cache-control", "no-cache");
        myResponse.setHeader("transfer-encoding", "chunked");
    }

    /**
     * Listens for occurring events (can be retrieved from the {@link de.novanic.eventservice.service.registry.user.UserInfo} with
     * {@link de.novanic.eventservice.service.registry.user.UserInfo#retrieveEvents(int)}) and should prepare or transfer the retrieved events
     * directly. The reason for the listen and transfer preparation within one single method is, that the {@link de.novanic.eventservice.service.connection.strategy.connector.ConnectionStrategyServerConnector}
     * should have the control about listening and transfer of the occurred events.
     * The streaming implementation needs a response to stream the events to the clients. That can be prepared with
     * {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector#prepare(javax.servlet.http.HttpServletResponse)}.
     * @param aUserInfo {@link de.novanic.eventservice.service.registry.user.UserInfo} which holds new occurred events
     * @return occurred events
     * @throws EventServiceException
     */
    public List<DomainEvent> listen(UserInfo aUserInfo) throws EventServiceException {
        List<DomainEvent> theEvents = new ArrayList<DomainEvent>();
        try {
            //loops until the max. waiting time is exceed
            do {
                List<DomainEvent> theCurrentEvents = aUserInfo.retrieveEvents(getConfiguration().getMaxEvents());
                if(!theCurrentEvents.isEmpty()) {
                    aUserInfo.reportUserActivity();
                    theEvents.addAll(theCurrentEvents);
                    for(DomainEvent theEvent: theCurrentEvents) {
                        //serialization and escaping
                        String theSerializedEvent = serialize(theEvent);
                        theSerializedEvent = escapeSerializedData(theSerializedEvent);
                        //writing to the stream
                        printStatement(encode(theSerializedEvent), myOutputStream);
                    }
                    aUserInfo.reportUserActivity();
                }
            } while(!waitMaxWaitingTime(aUserInfo));
            //TODO think of a max. connection time, because max. waiting time describes the waiting time max. time between events and another time is required to define the max. connection time to avoid client side timeout detection

            //writing cycle command to the stream
            printStatement(CYCLE_TAG, myOutputStream);
        } catch(FlushException e) {
            LOG.debug(e.getMessage());
        } finally {
            try {
                close(myOutputStream);
            } catch(CloseException e) {
                LOG.debug(e.getMessage());
            }
        }
        return theEvents;
    }

    /**
     * A {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector} has to be
     * cloneable, because it isn't stateless caused by the necessary for a client dependent response
     * (see {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector#prepare(javax.servlet.http.HttpServletResponse)}).
     * @return the cloned {@link de.novanic.eventservice.service.connection.strategy.connector.streaming.StreamingServerConnector}
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Transforms an event to a String to make it streamable.
     * @param anEvent event to serialize
     * @return serialized event (the event as a String)
     * @throws EventServiceException
     */
    private String serialize(DomainEvent anEvent) throws EventServiceException {
    	try {
            ServerSerializationStreamWriter theServerSerializationStreamWriter = new ServerSerializationStreamWriter(mySerializationPolicy);
    		theServerSerializationStreamWriter.setFlags(0);
    		theServerSerializationStreamWriter.prepareToWrite();

			theServerSerializationStreamWriter.serializeValue(anEvent, DomainEvent.class);

			return theServerSerializationStreamWriter.toString();
    	} catch(SerializationException e) {
			throw new EventServiceException("Error on serializing the event \"" + anEvent
                    + "\" for domain \"" + anEvent.getDomain() + "\"!", e);
		}
    }

    /**
     * Escapes the serialized data.
     * @param aSerializedData serialized data to escape
     * @return escaped serialized data
     */
    private String escapeSerializedData(String aSerializedData) {
    	String theEscapedData = aSerializedData;
    	theEscapedData = theEscapedData.replaceAll("\\\\", "\\\\\\\\");
    	theEscapedData = theEscapedData.replaceAll("\\\'", "\\\\\'");
    	return theEscapedData;
    }

    /**
     * Prints a statement to a stream.
     * @param aStatement statement to print
     * @param anOutputStream stream
     * @throws EventServiceException
     */
    private void printStatement(byte[] aStatement, OutputStream anOutputStream) throws EventServiceException, FlushException {
        try {
            anOutputStream.write(SCRIPT_TAG_PREFIX);
            anOutputStream.write(aStatement);
            anOutputStream.write(SCRIPT_TAG_SUFFIX);
        } catch(IOException e) {
            throw new EventServiceException("Error on printing statement \"" + new String(aStatement) + "\"!", e);
        } finally {
            flush(aStatement, anOutputStream);
        }
    }

    private void flush(byte[] aStatement, OutputStream anOutputStream) throws FlushException {
        try {
            anOutputStream.flush();
            myResponse.flushBuffer();
        } catch(IOException e) {
            throw new FlushException(aStatement, e);
        }
    }

    private void close(OutputStream anOutputStream) throws CloseException {
        try {
            anOutputStream.close();
        } catch(IOException e) {
            throw new CloseException(e);
        }
    }

    private static class CloseException extends Exception
    {
        private CloseException(Throwable aThrowable) {
            super("Error on closing output stream!", aThrowable);
        }
    }

    private static class FlushException extends Exception
    {
        private FlushException(byte[] aFlushingStatement, Throwable aThrowable) {
            super(createMessage(aFlushingStatement), aThrowable);
        }

        private static String createMessage(byte[] aFlushingStatement) {
            return "Flushing wasn't successful (\"" + new String(aFlushingStatement) + "\")!";
        }
    }
}
