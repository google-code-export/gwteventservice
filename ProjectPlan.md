# GWTEventService 1.1 - Project Plan #

## Core ##

### Refactoring ###

| **State** | **Task** | **Description** |
|:----------|:---------|:----------------|
| <font color='green'>Done</font> | Platform Refactoring | Preparation of the platform for new feature implementations |
| <font color='green'>Done</font> | Lock free programming | Locks / synchronized blocks removed and replaced by atomics for more performance and optimized multi-threading support |
| <font color='green'>Done</font> | More multi-threading support | Some parts are running in threading mode (new "UserActivityScheduler" to observe client timeouts) |
| <font color='green'>Done</font> | Configuration logging improved | Logging of the used GWTEventService configuration improved |
| <font color='green'>Done</font> | Test coverage increased | Test coverage increased (to 100% on the server side) |
| Open | Queued server calls | Grouping of server calls and optional synchronized/queued method variants for RemoteEventService (client-server interface) to prevent server call conflicts, when the callback parameter isn't used |

### Features ###

| **State** | **Task** | **Description** |
|:----------|:---------|:----------------|
| <font color='green'>Done</font> | EventFilter enhancements | Additional interfaces and factories to simplify to building filter sequences |
| <font color='green'>Done</font> | Timeout listener | Timeout listener to observe client timeouts (moved from GWTEventService 1.2 to 1.1). See the concept: [GWTEventService - Timeout-Concept](GWTEventServiceTimeoutConcept.md) |
| <font color='green'>Done</font> | Revised configuration loading | Improvements in registration of custom configuration loaders and generic proxy for caching purposes |
| <font color='green'>Done</font> | Configration with Web-Descriptor | Additional configuration possibility with Web-Descriptor / Servlet-Parameters |
| <font color='orange'>Moved to 1.2</font> | Support for Annotations | Extended support for Annotations (for e.g. for listener-event mappings) |
| <font color='orange'>Moved to 1.2</font> | Simplified listener-event mapping | Simplified listener-event mapping and an optional solution with Annotations |
| <font color='orange'>Moved to 1.2</font> | Optional client side polling | Polling isn't required by GWTEventService, but it could be configured to reduce cycles of server calls when many "unimportant" events can occure. |
| <font color='orange'>Started</font> | Documentation | Documentation and release notes |

## DemoApplicationApp ##

### Refactoring ###

| **State** | **Task** | **Description** |
|:----------|:---------|:----------------|
| Open | Some usability bug-fixes | - |