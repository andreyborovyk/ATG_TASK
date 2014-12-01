#!/bin/sh
# @version $Id: //product/DCS/version/10.0.3/templates/DCS/install/importDCS.sh#2 $$Change: 651448 $

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
echo "**************************************************"
echo "Importing data into the Personalization Repository"
echo "***************************************************"

echo
echo "Importing DCS/install/data/initial-segment-lists.xml into /atg/userprofiling/PersonalizationRepository"

bin/startSQLRepository -m DPS -repository /atg/userprofiling/PersonalizationRepository -import "${DYNAMO_HOME}/../DCS/install/data/initial-segment-lists.xml"

exit 0
