**< GWTEventService 1.0.2 >**

<u>GWTEventService 1.0.2</u>

  * Bugfix (Ticket 6): The callback wasn't used when a listener is removed without a domain (domain-less listener)
  * Bugfix (Ticket 7): Empty domains weren't removed automatically
  * Bugfix (Ticket 8): Possible dead-lock when a user is registered and another user is concurrent deregistered
  * Improvements for domain-less usage
  * Concurrency optimizations

<u>GWTEventService 1.0.1</u>

  * Bugfix: Work-around to avoid multiple session creation (GWT [issue 1826](https://code.google.com/p/gwteventservice/issues/detail?id=1826)) when GWTEventService had to execute the first server call of the application
  * Bugfix (Ticket 3): Chrome appeared as if the page never finishes loading when GWTEventService had to execute the first server call of the application
  * Bugfix: Client side multi-threading bug fixed, which could occur when a listener is removed while processing listen results
  * Client commands introduced to solve the problems described above (new interfaces ClientCommand and ClientCommandScheduler)
  * Some client side optimizations

<u>Changes from GWTEventService 0.9 to 1.0</u>

  * Logging improved
  * Bug fixes
  * Some small improvements
  * No API changes compared to GWTEventService 0.9
  * Documentation completed
  * Build scripts updated