#!/bin/sh

##########################################################################
#
# This script verifies that DYNAMO_HOME and DAS_ROOT are set.  If either
# is not set, it attempts to find its value.
#
##########################################################################


# directory that this script resides in
_SCRIPT_DIR=`dirname $0`

# The function tries to set THIRD_PARTY_APP_SERVER using 
# corresponding system variable.
#
setThirdPartyAppServer()
{
  # Provide a default value for CATALINA_HOME.
  if [ ! "${CATALINA_HOME}" -o -z "${CATALINA_HOME}" ]; then
    CATALINA_HOME="${DYNAMO_HOME}/../Tomcat/apache-tomcat-6.0.26"
    export CATALINA_HOME
  fi

  if [ "x$WL_HOME" != "x" ] ; then
    THIRD_PARTY_APP_SERVER="weblogic"
  elif [ "x$JBOSS_HOME" != "x" ] ; then
    THIRD_PARTY_APP_SERVER="jboss"
  elif [ "x$WAS_HOME" != "x" ] ; then
    THIRD_PARTY_APP_SERVER="websphere"
  elif [ "x$CATALINA_HOME" != "x" ] ; then
    THIRD_PARTY_APP_SERVER="tomcat"
  fi
}

#
# This function tries to find DYNAMO_HOME by first looking in the
# current directory, then the parent directory, then the script
# directory.
#
# If it cannot find it tells the user and then exits the scripts.
#
# If it does find it tells the user what it thinks DYNAMO_HOME is.
#
findDynamoHome()
{
  if [ -r dafEnv.sh ] ; then
    DYNAMO_HOME=..
  elif [ -r bin/dafEnv.sh ] ; then
    DYNAMO_HOME=.
  elif [ -r "$_SCRIPT_DIR/dafEnv.sh" ] ; then
    DYNAMO_HOME="$_SCRIPT_DIR/.."
  else
    # failure
    echo "You must run this script from the ATG10.0.3/home/bin directory.  Alternatively,"
    echo "if you set the DYNAMO_HOME environment variable to point to the home"
    echo "subdirectory, you can run this script from any directory. For information"
    echo "about setting DYNAMO_HOME, see the Dynamo Administration Guide."
    exit 1
  fi

  # success
  if [ "${CHECK_DYNAMO_HOME_DEBUG}" = "true" ] ; then
    echo "Valid DYNAMO_HOME was located: \"${DYNAMO_HOME}\""
    echo
  fi
}

#
# If DYNAMO_HOME is set by the user, attempt to use that value.  If it is
# not valid, or not set at all, try to use the working directory.
#
echo DYNAMO_HOME = $DYNAMO_HOME

if [ "x${DYNAMO_HOME}" != "x" ] ; then
  if [ ! -r ${DYNAMO_HOME}/bin/dafEnv.sh ] ; then
    # DYNAMO_HOME invalid, try to find it
    echo "Invalid setting of DYNAMO_HOME: \"${DYNAMO_HOME}\""
    echo "Attempting to locate a valid DYNAMO_HOME based on your working directory."
    echo
    findDynamoHome
  fi
else
  # DYNAMO_HOME not set, try to find it
  if [ "${CHECK_DYNAMO_HOME_DEBUG}" = "true" ] ; then
    echo "DYNAMO_HOME is not set."
    echo "Attempting to locate a valid DYNAMO_HOME based on your working directory."
    echo
  fi
  findDynamoHome
fi

# Set up our third party app server
setThirdPartyAppServer

#
# Change to DYNAMO_HOME directory and then set DYNAMO_HOME to "."
#
cd $DYNAMO_HOME
DYNAMO_HOME=.

#
# If DAS_ROOT is not set by the user, attempt to find its value from
# dasEnv.sh.  If it is not there and the user is not running a third
# party app server then warn the user.
#
if [ "x$DAS_ROOT" = "x" ] ; then
  if [ -r ${DYNAMO_HOME}/localconfig/dasEnv.sh ] ; then
    . ${DYNAMO_HOME}/localconfig/dasEnv.sh
    if [ "x$DAS_ROOT" = "x" ] ; then
      if [ "x$WL_HOME" != "x" ] ; then
        THIRD_PARTY_APP_SERVER="weblogic"
      elif [ "x$JBOSS_HOME" != "x" ] ; then
        THIRD_PARTY_APP_SERVER="jboss"
      elif [ "x$WAS_HOME" != "x" ] ; then
        THIRD_PARTY_APP_SERVER="websphere"
      elif [ "x$CATALINA_HOME" != "x" ] ; then
        THIRD_PARTY_APP_SERVER="tomcat" ;
      fi
    fi
  else
    # warning
    if [ "x$THIRD_PARTY_APP_SERVER" = "x" ]; then
      if [ "${CHECK_DYNAMO_HOME_DEBUG}" = "true" ] ; then
        echo "WARNING: DAS_ROOT is not set.  If you are using the Dynamo Application Server"
        echo "then you must set the environment variable DAS_ROOT to point to the"
        echo "Dynamo Application Server installation directory."
        echo
      fi
    fi
  fi
fi
