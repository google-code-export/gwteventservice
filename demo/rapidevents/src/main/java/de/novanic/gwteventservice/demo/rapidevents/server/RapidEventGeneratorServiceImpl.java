package de.novanic.gwteventservice.demo.rapidevents.server;

import de.novanic.eventservice.service.RemoteEventServiceServlet;
import de.novanic.gwteventservice.demo.rapidevents.client.RapidEventGeneratorService;
import de.novanic.gwteventservice.demo.rapidevents.client.event.RapidEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.ControlEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.StartEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.StopEvent;

/**
 * @author sstrohschein
 *         <br>Date: 26.07.12
 *         <br>Time: 00:00
 */
public class RapidEventGeneratorServiceImpl extends RemoteEventServiceServlet implements RapidEventGeneratorService
{
    private boolean isRunning;
    private int myCurrentId;

    public void start() {
        isRunning = true;
        addEvent(ControlEvent.RAPID_EVENT_CONTROL_DOMAIN, new StartEvent());
        final Thread theEventAddingThread = new Thread(new Runnable() {
            public void run() {
                while(isRunning) {
                    for(int i = 0; i < 100; i++) {
                        addEvent(RapidEvent.RAPID_EVENT_DOMAIN, new RapidEvent(++myCurrentId));
                    }
                    //give other threads a chance to connect
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException e) {
                        throw new RuntimeException("Event adding thread aborted!", e);
                    }
                }
            }
        });
        theEventAddingThread.setDaemon(true);
        theEventAddingThread.start();
    }

    public void stop() {
        isRunning = false;
        addEvent(ControlEvent.RAPID_EVENT_CONTROL_DOMAIN, new StopEvent());
    }

    public boolean isRunning() {
        return isRunning;
    }
}