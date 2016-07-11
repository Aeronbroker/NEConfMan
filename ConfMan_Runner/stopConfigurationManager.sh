#!/bin/bash
PID=`cat pid.file 2> /dev/null`
if [ -z "$PID" ];
then
	echo "IoT Discovery is not running"
else
	kill -9 $PID
	rm pid.file
fi
