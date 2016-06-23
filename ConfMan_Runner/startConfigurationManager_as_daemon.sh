#!/bin/bash
configProperties="/path/to/config.properties"

if [ "$1" = "-r" ]; then
	if [ -e "$configProperties" ]
	then
		str=`cat $configProperties | grep couchdb_registerContextDbName`; 
		couchdb_registerContextDbName="${str#*=}";
		couchdb_registerContextDbName=`echo $couchdb_registerContextDbName | tr '[:upper:]' '[:lower:]'`

		str=`cat $configProperties | grep couchdb_subscriptionDbName`; 
		couchdb_subscriptionDbName="${str#*=}";
		couchdb_subscriptionDbName=`echo $couchdb_subscriptionDbName | tr '[:upper:]' '[:lower:]'`

	
		curl -X DELETE localhost:5984/$couchdb_registerContextDbName
		curl -X DELETE localhost:5984/$couchdb_subscriptionDbName
	else
		echo "Please specify the path to the config.properties in the bash script"
	fi
fi

nohup java -jar confman/org.eclipse.osgi-3.7.0.v20110221.jar >/dev/null 2>&1 &
