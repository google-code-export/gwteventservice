Here are some code examples to demonstrate the implementation of events and listeners. UserJoinEvent and UserLeaveEvent are subclasses of GameUserEvent.

**GameUserListener**
```
package de.novanic.gwteventservice.manual.example;

import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import de.novanic.gwteventservice.manual.example.event.UserJoinEvent;
import de.novanic.gwteventservice.manual.example.event.UserLeaveEvent;

public interface GameUserListener extends RemoteEventListener
{
	void onUserJoin(UserJoinEvent aJoinEvent);
	
	void onUserLeave(UserLeaveEvent aLeaveEvent);
}
```

**GameUserListenerAdapter**
```
package de.novanic.gwteventservice.manual.example;

import de.novanic.eventservice.client.event.Event;
import de.novanic.gwteventservice.manual.example.event.UserJoinEvent;
import de.novanic.gwteventservice.manual.example.event.UserLeaveEvent;

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

**GameUserEvent**
```
package de.novanic.gwteventservice.manual.example.event;

import de.novanic.eventservice.client.event.Event;
import de.novanic.gwteventservice.manual.example.Game;
import de.novanic.gwteventservice.manual.example.User;

public class GameUserEvent implements Event
{
	private Game myGame;
	private User myUser;
	
	public GameUserEvent() {}
	
	public GameUserEvent(Game aGame, User aUser) {
		setGame(aGame);
		setUser(aUser);
	}
	
	public Game getGame() {
		return myGame;
	}
	
	public void setGame(Game aGame) {
		myGame = aGame;
	}
	
	public User getUser() {
		return myUser;
	}
	
	public void setUser(User aUser) {
		myUser = aUser;
	}
}
```

**Example class of usage**
```
package de.novanic.gwteventservice.manual.example;

import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.gwteventservice.manual.example.event.UserJoinEvent;
import de.novanic.gwteventservice.manual.example.event.UserLeaveEvent;

public class UsageClass
{
  private static final Domain GAME_DOMAIN = DomainFactory.getDomain("gamedom");
	
  public void init() {
    //do something
		
    RemoteEventServiceFactory theEventServiceFactory = 			
			RemoteEventServiceFactory.getInstance();
    RemoteEventService theEventService = 
			theEventServiceFactory.getRemoteEventService();
		
    theEventService.addListener(GAME_DOMAIN, new GameUserListenerAdapter() {
      public void onUserJoin(UserJoinEvent userJoinEvent) {
        //do something with the new user
      }

      public void onUserLeave(UserLeaveEvent userLeaveEvent) {
        //do something with the leaved user
      }			
    });
		
    //do something
  }
}
```