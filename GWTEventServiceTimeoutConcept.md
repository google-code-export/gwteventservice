# GWTEventService - Timeout-Concept #

There are two parts of a timeout listening implementation. One to allow the server side to send timeout events to all clients and one part to let the client observe their own connection state.


### Part 1 (UnlistenEvents for all listening clients when a timeout occures) ###

The interface RemoteEventService will provide methods to add UnlistenListener instances to a global UnlistenDomain. That method will require the UnlistenListener and a has an optional parameter for a specific UnlistenEvent. The UnlistenEvent can be setup with client specific information for example the user object of your user management system. That event will be hold at the server side and will be sent to all clients which have an UnlistenListener registered. With that way it is possible for other clients to remove the specific user for example from a view when a timeout of a other user occures.

When no UnlistenEvent is registered with the UnlistenListener, a generic UnlistenEvent is triggered and send to all registered clients. That solution doesn't need additional server side memory to hold specific UnlistenEvents and the developer doesn't need to implement a specific UnlistenEvent.

There are various options to configure the UnlistenListener:

| **Mode/Option** | **Parts** | **Description** |
|:----------------|:----------|:----------------|
| UnlistenListener.LOCAL | Part 2 | only called when the client detects a timeout (no server connection / domain registration required) |
| UnlistenListener.TIMEOUT | Part 1 and 2 | the UnlistenListener is called on a timeout and the UnlistenEvent will be sent to all clients |
| UnlistenListener.UNLISTEN | Part 1 and 2 | the UnlistenListener is called when a listener is removed and the UnlistenEvent will be sent to all clients |

<br>
<h3>Part 2 (Connection status for the client)</h3>

The method RemoteEventService#getConnectionState():int will be implemented to get the status of the connection. The UnlistenListener of Part 1 can be used to observe a change of the connection status (with the option UnlistenListener.LOCAL). That can be useful to let the application know the UnlistenEvent when the server connection is completely disconnected and the UnlistenEvent of Part 1 can not be received.