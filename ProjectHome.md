**GWTEventService** is an event-based client-server communication framework. It uses GWT-RPC and the Comet / server-push technique. The client side offers a high-level API with opportunities to register listeners to the server like to a GUI component. Events can be added to a context/domain on the server side and the listeners on the client side get informed about the incoming events. The server side is completely independent of the client implementation and is highly configurable. Domains can be defined to decide which events are important for the different contexts.

![http://29539.webhosting15.1blu.de/gwteventservice/images/logo.png](http://29539.webhosting15.1blu.de/gwteventservice/images/logo.png)

**Advantages**
  * Encapsulation of the client-server communication
  * High-level API with listeners and events
  * Only one open connection for event listening
  * Reduction of server calls
  * Reduction of connection peaks
  * Events are returned directly when the event has occurred (instead of polling)
  * Events are bundled to reduce server calls
  * Server-side event filtering to reduce server calls
  * Based on the GWT-RPC mechanism
  * Automatic timeout recognition and handling
  * Extensible architecture
###### These advantages are not guaranteed for all cases and could also be disadvantages in some special cases compared with polling or other technologies. ######


  * **GWTEventService 1.2.1 (released since 2012-12-21)**
  * GWTEventService 1.2 (released since 2012-01-09)
  * GWTEventService 1.1.1 (released since 2010-04-04)
  * GWTEventService 1.0.2 (released since 2009-09-12)