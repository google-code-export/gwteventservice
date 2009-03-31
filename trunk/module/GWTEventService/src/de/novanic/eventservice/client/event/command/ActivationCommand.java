package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import de.novanic.eventservice.client.event.EventNotification;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:31:34
 */
public class ActivationCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private EventFilter myEventFilter;
    private EventNotification myEventNotification;

    public ActivationCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, EventFilter anEventFilter,
                             EventNotification anEventNotification, AsyncCallback<Void> aCallback) {
        super(aRemoteEventConnector, aCallback);
        myDomain = aDomain;
        myEventFilter = anEventFilter;
        myEventNotification = anEventNotification;
    }

    public void execute() {
        getRemoteEventConnector().activate(myDomain, myEventFilter, myEventNotification, getCommandCallback());
    }
}
