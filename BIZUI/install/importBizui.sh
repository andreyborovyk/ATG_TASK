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
echo "Importing BIZUI data"
echo "*************************"

echo
echo "Importing BIZUI/install/data/portal.xml into /atg/portal/framework/PortalRepository"

bin/startSQLRepository -m BIZUI -repository /atg/portal/framework/PortalRepository -import "${DYNAMO_HOME}/../BIZUI/install/data/portal.xml"

echo "Importing BIZUI/install/data/profile.xml into /atg/userprofiling/InternalProfileRepository"

bin/startSQLRepository -m BIZUI -repository /atg/userprofiling/InternalProfileRepository -import "${DYNAMO_HOME}/../BIZUI/install/data/profile.xml"

echo "Importing BIZUI/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../BIZUI/install/data/viewmapping.xml"

chmod a+x ${DYNAMO_HOME}/../Publishing/base/bin/executeSQL
${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Publishing.base -f ${DYNAMO_HOME}/../Publishing/base/install/install-cleanup.sql

exit 0
