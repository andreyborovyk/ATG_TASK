
REM
REM This script sets up the environment before running dynamo or compiling
REM files that use dynamo classes.
REM

REM Remove the dynamo.env file because it could be
REM left over from a previous instance of dynamo
REM and may have unwanted configuration information.
REM If this file is needed it will be recreated
REM in dafEnv
if EXIST %DYNAMO_HOME%\dynamo.env del %DYNAMO_HOME%\dynamo.env

if NOT DEFINED SERVERNAME set SERVERNAME=.
if NOT DEFINED TEMP set TEMP=.
if NOT EXIST %TEMP% mkdir %TEMP%
set TMPFILE=%TEMP%\jvers.txt
set TMPOUT=%TEMP%\findj.txt
set JAVA_VER=1.5

if NOT "%DYNAMO_HOME%" == "" goto :homeok
  echo You must set the DYNAMO_HOME variable to point to your Dynamo
  echo install directory
goto :endofenv
:homeok

set DASENV=%DYNAMO_HOME%\localconfig\dasEnv.bat
if NOT EXIST %DASENV% (
  echo Warning: %DASENV% does not exist; did you run the installer?
  echo          I will try to work around the problem.
  if NOT DEFINED DAS_ROOT set DAS_ROOT=%DYNAMO_HOME%\..
  set ATGJRE=java
) else (
  call %DASENV%
)
if NOT EXIST %DASENV% (
  echo DAS_ROOT=%DAS_ROOT%
  echo ATGJRE=%ATGJRE%
)
call %DYNAMO_HOME%\bin\j2eeEnv.bat

if "%DYNAMO_APP_JAR%"=="" set DYNAMO_APP_JAR=startDynamo.jar
set CREATE_JAR_OPT=-createJar %DYNAMO_HOME%\%SERVERNAME%\%DYNAMO_APP_JAR%

:parse
if "%1"=="" goto :parsedone
set FIRSTCHAR=%1
set FIRSTCHAR=%FIRSTCHAR:~0,1%
if "%FIRSTCHAR%"=="-" goto :parseswitch
goto :parsedone

:parseswitch
if "%1"=="-m" goto :modules
if "%1"=="-createJar" goto :createJar
echo Unknown option: '%1'  

:usage
  echo dynamoEnv.bat [-m additional-module-list] [-createJar]
  echo -m         Run additionally with the specified modules
  echo -createJar Create a jar file containing all the needed classes from
  echo            jar files in the class path. 
  goto endofenv

:modules
  shift
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  set DYNAMO_MODULES=%DYNAMO_MODULES%;%1
  goto :modules

:createJar
  set CREATE_JAR_OPT=-createJar:classes %DYNAMO_HOME%\%SERVERNAME%\startDynamo.jar 
  shift
  if "%1"=="" goto :parsedone
  goto :parseswitch

:parsedone

REM
REM Set DYNAMO_ROOT to be the directory above DYNAMO_HOME
REM
set DYNAMO_ROOT=%DYNAMO_HOME%\..

REM
REM PATH must include location of DLL for IIS Configuration Manager
REM

set PATH=%PATH%;%DYNAMO_ROOT%\DAS\os_specific_files\i486-unknown-win32;%DYNAMO_ROOT%\DAS\os_specific_files\i486-unknown-win32\ice

REM 
REM Set the initial JAVA_ARGS - could be overriden in environment.bat
REM

if NOT DEFINED JAVA_VM set JAVA_VM=%ATGJRE%

if "%THIRD_PARTY_APP_SERVER%" == "weblogic" (
  set MEM_ARGS=-Xms512m -Xmx1024m
) else (
  if NOT DEFINED JAVA_ARGS set JAVA_ARGS=-Xms512m -Xmx1024m
)

::
:: Only set these JAVA_ARGS if we are not using another 3rd party
:: server (e.g. weblogic) for our application server.
::

if DEFINED THIRD_PARTY_APP_SERVER goto :modulePath
set JAVA_ARGS=-Djava.security.policy="%DYNAMO_HOME%/lib/java.policy" -Datg.j2eeserver.root="%DAS_ROOT%" %JAVA_ARGS%

::
:: module path, a comma-separated list of paths where modules are located
::
:modulePath
if NOT DEFINED DAS_ROOT goto :simpleModulePath
if "%DAS_STAND_ALONE%" == "true" goto :simpleModulePath
if "%DAS_ROOT%" == "%DYNAMO_ROOT%" goto :simpleModulePath
set JAVA_ARGS=-Datg.dynamo.modulepath="%DYNAMO_ROOT%;%DAS_ROOT%" %JAVA_ARGS%
goto :postJAVA_ARGS

:simpleModulePath
set JAVA_ARGS=-Datg.dynamo.modulepath="%DYNAMO_ROOT%" %JAVA_ARGS%

:postJAVA_ARGS
set JAVA_ARGS=-Djava.security.policy=lib/java.policy -Datg.dynamo.home="%DYNAMO_HOME%" -Datg.dynamo.root="%DYNAMO_ROOT%" -Datg.dynamo.display= -Djava.protocol.handler.pkgs=atg.net.www.protocol -Djava.naming.factory.url.pkgs=atg.jndi.url %JAVA_ARGS%

set DYNAMO_CLASSPATH=%DYNAMO_HOME%\locallib\;%DYNAMO_HOME%\lib\launcher.jar
if "%CLASSPATH%"=="" goto :noclasspath
set DYNAMO_CLASSPATH=%DYNAMO_CLASSPATH%;%CLASSPATH%
:noclasspath
set CLASSPATH=%DYNAMO_CLASSPATH%

REM This is true if -b is specified  
if "%DYNAMO_BASE%"=="true" goto :skipapps

REM
REM The preEnvironment.bat file is a historical relic.
REM
if EXIST %DYNAMO_HOME%\bin\preEnvironment.bat call %DYNAMO_HOME%\bin\preEnvironment.bat

:skipapps
REM
REM This environment.bat can set CLASSPATH, CONFIGPATH and JAVA_ARGS so that 
REM a component can modify these values or put regular variables into
REM the environment of dynamo
REM
if EXIST %DYNAMO_HOME%\localconfig\environment.bat call %DYNAMO_HOME%\localconfig\environment.bat

if "%SERVERNAME%"=="." goto :noserver
if "%SERVERNAME%"=="%DYNAMO_HOME%" goto :noserver
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.server.home="%SERVERNAME%"
if EXIST %DYNAMO_HOME%\%SERVERNAME%\localconfig\environment.bat call %DYNAMO_HOME%\%SERVERNAME%\localconfig\environment.bat
goto :setconfigpath

:noserver
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.server.home="%DYNAMO_HOME%"

:setconfigpath
if NOT DEFINED SERVERCOMMAND set SERVERCOMMAND=%DYNAMO_HOME%\%SERVERNAME%\defaultCommand.bat

::
:: Installation will add all installed modules to DYNAMO_MODULES in
:: environment.sh.  We slam in just DAS here for the base
:: configuration.
::
if "%DYNAMO_BASE%"=="true" set DYNAMO_MODULES=DAS
if NOT DEFINED DYNAMO_MODULES set DYNAMO_MODULES=DAS

::
:: If we are not using a 3rd party app server then add J2EEServer,
:: J2EEAPI to list of modules.
::
:defDynamoModules

:: THIS IS THE PATCH TO HANDLE THE -server TYPE

echo %JAVA_VM% > JAVAVM
findstr /I /R ".*\\java(.exe)* [ ]*-server" JAVAVM > nul

if ERRORLEVEL 1 goto :notfound
set JAVA_VM=%JAVA_VM: -server=%
set JAVA_ARGS_PREPEND=-server

:notfound

"%JAVA_VM%" %JAVA_ARGS_PREPEND% -fullversion > %TMPFILE% 2>&1
find "is not recognized" %TMPFILE% > %TMPOUT% 2>&1
if ERRORLEVEL 1 goto javavm_set

echo Your setting of JAVA_VM appears to be invalid.  Attempting to use the 
echo Java VM which comes with Dynamo (%ATGJRE%)

set JAVA_VM=%ATGJRE%
"%JAVA_VM%" -fullversion > %TMPFILE% 2>&1
find "is not recognized" %TMPFILE% > %TMPOUT% 2>&1
if ERRORLEVEL 1 goto javavm_set

echo Could not locate java.exe.  Cannot run.
set JAVA_VM=
goto :endofenv

:javavm_set

REM
REM See if JRockit is being used by looking at BEA_JAVA_VM, which
REM will be holding the original JAVA_VM flag to be supplied to WL.
REM

if "%BEA_JAVA_VM%"=="-jrockit" (
  set IS_JROCKIT_JDK=true
  goto :non_ibm_jdk
) else (
  set IS_JROCKIT_JDK=false
)

"%JAVA_VM%" %JAVA_ARGS_PREPEND% -version > %TMPFILE% 2>&1

REM
REM 2nd chance for JRockit, just in case they're using it without
REM the flag
REM

find "JRockit" %TMPFILE% > %TMPOUT% 2>&1
if ERRORLEVEL 1 (
  set IS_JROCKIT_JDK=false
) else (
  set IS_JROCKIT_JDK=true
)

REM
REM If Java is IBM JDK, IBM module is included 
REM

find "IBM" %TMPFILE% > %TMPOUT% 2>&1
if ERRORLEVEL 1 goto :non_ibm_jdk

REM Should no longer need this without DAS.
REM if "%THIRD_PARTY_APP_SERVER%" == "" set DYNAMO_MODULES=%DYNAMO_MODULES%;IBM
goto :jdk_set

:non_ibm_jdk

REM
REM If we're using a non-IBM JVM, and MaxPermSize hasn't been set,
REM then add MaxPermSize and MaxNewSize settings.
REM If we're on BEA, add the MaxPermSize and MaxNewSize settings to
REM MEM_ARGS, which is used by the BEA startup script
REM

if NOT DEFINED JAVA_ARGS goto :java_args_empty
set NO_MAX_PERM_SIZE=%JAVA_ARGS:MaxPermSize=%

set NO_MAX_PERM_SIZE_WITHOUT_QUOTES=%NO_MAX_PERM_SIZE:"=%
set JAVA_ARGS_WITHOUT_QUOTES=%JAVA_ARGS:"=%
if NOT "%NO_MAX_PERM_SIZE_WITHOUT_QUOTES%"=="%JAVA_ARGS_WITHOUT_QUOTES%" goto :jdk_set

:java_args_empty
if "%THIRD_PARTY_APP_SERVER%" == "weblogic" (
  if NOT "%IS_JROCKIT_JDK%" == "true" (
    set MEM_ARGS=%MEM_ARGS% -XX:MaxPermSize=128m -XX:MaxNewSize=128m
  )
)

if "%THIRD_PARTY_APP_SERVER%" == "websphere" ( 
  set JAVA_ARGS=%JAVA_ARGS% -Djava.net.preferIPv4Stack=true
) else (
  if NOT "%IS_JROCKIT_JDK%" == "true" (
    set JAVA_ARGS=%JAVA_ARGS% -XX:MaxPermSize=128m -XX:MaxNewSize=128m 
  )
)

:jdk_set
if NOT "%LIVECONFIG%"=="" set JAVA_ARGS=%JAVA_ARGS% -D%LIVECONFIG%
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.modules=%DYNAMO_MODULES%
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.layers=%CONFIG_LAYERS%
:postLiveconfig

REM Make sure we only set this variable on 3rd party app servers
if NOT "%THIRD_PARTY_APP_SERVER%"=="" (
     set DYN_CP_MOD_OPT=-classPathModules %DYNAMO_MODULES%
)


if "%THIRD_PARTY_APP_SERVER%" == "" (
  echo Application Server: DAS
) else (
  echo Application Server: %THIRD_PARTY_APP_SERVER%
)

"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% -Datg.dynamo.appserver=%THIRD_PARTY_APP_SERVER% -Datg.dynamo.root=%DYNAMO_ROOT% atg.applauncher.dynamo.DynamoServerLauncher %CREATE_JAR_OPT% -environment "%SERVERCOMMAND%" %DYNAMO_MODULES% %DYN_CP_MOD_OPT% "%CONFIGPATH%" 
call %SERVERCOMMAND%

REM
REM This postEnvironment.bat is modified by the Admin Config to append
REM information to the CLASSPATH.
REM
if EXIST %DYNAMO_HOME%\localconfig\postEnvironment.bat call %DYNAMO_HOME%\localconfig\postEnvironment.bat

echo %JAVA_VM% > JAVAVM
findstr /I /R ".*\\java(.exe)* [ ]*-server" JAVAVM > nul

if ERRORLEVEL 1 goto :setserver
set JAVA_VM=%JAVA_VM: -server=%
set JAVA_ARGS_PREPEND=-server

:setserver
if "%SERVERNAME%"=="." goto :endofenv
if "%SERVERNAME%"=="%DYNAMO_HOME%" goto :endofenv
if EXIST %DYNAMO_HOME%\%SERVERNAME%\localconfig\postEnvironment.bat call %DYNAMO_HOME%\%SERVERNAME%\localconfig\postEnvironment.bat

:: THIS IS THE PATCH TO HANDLE THE -server TYPE , if somebody set JAVA_VM in postEnvironment.bat

echo %JAVA_VM% > JAVAVM
findstr /I /R ".*\\java(.exe)* [ ]*-server" JAVAVM > nul

if ERRORLEVEL 1 goto :endofenv
set JAVA_VM=%JAVA_VM: -server=%
set JAVA_ARGS_PREPEND=-server

:endofenv
del %TMPFILE%
del %TMPOUT%
del JAVAVM
