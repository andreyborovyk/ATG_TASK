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

cp ${DYNAMO_HOME}/../Publishing/base/bin/startRepositoryLoader ${DYNAMO_HOME}/bin
chmod a+x ${DYNAMO_HOME}/bin/startRepositoryLoader


echo "*************************"
echo "Importing Publishing WebAppRef files"
echo "*************************"

echo
echo "Importing WebAppRef files into /atg/epub/file/PublishingFileRepository"

cd ${DYNAMO_HOME}/../Publishing/base
rm -rf temp
mkdir temp
cd ${DYNAMO_HOME}/../Publishing/base/temp
${JAVA_HOME}/bin/jar xvf ${DYNAMO_HOME}/../Publishing/WebAppRef/j2ee-apps/webappref/webappref.war index.jsp index2.jsp .

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base -m Publishing.WebAppRefVer -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../Publishing/base/temp -update -filemapping /atg/epub/file/typemappers/WebAppRefJavaServerPageTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf ${DYNAMO_HOME}/../Publishing/base/temp


rm -f ${DYNAMO_HOME}/bin/startRepositoryLoader

chmod a+x ${DYNAMO_HOME}/../Publishing/base/bin/executeSQL
${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Publishing.base -f ${DYNAMO_HOME}/../Publishing/base/install/install-cleanup.sql

exit 0
