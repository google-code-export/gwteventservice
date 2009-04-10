package de.novanic.eventservice.client.event.command;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sstrohschein
 *         <br>Date: 27.03.2009
 *         <br>Time: 23:29:21
 */
public interface ClientCommand<R>
{
    void execute();

    void setCommandCallback(AsyncCallback<R> anAsyncCallback);

    AsyncCallback<R> getCommandCallback();
}
