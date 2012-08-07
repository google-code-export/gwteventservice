package de.novanic.gwteventservice.demo.rapidevents.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

public interface RapidEventGeneratorServiceAsync
{
    void start(AsyncCallback<Void> aCallback);

    void stop(AsyncCallback<Void> aCallback);

    void isRunning(AsyncCallback<Boolean> async);
}
