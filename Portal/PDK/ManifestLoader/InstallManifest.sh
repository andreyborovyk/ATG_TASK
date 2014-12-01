#!/bin/sh

PROGRAM=`basename $0`
PROGRAM_DIR=`dirname $0`
OWD=`pwd`
ORIG_ARGS=$*

#
# Allow us to be in the bin directory when we start
#
if [ -r startDynamo ] ; then
  DYNAMO_HOME=..
fi

if [ "x${DYNAMO_HOME}" = "x" ] ; then 
	DYNAMO_HOME=`pwd`
fi

export DYNAMO_HOME

# checkhome:
cd ${DYNAMO_HOME}
if [ ! -f bin/startSQLRepository ] ; then 
  echo - invalid setting of DYNAMO_HOME=$DYNAMO_HOME
  exit
fi

#
# If run from the install dir, set DYNAMO_HOME automatically.  Otherwise,
# pick it up as an environment variable
#
if [ -r bin/startDynamo ] ; then
  DYNAMO_HOME=.
elif [ "$DYNAMO_HOME" = "" ] ; then
  if [ -f "$PROGRAM_DIR/startDynamo" ]; then
    DYNAMO_HOME="$PROGRAM_DIR/.."
    cd $DYNAMO_HOME
  else
    echo You must either run this script from the Dynamo installation directory
    echo or you should set the environment variable DYNAMO_HOME to point to this
    echo directory.
    exit 1
  fi
else
  cd $DYNAMO_HOME
  if [ ! -r bin/startDynamo ] ; then
    echo Unable to find ${DYNAMO_HOME}/bin/startDynamo - invalid setting of DYNAMO_HOME
    exit 1
  fi
fi

#
# Load common vars
#
$DYNAMO_HOME/bin/commonVars.sh

#
# Make sure Portals is installed
#
if [ ! -f ${DYNAMO_HOME}/../Portal/paf/portal.war ] ; then 
  echo Portals must be installed to run InstallManifest. 
  exit
fi

CLASSPATH=${DYNAMO_HOME}/../Portal/PDK/ManifestLoader/ManifestLoader.jar:${CLASSPATH} ; export CLASSPATH

DYNAMO_MODULES="Portal.paf"
SERVERNAME="."
SERVERCONFIGPATH=""
TPARGS=""

#
# Add appropriate dir or jar to config path for TransactionManager
#
if [ -d ../DAS/config/dtmconfig ] ; then
  SERVERCONFIGPATH=:../DAS/config/dtmconfig
fi
if [ -f ../DAS/config/dtmconfig.jar ] ; then
  SERVERCONFIGPATH=:../DAS/config/dtmconfig.jar
fi

SERVERCONFIGPATH="${SERVERCONFIGPATH}:localconfig"

while [ "x$1" != "x" ]
do
case $1 in
      -s) SERVERNAME="servers/$2"
          SERVERCONFIGPATH="${SERVERCONFIGPATH}:servers/$2/localconfig"
          shift
          ;;
      -m) while [ "x$2" != "x" ]
	  do
	    case $2 in
	       -*)  break;;
	   Admin*) ADMIN_MODULES="${ADMIN_MODULES}:$2" ;;
		*) ADDITIONAL_MODULES="${ADDITIONAL_MODULES}:$2" ;;
	    esac
	  shift
	  done;;
       *) TPARGS="${TPARGS} \"$1\""
          ;;
      esac
  shift
done

if [ "x${ADDITIONAL_MODULES}" != "x" ] ; then
    DYNAMO_MODULES=${DYNAMO_MODULES}:${ADDITIONAL_MODULES}
fi

. bin/dynamoEnv.sh

if [ "x${JAVA_VM}" = "x" ] ; then 
  echo JAVA_VM not set.
  exit
fi

CONFIGPATH=${CONFIGPATH}$SERVERCONFIGPATH

#echo JAVA_ARGS=$JAVA_ARGS
cat << EOF > /tmp/tempcmd$$
${JAVA_VM} ${JAVA_ARGS} atg.portal.util.ManifestLoader InstallManifest -startDir ${OWD} -configPath ${CONFIGPATH} ${TPARGS}
EOF

. /tmp/tempcmd$$

rm /tmp/tempcmd$$

# endofscript:
