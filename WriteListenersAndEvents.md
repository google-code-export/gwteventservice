Now the interesting part: How can I make it work? First you will need to write your events like defined within the previous chapters. The events must implement the Event interface (de.novanic.eventservice.client.Event), must be available to the client- and to the server side and must be serializable, because the events are transferred from the server to the client side. A simple project structure to reach that can be seen within the <a href='http://gwteventservice.googlecode.com/svn/trunk/doc/Manual.pdf'>manual</a>. In the most projects, the events will be added from the server side, because in the most cases the events are known and triggered by the server and the events should be distributed to the clients, but it is also possible to add events directly from the client side. On the server side events can be added via an EventExecutorService (method addEvent(Domain, Event)). An EventExecutorService can be got from the EventExecutorServiceFactory or if you have a servlet, you can extend from RemoteEventServiceServlet which is an implementation of EventExecutorService. That's it for the server side.

The listeners must only be accessible by the client side, because the server doesn't need to know anything about listeners. The only very logical method you need to implement is the "apply" method of the RemoteEventListener interface (de.novanic.eventservice.client.listener.RemoteEventListener). The apply method gets an Event as a parameter and your logic must be able to decide how to handle the event. It's a good practice to define methods in a listener interface like "onUserJoin" and "onUserLeave" which can be implemented in the real implementation of the listener. An adapter class or abstract implementation of the listener can offer the apply method implementation. It is often needed to differ the events via "instanceof" checks. That is planned to get improved with a later version of GWTEventService.

Complete code examples can be found within the appendix. Here is an example of an implemented "apply" method:

GameUserListenerAdapter

```
public abstract class GameUserListenerAdapter implements GameUserListener
{	
	public void apply(Event anEvent) {
		if(anEvent instanceof UserJoinEvent) {
			onUserJoin((UserJoinEvent)anEvent);
		} else if(anEvent instanceof UserLeaveEvent) {
			onUserLeave((UserLeaveEvent)anEvent);
		}
	}
	
	public void onUserJoin(UserJoinEvent aUserJoinEvent) {}

	public void onUserLeave(UserLeaveEvent aUserLeaveEvent) {}
}
```

Now you have the events and the listeners. Maybe you recognized that you need a Domain (de.novanic.eventservice.client.event.domain.Domain) to add an event. This is the last step: The registering of listeners and adding of events with domains. A domain can be got from the DomainFactory (de.novanic.eventservice.client.event.domain.DomainFactory) with a unique String (domain name). It would be a good idea to hold the domain name or the Domain as a constant, available for the client- and server side, to ensure that you access the right domains.

RemoteEventService (de.novanic.eventservice.client.event.RemoteEventService) can be used to register and deregister listeners (client side). The equivalent methods are named "addListener" and "removeListener". All methods have an optional callback parameter, if you need to know when the change is really active.

Something special and optional is the EventFilter (de.novanic.eventservice.client.event.filter.EventFilter). EventFilter instances can be added from the client side and are processed on the server side. With an EventFilter the server side can decide if an event is really important for the user or not. Please take a look into the <a href='http://gwteventservice.googlecode.com/svn/trunk/doc/Manual.pdf'>manual</a> when you want to learn more about filtering of events on the server side.