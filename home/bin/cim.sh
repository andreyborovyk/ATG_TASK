#!/bin/sh 

PROGRAM=`basename $0`
PROGRAM_DIR=`dirname $0`

ulimit -n 1000

# No coredumps
ulimit -c 0

#
# Usage
#
usage() {
  echo
  echo "Usage:"
  echo "cim.sh [-debug] [-record] [-batch inputFilePath]"
  echo
}


# check to make sure dynamo home is set
. $PROGRAM_DIR/checkDynamoHome.sh
DYNAMO_HOME=`pwd`
if [ -r ${DYNAMO_HOME}/localconfig/dasEnv.sh ] ; then
	. ${DYNAMO_HOME}/localconfig/dasEnv.sh
fi 

# check JAVA_HOME
  if [ "$ATGJRE" = "" ] ; then
	 echo You must set the ATGJRE variable to point to the java executable
	 exit 1
  fi
. $DYNAMO_HOME/bin/j2eeEnv.sh  
#
# check and prepare environment for CIM execution
#


if [ "$DEVTOOLS_CLASSPATH" ] ; then
    
  # +++ devtools env setup

  DYNAMO_ROOT=$DEVTOOLS_PROJECTROOT/Dynamo
  CIM_HOME=$DEVTOOLS_PROJECTROOT/CIM
  DBSETUP_HOME=$DEVTOOLS_PROJECTROOT/ATGDBSetup/ATGDBSetupEar/_build; export DBSETUP_HOME

  CIM_CLASSPATH=$CLASSPATH:$DEVTOOLS_PROJECTROOT/devtools/lib/ant.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/ant-nodeps.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/ant-launcher.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/commons-beanutils-1.7.0.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/commons-digester-1.8.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/commons-logging-1.1.1.jar:$CIM_HOME/lib/hsqldb-1.8.0.7.jar:$CIM_HOME/lib/jline-0.9.94.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/incanto-0.2.0.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/jaxb-api101.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/jaxb-impl104.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/jaxb-libs104.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/relaxngDatatype.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/xalan.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/xercesImpl.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/xml-apis.jar:$DEVTOOLS_PROJECTROOT/DAS/src/lib/xsdlib.jar:$DYNAMO_ROOT/home/lib/assembler.jar:$DYNAMO_ROOT/home/lib/launcher.jar:$DEVTOOLS_PROJECTROOT/DAS/buildtree/:$DEVTOOLS_PROJECTROOT/ATGDBSetup/ATGDBSetupEar/_build/classes:$CIM_HOME/build/classes/:$CLASSPATH				
  
else

  # +++ NON devtools env setup

  # check DYNAMO_HOME
  if [ "${DYNAMO_HOME}" = "" ] ; then
      echo "You must set the DYNAMO_HOME environment variable to point to your"
      echo "Dynamo install directory."
      exit 1
  fi

  DYNAMO_ROOT=$DYNAMO_HOME/..

  CIM_HOME=$DYNAMO_ROOT/CIM
  DBSETUP_HOME=$DYNAMO_ROOT/ATGDBSetup; export DBSETUP_HOME

  CIM_CLASSPATH=$CLASSPATH:$CIM_HOME/lib/ant.jar:$CIM_HOME/lib/ant-nodeps.jar:$CIM_HOME/lib/ant-launcher.jar:$CIM_HOME/lib/commons-beanutils-1.7.0.jar:$CIM_HOME/lib/commons-digester-1.8.jar:$DYNAMO_ROOT/DAS/lib/commons-logging-1.1.1.jar:$CIM_HOME/lib/hsqldb-1.8.0.7.jar:$CIM_HOME/lib/jline-0.9.94.jar:$CIM_HOME/lib/incanto-0.2.0.jar:$DYNAMO_ROOT/DAS/lib/jaxb-api101.jar:$DYNAMO_ROOT/DAS/lib/jaxb-impl104.jar:$DYNAMO_ROOT/DAS/lib/jaxb-libs104.jar:$DYNAMO_ROOT/DAS/src/lib/relaxngDatatype.jar:$DYNAMO_ROOT/DAS/src/lib/xalan.jar:$DYNAMO_ROOT/DAS/src/lib/xercesImpl.jar:$DYNAMO_ROOT/DAS/src/lib/xml-apis.jar:$DYNAMO_ROOT/DAS/src/lib/xsdlib.jar:$DYNAMO_ROOT/home/lib/assembler.jar:$DYNAMO_ROOT/home/lib/launcher.jar:$DYNAMO_ROOT/DAS/lib/classes.jar:$DYNAMO_ROOT/DAS/lib/resources.jar:$DYNAMO_ROOT/ATGDBSetup/j2ee-apps/ATGDBSetup/web-app/WEB-INF/lib/classes.jar:$CIM_HOME/lib/classes.jar:$CLASSPATH	
	
fi

  CIM_ARGS="-Dcim.home=$CIM_HOME -Ddynamo.root=$DYNAMO_ROOT"

  CLASSPATH=$CIM_CLASSPATH
  export CLASSPATH


#
# Parse the input arguments
#

DEBUG_ARGS=""
RECORD_ARGS=""

# default mode is console
MODE_ARGS="-Dcim.console=true"

while [ $# -ne 0 ]
  do
  if [ "$1" = "-debug" ] ; then
      DEBUG_ARGS="-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=3000"
	  echo "Debug mode enabled on port 3000.  Waiting for connection to Attach."
      
  elif [ "$1" = "-mock" ] ; then
      MODE_ARGS="-Dcim.mock=true"      
  
  elif [ "$1" = "-record" ] ; then
      RECORD_ARGS="-Dcim.record=true"  
	  echo "Batch Mode Recording Enabled"
	  
  elif [ "$1" = "-batch" ] ; then
      shift
      if [ -f "$1" ]; then
	  MODE_ARGS="-Dcim.batch=true -Dcim.batch.inputFileName=$1"
      else
	  echo "Not a valid file : $1"
	  usage
	  exit 1
      fi
      
  else 
      echo "Invalid option(s) supplied"
      usage
      exit 1
  fi

  shift

done

"$ATGJRE" -Xmx512m $DEBUG_ARGS $MODE_ARGS $RECORD_ARGS $CIM_ARGS atg.cim.Launcher


