#!/bin/bash

DIR=`dirname $0`
cd $DIR
java -Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -cp .:./config: -jar ./lib/device-state-manager-0.0.1-SNAPSHOT.jar /dev/null 2>&1 &
#java -Xms1024m -Xmx4096m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m  -cp .:./config:./lib/* com.ns.insecticidallamp.InsecticidallampApplication> /dev/null 2>&1 &
echo $! > pid.pid