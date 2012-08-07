package de.novanic.gwteventservice.demo.rapidevents.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author sstrohschein
 *         <br>Date: 25.07.12
 *         <br>Time: 23:58
 */
@RemoteServiceRelativePath("RapidEventGeneratorService")
public interface RapidEventGeneratorService extends RemoteService
{
    void start();

    void stop();

    boolean isRunning();
}