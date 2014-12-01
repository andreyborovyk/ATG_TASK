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
echo "Importing AssetUI.Search data for custom catalogs"
echo "*************************"

echo
echo "Importing AssetUI/Search/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../AssetUI/Search/install/data/viewmapping.xml"

echo "Importing AssetUI/Search/install/data/refinementData.xml into /atg/search/repository/RefinementRepository"

bin/startSQLRepository -m AssetUI.Search -m DAF.Search.Versioned -repository /atg/search/repository/RefinementRepository -import "${DYNAMO_HOME}/../AssetUI/Search/install/data/refinementData.xml" -workspace RefinementInitialImport1 -comment RefinementComment1

exit 0
