#!/bin/sh

#
# This script sets up the environment before running dynamo or compiling
# files that use dynamo classes.  You must set DYNAMO_HOME to your dynamo
# installation directory before running this script.
#


PSEPARATOR=":";
ATG_IS_CYGWIN=false
if [ -f /bin/cygpath ] ; then
  PSEPARATOR=";"
  ATG_IS_CYGWIN=true
fi


if [ "${DYNAMO_HOME}" = "" ] ; then
  echo "You must set the DYNAMO_HOME environment variable to point to your"
  echo "Dynamo install directory."
  DYNAMO_HOME=.
  DYNAMO_ROOT=..
else
  #
  # Set DYNAMO_ROOT to be the directory above DYNAMO_HOME
  #
  DYNAMO_ROOT=${DYNAMO_HOME}/..
fi
export DYNAMO_ROOT
# 
# Remove the dynamo.env file because it could be 
# left over from a previous instance of dynamo
# and may have unwanted configuration information.
# If this file is needed it will be recreated
# in dafEnv
if [ -f ${DYNAMO_HOME}/dynamo.env ] ; then
 rm $DYNAMO_HOME/dynamo.env
fi
DASENV=$DYNAMO_HOME/localconfig/dasEnv.sh
if [ -f $DASENV ]; then
  . $DASENV
else
  echo "Warning: $DASENV does not exist; did you run the installer?"
  echo "         I will try to work around the problem."
  if [ "${DAS_ROOT}" = "" ] ; then
    DAS_ROOT=$DYNAMO_HOME/..
  fi
  if [ "${ATGJRE}" = "" ] ; then
    ATGJRE=java
  fi
  
  if [ -f /bin/cygpath ] ; then
    DAS_ROOT=`cygpath -w $DAS_ROOT`
  fi

  echo DAS_ROOT=$DAS_ROOT
  echo ATGJRE=$ATGJRE
fi
. $DYNAMO_HOME/bin/j2eeEnv.sh

# UNIX type for this machine
UNIXFLAVOR=`uname`

# UNIX version number
UNIXVER=`uname -r`

SUN_OSDIR_NAME="sparc-sun-solaris2"
HPUX_OSDIR_NAME="hppa1.0-hp-hpux11.00"
AIX_OSDIR_NAME="powerpc-ibm-aix4.2"
LINUX_OSDIR_NAME="i486-unknown-linux2"
CYGWIN_OSDIR_NAME="i486-unknown-win32"

#
# get_flavor - determine which variant of UNIX is being run;
# if SunOS/Solaris, HP-UX or AIX, determine the OS-specific component
# directory; otherwise, assume Solaris.
#

# map SunOS to Solaris
if [ $UNIXFLAVOR = SunOS ]
then
        case $UNIXVER in
        5.6*)   UNIXFLAVOR=Solaris
        ;;
        5.7*)   UNIXFLAVOR=Solaris
        ;;
        5.8*)   UNIXFLAVOR=Solaris
        ;;
        5.9*)   UNIXFLAVOR=Solaris
        ;;
        5.10*)  UNIXFLAVOR=Solaris
        ;;
    esac
fi

# make sure we're installing on a platform we support
if [ $UNIXFLAVOR = Solaris ]
then
    OSDIRNAME=$SUN_OSDIR_NAME
elif [ $UNIXFLAVOR = AIX ]
then
    OSDIRNAME=$AIX_OSDIR_NAME
elif [ $UNIXFLAVOR = HP-UX ]
then
    OSDIRNAME=$HPUX_OSDIR_NAME
elif [ $UNIXFLAVOR = Linux ]
then
    OSDIRNAME=$LINUX_OSDIR_NAME
elif [ $UNIXFLAVOR = CYGWIN_NT-5.1 ]
then
    OSDIRNAME=$CYGWIN_OSDIR_NAME
else
    echo "Warning: unable to determine Unix variant.  Assuming Solaris."
    UNIXFLAVOR=Solaris
    OSDIRNAME=$SUN_OSDIR_NAME
fi

#
# Set the JAVA_VM - could be overridden by environment.sh
#
if [ "x$JAVA_VM" = "x" ] 
then
  JAVA_VM="$ATGJRE"
fi


#
# if JAVA JDK is IBM JDK, include IBM module
#
if [ "$TEMP" = "" ]; then
   TEMP=/tmp
fi
# change the path back to DOS for cygwin 
if [ -f /bin/cygpath ] ; then
  TEMP=`cygpath -d $TEMP`
fi
echo TEMP IS NOW $TEMP
TMPFILE=$TEMP/jdkname$$.txt
if [ "x${SKIP_JAVA_VERSION}" != "xTRUE" ]; then
  # look for jrockit flag in original BEA JAVA_VM setting
  if [ "$BEA_JAVA_VM" = "-jrockit" ]; then
    IS_JROCKIT_JDK=true
  else
    IS_JROCKIT_JDK=false
  fi
  $JAVA_VM -version >$TMPFILE 2>&1
  if [ `grep -c IBM $TMPFILE` -gt 0 ];then 
      IS_IBM_JDK=true
  else
      IS_IBM_JDK=false
  fi
  # second chance for jrockit
  if [ `grep -c JRockit $TMPFILE` -gt 0 ];then 
      IS_JROCKIT_JDK=true
  fi
  rm $TMPFILE
else
  if [ "$IS_JROCKIT_JDK" = "" ]; then
    IS_JROCKIT_JDK=false
  fi
  if [ "$IS_IBM_JDK" = "" ]; then
    IS_IBM_JDK=false
  fi
fi


echo "Before base, JAVA_ARGS=${JAVA_ARGS}"
JAVA_ARGS=${JAVA_ARGS:-"-Xms512m -Xmx1024m"}

# NOTE: If a system property is defined more than once using -D, the
# last setting is the one used.  For example if JAVA_ARGS="-Dfoo=foo
# -Dfoo=bar", then the system property "foo" is set to the value
# "bar".

if [ "true" = "$IS_IBM_JDK" ]; then
    JAVA_ARGS="${JAVA_ARGS} -Djava.net.preferIPv4Stack=true"
elif [ "true" = "$IS_JROCKIT_JDK" ]; then
    JAVA_ARGS="${JAVA_ARGS}"
elif [ "$OSDIRNAME" = "$HPUX_OSDIR_NAME" ] ; then
    JAVA_ARGS="${JAVA_ARGS} -Xss512k -XX:MaxPermSize=128m"
elif [ "$UNIXFLAVOR" = "Solaris" ] ; then
    JAVA_ARGS="${JAVA_ARGS} -XX:MaxPermSize=128m -XX:MaxNewSize=128m"
elif [ "$UNIXFLAVOR" = "Linux" ] ; then
    JAVA_ARGS="${JAVA_ARGS} -XX:MaxPermSize=128m -XX:MaxNewSize=128m"
elif [ "$UNIXFLAVOR" = "AIX" ] ; then
    JAVA_ARGS="${JAVA_ARGS} -Djava.net.preferIPv4Stack=true"
fi

#
# Only set these JAVA_ARGS if we are not using another 3rd party
# server (e.g. weblogic) for our application server.
#
if [ "x$THIRD_PARTY_APP_SERVER" = "x" ] ; then
    JAVA_ARGS=" -Djava.security.policy=${DYNAMO_HOME}/lib/java.policy -Datg.j2eeserver.root=${DAS_ROOT} $JAVA_ARGS"
fi

#
# Module path, a comma-separated list of paths where modules are located
#

if [ -f /bin/cygpath ] ; then
  DYNAMO_ROOT=`cygpath -w $DYNAMO_ROOT`
fi

if [ "x$DAS_ROOT" = "x" ] ; then
    # DAF case
    JAVA_ARGS=" -Datg.dynamo.modulepath=${DYNAMO_ROOT} $JAVA_ARGS"
elif [ "$DAS_STAND_ALONE" = "true" ] ; then
    # DAS only case
    JAVA_ARGS=" -Datg.dynamo.modulepath=${DYNAMO_ROOT} $JAVA_ARGS"
elif [ "$DAS_ROOT" != "$DYNAMO_ROOT" ] ; then
    # Dynamo case
    JAVA_ARGS=" -Datg.dynamo.modulepath=${DYNAMO_ROOT}${PSEPARATOR}${DAS_ROOT} $JAVA_ARGS"
else
    # default case
    JAVA_ARGS=" -Datg.dynamo.modulepath=${DYNAMO_ROOT} $JAVA_ARGS"
fi

JAVA_ARGS=" -Djava.security.policy=lib/java.policy -Datg.dynamo.home=${DYNAMO_HOME} -Datg.dynamo.root=${DYNAMO_ROOT} -Datg.dynamo.display=$DISPLAY -Djava.protocol.handler.pkgs=atg.net.www.protocol -Djava.naming.factory.url.pkgs=atg.jndi.url $JAVA_ARGS"

JAVA_VER=1.5
export JAVA_VER

DYNAMO_CLASSPATH=${DYNAMO_HOME}/locallib/${PSEPARATOR}${DYNAMO_HOME}/lib/launcher.jar
if [ "x$CLASSPATH" != "x" ] ; then
  DYNAMO_CLASSPATH=${DYNAMO_CLASSPATH}${PSEPARATOR}${CLASSPATH}
fi
CLASSPATH=${DYNAMO_CLASSPATH}
#if [ -f /bin/cygpath ] ; then
#  CLASSPATH=`cygpath -p -w ${CLASSPATH}`
#fi
export CLASSPATH

PATH=${PATH}:${DYNAMO_ROOT}/DAS/os_specific_files/${OSDIRNAME}
export PATH

# Under *nix native code needs to be available from LD_LIBRARY_PATH
if [ "x" != "x$LD_LIBRARY_PATH" ] ; then
    LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${DYNAMO_ROOT}/DAS/os_specific_files/${OSDIRNAME}
else
    LD_LIBRARY_PATH=${DYNAMO_ROOT}/DAS/os_specific_files/${OSDIRNAME}
fi

export LD_LIBRARY_PATH

#
# If the user specified the -b option, we skip the loading of the
# application's environment.
#
if [ "x$DYNAMO_BASE" != "x1" ] ; then
  #
  # Load preEnvironment.sh which is modified by installers to separate
  # user's information from programmatic installed information.
  #
  if [ -r ${DYNAMO_HOME}/bin/preEnvironment.sh ] ; then
    . ${DYNAMO_HOME}/bin/preEnvironment.sh
  fi
fi

#
# Get the general application
#
if [ -r ${DYNAMO_HOME}/localconfig/environment.sh ] ; then
  . ${DYNAMO_HOME}/localconfig/environment.sh
fi

#
# Load server specific environment file, if it exists
# 
if [ "$SERVERNAME" != "." ] && [ "$SERVERNAME" != "$DYNAMO_HOME" ] ; then
  JAVA_ARGS="$JAVA_ARGS -Datg.dynamo.server.home=${SERVERNAME}"
  if [ -r $SERVERNAME/localconfig/environment.sh ] ; then
    . $SERVERNAME/localconfig/environment.sh
    else if [ -r $DYNAMO_HOME/$SERVERNAME/localconfig/environment.sh ] ; then
      . $DYNAMO_HOME/$SERVERNAME/localconfig/environment.sh
    fi
  fi
else
  JAVA_ARGS="$JAVA_ARGS -Datg.dynamo.server.home=${DYNAMO_HOME}"
fi

#
# Determine the CLASSPATH and CONFIGPATH by merging explicitly
# specified values with the values computed by analyzing the
# DYNAMO_MODULES list.
#

#
# Installation will add all installed modules to DYNAMO_MODULES in
# environment.sh.  We slam in just DAS here for the base
# configuration.
#
if [ "x$DYNAMO_BASE" = "x1" ] ; then
  DYNAMO_MODULES=DAS
else
  DYNAMO_MODULES=${DYNAMO_MODULES:-"DAS"}
fi

#
# if JAVA JDK is IBM JDK, include IBM module
#
# The following is only for DAS, which is no longer supported
#

#if [ "true" = "$IS_IBM_JDK" ];then 
#    if [ "x$THIRD_PARTY_APP_SERVER" = "x" ] ; then
#        DYNAMO_MODULES=${DYNAMO_MODULES}:IBM
#    fi
#fi

if [ -f /bin/cygpath ] ; then
  DYNAMO_MODULES=`cygpath -pw ${DYNAMO_MODULES}`
fi

JAVA_ARGS="${JAVA_ARGS} -Datg.dynamo.modules=${DYNAMO_MODULES}"
JAVA_ARGS="${JAVA_ARGS} -Datg.dynamo.layers=${CONFIG_LAYERS}"

if [ "${LIVECONFIG}" != "" ]; then
    JAVA_ARGS="${JAVA_ARGS} -D${LIVECONFIG}"
fi

TEMP_ENVIRONMENT_SCRIPT="${TEMP}/dynamoEnv$$.sh"
if [ -f /bin/cygpath ] ; then
  TEMP_ENVIRONMENT_SCRIPT=`cygpath -w ${TEMP_ENVIRONMENT_SCRIPT}`
fi
# On DAF atg.dynamo.root won't be in JAVA_ARGS so it needs to be listed here


# If this variable is set, we use it to define a separate list of modules
# used only to compute the classpath.  This enables us to put a super set
# of the modules into the classpath for running on 3rd party web servers
# where we've installed a different set of web apps than the modules we
# actually want to configure.

if [ "x${THIRD_PARTY_APP_SERVER}" != "x" ] ; then
 DYN_CP_MOD_OPT="-classPathModules ${DYNAMO_MODULES}"
fi

if [ -f /bin/cygpath ] ; then
  DYNAMO_MODULES=`cygpath -pw ${DYNAMO_MODULES}`
fi
echo "CLASSPATH=${CLASSPATH}"

DYNAMO_SERVER_LAUNCHER_CMD="${JAVA_VM} ${JAVA_ARGS} -Datg.dynamo.root=${DYNAMO_ROOT} -Datg.cygwin=${ATG_IS_CYGWIN} atg.applauncher.dynamo.DynamoServerLauncher -environment ${TEMP_ENVIRONMENT_SCRIPT} ${DYNAMO_MODULES} ${DYN_CP_MOD_OPT} ${CONFIGPATH}"
#echo "${JAVA_VM} ${JAVA_ARGS} -Datg.dynamo.root=${DYNAMO_ROOT} atg.applauncher.dynamo.DynamoServerLauncher -environment ${TEMP_ENVIRONMENT_SCRIPT} ${DYNAMO_MODULES} ${DYN_CP_MOD_OPT} ${CONFIGPATH}"
$DYNAMO_SERVER_LAUNCHER_CMD
DYNAMO_SERVER_LAUNCHER_STATUS=$?
if [ "$DYNAMO_SERVER_LAUNCHER_STATUS" != "0" ] ; then
  echo "Error: $DYNAMO_SERVER_LAUNCHER_CMD failed"
  echo "CLASSPATH was $CLASSPATH"
  echo "DYNAMO_HOME was $DYNAMO_HOME"
  echo "DYNAMO_MODULES was $DYNAMO_MODULES"
  rm -f $TEMP_ENVIRONMENT_SCRIPT
  return 1
fi
. $TEMP_ENVIRONMENT_SCRIPT
rm -f $TEMP_ENVIRONMENT_SCRIPT

#
# Load final modifications to environment.
#
if [ -r ${DYNAMO_HOME}/localconfig/postEnvironment.sh ] ; then
  . ${DYNAMO_HOME}/localconfig/postEnvironment.sh
fi

#
# Load final modifications to server specific environment
# 
if [ "$SERVERNAME" != "." ] && [ "$SERVERNAME" != "$DYNAMO_HOME" ] ; then
  if [ -r $SERVERNAME/localconfig/postEnvironment.sh ] ; then
    . $SERVERNAME/localconfig/postEnvironment.sh
    else if [ -r $DYNAMO_HOME/$SERVERNAME/localconfig/postEnvironment.sh ] ; then
	. $DYNAMO_HOME/$SERVERNAME/localconfig/postEnvironment.sh
    fi
  fi
fi
