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
echo "Importing AssetUI data"
echo "*************************"

echo
echo "Importing AssetUI/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../AssetUI/install/data/viewmapping.xml"

echo
echo "Importing AssetUI/install/data/assetManagerViews.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../AssetUI/install/data/assetManagerViews.xml"

exit 0
