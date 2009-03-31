package de.novanic.eventservice.client.event.command;

import de.novanic.eventservice.client.event.RemoteEventConnector;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:34:41
 */
public abstract class ServerCallCommand<R> implements ClientCommand<R>
{
    private RemoteEventConnector myRemoteEventConnector;
    private AsyncCallback<R> myCallback;

    public ServerCallCommand(RemoteEventConnector aRemoteEventConnector, AsyncCallback<R> aCallback) {
        myRemoteEventConnector = aRemoteEventConnector;
        myCallback = aCallback;
    }

    protected RemoteEventConnector getRemoteEventConnector() {
        return myRemoteEventConnector;
    }

    public void setCommandCallback(AsyncCallback<R> anAsyncCallback) {
        myCallback = anAsyncCallback;
    }

    public AsyncCallback<R> getCommandCallback() {
        return myCallback;
    }
}
