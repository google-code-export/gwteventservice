package de.novanic.eventservice.client.event;

import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEventListener;
import de.novanic.eventservice.client.event.listener.unlisten.UnlistenEvent;

/**
 * @author sstrohschein
 *         <br>Date: 24.10.2009
 *         <br>Time: 19:25:14
 */
public class UnlistenEventListenerTestMode extends EventListenerTestMode implements UnlistenEventListener
{
    public void onUnlisten(UnlistenEvent anUnlistenEvent) {
        addEvent(anUnlistenEvent);
    }
}