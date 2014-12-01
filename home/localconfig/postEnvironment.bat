REM postEnvironment.bat
REM Modify your CLASSPATH, CONFIGPATH, and JAVA_ARGS 
REM settings in the this file.
REM See the Dynamo documentation for more information.

REM CLASSPATH variable
set CLASSPATH=%CLASSPATH%;C:\AE10\jboss-5.1.0.GA\common\lib\mysql-connector-java-5.1.13-bin.jar
REM PATH variable
set PATH=%PATH%
REM CONFIGPATH variable
set CONFIGPATH=%CONFIGPATH%;C:\AE10\ATG10.0.3\DAF\config\dafconfig.jar
REM JAVA_ARGS variable
REM set JAVA_ARGS=-classic %JAVA_ARGS%

REM Recommended heap settings for deployment(required for Portals App. 
REM Framework)
REM -XX:NewSize and -XX:MaxNewSize must be at least 25% of heapsize - 
REM examples are for 512MB heap 

REM  uncomment and adjust for your heap:
REM set JAVA_ARGS=%JAVA_ARGS% -XX:NewSize=128m -XX:MaxNewSize=128m -XX:SurvivorRatio=40 -Xmx512m -Xms512m

REM Decrease RMI garbage collection frequency
set JAVA_ARGS=%JAVA_ARGS% -Dsun.rmi.dgc.server.gcInterval=3600000

REM enable debugging Dynamo over JDWP
if "%DEBUG_ENABLED%" == "true" (
 set JAVA_ARGS=%JAVA_ARGS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%JAVA_DEBUG_PORT%
)
