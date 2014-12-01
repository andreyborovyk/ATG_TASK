#!/bin/sh

# postEnvironment.sh
# Modify your CLASSPATH, CONFIGPATH, and JAVA_ARGS 
# settings in this file.
# See the Dynamo documentation for more information.

# CLASSPATH variable
CLASSPATH=${CLASSPATH}
# PATH variable
PATH=${PATH}
# CONFIGPATH variable
CONFIGPATH=${CONFIGPATH}
# JAVA_ARGS variable
JAVA_ARGS=${JAVA_ARGS}

# Recommended heap settings for deployment(required for Portals App. Framework)
# -XX:NewSize and -XX:MaxNewSize must be at least 25% of heapsize - examples
# are for 512MB heap 
#
# uncomment on Solaris and adjust for your heap:
#JAVA_ARGS="${JAVA_ARGS} -XX:NewSize=128m -XX:MaxNewSize=128m -XX:SurvivorRatio=40 -Xmx512m -Xms512m"
#
# uncomment on HP/UX and adjust for your heap: 
#JAVA_ARGS="${JAVA_ARGS} -Xmn128m -XX:SurvivorRatio=40 -Xmx512m -Xms512m"
#
# Decrease the default RMI garbage collection rate
JAVA_ARGS="${JAVA_ARGS} -Dsun.rmi.dgc.server.gcInterval=3600000"
# Enable debugging Dynamo over JDWP if DEBUG_ENABLED is set to true
if [ "$DEBUG_ENABLED" = "true" ] ; then
 JAVA_ARGS="${JAVA_ARGS} -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$JAVA_DEBUG_PORT"
fi
