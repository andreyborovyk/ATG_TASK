#!/bin/sh

# -x

# -- 
# -- This script set up variables that would normally be set in startDynamo.
# -- It then calls dynamoEnv.sh before it exits.
# --

if [ "${DYNAMO_ROOT}" = "" ] ; then
  echo "You must set the DYNAMO_ROOT variable before running dafEnv.sh"
  exit 1
fi

DYNAMO_HOME=${DYNAMO_ROOT}/home
export DYNAMO_HOME

POINTER_DIR=.

#if [ "x$#" = "x1" ]; then
#  POINTER_DIR="$1"
#fi

# destination directory in which to place dynamo.env
if [ "${SERVERNAME}" = "" ] || [ "${SERVERNAME}" = "." ] ; then
 SERVERNAME="."
 SERVERHOME=$DYNAMO_HOME
 DST_DIR=$DYNAMO_HOME
 SERVERCONFIGPATH=${DYNAMO_HOME}/localconfig
else
 if [ -d ${DYNAMO_HOME}/${SERVERNAME} ]; then
   DST_DIR=${DYNAMO_HOME}/${SERVERNAME}
   SERVERCONFIGPATH=${DYNAMO_HOME}/localconfig:${DYNAMO_HOME}/${SERVERNAME}/localconfig
   SERVERHOME=${DYNAMO_HOME}/${SERVERNAME}
 else
   echo "Dynamo server home directory ${DYNAMO_HOME}/${SERVERNAME} does not exist.";
   DST_DIR=$DYNAMO_HOME
   SERVERHOME=$DYNAMO_HOME
   SERVERCONFIGPATH=${DYNAMO_HOME}/localconfig
 fi
fi

# DAF must always run with at least DPS for licensing since we don't
# yet have DAF licenses.
DYNAMO_MODULES="DPS:${DYNAMO_MODULES}"

# Include the DAS-UI module by default in front of the user specified
# modules (if any)
if [ "${DYNAMO_NO_UI}" = "" ] ; then
  DYNAMO_MODULES="DAS-UI:${DYNAMO_MODULES}"
fi

# -- Set Up dynamo variables
. $DYNAMO_HOME/bin/dynamoEnv.sh

# Get the user agreement on license, quit otherwise
${JAVA_VM} atg.nucleus.Nucleus -requireLicense || exit 1

if [ ! -r ${DYNAMO_ROOT}/DAF/config/dafconfig.jar ] ; then
  echo "Unable to find file: $DYNAMO_ROOT/DAF/config/dafconfig.jar" 
  exit 1
fi

# Do not add dafconfig if THIRD_PARTY_APP_SERVER  not set
if [ ! "${THIRD_PARTY_APP_SERVER}" = "" ] ; then
  CONFIGPATH=${CONFIGPATH}:$DYNAMO_ROOT/DAF/config/dafconfig.jar
fi


ASCONFIG=""

#--------------------------------------------------------------
# App server specific config needs to comes after dafconfig.jar
if [ "${THIRD_PARTY_APP_SERVER}" = "ias" ] ; then
  ASCONFIG="iasconfig"
  # Only need xerces.jar which is bundled in classes.jar for now
  CLASSPATH=$DYNAMO_ROOT/DAS/lib/classes.jar:$CLASSPATH
elif [ "${THIRD_PARTY_APP_SERVER}" = "websphere" ] ; then
  ASCONFIG="wsconfig"
elif [ "${THIRD_PARTY_APP_SERVER}" = "weblogic" ] ; then
  ASCONFIG="wlconfig"
elif [ "${THIRD_PARTY_APP_SERVER}" = "bluestone" ] ; then
  ASCONFIG=""
elif [ "${THIRD_PARTY_APP_SERVER}" = "oas" ] ; then
  ASCONFIG="oasconfig"
else
  echo "Unrecognized value for THIRD_PARTY_APP_SERVER variable: ${THIRD_PARTY_APP_SERVER}.  Should be one of weblogic, websphere, bluestone, ias, oas"
  ASCONFIG=""
fi

if [ ! "${ASCONFIG}" = "" ] ; then
  if [ ! -r ${DYNAMO_ROOT}/DAF/config/${ASCONFIG}.jar ] ; then
    echo "Unable to find app server specific config file: $DYNAMO_ROOT/DAF/config/${ASCONFIG}.jar" 
  else
    CONFIGPATH=${CONFIGPATH}:$DYNAMO_ROOT/DAF/config/${ASCONFIG}.jar
  fi
fi

# -- Add Dynamo localconfig to CONFIGPATH
CONFIGPATH=$CONFIGPATH:$SERVERCONFIGPATH
export CONFIGPATH

# -- Java Arguments
# Add path to dynamo.env in JAVA_ARGS
JAVA_ARGS="${JAVA_ARGS} -Datg.dynamo.environmentfile=${DST_DIR}/dynamo.env"

# -- Nucleus
# Tells Nucleus where to get server specific dynamo.env
DYNAMOENV_COMMAND="${JAVA_VM} ${JAVA_ARGS} -Datg.dynamo.root=${DYNAMO_ROOT} atg.applauncher.dynamo.DynamoServerLauncher -dynamoenv"
${DYNAMOENV_COMMAND} ${POINTER_DIR}/dynamo.env atg.dynamo.environmentfile=${DST_DIR}/dynamo.env
# Now write the main one to $DYNAMO_HOME
DYNAMO_ENV_ARGS=atg.dynamo.configpath=${CONFIGPATH}
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},atg.dynamo.modules=${DYNAMO_MODULES}
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.root=${DYNAMO_ROOT}"
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.home=${DYNAMO_HOME}" 
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.server.home=${SERVERHOME}"
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.appserver=${THIRD_PARTY_APP_SERVER}"
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.display=${DISPLAY}"
DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"atg.dynamo.classpath=${CLASSPATH}"
if [ ! "${LIVECONFIG}" = "" ] ; then
  DYNAMO_ENV_ARGS=${DYNAMO_ENV_ARGS},"${LIVECONFIG}"
fi
echo $DYNAMO_ENV_ARGS
${DYNAMOENV_COMMAND} ${DST_DIR}/dynamo.env ${DYNAMO_ENV_ARGS} 
cat ${DST_DIR}/dynamo.env

# -- END DAF 
