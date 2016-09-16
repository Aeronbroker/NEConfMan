NEC ConfMan
==

The **NEC Configuration Management** or **NEC ConfMan** is an implementation of the **FIWARE IoT Discovery Generic Enabler** done by NEC. This implementation is specifically designed to interwork with the **IoT Broker GE** FIWARE reference implementation, serving as the registry of FIWARE NGSI context providers.

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
mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpclient -Dversion=4.2.0-osgi -Dfile=ngsi.api-5.3.3.jar -Dpackaging=jar -DgeneratePom=true
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
* CouchDB (version 1.6 or higher recommended).

Runtime Environment
--
A pre-configured runtime environment based on Equinox is included by this release in the folder `ConfMan_Runner`. To start the ConfMan, run one of the two executable scripts `startConfigurationManager.bat` or `startConfigurationManager.sh`, depending on your operating system.


Configure the NEC ConfMan with setup scripts
---
NEConfMan configuration variables are spread among several files. For this reason is highly recommended the usage of the *setup.sh* script (only for Bash shell in UNIX system) available in the *ConfMan-Runner* folder (please note it is important that the structure of the folder needs to be the same as the one in the github repository https://github.com/Aeronbroker/NEConfMan).

First we have to enter in the *ConfMan-Runner* folder:
```
cd ConfMan-Runner
```

For a very quick configuration of the NEConfMan the following command needs to be run (please make sure the setup.sh has the execute rights with a *chmod +x setup.sh*):
```
./setup.sh --auto
```

The setup script will look at the default configurations in the *confman.conf.default* file and it will manage to automatically figure out the necessary path for the configuration.

The paths will be configured only to the NEConfMan configuration files and not in these setup files. In order to run in the future the *setup.sh* script without specifying the *--auto* option, the following command needs to be run:
```
./setup.sh --auto --propagateauto
```

The *--propagateauto* instructs the setup script to create a local configuration file, *confman.conf.local*, with the right path configurations. From now on the next setup action can be done simply by the following command:
```
./setup.sh
```

In order to setup the NEConfMan differently from the default configuration, it is necessary to specify the custom preference in the *confman.conf.local*. If the latter file has not yet been created by the --propagateauto options, it needs to be manually created. There are two possibilities:
* Create and empty file called *confman.conf.local*, add at the first line *#!/bin/bash* and give the execute permissions.
* Copy the *confman.conf.default* into *confman.conf.local* and give the execute permissions.

No differences between the two options, since the setup script will first look into the *confman.conf.default* file and then all the variables will be overwritten by the *confman.conf.local* file.
Now the custom preferences can be set.

Please note: if a *./setup.sh --auto --propagateauto* has not yet been run once, also the setup.sh needs to have the following 4 paths set at the beginning of the script:
```
confman_configini='/opt/NEConfMan/ConfMan_Runner/confman/configuration/config.ini'
confman_configxml='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.xml'
confman_configproperties='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.properties'
confman_launcher="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager.sh"
confman_launcherasdaemon="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager_as_daemon.sh"

```

For having the custom preferences set it is necessary to simply run:
```
./setup.sh
```

After running the setup.sh, the NEConfMan can be started with the provided scripts in *ConfMand-Runner* folder.

Manual System Configuration
---
The NEC ConfMan is configured by means of the following files
* `ConfMan_Runner/confman/configuration/config.ini` is the configuration file for the Equinox OSGi environment. The most important parameters to set here are
 * `tomcat.init.port`: The port to which the ConfMan's NGSI API is listening to
 * `dir.config`: the (absolute path) of the location of the fiwareRelease folder
* `fiwareRelease/confmanconfig/configurationManager/config/` folder
 * `config.properties`: Database configuration (including credentials for postgreSQL; see the system requirements section above)
 * `config.xml`: location of xml schema files for message body syntax checking, and path prefix of the exposed FIWARE NGSI-9 REST interface
*  `fiwareRelease/confmanconfig/bundleConfigurations/services/org.ops4j.pax.logging.properties`: configuration of logger

NGSI bundle issue
---
The NEConfMan is using the JAVA NGSI specification of the IoT Broker. This specification is given as a jar archive in the *ConfManTargetPlatform/neclab* folder. 
It may happen that in such folder there is not the most updated one and it may cause some issue due to incompatibility. In order to get the last one, it is necessary to build the last version from the source code (https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.ngsi.api) and then position the resulting jar file in the *ConfManTargetPlatform/neclab*.
