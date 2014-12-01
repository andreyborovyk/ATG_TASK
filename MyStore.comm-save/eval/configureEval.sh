#!/bin/bash 

PROGRAM=`basename $0`
PROGRAM_DIR=`pwd`

ulimit -n 1000

# No coredumps
ulimit -c 0


if [ "$DEVTOOLS_CLASSPATH" ] ; then
    
  # +++ devtools env setup

  DYNAMO_ROOT=$DEVTOOLS_PROJECTROOT/Dynamo
  DYNAMO_HOME=$DYNAMO_ROOT/home
  CIM_HOME=$DEVTOOLS_PROJECTROOT/CIM

  EVAL_CLASSPATH=$DEVTOOLS_PROJECTROOT/devtools/lib/ant.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/ant-nodeps.jar:$DEVTOOLS_PROJECTROOT/devtools/lib/ant-launcher.jar;
else

  # +++ NON devtools env setup

  # check JAVA_HOME
  if [ "$JAVA_HOME" = "" ] ; then
    echo "The JAVA_HOME environment variable must exist and point to the jdk1.6 directory"
    exit 0
	fi 

  # check DYNAMO_HOME
  if [ "${DYNAMO_HOME}" = "" ] ; then
      echo "You must set the DYNAMO_HOME environment variable to point to your"
      echo "Dynamo install directory."
      exit 0
  fi

  DYNAMO_ROOT=$DYNAMO_HOME/..
  CIM_HOME=$DYNAMO_ROOT/CIM

  EVAL_CLASSPATH=$CIM_HOME/lib/ant.jar:$CIM_HOME/lib/ant-nodeps.jar:$CIM_HOME/lib/ant-launcher.jar;
fi

. $DYNAMO_HOME/localconfig/dasEnv.sh


CIM_OUT_SCRIPT=$PROGRAM_DIR/cimOut.cim

CIMBATCH_APPSERVER=$PROGRAM_DIR/cimbatch-jboss.cim
CIMBATCH_DB=$PROGRAM_DIR/cimbatch-soliddb.cim
CIMBATCH_ATGSERVER=$PROGRAM_DIR/cimbatch-atgserver.cim
CIMBATCH_DEPLOY=$PROGRAM_DIR/cimbatch-jbossdeploy.cim

if [ "$JBOSS_HOME" != "" ] ; then
  CIMBATCH_APPSERVER=$PROGRAM_DIR/cimbatch-jboss.cim
  CIMBATCH_DEPLOY=$PROGRAM_DIR/cimbatch-jbossdeploy.cim

  APP_SERVER_ARGS="-Djboss.home=$JBOSS_HOME"
fi

if [ "$WL_HOME" != "" ] ; then

  echo Evaluation Configuration is not available with a Weblogic application server.
  exit

  #. ${DYNAMO_HOME}/localconfig/environment.sh
  #eval `awk -F = '$1=="directoryRoots"{print "DOMAIN_PATH=\"" $2 "/../\"" }' ${DYNAMO_HOME}/localconfig/atg/devtools/ContainerAppDirectoryAgent.properties`
  #CIMBATCH_APPSERVER=$PROGRAM_DIR/cimbatch-wl.cim
  #CIMBATCH_DEPLOY=$PROGRAM_DIR/cimbatch-wldeploy.cim
  #APP_SERVER_ARGS="-Dwl.path=$WL_HOME -Dwl.version=$WL_VERSION -Ddomain.path=$DOMAIN_PATH  -Dbea.home=$BEA_HOME"
fi

if [ "$WAS_HOME" != "" ] ; then
  echo Evaluation Configuration is not available with a Websphere application server.
  exit

  #Get node name from ConainterAppDirectoryAgent
  #eval `awk -F = '$1=="directoryRoots"{print "DOMAIN_PATH=\"" $2 "\"" }' ${DYNAMO_HOME}/localconfig/atg/devtools/ContainerAppDirectoryAgent.properties`
  #eval `echo $DOMAIN_PATH | awk -F "/" '{ print "WAS_NODE=\"" $NF "\"" }'`
  
  #CIM_SCRIPT="$PROGRAM_DIR%/cimbatch-was.cim"
  #CIM_SCRIPT="$PROGRAM_DIR%/cimbatch-wasdeploy.cim"
  #APP_SERVER_ARGS="-Dwas.path=$WAS_HOME -Dwas.node=$WAS_NODE"
fi


ANT_CMD_LINE_ARGS="-Dcim.out=$CIM_OUT_SCRIPT -Dcim.appserver=$CIMBATCH_APPSERVER -Dcim.db=$CIMBATCH_DB -Dcim.atgserver=$CIMBATCH_ATGSERVER -Dcim.deploy=$CIMBATCH_DEPLOY -Ddynamo.home=$DYNAMO_HOME/ -Ddynamo.root=$DYNAMO_ROOT/ -Dcim.home=$CIM_HOME/ $APP_SERVER_ARGS"
ANT_ARGS="-f $PROGRAM_DIR/evalbuild.xml"

echo "Do you wish to load evaluation data (Y/N)?"
read LOAD_DATA

if [ ${LOAD_DATA:0:1} == "Y" -o ${LOAD_DATA:0:1} == "y" ] ; then
  "$ATGJRE" $ANT_OPTS -classpath "$EVAL_CLASSPATH" -Xms64m -Xmx256m org.apache.tools.ant.Main $ANT_ARGS $ANT_CMD_LINE_ARGS
  $DYNAMO_HOME/bin/cim.sh -batch $CIM_OUT_SCRIPT
fi