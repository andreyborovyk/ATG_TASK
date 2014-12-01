#
# If we are running on Websphere or Weblogic, we need to explicitly
# add their jarfile with J2EEAPI classes to the classpath.
#

#4cygwin
echo $CLASSPATH
PSEPARATOR=":"
if [ -f /bin/cygpath ] ; then
  PSEPARATOR=";"
fi
if [ "x$CATALINA_HOME" != "x" ] ; then
  J2EE_CLASSPATH=${CATALINA_HOME}/lib/servlet-api.jar${PSEPARATOR}${CATALINA_HOME}/lib/jsp-api.jar${PSEPARATOR}${DYNAMO_HOME}/../DafEar/Tomcat/atomikos/jta.jar${PSEPARATOR}${DYNAMO_HOME}/../DAS/lib/min-ejb3-api.jar
fi
if [ "x$WL_HOME" != "x" ] ; then
  J2EE_CLASSPATH=${WL_HOME}/server/lib/api.jar${PSEPARATOR}${WL_HOME}/server/lib/wlclient.jar 
fi
if [ "x${WAS_HOME}" != "x" ] ; then
  J2EE_CLASSPATH=${WAS_HOME}/lib/j2ee.jar
fi
if [ "x$JBOSS_HOME" != "x" ] ; then
  J2EE_CLASSPATH=${JBOSS_HOME}/common/lib/jsp-api.jar${PSEPARATOR}${JBOSS_HOME}/common/lib/jboss-javaee.jar${PSEPARATOR}${JBOSS_HOME}/lib/jboss-javaee.jar${PSEPARATOR}${JBOSS_HOME}/common/lib/servlet-api.jar
fi
if [  "x${CLASSPATH}" = "x" ] ; then
  CLASSPATH=${J2EE_CLASSPATH}
else
  CLASSPATH=${CLASSPATH}${PSEPARATOR}${J2EE_CLASSPATH}
fi
#if [ -f /bin/cygpath ] ; then
#  if [ "x${CLASSPATH}" != "x" ] ; then
#    CLASSPATH=`cygpath -pw ${CLASSPATH}`
#  fi
#fi
