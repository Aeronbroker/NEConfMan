Semantic in the IoT Discovery
==================

The NEConfMan has an optional feature for having semantic capabilities. The current status is the support for checking **subtypes** of a certain type when a query or subscription is done.

The following workflow explains how to enable and use this feature.

Enable the Semantic feature
-----------
For enabling the semantic feature, the NEConfMan needs to be connected to an instance of the **NECKnowledgeBase** server (https://github.com/Aeronbroker/NECKnowledgeBase). This is achieved by the following steps.
* The NECKnowledgeBase server needs to be installed either on the machine where the IoT Broker is running, or on a remote machine.
* The NEConfMan plugin eu.neclab.iotplatform.knowledgebase needs to be activated. This can be done by setting the following configuration in the ConfMan-runner/confman.conf.local:
```
confman_semantic="enabled"
```
If not set, the default value in ConfMan-runner/confman.conf.deafult is *enabled*.
Then run
```
./setup.sh
```

In order to better understand the configuration procedure please read: https://github.com/Aeronbroker/NEConfMan#configure-the-nec-confman-with-setup-scripts.

* Configure the activated plugin by adding the following configurations in the ConfMan-runner/confman.conf.local:
```
# Knowledge Base configuration
confman_knowledgebaseaddress='http://127.0.0.1'
confman_knowledgebaseport=8015
```
If not set, the default values in ConfMan-runner/confman.conf.default are * http://127.0.0.1 * and *8015*.

Then run
```
./setup.sh
```
