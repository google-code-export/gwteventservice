The deployment of a GWT application which uses GWTEventService is very easy when your application is already prepared for deploying. That short article describes the deployment with Apache Tomcat (http://tomcat.apache.org/), but is comparable to other web servers.

The JAR file of GWTEventService must simply be put into the "lib" directory of your WAR archive (or exploded directory) and a servlet mapping must be added to your web module deployment descriptor (web.xml).  The following two figures show the structure of a WAR file containing the GWTEventService library and a web.xml with the important part of the appropriate servlet mapping.

**WAR structure
  ***WAR*****<your module path>*****META-INF*****WEB-INF*****classes*****lib*****gwt-servlet.jar*** gwteventservice.jar
      ***web.xml

**web.xml
```
<web-app>

  ...

  <servlet>
    <servlet-name>EventService</servlet-name>
    <servlet-class>
      de.novanic.eventservice.service.EventServiceImpl
    </servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>EventService</servlet-name>
    <url-pattern>/<your-GWT-module-path>/gwteventservice</url-pattern>
  </servlet-mapping>

  ...  

</web-app>
```**

If you need to know more about the deployment process you can take a look at the developer guide of GWTEventService. There is a description/example for the deployment of the demo application.