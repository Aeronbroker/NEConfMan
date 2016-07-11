#!/bin/bash

CONFIGINI="configuration/config.ini"

RESET=false
CONSOLE=false

# READING THE OPTIONS
while [[ $# > 0 ]]
do
key="$1"
case $key in
    -r|--reset)
		RESET=true
		;;
	--console)
		CONSOLE=true
		;;
    -p|--property)
		if [[ "$2" != *=* ]]
		then
			echo "WARN: wrong property specification $1 $2. Please use -p|--property <property_name>=\"<property_value>\""
		else
			#PROPERTIES[${#PROPERTIES[*]}+1]="$2"
			value=${2/=/=\'}
			value="$value'"
			PROPERTIES+=($value)
			shift # past argument
		fi
		;;
    *)
		echo "WARN: Unknown option $key"
        # unknown option
    ;;
esac
shift # past argument or value
done

if [ $CONSOLE == false ]
then
	if [ -f pid.file ];
	then
		if [ -z "`ps -ef | grep org.eclipse.osgi-3.7.0.v20110221.jar | grep -v grep`" ]; 
		then 
			rm pid.file
		else
			HERE=`pwd`
			echo "An instance of the IoT Discovery seems already running. If you are sure that this is not the case, delete the file $HERE/pid.file"
			exit 1
		fi
	fi
fi

if [ -n "$PROPERTIES" ]
then
	echo "RunTime properties set:"
	printf '%s\n' "${PROPERTIES[@]}"
	echo "#!/bin/bash" > .confman.conf.runtime 
	printf '%s\n' "${PROPERTIES[@]}" >> .confman.conf.runtime
else 
	rm -f .confman.conf.runtime
fi

./setup.sh

. confman_functions.sh


if [ "$RESET" = "true" ]; then
	enableBundle reset
else
	disableBundle reset
fi

if [ $CONSOLE == true ]
then
	java -jar confman/org.eclipse.osgi-3.7.0.v20110221.jar -console
else
	nohup java -jar confman/org.eclipse.osgi-3.7.0.v20110221.jar >/dev/null 2>&1 & echo $! > pid.file
fi
