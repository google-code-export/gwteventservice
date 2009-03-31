package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import de.novanic.eventservice.client.event.filter.EventFilter;
import de.novanic.eventservice.client.event.domain.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:32:11
 */
public class RegistrationEventFilterCommand extends ServerCallCommand<Void>
{
    private Domain myDomain;
    private EventFilter myEventFilter;

    public RegistrationEventFilterCommand(RemoteEventConnector aRemoteEventConnector, Domain aDomain, EventFilter anEventFilter,
                                          AsyncCallback<Void> aAsyncCallback) {
        super(aRemoteEventConnector, aAsyncCallback);
        myDomain = aDomain;
        myEventFilter = anEventFilter;
    }

    public void execute() {
        getRemoteEventConnector().registerEventFilter(myDomain, myEventFilter, getCommandCallback());
    }
}
