@echo off
setlocal

REM Check for DYNAMO_HOME
if "%DYNAMO_HOME%" == "" goto :dynamohomeerror
if NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

REM Check for JAVA_HOME
if "%JAVA_HOME%" == "" goto :javahomeerror
if NOT EXIST %JAVA_HOME%\nul goto :javahomedirerror

REM 
REM Convert forward slashes to backslashes in DYNAMO_HOME before trying to
REM cd there, just in case the user is running Cygwin or something like it 
REM and using Unix-style paths on a Windows machine.
REM 

set DYNAMO_HOME=%DYNAMO_HOME:/=\%
if NOT EXIST %DYNAMO_HOME%\META-INF\MANIFEST.MF goto :dynamohomeerror
goto :homedone

:dynamohomeerror
echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
echo **
goto :pauseexit

:dynamohomedirerror
echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME% does not exist
echo **
goto :pauseexit

:javahomeerror
echo ** Invalid setting of JAVA_HOME: %JAVA_HOME%
echo **
goto :pauseexit

:javahomedirerror
echo ** Invalid setting of JAVA_HOME: %JAVA_HOME% does not exist
echo **
goto :pauseexit

:homedone
  cd /d %DYNAMO_HOME%

  echo *************************
  echo Importing BIZUI data
  echo *************************

  echo.
  echo Importing BIZUI/install/data/portal.xml into /atg/portal/framework/PortalRepository

  call bin\startSQLRepository.bat -m BIZUI -repository /atg/portal/framework/PortalRepository -import "%DYNAMO_HOME%\..\BIZUI\install\data\portal.xml"

  echo.
  echo Importing BIZUI/install/data/profile.xml into /atg/userprofiling/InternalProfileRepository

  call bin\startSQLRepository.bat -m BIZUI -repository /atg/userprofiling/InternalProfileRepository -import "%DYNAMO_HOME%\..\BIZUI\install\data\profile.xml"

  echo.
  echo Importing BIZUI/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository

  call bin\startSQLRepository.bat -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "%DYNAMO_HOME%\..\BIZUI\install\data\viewmapping.xml"


  call ..\Publishing\base\bin\executeSQL.bat -m Publishing.base -f %DYNAMO_HOME%\..\Publishing\base\install\install-cleanup.sql

  goto :endofscript

:pauseexit
pause

:endofscript
if exist endofscript rmdir endofscript

endlocal
