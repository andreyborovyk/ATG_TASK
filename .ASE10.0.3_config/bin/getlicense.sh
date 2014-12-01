#!/bin/sh


# *** NOTE: licensed modules have been hard-coded.  replace PRODUCT_LICENSE_MODULES with appropriate module(s). 

USERNAME=$1
PASSWORD=$2


PROGRAM=`basename $0`
ORIGCLASSPATH=$CLASSPATH
ORIGCONFIG=$CONFIGPATH
ORIGPATH=$PATH
ORIGARGS=$JAVA_ARGS

usage() 
{
    echo "USAGE: ${PROGRAM} [-v | -i] [-m additional-module-list]"
    echo "     -v   Print version info and exit"
    echo "     -i   Print IP addresses of this machine and exit"
    echo "     -m   Run with the additionally specified modules\n"
}

ulimit -n 1000

# No coredumps
ulimit -c 0

#
# Check that DYNAMO_HOME and DAS_ROOT are set
#
PROGRAM_DIR=`dirname $0`
. $PROGRAM_DIR/checkDynamoHome.sh

#
# Load common vars
#
. bin/commonVars.sh

SERVERNAME="."
SERVERCONFIGPATH=":localconfig"
DYNAMO_MODULES="DAS:DAS-UI"
DYNAMO_LICENSED_MODULES="DAS:DAS-UI"

#
# Save original first argument to pick up later
#

#
# Logging is turned on by default
#
LOG=1

if [ ! -d $SERVERNAME ] ; then
  echo "Cannot find server directory named: $SERVERNAME in the current directory: `pwd`"
  exit 1
fi


#############################################################
# Setup a trap for the given signals to pass to our child process
shutdown_dynamo ()
{
  if [ "x$FOREGROUND" != "x1" ] ; then  
    if [ "x$DYNAMO_PID" != "x$$" ] ;  then
        if [ "$DYNAMO_PID" != "unset" ] ; then
	  kill -TERM $DYNAMO_PID > /dev/null 2>&1
        elif [ -r $DYNAMO_PIDFILE ] ; then
          kill -TERM `cat $DYNAMO_PIDFILE`
        fi
    fi
  fi
}

trap "shutdown_dynamo" 1 2 3 


if [ "x${ADDITIONAL_MODULES}" != "x" ] ; then
    DYNAMO_LICENSED_MODULES=${DYNAMO_LICENSED_MODULES}:${ADDITIONAL_MODULES}
fi

#
# Load in default environment values
#
if [ -r ${DYNAMO_HOME}/localconfig/environment.sh ] ; then
  . ${DYNAMO_HOME}/localconfig/environment.sh
fi
DYNAMO_MODULES=LM:${DYNAMO_LICENSED_MODULES}
. bin/dynamoEnv.sh

if [ "x$ARG1" = "x-i" ] || [ "x$ARG1" = "x-v" ] ; then
   ${JAVA_VM} ${JAVA_ARGS} atg.service.dynamo.Configuration $ARG1
exit
fi

#
# Tack this onto the end so that it is always at the end of the config path
#
CONFIGPATH=${CONFIGPATH}${SERVERCONFIGPATH}

#
# append DCC to modules list if we are to run it in this VM
#
if [ "x${RUN_DCC}" = "x1" ] ; then
  DYNAMO_MODULES=${DYNAMO_MODULES}:DCC
fi

# EnvironmentLogger tool
${JAVA_VM} ${JAVA_ARGS} atg.service.dynamo.EnvironmentLogger LM:$DYNAMO_LICENSED_MODULES $CONFIGPATH

STATUS=17
COMMAND="${JAVA_VM} ${JAVA_ARGS} ${DYN_EXTRA_ARGS} atg.applauncher.dynamo.DynamoServerLauncher LM LM:$DYNAMO_LICENSED_MODULES $CONFIGPATH $USERNAME $PASSWORD"
DYNAMO_PIDFILE="$SERVERNAME/logs/$DYNAMO_PIDFILE_NAME"
STATUS_FILE="$SERVERNAME/logs/$STATUS_FILE_NAME"

dynamo_startup_info() 
{
    ${JAVA_VM} ${JAVA_ARGS} atg.service.dynamo.Configuration -v 
    echo "" 
    ${JAVA_VM} -version 2>&1
    ${JAVA_VM} -fullversion 2>&1
    echo; echo "JAVA_ARGS:       $JAVA_ARGS"
    echo; echo "CONFIGPATH:      $CONFIGPATH"
    echo; echo "CLASSPATH:       $CLASSPATH"
    echo; echo "PATH:            $PATH"
    if [ "x" != "x$LD_LIBRARY_PATH" ] ; then
      echo; echo "LD_LIBRARY_PATH: $LD_LIBRARY_PATH"    
    fi 
    echo ""
}

dynamo_startup_info
echo "command: $COMMAND"
exec $COMMAND 2>&1       


