#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

confman_configini='/opt/NEConfMan/ConfMan_Runner/confman/configuration/config.ini'
confman_configxml='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.xml'
confman_configproperties='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.properties'
confman_launcher="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager.sh"
confman_launcherasdaemon="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager_as_daemon.sh"
confman_loggerproperties="$confman_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"


confman_version="5.1.3"

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

	
	if grep -q "$key=.*" "$3"; then
		sed -i "s/$key=.*/$key=\'$value\'/g" "$3"
	else 
		echo "$1='$2'" >> "$3"
	fi

}

function disableBundle {

	sed -i "s/.*$1-$confman_version.jar.*//g" $confman_configini

}

function enableBundle {
	
	found=`grep $1-$confman_version.jar confman/configuration/config.ini`
	if [ -n "$found" ];
	then
		return
	fi
	
	builder_target=`dirname $confman_configini`
	builder_target=`dirname $builder_target`
	builder_target=`dirname $builder_target`
	builder_target=`dirname $builder_target`
	builder_target="$builder_target/eu.neclab.iotplatform.confman.builder/target/builder-$confman_version-assembly/bundle/"
		
	if [ ! -e $builder_target/$1-$confman_version.jar ];
	then
		echo "WARNING bundle not found: $builder_target/$1-$confman_version.jar"
		return;
	fi
	
	
	lastline=`awk '/./{line=$0} END{print line}' $confman_configini`
	lastline=${lastline//\./\\\.}
	lastline=${lastline//\//\\/}
	if [[ $lastline != *,* ]] ; then
		sed -i "s/$lastline/$lastline, \\\/g" $confman_configini
	fi
	
	if [ "$2" == "nostart" ];
	then
		echo "../../eu.neclab.iotplatform.confman.builder/target/builder-$confman_version-assembly/bundle/$1-$confman_version.jar, \\" >> $confman_configini
	else
		echo "../../eu.neclab.iotplatform.confman.builder/target/builder-$confman_version-assembly/bundle/$1-$confman_version.jar@start, \\" >> $confman_configini
	fi
}

function correctConfigIni {

	lastline=`awk '/./{line=$0} END{print line}' $confman_configini`
	correctlastline=${lastline/, \\/}
	correctlastline=${correctlastline//\./\\\.}
	correctlastline=${correctlastline//\//\\/}
	sed -i "s/$correctlastline, \\\/$correctlastline/g" $confman_configini
	
	sed -i '/^$/d' $confman_configini
	
}

# Escape characters
sed -e 's/[]\/$*.^|[]/\\&/g' ./confman.conf.default > ./.confman.conf.default.escaped
chmod +x .confman.conf.default.escaped
. ./.confman.conf.default.escaped

if [ -e confman.conf.local ];
then
	echo "Reading default preferences from confman.conf.local"
	sed -e 's/[]\/$*.^|[]/\\&/g' ./confman.conf.local > ./.confman.conf.local.escaped
	chmod +x .confman.conf.local.escaped
	. ./.confman.conf.local.escaped
fi


if [ "$AUTOSETUP" = true ]; 
then
	confmandir="$(dirname `pwd`)"
	


	confman_dirconfig="$confmandir/fiwareRelease"
	confman_bundlesconfigurationlocation="$confmandir/fiwareRelease/bundleConfigurations"
	confman_logfile="$confmandir/ConfMan_Runner/logs/iotdiscovery.log"
	
	confman_configini_auto="$confmandir/ConfMan_Runner/confman/configuration/config.ini"
	confman_configxml_auto="$confmandir/fiwareRelease/confmanconfig/configurationManager/config/config.xml"
	confman_configproperties_auto="$confmandir/fiwareRelease/confmanconfig/configurationManager/config/config.properties"
	confman_launcher_auto="$confmandir/ConfMan_Runner/startConfigurationManager.sh"
	confman_launcherasdaemon_auto="$confmandir/ConfMan_Runner/startConfigurationManager_as_daemon.sh"
	confman_loggerproperties_auto="$confman_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"

	confman_version_auto=`grep -m1 "<version>" $confmandir/eu.neclab.iotplatform.confman.builder/pom.xml`;
	if [ -z "$confman_version_auto" ];
	then
		echo "WARNING: impossible to read the version from the $confmandir/eu.neclab.iotplatform.confman.builder/pom.xml. Please set it in the setup.sh manually"
	else
		confman_version_auto=${confman_version_auto/<version>};
		confman_version_auto=${confman_version_auto/<\/version>};
		confman_version_auto=${confman_version_auto//	};
		confman_version_auto=${confman_version_auto// };
	fi
		
	if [ "$PROPAGATEAUTO" = true ];
	then

		if [ ! -e confman.conf.local ];
		then
			touch confman.conf.local
			echo "#!/bin/bash" >> confman.conf.local
		fi
	
		setConfiguration "confman_dirconfig" "$confman_dirconfig" "confman.conf.local"
		setConfiguration "confman_bundlesconfigurationlocation" "$confman_bundlesconfigurationlocation" "confman.conf.local"
		setConfiguration "confman_logfile" "$confman_logfile" "confman.conf.local"
		
		setConfiguration "confman_configini" "$confman_configini_auto" "setup.sh"
		setConfiguration "confman_configxml" "$confman_configxml_auto" "setup.sh"
		setConfiguration "confman_configproperties" "$confman_configproperties_auto" "setup.sh"
		setConfiguration "confman_launcher" "$confman_launcher_auto" "setup.sh"
		setConfiguration "confman_launcherasdaemon" "$confman_launcherasdaemon_auto" "setup.sh"
		setConfiguration "confman_loggerproperties" "$confman_loggerproperties_auto" "setup.sh"
		
		if [ -n "$confman_version_auto" ];
		then
				setConfiguration "confman_version" "$confman_version_auto" "setup.sh"
		fi
	fi

	confman_configini=$confman_configini_auto
	confman_configxml=$confman_configxml_auto
	confman_configproperties=$confman_configproperties_auto
	confman_version=$confman_version_auto
	confman_launcher=$confman_launcher_auto
	confman_launcherasdaemon=$confman_launcherasdaemon_auto
	confman_loggerproperties=$confman_loggerproperties_auto
	
	confman_dirconfig=${confman_dirconfig//\./\\\.}
	confman_dirconfig=${confman_dirconfig//\//\\/}
	
	confman_bundlesconfigurationlocation=${confman_bundlesconfigurationlocation//\./\\\.}
	confman_bundlesconfigurationlocation=${confman_bundlesconfigurationlocation//\//\\/}
	
	confman_logfile=${confman_logfile//\./\\\.}
	confman_logfile=${confman_logfile//\//\\/}
fi


## START OF AUTOMATIC SCRIPT
#Confman setup
setPropertyIntoIni "dir.config" "$confman_dirconfig" "$confman_configini"
setPropertyIntoIni "bundles.configuration.location" "$confman_bundlesconfigurationlocation" "$confman_configini"
setPropertyIntoIni "tomcat.init.port" "$confman_tomcatinitport" "$confman_configini"

setPropertyIntoXML "pathPreFix_ngsi9" "$confman_pathprefixngsi9" "$confman_configxml"
setPropertyIntoXML "schema_ngsi9_operation" "$confman_schemangsi9operation" "$confman_configxml"
setPropertyIntoXML "Ngsi9_10_dataStructure_v07" "$confman_schemangsidatastructure" "$confman_configxml"

setPropertyIntoProperties "postgres_user" "$confman_postgresuser" "$confman_configproperties"
setPropertyIntoProperties "postgres_password" "$confman_postgrespassword" "$confman_configproperties"
setPropertyIntoProperties "postgres_url" "$confman_postgresurl" "$confman_configproperties"
setPropertyIntoProperties "postgres_dbName" "$confman_postgresdbname" "$confman_configproperties"
setPropertyIntoProperties "couchdb_ip" "$confman_couchdbipandport" "$confman_configproperties"
setPropertyIntoProperties "couchdb_registerContextDbName" "$confman_couchdbregistercontextdbname" "$confman_configproperties"
setPropertyIntoProperties "couchdb_subscriptionDbName" "$confman_couchdbsubscriptiondbname" "$confman_configproperties"

setPropertyIntoProperties "log4j.appender.ReportFileAppender.File" "$confman_logfile" "$confman_loggerproperties"

setConfiguration "configProperties" "$confman_configproperties" "$confman_launcher"
setConfiguration "configProperties" "$confman_configproperties" "$confman_launcherasdaemon"


##ENABLE BASIC BUNDLE
enableBundle commons
enableBundle coreextensible
enableBundle couchdb
enableBundle ngsi.api
enableBundle postgres
enableBundle restcontroller
enableBundle serverconf nostart
enableBundle utilitystorage
enableBundle extensionmanager

##ENABLE GEO DISCOVERY BUNDLES
if [ "$confman_geodiscovery" == "enabled" ]
then
	enableBundle postgis
	enableBundle geoindexer
	enableBundle ngsi9geoextension
else
	disableBundle postgis
	disableBundle geoindexer
	disableBundle ngsi9geoextensionfi
fi

##ENABLE NGSI FEDERATION
if [ "$iotbroker_historicalagent" == "enabled" ]
then
	enableBundle ngsihierarchyextension
	enableBundle ngsihierarchystorage
else
	disableBundle ngsihierarchyextension
	disableBundle ngsihierarchystorage
fi

correctConfigIni
