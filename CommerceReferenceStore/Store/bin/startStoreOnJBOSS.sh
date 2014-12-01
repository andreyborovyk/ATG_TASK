#!/bin/sh 

PROGRAM=`basename $0`

ulimit -n 1000

# No coredumps
ulimit -c 0

usage()
{
    echo "*** startStoreOnJBOSS server-name [-clean] [-l] [-c server-config-name] [-ear ear-file-name] [-f additional-flag] [-j jboss-home-dir] [-layer config layers] [-debug] [-debugPort port-num] [-m additional-module-list]"
    echo "***"
    echo "*** server-name is the name of the Dynamo server to use."
    echo "*** -clean Delete existing ear file and data source xml files."
    echo "*** -l Run with the liveconfig config layer."
    echo "*** -c The jboss server to use. Default value is atg."
    echo "*** -ear The destination ear file name. Default value is ATG.ear."
    echo "*** -f Run additionally with the specified runAssembler flag."
    echo "*** -j The location of the jboss installation directory."
    echo "*** -layer Run additionally with the specified configuration layers"
    echo "*** -m Run additionally with the specified modules."
    echo "*** -debug     Run with remote debugging enabled on port 3000"
    echo "*** -debugPort Specifies the port for remote debugging"
    exit 0
}

unknownoptionerror()
{
    echo "Unknown option: $1"
    usage
}

nonexistantserver()
{
    echo "***"
    echo "*** The server directory for server: ${SERVER_NAME} does not exist."
    echo "*** You must specify a valid server."
    echo "***"
    usage
}

nostartupproperties()
{
    echo "***"
    echo "*** Startup properties could not be found verify that the file "
    echo "*** ${DYNAMO_HOME}/servers/${SERVER_NAME}/moduleList.properties exits."
    echo "***"
    usage
}

nonexistantjbossserver()
{
    echo "***"
    echo "*** The JBoss server directory for server: ${JBOSS_SERVER} does not exist."
    echo "*** You must specify a valid JBoss server."
    echo "***"
    usage
}

unknownjbosshome()
{
    echo "***"
    echo "*** JBOSS_HOME is not defined. You must specify the JBOSS_HOME dir."
    echo "***"
    usage
}

nonexistantjbosshome()
{
    echo "***"
    echo "*** The JBOSS_HOME: ${JBOSS_HOME} does not exist."
    echo "*** You must specify a valid path for the JBOSS_HOME dir."
    echo "***"
    usage
}

emptyjbossserver()
{
    echo "***"
    echo "*** No JBoss server configuration specified."
    echo "*** You must specify a valid server configuration "
    echo "*** when using the -c option."
    echo "***"
    usage
}

emptyearname()
{
    echo "***"
    echo "*** No ear file name specified."
    echo "*** You must specify an ear file name when using the -ear option."
    echo "***"
    usage         

}

nolayers()
{
    echo "***"
    echo "*** No configuration layers have been specified. You must specify"
    echo "*** one or more configuration layers when using the -layer option."
    echo "***"
    usage
}

FIRSTCHAR=$1
SERVER_NAME=
MODULE_LIST=
LIVECONFIG=
ADDITIONAL_MODULES=DafEar.Admin
CLEANSTART="false"
EARFILE_NAME="ATG.ear"
JBOSS_SERVER="atg"
JBOSS_SERVER_ARG="-c ${JBOSS_SERVER}"
JBOSS_BIND_ARG="-b 0.0.0.0"

PROGRAM_DIR=`dirname $0`
. $PROGRAM_DIR/../../../home/bin/checkDynamoHome.sh

DASENV=$DYNAMO_HOME/localconfig/dasEnv.sh
if [ -f $DASENV ]; then
    # handle das env
    . $DASENV
else
  echo "JBOSS_HOME=${JBOSS_HOME}" >> ${DASENV}
fi

#
# Default java debugging port
#
JAVA_DEBUG_PORT=3000

# parse the input params
while [ "x$1" != "x" ]
do
  case $1 in
      -help) usage
    ;;
      -l) LIVECONFIG="-liveconfig"
    ;;
      -clean) CLEANSTART="true"
    ;;
      -ear) shift
          EARFILE_NAME="$1"
          if [ ! ${EARFILE_NAME} -o -z ${EARFILE_NAME} ]; then
              emptyearname
          fi
          ;;
      -c) shift
          JBOSS_SERVER="$1"
          if [ ! ${JBOSS_SERVER} -o -z ${JBOSS_SERVER} ]; then
              emptyjbossserver
          fi
          JBOSS_SERVER_ARG="-c ${JBOSS_SERVER}"
          ;;
      -m) 
          while [ "x$2" != "x" ] 
            do
            case $2 in
                -*) break
                    ;;
                *) if [ -n $ADDITIONAL_MODULES ]; then
                    ADDITIONAL_MODULES="${ADDITIONAL_MODULES}:$2" 
                    else
                    ADDITIONAL_MODULES="$2" 
                    fi
                    ;;
            esac
            shift
          done
          echo "ADDITIONAL_MODULES=$ADDITIONAL_MODULES"
          ;;      
      -layer)
          while [ "x$2" != "x" ]
            do
            case $2 in
                -*) break
                    ;;
                *) if [ "$ADDITIONAL_LAYERS" ]; then
                    ADDITIONAL_LAYERS="${ADDITIONAL_LAYERS} $2" 
                    else
                    ADDITIONAL_LAYERS="-layer $2" 
                    fi
                    ;;
            esac
            shift
          done
          echo "ADDITIONAL_LAYERS=$ADDITIONAL_LAYERS"
          ;;
      -j) shift
    JBOSS_HOME="$1"
    ;;
      -f) shift
    ADDITIONAL_FLAGS="$1 ${ADDITIONAL_FLAGS}"
    ;;
      -debug) DEBUG_ENABLED=true;;
      -debugPort) JAVA_DEBUG_PORT=$2 
              shift
    ;;
      -*) unknownoptionerror
    ;;
      *) SERVER_NAME="$1"
    if [ ! -d ${DYNAMO_HOME}/servers/${SERVER_NAME} ]; then
        nonexistantserver
    fi
    ;;
  esac
  shift
done

if [ "x${SERVER_NAME}" == "x" ]; then
  nonexistantserver
fi

#Pull the list of modules from the property file
if [ -f ${DYNAMO_HOME}/servers/${SERVER_NAME}/localconfig/moduleList.properties ]; then
  eval `awk -F = '$1=="modules"{print "MODULE_LIST=\"" $2 "\"" }' ${DYNAMO_HOME}/servers/${SERVER_NAME}/localconfig/moduleList.properties`
  MODULE_LIST=`echo $MODULE_LIST | sed 's/, /:/g'`
else
  nostartupproperties
fi

SERVER_NAME="-server ${SERVER_NAME}"
if [ -n $ADDITIONAL_MODULES ]; then
  MODULE_LIST="${MODULE_LIST}:${ADDITIONAL_MODULES}"
fi

# JBOSS_HOME is not defined
if [ ! ${JBOSS_HOME} -o -z ${JBOSS_HOME} ]; then
    unknownjbosshome
fi

# JBOSS_HOME is an invalid value
if [ ! -d ${JBOSS_HOME}/ ]; then
    nonexistantjbosshome
fi

# Layer option present, but no layers specified
if [ "${ADDITIONAL_LAYERS}" = "-layers" ]; then
    nolayers
fi

# copy the datasources

ATG_MYSQL_FILE="${JBOSS_HOME}/server/${JBOSS_SERVER}/deploy/atg-mysql-ds.xml"
ATG_APPS_FILE="${JBOSS_HOME}/server/${JBOSS_SERVER}/deploy/atg-apps-mysql-ds.xml"
EAR_FILE="${JBOSS_HOME}/server/${JBOSS_SERVER}/deploy/${EARFILE_NAME}"

if [ ! -d ${JBOSS_HOME}/server/${JBOSS_SERVER}/ ]; then
    nonexistantjbossserver
fi

if [ "${CLEANSTART}" = "true" ]; then
    rm -rf ${ATG_MYSQL_FILE}
    rm -rf ${ATG_APPS_FILE}
    rm -rf ${EAR_FILE}
fi

if [ ! -f ${ATG_MYSQL_FILE} ]; then
    cp ${DYNAMO_HOME}/../DafEar/Tomcat/copy-to-jboss_home-your_server-deploy-dir/atg-mysql-ds.xml ${ATG_MYSQL_FILE}
fi

if [ ! -f ${ATG_APPS_FILE} ]; then
    cp ${DYNAMO_HOME}/../DafEar/Tomcat/copy-to-jboss_home-your_server-deploy-dir/atg-apps-mysql-ds.xml ${ATG_APPS_FILE}
fi

if [ -f /bin/cygpath ] ; then
  ADDITIONAL_MODULES=`cygpath -pw $MODULE_LIST`
fi

bin/runAssembler ${ADDITIONAL_FLAGS} ${SERVER_NAME} ${LIVECONFIG} ${EAR_FILE} ${ADDITIONAL_LAYERS} -m ${MODULE_LIST}

if [ "${?}" != 0 ] ; then
 echo "Errors encountered during ear file assembly."
 exit 1
fi

${JBOSS_HOME}/bin/run.sh ${JBOSS_SERVER_ARG} ${JBOSS_BIND_ARG}

exit 0 

