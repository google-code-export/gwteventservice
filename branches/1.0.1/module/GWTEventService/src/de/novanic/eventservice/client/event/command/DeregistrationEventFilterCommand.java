package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:33:18
 */
public class DeregistrationEventFilterCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;

    public DeregistrationEventFilterCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
    }

    public void execute() {
        getRemoteEventConnector().deregisterEventFilter(myDomain, getCommandCallback());
    }
}
