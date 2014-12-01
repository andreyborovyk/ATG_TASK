REM
REM If we are running on Websphere or Weblogic, we need to explicitly
REM add their jarfile with J2EEAPI classes to the classpath.
REM
if DEFINED CATALINA_HOME set CLASSPATH=%CLASSPATH%;%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\lib\jsp-api.jar;%DYNAMO_HOME%\..\DafEar\Tomcat\atomikos\jta.jar;%DYNAMO_HOME%\..\DAS\lib\min-ejb3-api.jar
if DEFINED WL_HOME set CLASSPATH=%CLASSPATH%;%WL_HOME%\server\lib\api.jar;%WL_HOME%\server\lib\wlclient.jar
if DEFINED WAS_HOME set CLASSPATH=%CLASSPATH%;%WAS_HOME%\lib\j2ee.jar
if DEFINED JBOSS_HOME set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\common\lib\jboss-javaee.jar;%JBOSS_HOME%\lib\jboss-javaee.jar;%JBOSS_HOME%\common\lib\jsp-api.jar;%JBOSS_HOME%\common\lib\servlet-api.jar
