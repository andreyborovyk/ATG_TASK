@echo off
setlocal

REM make Dynamo server
REM command-line arguments:
REM %1 server-name
REM %2 rmi port value
REM %3 drp port value

set PROGRAM_DIR=%~dp0
call %PROGRAM_DIR%\checkDynamoHome.bat

if "%CHECK_DYNAMO_HOME%" == "fail" (
   echo Unable to set DYNAMO_HOME
   goto endofscript
)

REM check arguments

set SERVER_NAME=%1
shift

set RMI_PORT=%1
shift

set DRP_PORT=%1
shift

if "%SERVER_NAME%"=="" goto usage
if "%RMI_PORT%"=="" goto usage
if "%DRP_PORT%"=="" goto usage

REM create directory structure

if EXIST %DYNAMO_HOME%\servers\%SERVER_NAME% (
   echo Server %SERVER_NAME% already exists.
   goto endofscript
)

xcopy %DYNAMO_HOME%\servers\original %DYNAMO_HOME%\servers\%SERVER_NAME% /e /i /q > NUL
mkdir %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\atg
mkdir %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\atg\dynamo

REM create properties files

(echo name=Server %SERVER_NAME%) > %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\CONFIG.properties

(echo rmiEnabled=true) > %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\atg\dynamo\Configuration.properties
(echo rmiPort=%RMI_PORT%) >> %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\atg\dynamo\Configuration.properties
(echo drpPort=%DRP_PORT%) >> %DYNAMO_HOME%\servers\%SERVER_NAME%\localconfig\atg\dynamo\Configuration.properties

echo Finished making Server %SERVER_NAME%.

goto :endofscript

:usage
echo Usage: makeDynamoServer.bat <servername> <rmiport> <drpport>
goto :endofscript


:endofscript
endlocal
