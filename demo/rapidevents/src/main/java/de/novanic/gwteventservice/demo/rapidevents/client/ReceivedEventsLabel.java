package de.novanic.gwteventservice.demo.rapidevents.client;

import com.google.gwt.user.client.ui.Label;

/**
 * @author sstrohschein
 *         <br>Date: 02.08.12
 *         <br>Time: 19:55
 */
public class ReceivedEventsLabel extends Label
{
    private static final String RECEIVED_EVENTS_PREFIX = "Received events: ";

    private int myReceivedEventCount;

    public ReceivedEventsLabel() {
        resetEventCount();
    }

    public void incrementReceivedEventCount() {
        showEventCount(++myReceivedEventCount);
    }

    public void resetEventCount() {
        showEventCount((myReceivedEventCount = 0));
    }

    private void showEventCount(int anEventCount) {
        setText(RECEIVED_EVENTS_PREFIX + anEventCount);
    }
}