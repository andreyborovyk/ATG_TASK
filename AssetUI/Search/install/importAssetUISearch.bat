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

  echo *************************
  echo Importing AssetUI.Search data for custom catalogs
  echo *************************

  echo.
  echo Importing AssetUI/Search/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository

  call bin\startSQLRepository.bat -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "%DYNAMO_HOME%\..\AssetUI\Search\install\data\viewmapping.xml"
  
  echo Importing AssetUI/Search/install/data/refinementData.xml into /atg/search/repository/RefinementRepository

  call bin\startSQLRepository.bat -m AssetUI.Search -m DAF.Search.Versioned -repository /atg/search/repository/RefinementRepository -import "%DYNAMO_HOME%\..\AssetUI\Search\install\data\refinementData.xml" -workspace RefinementInitialImport1 -comment RefinementComment1

  goto :endofscript

:pauseexit
pause

:endofscript

endlocal
