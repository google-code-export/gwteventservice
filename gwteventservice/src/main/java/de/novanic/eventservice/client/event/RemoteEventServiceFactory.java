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
package de.novanic.eventservice.client.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.ClientHandler;
import de.novanic.eventservice.client.DefaultClientHandler;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.connection.callback.AsyncCallbackWrapper;
import de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector;
import de.novanic.eventservice.client.event.command.ClientCommand;
import de.novanic.eventservice.client.event.service.creator.DefaultEventServiceCreator;
import de.novanic.eventservice.client.event.service.creator.EventServiceCreator;

/**
 * The RemoteEventServiceFactory is used to create the RemoteEventService and to ensure that only one instance of
 * RemoteEventServiceFactory and RemoteEventService exists (singleton).
 * The factory does also provide methods to create service mappings and handlers.
 * @see DefaultRemoteEventService
 *
 * @author sstrohschein
 * <br>Date: 08.06.2008
 * <br>Time: 14:44:17
 */
public class RemoteEventServiceFactory
{
    private volatile RemoteEventService myRemoteEventService;

    /**
     * The RemoteEventServiceFactory should be created via the getInstance method.
     * @see RemoteEventServiceFactory#getInstance()
     */
    protected RemoteEventServiceFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class RemoteEventServiceFactoryHolder {
        private static RemoteEventServiceFactory INSTANCE = createRemoteEventServiceFactory();
    }

    /**
     * This method should be used to create an instance of RemoteEventServiceFactory.
     * RemoteEventServiceFactory is a singleton, so this method returns always the same instance of
     * RemoteEventServiceFactory.
     * @return RemoteEventServiceFactory (singleton)
     */
    public static RemoteEventServiceFactory getInstance() {
        return RemoteEventServiceFactoryHolder.INSTANCE;
    }

    /**
     * This method should be used to create an instance of RemoteEventService.
     * RemoteEventService is a singleton, so this method returns always the same instance of RemoteEventService.
     * @return RemoteEventService (singleton)
     */
    public RemoteEventService getRemoteEventService() {
        if(myRemoteEventService != null) {
            return myRemoteEventService;
        }
        EventServiceCreator theEventServiceCreator = DefaultEventServiceCreator.getInstance();
        return getRemoteEventService(new GWTRemoteEventConnector(theEventServiceCreator));
    }

    /**
     * This method should be used to create an instance of RemoteEventService.
     * RemoteEventService is a singleton, so this method returns always the same instance of RemoteEventService.
     * @param aRemoteEventConnector {@link de.novanic.eventservice.client.connection.strategy.connector.RemoteEventConnector}
     * to specify the connection to the server side
     * @return RemoteEventService (singleton)
     */
    public RemoteEventService getRemoteEventService(RemoteEventConnector aRemoteEventConnector) {
        if(myRemoteEventService == null) {
            synchronized(this) {
                if(myRemoteEventService == null) {
                    myRemoteEventService = new DefaultRemoteEventService(aRemoteEventConnector);
                }
            }
        }
        return myRemoteEventService;
    }

    /**
     * Creates an instance of {@link RemoteEventServiceFactory} (everytime a new instance).
     * @return new instance of {@link RemoteEventServiceFactory} 
     */
    private static RemoteEventServiceFactory createRemoteEventServiceFactory() {
        return new RemoteEventServiceFactory();
    }

    /**
     * Requests a {@link de.novanic.eventservice.client.ClientHandler} which contains the client-/connection-id and
     * provides it via the callback. A server-call is only executed when no other server call was executed before because
     * at least one server call has to be executed before to generate the client-/connection-id at the server-side.
     * @param anAsyncCallback callback with a {@link de.novanic.eventservice.client.ClientHandler}
     */
    public void requestClientHandler(final AsyncCallback<ClientHandler> anAsyncCallback) {
        RemoteEventServiceAccessor theRemoteEventServiceAccessor = (RemoteEventServiceAccessor)getRemoteEventService();
        theRemoteEventServiceAccessor.schedule(new GetClientIdCommand(new AsyncCallbackWrapper<ClientHandler>(anAsyncCallback) {
            public void onSuccess(ClientHandler aClientHandler) {
                String theConnectionId = ConfigurationTransferableDependentFactory.getConfiguration().getConnectionId();
                ClientHandler theClientHandler = new DefaultClientHandler(theConnectionId);
                super.onSuccess(theClientHandler);
            }
        }));
    }

    /**
     * Registers an user-/client-specific {@link de.novanic.eventservice.client.ClientHandler} to an existing service instance to provide the connection-/client-id with every request / server-call.
     * That makes it possible to add user-specific events or domain-user-specific EventFilters dynamically when the {@link de.novanic.eventservice.client.ClientHandler}
     * is provided. The {@link de.novanic.eventservice.client.ClientHandler} can be got from
     * {@link de.novanic.eventservice.client.event.RemoteEventServiceFactory#requestClientHandler(com.google.gwt.user.client.rpc.AsyncCallback)}.
     * The {@link de.novanic.eventservice.client.ClientHandler} could also be transferred manually to your custom service to manage or use the connection-/client-ids
     * for user-specific events or domain-user-specific EventFilters.
     * The service could extend from RemoteEventServiceServlet or use the EventExecutorService to execute events.
     * Events (also user-specific) can also be executed directly from the client-side (see {@link de.novanic.eventservice.client.event.RemoteEventService}).
     * @param anAsyncServiceInstance an async service (instance) which needs to add user-specific events or domain-user-specific EventFilters dynamically from the server-side
     * @param aClientHandler {@link de.novanic.eventservice.client.ClientHandler} to provide the connection-/client-id for user-specific events or domain-user-specific EventFilters
     * @return (re-)mapped service instance with a provided {@link de.novanic.eventservice.client.ClientHandler}
     */
    public void registerClientSpecificHandler(ServiceDefTarget anAsyncServiceInstance, ClientHandler aClientHandler) {
        StringBuilder theServiceURLStringBuilder = new StringBuilder(anAsyncServiceInstance.getServiceEntryPoint());
        theServiceURLStringBuilder.append("?id=");
        theServiceURLStringBuilder.append(aClientHandler.getConnectionId());
        
        anAsyncServiceInstance.setServiceEntryPoint(theServiceURLStringBuilder.toString());
    }

    /**
     * That method should only be used in TestCases, because it resets the factory and the factory can't ensure
     * anymore that only one instance exists!
     */
    public static void reset() {
        RemoteEventServiceFactoryHolder.INSTANCE = createRemoteEventServiceFactory();
    }

    /**
     * That is a simple internal command which is used to ensure that the init command ({@link de.novanic.eventservice.client.event.command.InitEventServiceCommand})
     * was already be executed. The initialization is needed, because the the connection-/client-id is generated at the service-side.
     * That command doesn't execute a server-call directly. A server-call is only executed from {@link de.novanic.eventservice.client.event.command.InitEventServiceCommand}
     * when it wasn't done before.
     */
    private class GetClientIdCommand implements ClientCommand<ClientHandler>
    {
        private AsyncCallback<ClientHandler> myCallback;

        /**
         * Creates a new command with a provided callback. The {@link com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(Object)} method
         * is called on the execution of this command.
         * @param aCallback callback
         */
        private GetClientIdCommand(AsyncCallback<ClientHandler> aCallback) {
            myCallback = aCallback;
        }

        /**
         * The {@link com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(Object)} method of the provided callback
         * is called on the execution of this command.
         */
        public void execute() {
            getCommandCallback().onSuccess(null);
        }

        /**
         * Returns the provided callback.
         * @return provided callback
         */
        public AsyncCallback getCommandCallback() {
            return myCallback;
        }
    }
}