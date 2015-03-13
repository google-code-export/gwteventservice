![http://29539.webhosting15.1blu.de/gwteventservice/images/logo.png](http://29539.webhosting15.1blu.de/gwteventservice/images/logo.png)

**GWTEventService** is an event-based client-server communication framework. It uses GWT-RPC and the Comet / server-push technique. The client side offers a high-level API with opportunities to register listeners to the server like to a GUI component. Events can be added to a domain on the server side and the listeners on the client side get informed about the incoming events. The server side is completely independent of the client implementation and is highly configurable. Domains can be defined to decide which events are important for the different contexts.

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


These wiki pages contain only a small set of the basic documentation of the project. The whole documentation/manual can be found <a href='http://gwteventservice.googlecode.com/svn/trunk/doc/Manual.pdf'>here</a>.