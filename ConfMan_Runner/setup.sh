#!/bin/bash

# Functions
. confman_functions.sh

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

if [ -e .confman.conf.runtime ];
then
	echo "Reading runtime preferences from .confman.conf.runtime"
	sed -e 's/[]\/$*.^|[]/\\&/g' ./.confman.conf.runtime > ./.confman.conf.runtime.escaped
	chmod +x .confman.conf.runtime.escaped
	. ./.confman.conf.runtime.escaped
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
	confman_knowledgebaseproperties_auto="$confmandir/fiwareRelease/confmanconfig/knowledgeBase/knowledgeBase.properties"

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
		
		setConfiguration "confman_configini" "$confman_configini_auto" "confman_functions.sh"
		setConfiguration "confman_configxml" "$confman_configxml_auto" "confman_functions.sh"
		setConfiguration "confman_configproperties" "$confman_configproperties_auto" "confman_functions.sh"
		setConfiguration "confman_launcher" "$confman_launcher_auto" "confman_functions.sh"
		setConfiguration "confman_launcherasdaemon" "$confman_launcherasdaemon_auto" "confman_functions.sh"
		setConfiguration "confman_loggerproperties" "$confman_loggerproperties_auto" "confman_functions.sh"
		setConfiguration "confman_knowledgebaseproperties" "$confman_knowledgebaseproperties_auto" "confman_functions.sh"
		
		if [ -n "$confman_version_auto" ];
		then
				setConfiguration "confman_version" "$confman_version_auto" "confman_functions.sh"
		fi
	fi

	confman_configini=$confman_configini_auto
	confman_configxml=$confman_configxml_auto
	confman_configproperties=$confman_configproperties_auto
	confman_version=$confman_version_auto
	confman_launcher=$confman_launcher_auto
	confman_launcherasdaemon=$confman_launcherasdaemon_auto
	confman_loggerproperties=$confman_loggerproperties_auto
	confman_knowledgebaseproperties=$confman_knowledgebaseproperties_auto
	
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
setFirstPropertyValueOverMultipleValuesIntoProperties "log4j.rootLogger" "$confman_loglevel" "$confman_loggerproperties"

setConfiguration "configProperties" "$confman_configproperties" "$confman_launcher"
setConfiguration "configProperties" "$confman_configproperties" "$confman_launcherasdaemon"

setPropertyIntoProperties "knowledgebase_address" "$confman_knowledgebaseaddress" "$confman_knowledgebaseproperties"
setPropertyIntoProperties "knowledgebase_port" "$confman_knowledgebaseport" "$confman_knowledgebaseproperties"

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

##ENABLE SEMANTIC BUNDLES
if [ "$confman_semantic" == "enabled" ]
then
	enableBundle knowledgebase
else
	disableBundle knowledgebase
fi

correctConfigIni
