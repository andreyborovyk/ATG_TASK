REM --
REM -- This script set up variables that would normally be set in startDynamo.
REM -- It then calls dynamoEnv.bat before it exits.
REM --

if NOT "%DYNAMO_ROOT%" == "" goto :rootset

echo "You must set the variable DYNAMO_ROOT before running dafEnv.bat. Example: set DYNAMO_ROOT=C:\ATG\ATG10.0.3"
goto :endofscript

:rootset
set DYNAMO_HOME=%DYNAMO_ROOT%\home

:homedone
set SERVERCONFIGPATH=%DYNAMO_HOME%\localconfig
goto checkserver

:checkserver
if "%SERVERNAME%" == "." goto :noserver
if NOT "%SERVERNAME%" == "" goto :serverset
REM -- Use this path when there is no specific Dynamo server name
:noserver
set SERVERHOME=%DYNAMO_HOME%
set SERVERNAME=.
set DST_DIR=%DYNAMO_HOME%
goto :dstdirset
REM -- Use this path when the use has specified a Dynamo server name
:serverset
REM -- Set up dst dir
set SERVERCONFIGPATH=%DYNAMO_HOME%\localconfig;%DYNAMO_HOME%\%SERVERNAME%\localconfig
set SERVERHOME=%DYNAMO_HOME%\%SERVERNAME%
if "%DST_DIR%" == "" (
  if EXIST %DYNAMO_HOME%\%SERVERNAME% (
    set DST_DIR=%DYNAMO_HOME%\%SERVERNAME%
    echo Using Dynamo configuration %SERVERNAME%
  ) ELSE (
    echo Server %SERVERNAME% does not exist in %DYNAMO_HOME%
REM - Use DYNAMO_HOME instead - Should we even allow Dynamo to Start ?
    set DST_DIR=%DYNAMO_HOME%
  )
) 

if "%1"=="" goto :dstdirset
set DST_DIR=%1
shift

:dstdirset
REM - For DAF, DYNAMO_MODULES must include DPS
set DYNAMO_MODULES=DPS;%DYNAMO_MODULES%

REM - DYNAMO_MODULES gets set often before the start script.  Unless the
REM - DYNAMO_NO_UI variable is set, we must add in the DAS-UI module.  It
REM - should be put before the other application specific modules
if NOT "%DYNAMO_NO_UI%" == "" goto :calldynamoenv
set DYNAMO_MODULES=DAS-UI;%DYNAMO_MODULES%


:calldynamoenv

REM -- Set Up dynamo variables
call %DYNAMO_HOME%\bin\dynamoEnv.bat %*

REM Get user's agreement on license, quit otherwise
"%JAVA_VM%" atg.nucleus.Nucleus -requireLicense
if ERRORLEVEL 1 exit

if EXIST %DYNAMO_ROOT%\DAF\config\dafconfig.jar goto :dafconfigexist
echo Unable to locate dafconfig.jar - place it into %DYNAMO_ROOT%\DAF\config\dafconfig.jar
goto :endofscript

:dafconfigexist

REM If THIRD_PARTY_APP_SERVER is not set then skip this script
if "%THIRD_PARTY_APP_SERVER%" == "" goto :skipasconfig

set CONFIGPATH=%CONFIGPATH%;%DYNAMO_ROOT%\DAF\config\dafconfig.jar

if "%THIRD_PARTY_APP_SERVER%" == "ias" goto :iasconfig
if "%THIRD_PARTY_APP_SERVER%" == "weblogic" goto :wlconfig
if "%THIRD_PARTY_APP_SERVER%" == "websphere" goto :wsconfig
if "%THIRD_PARTY_APP_SERVER%" == "bluestone" goto :bsconfig
if "%THIRD_PARTY_APP_SERVER%" == "oas" goto :oasconfig

echo Unrecognized value for THIRD_PARTY_APP_SERVER variable: %THIRD_PARTY_APP_SERVER%.  Should be one of ias, weblogic, websphere, bluestone, oas

:iasconfig
set ASCONFIG=iasconfig
REM - This needs to only pick up the newer version of xerces.jar which is 
REM - included in classes.jar for DAS.  
set CLASSPATH=%DYNAMO_ROOT%/DAS/lib/classes.jar:%CLASSPATH%
goto :asconfig
:wsconfig
set ASCONFIG=wsconfig
goto :asconfig
:wlconfig
set ASCONFIG=wlconfig
goto :asconfig
:bsconfig
REM set ASCONFIG=bsconfig
goto :skipasconfig
:oasconfig
set ASCONFIG=oasconfig
goto :asconfig

:asconfig
if EXIST %DYNAMO_ROOT%\DAF\config\%ASCONFIG%.jar goto :doasconfig
echo Unable to locate app server specific config .jar file: %DYNAMO_ROOT%\DAF\config\%ASCONFIG%.jar 
goto :skipasconfig

:doasconfig
set CONFIGPATH=%CONFIGPATH%;%DYNAMO_ROOT%\DAF\config\%ASCONFIG%.jar
:skipasconfig
REM -- Add the path to the environment file to JAVA_ARGS
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.environmentfile=%DST_DIR%\dynamo.env
REM -- Add the app server to the JAVA_ARGS
set JAVA_ARGS=%JAVA_ARGS% -Datg.dynamo.appserver=%THIRD_PARTY_APP_SERVER%
REM -- Add Dynamo localconfig and server specific localconfig to CONFIGPATH
set CONFIGPATH=%CONFIGPATH%;%SERVERCONFIGPATH%
REM -- Write a dynamo.env file with the path to the server specific version
REM echo atg.dynamo.environmentfile=%DST_DIR%\dynamo.env > .\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry .\dynamo.env atg.dynamo.environmentfile=%DST_DIR%\dynamo.env
REM -- Write out the System properties to dynamo.env so they don't need to be on the command line
REM "%JAVA_VM%" %JAVA_ARGS_PREPEND%  %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -expand atg.dynamo.configpath=%CONFIGPATH% > %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.configpath=%CONFIGPATH%

REM "%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -expand atg.dynamo.modules=%DYNAMO_MODULES% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.modules=%DYNAMO_MODULES%
REM echo atg.dynamo.root=%DYNAMO_ROOT% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.root=%DYNAMO_ROOT%
REM echo atg.dynamo.home=%DYNAMO_HOME% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.home=%DYNAMO_HOME%
REM echo atg.dynamo.server.home=%SERVERHOME% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.server.home=%SERVERHOME%
REM echo atg.dynamo.appserver=%THIRD_PARTY_APP_SERVER% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.appserver=%THIRD_PARTY_APP_SERVER%
REM echo atg.dynamo.classpath=%CLASSPATH% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env atg.dynamo.classpath=%CLASSPATH%
if "%LIVECONFIG%" == "" goto :envdone
REM echo %LIVECONFIG% >> %DST_DIR%\dynamo.env
"%JAVA_VM%" %JAVA_ARGS_PREPEND% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -dynamoenventry %DST_DIR%\dynamo.env %LIVECONFIG%
:envdone

echo ------- DYNAMO ENVIRONMENT -------
type %DST_DIR%\dynamo.env

REM -- END DAF ENV

:endofscript

