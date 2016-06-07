#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

confman_configini='/opt/NEConfMan/ConfMan_Runner/confman/configuration/config.ini'
confman_configxml='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.xml'
confman_configproperties='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.properties'

AUTOSETUP='false'
PROPAGATEAUTO='false'

while [[ $# > 0 ]]
do
key="$1"
case $key in
    --auto)
    AUTOSETUP=true
    ;;
    --propagateauto)
    PROPAGATEAUTO=true
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

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

function setConfiguration {
	
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}
	
	value=$2
	value=${value//\./\\\.}
	value=${value//\//\\/}

	
	sed -i "s/$key=.*/$key=\'$value\'/g" "$3"

}





if [ "$AUTOSETUP" = false ]; 
then
	# Escape characters
	sed -e 's/[]\/$*.^|[]/\\&/g' ./confman.conf.local > ./.confman.conf.escaped
	chmod +x .confman.conf.escaped
	. ./.confman.conf.escaped
else

	# Escape characters
	sed -e 's/[]\/$*.^|[]/\\&/g' ./confman.conf.default > ./.confman.conf.escaped
	chmod +x .confman.conf.escaped
	. ./.confman.conf.escaped
	
	confmandir="$(dirname `pwd`)"
	
	confman_configini="$confmandir/ConfMan_Runner/confman/configuration/config.ini"
	confman_configxml="$confmandir/fiwareRelease/confmanconfig/configurationManager/config/config.xml"
	confman_configproperties="$confmandir/fiwareRelease/confmanconfig/configurationManager/config/config.properties"
	
	confman_dirconfig="$confmandir/fiwareRelease"
	confman_bundlesconfigurationlocation="$confmandir/fiwareRelease/bundleConfigurations"
	
	if [ "$PROPAGATEAUTO" = true ];
	then
	
		setConfiguration "confman_dirconfig" "$confman_dirconfig" "confman.conf.local"
		setConfiguration "confman_bundlesconfigurationlocation" "$confman_bundlesconfigurationlocation" "confman.conf.local"
		
		setConfiguration "confman_configini" "$confman_configini" "setup.sh"
		setConfiguration "confman_configxml" "$confman_configxml" "setup.sh"
		setConfiguration "confman_configproperties" "$confman_configproperties" "setup.sh"

	fi
	
	confman_dirconfig=${confman_dirconfig//\./\\\.}
	confman_dirconfig=${confman_dirconfig//\//\\/}
	
	confman_bundlesconfigurationlocation=${confman_bundlesconfigurationlocation//\./\\\.}
	confman_bundlesconfigurationlocation=${confman_bundlesconfigurationlocation//\//\\/}
fi


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
setPropertyIntoProperties "couchdb_ip" "$confman_couchdbipandport" "$confman_configproperties"
setPropertyIntoProperties "couchdb_registerContextDbName" "$confman_couchdbregistercontextdbname" "$confman_configproperties"
setPropertyIntoProperties "couchdb_subscriptionDbName" "$confman_couchdbsubscriptiondbname" "$confman_configproperties"
