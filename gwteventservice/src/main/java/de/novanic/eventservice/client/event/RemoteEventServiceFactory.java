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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import de.novanic.eventservice.client.ClientHandler;
import de.novanic.eventservice.client.DefaultClientHandler;
import de.novanic.eventservice.client.config.ConfigurationTransferableDependentFactory;
import de.novanic.eventservice.client.connection.callback.AsyncCallbackWrapper;
import de.novanic.eventservice.client.event.command.ClientCommand;

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
     * The session is needed to generate the client/user id.
     * @return RemoteEventService (singleton)
     */
    public RemoteEventService getRemoteEventService() {
        if(myRemoteEventService == null) {
            synchronized(this) {
                if(myRemoteEventService == null) {
                    myRemoteEventService = new DefaultRemoteEventService(new GWTRemoteEventConnector());
                }
            }
        }
        return myRemoteEventService;
    }

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
     * Creates a new service instance (with the mapping classes) which can be able to execute events from the server-side.
     * The service could extend from RemoteEventServiceServlet or use the EventExecutorService.
     * Events (also user-specific) can also be executed directly from the client-side (see {@link de.novanic.eventservice.client.event.RemoteEventService}).
     * When the service should be able to add user-specific events or domain-user-specific EventFilters dynamically please use the variant
     * with a {@link de.novanic.eventservice.client.ClientHandler} because the {@link de.novanic.eventservice.client.ClientHandler}
     * or the connection-/client-id is required for user-specific actions. That can be realized with the variant which requires a
     * {@link de.novanic.eventservice.client.ClientHandler} ({@link de.novanic.eventservice.client.event.RemoteEventServiceFactory#createEventExecutionService(String, Class, Class, de.novanic.eventservice.client.ClientHandler)})
     * or via transferring the {@link de.novanic.eventservice.client.ClientHandler} manually to your custom service
     * (creation via {@link de.novanic.eventservice.client.event.RemoteEventServiceFactory#requestClientHandler(com.google.gwt.user.client.rpc.AsyncCallback)}).
     * @param aServiceURL URL of the service mapping
     * @param anEventExecutionServiceInterface implementing interface of the service
     * @param anEventExecutionServiceAsyncInterface async-interface of the service
     * @param <EES> generic for the implementing interface of the service
     * @param <EESA> generic for the async-interface of the service
     * @return new service instance
     */
    public <EES extends RemoteService, EESA> EESA createEventExecutionService(String aServiceURL,
                                                                              Class<EES> anEventExecutionServiceInterface,
                                                                              Class<EESA> anEventExecutionServiceAsyncInterface) {
        return createEventExecutionService(aServiceURL, anEventExecutionServiceInterface, anEventExecutionServiceAsyncInterface, null);
    }

    /**
     * Creates a new service instance (with the mapping classes) which can be able to execute events from the server-side.
     * The service could extend from RemoteEventServiceServlet or use the EventExecutorService.
     * User-specific events and domain-user-specific EventFilters can also be added and changed dynamically with that service
     * when the {@link de.novanic.eventservice.client.ClientHandler} is provided. The {@link de.novanic.eventservice.client.ClientHandler}
     * can be got from {@link de.novanic.eventservice.client.event.RemoteEventServiceFactory#requestClientHandler(com.google.gwt.user.client.rpc.AsyncCallback)}.
     * The {@link de.novanic.eventservice.client.ClientHandler} could also be transferred manually to your custom service to manage or use the connection-/client-ids
     * for user-specific events or domain-user-specific EventFilters.
     * Events (also user-specific) can also be executed directly from the client-side (see {@link de.novanic.eventservice.client.event.RemoteEventService}).
     * @param aServiceURL URL of the service mapping
     * @param anEventExecutionServiceClass implementing interface of the service
     * @param anEventExecutionServiceAsyncClass async-interface of the service
     * @param aClientHandler {@link de.novanic.eventservice.client.ClientHandler} to provide the connection-/client-id for user-specific events or domain-user-specific EventFilters
     * @param <EES> generic for the implementing interface of the service
     * @param <EESA> generic for the async-interface of the service
     * @return new service instance with a provided {@link de.novanic.eventservice.client.ClientHandler}
     */
    public <EES extends RemoteService, EESA> EESA createEventExecutionService(String aServiceURL,
                                                                              Class<EES> anEventExecutionServiceClass,
                                                                              Class<EESA> anEventExecutionServiceAsyncClass,
                                                                              ClientHandler aClientHandler) {
        ServiceDefTarget theServiceEndPoint = (ServiceDefTarget)GWT.create(anEventExecutionServiceClass);
        final String theServiceURL;
        if(aClientHandler != null) {
            StringBuilder theStringBuilder = new StringBuilder(aServiceURL.length() + 50);
            theStringBuilder.append(aServiceURL);
            theStringBuilder.append("?id=");
            theStringBuilder.append(aClientHandler.getConnectionId());
            theServiceURL = theStringBuilder.toString();
        } else {
            theServiceURL = aServiceURL;
        }
        updateServiceURL(theServiceEndPoint, theServiceURL);
        return (EESA)theServiceEndPoint;
    }

    /**
     * Updates / changes the service-URL of a created service.
     * @param aServiceDefTarget mapped service instance
     * @param aServiceURL new service-URL
     */
    protected void updateServiceURL(ServiceDefTarget aServiceDefTarget, String aServiceURL) {
        if(aServiceDefTarget != null) {
            aServiceDefTarget.setServiceEntryPoint(aServiceURL);
        }
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