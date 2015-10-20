NEC ConfMan
==

The **NEC Configuration Management** or **NEC ConfMan** is an implementation of the **FIWARE IoT Discovery Generic Enabler** by NEC. This implementation is specifically designed to interwork with the **IoT Broker GE** FIWARE reference implementation, serving as the registry of FIWARE NGSI context providers.

The NEC ConfMan APIs are fully based on FIWARE NGSI-9. ConfMan supports the following operations for exchanging context availability information:
* The *registerContext* operation is available to register information about data sources in the ConfMan's database.
* The *discoveryContextAvailability* operation can be used to query ConfMan for data sources.
* Users can subscribe for data sources using the *subscribeContextAvailability*, *updateContextAvailabilitySubscription* and *unsubscribeContextAvailability* operations. Subscribed users need to expose a resource for receiving *notifyContextAvailability* messages from ConfMan.

Compiling the Source Code
==

System Requirements
--
The following software is required for compiling ConfMan
* JAVA JDK 7 or OpenJDK 7
* MAVEN 3

Compilation Procedure
--
The source code is organized as Maven projects and therefore can be compiled using Maven. 

At compile time, Maven will automatically install the required dependency. One exception is the dependency on package `eu.neclab.iotplatform.ngsi.api`. This package is delivered as part of the **IoT Broker GE reference implementation**. It can be found in the *ConfManTargetPlatform* folder and needs to be installed manually into the local Maven repository. If the IoT Broker GEri has been previously compiled on the same system, this step can be skipped. Otherwise, navigate into the `ConfManTargetPlatform/neclab` folder and type

```
mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpclient -Dversion=4.2.0-osgi -Dfile=ngsi.api-4.4.3.jar -Dpackaging=jar -DgeneratePom=true
```

To compile the ConfMan sources, first compile the ConfMan parent project. Navigate into the folder `eu.neclab.iotplatform.confman.parent` and type

```
mvn install
```

Having installed the parent project, the ConfMan bundles are compiled by navigating into the `eu.neclab.iotplatform.confman.builder` folder and typing again

```
mvn install
```

The compiled bundles will be placed as jar files in `eu.neclab.iotplatform.confman.builder/target/builder-4.3.3-assembly/bundle`. Note that the ConfMan repository contains a runtime environment which is pre-configured to load the OSGi bundles from exactly that folder.


Configuring and running NEC ConfMan
==


System Requirements
--
The NEC ConfMan is implemented in Java and packaged as a set of OSGi bundles. It therefore runs on multiple operating systems including Windows and Linux.

The following software additionally needs to be installed:
* Java 1.7 or higher
* PostgreSQL server (version 9.2 or higher recommended). Please note that the credentials for access by ConfMan need to be set consistent to the access credentials configured in the ConfMan config file.
* CouchDB (version 1.5.1. or higher recommended). 



