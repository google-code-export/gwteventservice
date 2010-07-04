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
package de.novanic.eventservice.client.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.config.EventServiceConfigurationTransferable;
import de.novanic.eventservice.client.connection.strategy.connector.ConnectionStrategyClientConnector;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.command.ClientCommand;
import de.novanic.eventservice.client.event.command.InitEventServiceCommand;
import de.novanic.eventservice.client.event.command.schedule.ClientCommandSchedulerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The RemoteEventServiceAccessor provides general methods for command execution and manages the
 * {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}.
 *
 * @author sstrohschein
 *         <br>Date: 04.07.2010
 *         <br>Time: 13:21:19
 */
public abstract class RemoteEventServiceAccessor
{
    private RemoteEventConnector myRemoteEventConnector;
    private Queue<ClientCommand<?>> myClientCommandQueue;
    private boolean isSessionInitialized;

    /**
     * Constructor of the AbstractRemoteEventService to manage the {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector} for the connection
     * between client side and server side
     */
    protected RemoteEventServiceAccessor(RemoteEventConnector aRemoteEventConnector) {
        myRemoteEventConnector = aRemoteEventConnector;
    }

    /**
     * Checks if the RemoteEventService is active (listening).
     * @return true when active/listening, otherwise false
     */
    protected boolean isListenActive() {
        return myRemoteEventConnector.isActive();
    }

    /**
     * Starts the init command and schedules all other commands till the init command is finished. That must be done
     * to avoid double initialized sessions (race condition GWT issue 1846).
     * @param aClientCommand command to schedule
     * @param <R> Return type of the command callback
     */
    protected <R> void schedule(final ClientCommand<R> aClientCommand) {
        if(myClientCommandQueue == null) {
            myClientCommandQueue = new LinkedList<ClientCommand<?>>();
            InitEventServiceCommand theInitCommand = new InitEventServiceCommand(getRemoteEventConnector(), new InitCommandCallback());
            theInitCommand.execute();
        }
        myClientCommandQueue.add(aClientCommand);
        executeCommands();
    }

    /**
     * Executes the scheduled commands ({@link ClientCommand}. The commands can be scheduled with
     * {@link RemoteEventServiceAccessor#schedule(de.novanic.eventservice.client.event.command.ClientCommand)}.
     */
    private void executeCommands() {
        if(isSessionInitialized) {
            ClientCommand<?> theClientCommand;
            while((theClientCommand = myClientCommandQueue.poll()) != null) {
                theClientCommand.execute();
            }
        }
    }

    /**
     * Returns the managed {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}.
     * @return managed {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}
     */
    protected RemoteEventConnector getRemoteEventConnector() {
        return myRemoteEventConnector;
    }

    /**
     * Callback for the init command.
     */
    private class InitCommandCallback implements AsyncCallback<EventServiceConfigurationTransferable>
    {
        /**
         * Executes the scheduled commands on success.
         * @param aConfiguration configuration for the client side
         */
        public void onSuccess(EventServiceConfigurationTransferable aConfiguration) {
            ConfigurationTransferableDependentFactory theConfigDependentFactory = ConfigurationTransferableDependentFactory.getInstance(aConfiguration);
            ConnectionStrategyClientConnector theConnectionStrategyClientConnector = theConfigDependentFactory.getConnectionStrategyClientConnector();
            getRemoteEventConnector().initListen(theConnectionStrategyClientConnector);
            finishFirstCall();
        }

        /**
         * Throws a runtime exception when the event service couldn't be activated / initialized.
         * @param aThrowable throwable caused by a failed server call
         */
        public void onFailure(Throwable aThrowable) {
            throw new RemoteEventServiceRuntimeException("Error on activating / initializing \"" + RemoteEventService.class.getName() + "\"!", aThrowable);
        }

        /**
         * Executes the scheduled commands.
         */
        private void finishFirstCall() {
            //Schedule the next command after the callback is finished. The timer is needed, because some browsers doesn't
            //notice the server call cycle, when the next command is executed directly.
            ClientCommandSchedulerFactory.getInstance().getClientCommandScheduler().schedule(new ClientCommand<Void>() {
                public void execute() {
                    isSessionInitialized = true;
                    executeCommands();
                }

                public AsyncCallback<Void> getCommandCallback() { return null; }
            });
        }
    }

    /**
     * Empty callback
     */
    protected static class VoidAsyncCallback implements AsyncCallback<Void>
    {
        public void onFailure(Throwable aThrowable) {}

        public void onSuccess(Void aResult) {}
    }
}