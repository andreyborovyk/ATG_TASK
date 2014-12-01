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
  echo Importing Publishing base data
  echo *************************

  echo.
  echo Importing Publishing/base/install/epub-role-data.xml into /atg/userprofiling/InternalProfileRepository

  call bin\startSQLRepository.bat -m DSS.InternalUsers -repository /atg/userprofiling/InternalProfileRepository -import "%DYNAMO_HOME%\..\Publishing\base\install\epub-role-data.xml"

  echo.
  echo Importing Publishing/base/install/epub-file-repository-data.xml into /atg/epub/file/PublishingFileRepository

  call bin\startSQLRepository.bat -m Publishing.base -repository /atg/epub/file/PublishingFileRepository -import "%DYNAMO_HOME%\..\Publishing\base\install\epub-file-repository-data.xml" -workspace FileRepositoryImport:main -comment Initial

  echo *************************
  echo Importing Publishing DSS files
  echo *************************

  echo.
  echo Importing DSS scenario files into /atg/epub/file/PublishingFileRepository

  copy %DYNAMO_HOME%\..\Publishing\base\bin\startRepositoryLoader.bat %DYNAMO_HOME%\bin

  cd %DYNAMO_HOME%\..\Publishing\base
  rmdir /S /Q tempconfig
  mkdir tempconfig
  cd tempconfig
  %JAVA_HOME%\bin\jar xf %DYNAMO_HOME%\..\DSS\config\config.jar
  
  cd %DYNAMO_HOME%\..\Publishing\base
  rmdir /S /Q temp
  mkdir temp
  mkdir temp\atg
  mkdir temp\atg\registry
  mkdir temp\atg\registry\data
  mkdir temp\atg\registry\data\scenarios
  mkdir temp\atg\registry\data\scenarios\DSS
  mkdir temp\atg\registry\data\scenarios\recorders
  cd temp\atg\registry\data\scenarios
  copy %DYNAMO_HOME%\..\Publishing\base\tempconfig\atg\registry\data\scenarios\DSS\*.sdl DSS
  copy %DYNAMO_HOME%\..\Publishing\base\tempconfig\atg\registry\data\scenarios\recorders\*.sdl recorders

  cd %DYNAMO_HOME%

  call bin\startRepositoryLoader.bat -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\Publishing\base\temp -update -filemapping /atg/epub/file/typemappers/ScenarioTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

  rmdir /S /Q %DYNAMO_HOME%\..\Publishing\base\temp
  rmdir /S /Q %DYNAMO_HOME%\..\Publishing\base\tempconfig
  del %DYNAMO_HOME%\bin\startRepositoryLoader.bat

  call ..\Publishing\base\bin\executeSQL.bat -m Publishing.base -f %DYNAMO_HOME%\..\Publishing\base\install\install-cleanup.sql

  goto :endofscript

:pauseexit
pause

:endofscript
if exist endofscript rmdir endofscript

endlocal
