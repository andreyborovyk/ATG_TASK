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

bin/startSQLRepository -m WebUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../DCS-UI/install/data/viewmapping.xml"

exit 0
