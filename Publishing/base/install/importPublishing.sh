#!/bin/sh

# Check for DYNAMO_HOME
if [ "x${DYNAMO_HOME}" != "x" ] ; then
  if [ -r ${DYNAMO_HOME}/META-INF/MANIFEST.MF ] ; then
    cd ${DYNAMO_HOME}
  else
    echo Invalid setting of DYNAMO_HOME: ${DYNAMO_HOME}
    exit 1
  fi
else
  echo DYNAMO_HOME must be set prior to running this script
  exit 1
fi

# Check for JAVA_HOME
if [ "x${JAVA_HOME}" = "x" ] ; then
  echo JAVA_HOME must be set prior to running this script
  exit 1
elif [ ! -d ${JAVA_HOME} ] ; then
  echo Invalid setting of JAVA_HOME. Directory ${JAVA_HOME} does not exist.
  exit 1
fi

echo
echo "*************************"
echo "Importing Publishing data"
echo "*************************"

echo
echo "Importing Publishing/base/install/epub-role-data.xml into /atg/userprofiling/InternalProfileRepository"

bin/startSQLRepository -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "${DYNAMO_HOME}/../Publishing/base/install/epub-role-data.xml"

echo
echo "Importing Publishing/base/install/epub-file-repository-data.xml into /atg/epub/file/PublishingFileRepository"

bin/startSQLRepository -m Publishing.base -repository /atg/epub/file/PublishingFileRepository -import "${DYNAMO_HOME}/../Publishing/base/install/epub-file-repository-data.xml" -workspace FileRepositoryImport:main -comment Initial

echo "*************************"
echo "Importing Publishing DSS files"
echo "*************************"

cp ${DYNAMO_HOME}/../Publishing/base/bin/startRepositoryLoader ${DYNAMO_HOME}/bin
chmod a+x ${DYNAMO_HOME}/bin/startRepositoryLoader

echo
echo "Importing DSS scenario files into /atg/epub/file/PublishingFileRepository"

cd ${DYNAMO_HOME}/../Publishing/base
rm -rf tempconfig
mkdir tempconfig
cd tempconfig
${JAVA_HOME}/bin/jar xf ${DYNAMO_HOME}/../DSS/config/config.jar

cd ${DYNAMO_HOME}/../Publishing/base
rm -rf temp
mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir temp/atg/registry/data
mkdir temp/atg/registry/data/scenarios
mkdir temp/atg/registry/data/scenarios/DSS
mkdir temp/atg/registry/data/scenarios/recorders
cd temp/atg/registry/data/scenarios
cp ${DYNAMO_HOME}/../Publishing/base/tempconfig/atg/registry/data/scenarios/DSS/*.sdl DSS
cp ${DYNAMO_HOME}/../Publishing/base/tempconfig/atg/registry/data/scenarios/recorders/*.sdl recorders
cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../Publishing/base/temp -update -filemapping /atg/epub/file/typemappers/ScenarioTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf ${DYNAMO_HOME}/../Publishing/base/temp
rm -rf ${DYNAMO_HOME}/../Publishing/base/tempconfig

chmod a+x ${DYNAMO_HOME}/../Publishing/base/bin/executeSQL
${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Publishing.base -f ${DYNAMO_HOME}/../Publishing/base/install/install-cleanup.sql

rm -f ${DYNAMO_HOME}/bin/startRepositoryLoader

exit 0
