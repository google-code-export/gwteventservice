package de.novanic.gwteventservice.demo.rapidevents.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.gwteventservice.demo.rapidevents.client.event.RapidEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.ControlEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.StartEvent;
import de.novanic.gwteventservice.demo.rapidevents.client.event.control.StopEvent;

/**
 * @author sstrohschein
 *         <br>Date: 25.07.12
 *         <br>Time: 23:52
 */
public class RapidEvents implements EntryPoint
{
    private ReceivedEventsLabel myReceivedEventsLabel;
    private Button myStartStopButton;
    private boolean isEventsRunning;
    private RapidEventGeneratorServiceAsync myRapidEventGeneratorService;

    public void onModuleLoad() {
        Panel thePanel = createUI();
        RootPanel.get().add(thePanel);

        myRapidEventGeneratorService = GWT.create(RapidEventGeneratorService.class);
        myRapidEventGeneratorService.isRunning(new AsyncCallback<Boolean>() {
            public void onSuccess(Boolean isRunningRemote) {
                if(!isEventsRunning && isRunningRemote) {
                    startLocal();
                }
                isEventsRunning = isRunningRemote;
            }

            public void onFailure(Throwable aThrowable) {
                throw new RuntimeException(aThrowable);
            }
        });

        //get the RemoteEventService for registration of RemoteEventListener instances
        RemoteEventService theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
        //add a listener to the RAPID_EVENT_CONTROL_DOMAIN to observe the starting and stopping of rapid event execution
        theRemoteEventService.addListener(ControlEvent.RAPID_EVENT_CONTROL_DOMAIN, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if(anEvent instanceof StartEvent) {
                    startLocal();
                } else if(anEvent instanceof StopEvent) {
                    stopLocal();
                } else {
                    throw new RuntimeException("Unknown/unhandled event \"" + anEvent.getClass().getName() + "\" detected!");
                }
            }
        });
    }

    private Panel createUI() {
        myStartStopButton = new Button();
        myStartStopButton.setText("Start");
        myStartStopButton.addClickHandler(new StartStopButtonListener());

        Button theResetButton = new Button();
        theResetButton.setText("Reset");
        theResetButton.addClickHandler(new ResetButtonListener());

        myReceivedEventsLabel = new ReceivedEventsLabel();

        HorizontalPanel theControlPanel = new HorizontalPanel();
        theControlPanel.add(myStartStopButton);
        theControlPanel.add(theResetButton);

        VerticalPanel theMainPanel = new VerticalPanel();
        theMainPanel.setSize("30%", "30%");
        theMainPanel.add(theControlPanel);
        theMainPanel.add(myReceivedEventsLabel);

        return theMainPanel;
    }

    private void addRapidEventListener() {
        removeRapidEventListener();

        //add a listener to the RAPID_EVENT_DOMAIN
        RemoteEventService theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
        theRemoteEventService.addListener(RapidEvent.RAPID_EVENT_DOMAIN, new RemoteEventListener() {
            public void apply(Event anEvent) {
                myReceivedEventsLabel.incrementReceivedEventCount();
            }
        });
    }

    private void removeRapidEventListener() {
        RemoteEventService theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
        theRemoteEventService.removeListeners(RapidEvent.RAPID_EVENT_DOMAIN);
    }

    private void start() {
        startLocal();
        myRapidEventGeneratorService.start(new VoidAsyncCallback());
    }

    private void startLocal() {
        addRapidEventListener();
        isEventsRunning = true;
        myStartStopButton.setText("Stop");
    }

    private void stop() {
        stopLocal();
        myRapidEventGeneratorService.stop(new VoidAsyncCallback());
    }

    private void stopLocal() {
        isEventsRunning = false;
        myStartStopButton.setText("Start");
    }

    private void reset() {
        stop();
        removeRapidEventListener();
        myReceivedEventsLabel.resetEventCount();
    }

    private class StartStopButtonListener implements ClickHandler
    {
        public void onClick(ClickEvent anEvent) {
            if(isEventsRunning) {
                stop();
            } else {
                start();
            }
        }
    }

    private class ResetButtonListener implements ClickHandler
    {
        public void onClick(ClickEvent anEvent) {
            reset();
        }
    }

    private class VoidAsyncCallback implements AsyncCallback<Void>
    {
        public void onSuccess(Void aResult) {}

        public void onFailure(Throwable aThrowable) {
            throw new RuntimeException(aThrowable);
        }
    }
}