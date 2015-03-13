To get an application running with GWTEventService, the Jars of GWTEventService must be included in the classpath. The gwt-xml must include an entry to inherit "_de.novanic.eventservice.GWTEventService_".

**Example**
![http://29539.webhosting15.1blu.de/gwteventservice/images/config-xml.png](http://29539.webhosting15.1blu.de/gwteventservice/images/config-xml.png)

If you want to execute the tests, you need to include the libraries JUnit 3.x or greater (http://www.junit.org) and EasyMock 2.x or greater (http://www.easymock.org). For more information about building GWTEventService yourself and setup the project (with sources), please take a look in the developer guide of GWTEventService.

To configure GWTEventService you can use “eventservice.properties” or define servlet-parameters with the web-descriptor. The location of the properties file must be attached to the classpath of the server side application. The default values and the description of the times can be seen in the following table.

Alternatively, a configuration loader can be defined if you want to load the configuration from another source. The interface "_ConfigurationLoader"_ must be implemented and must be attached as a custom loader ("_EventServiceConfigurationFactory#addCustomConfigurationLoader(...)_"). Custom loaders are executed in sequence and before the standard configuration loaders.

Overview about the configurable times:
| **Time (in milliseconds)** | **Default value** | **Description** |
|:---------------------------|:------------------|:----------------|
| Min. waiting time | 0 | Time to wait at minimum (even when events have already occurred)|
| Max. waiting time | 20000 | Time to wait at maximum (canceled when an event occurred)|
| Timeout time | 90000 | Time till timeout (when the time is reached, the user will be removed from listening for events)|

With that configuration possibilities, the server side behavior can be influenced very flexibly. If desired, it is even possible to run GWTEventService in polling mode if min. waiting time is set to a value greater than zero and max. waiting time is set to zero (server side polling mode).

The properties can be written equal to the name in the table above (without the quotes) or additional with the prefix “eventservice.”.