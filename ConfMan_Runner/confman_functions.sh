#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

confman_configini='/opt/NEConfMan/ConfMan_Runner/confman/configuration/config.ini'
confman_configxml='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.xml'
confman_configproperties='/opt/NEConfMan/fiwareRelease/confmanconfig/configurationManager/config/config.properties'
confman_launcher="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager.sh"
confman_launcherasdaemon="/opt/NEConfMan/ConfMan_Runner/startConfigurationManager_as_daemon.sh"
confman_loggerproperties="/opt/NEConfMan/fiwareRelease/bundleConfigurations/services/org.ops4j.pax.logging.properties"
confman_knowledgebaseproperties="/opt/NEConfMan/fiwareRelease/confmanconfig/knowledgeBase/knowledgeBase.properties"


confman_version="5.4.3"

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


function setFirstPropertyValueOverMultipleValuesIntoProperties {
	
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}

	
	sed -i "s/$key=[^,]*/$key=$2/g" "$3"

	
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
	
	found=`grep "\/$1-$confman_version.jar" confman/configuration/config.ini`
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
