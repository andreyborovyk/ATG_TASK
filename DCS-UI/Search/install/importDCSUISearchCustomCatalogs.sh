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
echo "Importing DCS-UI/Search/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../DCS-UI/Search/install/data/viewmapping.xml"

echo
echo "*************************"
echo "Importing DCS-UI.Search data for custom catalogs (Flex UI)"
echo "*************************"

echo
echo "Importing DCS-UI/Search/install/data/flex/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../DCS-UI/Search/install/data/flex/viewmapping.xml"

echo
echo "*************************"
echo "Importing RefinementRepository data for custom catalogs"
echo "*************************"

echo
echo "Importing DCS-UI/Search/install/data/refinement.xml into /atg/search/repository/RefinementRepository"

bin/startSQLRepository -m DCS.Search.CustomCatalogs.Versioned -m B2CCommerce.Versioned -repository /atg/search/repository/RefinementRepository -import "${DYNAMO_HOME}/../DCS-UI/Search/install/data/refinement.xml" -workspace RefinementRepositoryImport:main -comment GlobalFacetDefinition

exit 0
