package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Set;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:33:35
 */
public class DeactivationCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private Set<Domain> myDomains;

    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector) {
        super(aRemoteEventConnector, null);
    }

    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
    }

    public DeactivationCommand(RemoteEventConnector aRemoteEventConnector, Set<Domain> aDomains, AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomains = aDomains;
    }

    public void execute() {
        if(myDomain != null) {
            getRemoteEventConnector().deactivate(myDomain, getCommandCallback());
        } else if(myDomains != null) {
            getRemoteEventConnector().deactivate(myDomains, getCommandCallback());
        } else {
            getRemoteEventConnector().deactivate();
        }
    }
}
