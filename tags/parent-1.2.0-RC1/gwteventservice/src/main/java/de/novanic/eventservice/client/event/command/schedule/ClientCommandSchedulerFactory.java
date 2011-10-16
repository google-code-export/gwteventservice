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
package de.novanic.eventservice.client.event.command.schedule;

/**
 * The ClientCommandSchedulerFactory is used to create the ClientCommandScheduler and to ensure that only one instance of
 * ClientCommandSchedulerFactory and ClientCommandScheduler exists (singleton).
 * @see de.novanic.eventservice.client.event.command.schedule.ClientCommandScheduler
 *
 * @author sstrohschein
 *         <br>Date: 04.04.2009
 *         <br>Time: 22:26:18
 */
public class ClientCommandSchedulerFactory
{
    private volatile ClientCommandScheduler myClientCommandScheduler;

    /**
     * The ClientCommandSchedulerFactory should be created via the getInstance method.
     * @see ClientCommandSchedulerFactory#getInstance()
     */
    private ClientCommandSchedulerFactory() {}

    /**
     * Factory-Holder class to ensure thread-safe lazy-loading with IODH.
     */
    private static class ClientCommandSchedulerFactoryHolder {
        private static ClientCommandSchedulerFactory INSTANCE = new ClientCommandSchedulerFactory();
    }

    /**
     * This method should be used to create an instance of ClientCommandSchedulerFactory.
     * ClientCommandSchedulerFactory is a singleton, so this method returns always the same instance of
     * ClientCommandSchedulerFactory.
     * @return ClientCommandSchedulerFactory (singleton)
     */
    public static ClientCommandSchedulerFactory getInstance() {
        return ClientCommandSchedulerFactoryHolder.INSTANCE;
    }

    /**
     * This method should be used to create an instance of RemoteEventService.
     * RemoteEventService is a singleton, so this method returns always the same instance of RemoteEventService.
     * The session is needed to generate the client/user id.
     * @return RemoteEventService (singleton)
     */
    public ClientCommandScheduler getClientCommandScheduler() {
        if(myClientCommandScheduler == null) {
            synchronized(this) {
                if(myClientCommandScheduler == null) {
                    myClientCommandScheduler = new GWTCommandScheduler();
                }
            }
        }
        return myClientCommandScheduler;
    }

    /**
     * Sets an instance of ClientCommandScheduler which is returned by {@link ClientCommandSchedulerFactory#getClientCommandScheduler()}.
     * @param aClientCommandScheduler {@link ClientCommandScheduler}
     */
    public void setClientCommandSchedulerInstance(ClientCommandScheduler aClientCommandScheduler) {
        myClientCommandScheduler = aClientCommandScheduler;
    }

    /**
     * Resets the ClientCommandScheduler instance. 
     */
    public void reset() {
        setClientCommandSchedulerInstance(null);
    }
}