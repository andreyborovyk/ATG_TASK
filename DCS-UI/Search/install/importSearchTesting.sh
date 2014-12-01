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

echo
echo "*************************"
echo "Importing DCS-UI.Search data for custom catalogs"
echo "*************************"

echo
echo "Importing DCS-UI/Search/install/data/searchTestingData.xml into /atg/search/repository/SearchTestingRepository"

bin/startSQLRepository -m AssetUI.Search -repository /atg/search/repository/SearchTestingRepository -import "${DYNAMO_HOME}/../DCS-UI/Search/install/data/searchTestingData.xml"

exit 0
