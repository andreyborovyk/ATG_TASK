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

echo "*************************"
echo "Importing Site Admin role data"
echo "*************************"

bin/startSQLRepository -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "${DYNAMO_HOME}/../SiteAdmin/Versioned/install/data/siteadmin-role-data.xml"

echo "*************************"
echo "Importing Site Admin user data"
echo "*************************"

bin/startSQLRepository -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "${DYNAMO_HOME}/../SiteAdmin/Versioned/install/data/users.xml"

echo 
echo "****************************"
echo "Import SiteAdmin templates"
echo "***************************"

bin/startSQLRepository -m DAS.Versioned -repository /atg/multisite/SiteRepository -import "${DYNAMO_HOME}/../SiteAdmin/Versioned/install/data/templates.xml"

echo 
echo "****************************"
echo "Importing SiteAdmin data"
echo "***************************"

bin/startSQLRepository -m WebUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../SiteAdmin/Versioned/install/data/viewmapping.xml"

exit 0
