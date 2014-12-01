@echo off
setlocal

REM Check for DYNAMO_HOME
if "%DYNAMO_HOME%" == "" goto :dynamohomeerror
if NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

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

:homedone
  cd /d %DYNAMO_HOME%

  echo *****************************
  echo Importing SiteAdmin roles
  echo *****************************

  call bin\startSQLRepository.bat -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "%DYNAMO_HOME%\..\SiteAdmin\Versioned\install\data\siteadmin-role-data.xml"

  echo *****************************
  echo Importing SiteAdmin user data
  echo *****************************

  call bin\startSQLRepository.bat -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "%DYNAMO_HOME%\..\SiteAdmin\Versioned\install\data\users.xml"

  echo *****************************
  echo Importing SiteAdmin templates
  echo *****************************

  call bin\startSQLRepository.bat -m DAS.Versioned -repository /atg/multisite/SiteRepository -import "%DYNAMO_HOME%\..\SiteAdmin\Versioned\install\data\templates.xml"

  echo *****************************
  echo Importing SiteAdmin data
  echo *****************************

  call bin\startSQLRepository.bat -m WebUI -repository /atg/web/viewmapping/ViewMappingRepository -import "%DYNAMO_HOME%\..\SiteAdmin\Versioned\install\data\viewmapping.xml"

  goto :endofscript

:pauseexit
pause

:endofscript
if exist endofscript rmdir endofscript

endlocal
