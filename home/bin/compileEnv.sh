#!/bin/sh 

# Returns the CLASSPATH required for a given set of Dynamo 
# modules

# Global Variables
ENVFILE=.atgEnv.tmp
MODULES=$1
JAVA_COMMAND=java

# Prints usage information
usage()
{
 echo "usage: compileEnv.sh [ module list ]"
}

# Make sure we have everything we need to run
preFlightCheck() {
  if [ "x${DYNAMO_HOME}" = "x" ] ; then
    usage
    return 1
  fi
  if [ "x${MODULES}" = "x" ] ; then
    usage
    return 1
  fi
 }

setupJavaCommand() {
 if [ "x${JAVA_HOME}" != "x" ] ; then
   if [ -d ${JAVA_HOME} ] ; then
     if [ -f ${JAVA_HOME}/bin/java ] ; then 
      JAVA_COMMAND=${JAVA_HOME}/bin/java
     fi
   fi
 fi
}

handleModules() {
while [ "x$1" != "x" ]
do
MODULES=${MODULES}:$1
shift
done
export MODULES
}

# Actually invokes java to get the launcher
invokeLauncher() 
{
  ${JAVA_COMMAND} -classpath ${DYNAMO_HOME}/lib/launcher.jar -Datg.dynamo.root=${DYNAMO_ROOT} atg.applauncher.dynamo.DynamoServerLauncher -environment $ENVFILE ${MODULES}
  if [ -f ${ENVFILE} ] ; then
    . ${ENVFILE}
    rm ${ENVFILE}
  fi
}

# Main 
preFlightCheck
if [ "$?" -eq "1" ]
then
    return 1
fi
handleModules $*
setupJavaCommand
invokeLauncher
