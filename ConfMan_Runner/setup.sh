#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

confman_configini='/opt/NEConfMan/ConfMan_Runner/confman/configuration/config.ini'
confman_configxml='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.xml'
confman_configproperties='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.properties'

# Functions
function setPropertyIntoXML {
	if grep -q "<entry key=\"$1\">.*<\/entry>" "$3"; then
		
		sed -i "s/<entry key=\"$1\">.*<\/entry>/<entry key=\"$1\">$2<\/entry>/g" "$3"

	else
		
		sed -i "s/<\/properties>/<entry key=\"$1\">$2<\/entry>\n<\/properties>/g" "$3"		
	fi
}

function setPropertyIntoIni {
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}


	if grep -q "$key=.*" "$3"; then
		
		sed -i "s/$key=.*/$key=$2/g" "$3"

	else
		
		sed -i "s/# End of configurations/$key=$2\n# End of configurations/g" "$3"		
	fi
}

function setPropertyIntoProperties {
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}


	if grep -q "$key=.*" "$3"; then
		
		sed -i "s/$key=.*/$key=$2/g" "$3"

	else
		value=${2//\\/}
		echo "$key=$2" >> "$3"
	fi
}

# Escape characters
sed -e 's/[]\/$*.^|[]/\\&/g' ./confman.conf > ./.confman.conf.escaped
chmod +x .confman.conf.escaped
. ./.confman.conf.escaped


## START OF AUTOMATIC SCRIPT
#Confman setup
setPropertyIntoIni "dir.config" "$confman_dirconfig" "$confman_configini"
setPropertyIntoIni "bundles.configuration.location" "$confman_bundlesconfigurationlocation" "$confman_configini"
setPropertyIntoIni "tomcat.init.port" "$confman_tomcatinitport" "$confman_configini"

setPropertyIntoXML "pathPreFix_ngsi9" "$confman_pathprefixngsi9" "$confman_configxml"
setPropertyIntoXML "schema_ngsi9_operation" "$confman_schemangsi9operation" "$confman_configxml"

setPropertyIntoProperties "postgres_user" "$confman_postgresuser" "$confman_configproperties"
setPropertyIntoProperties "postgres_password" "$confman_postgrespassword" "$confman_configproperties"
setPropertyIntoProperties "postgres_url" "$confman_postgresurl" "$confman_configproperties"
setPropertyIntoProperties "postgres_dbName" "$confman_postgresdbname" "$confman_configproperties"
setPropertyIntoProperties "couchdb_registerContextDbName" "$couchdb_registerContextDbName" "$confman_configproperties"
setPropertyIntoProperties "couchdb_subscriptionDbName" "$confman_couchdbsubscriptiondbname" "$confman_configproperties"
