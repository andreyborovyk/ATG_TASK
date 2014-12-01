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

  copy %DYNAMO_HOME%\..\Publishing\base\bin\startRepositoryLoader.bat %DYNAMO_HOME%\bin
  
  echo *************************
  echo Importing Publishing WebAppRef files
  echo *************************

  echo.
  echo Importing WebAppRef files into /atg/epub/file/PublishingFileRepository
 
  cd %DYNAMO_HOME%\..\Publishing\base
  rmdir /S /Q temp
  mkdir temp
  cd %DYNAMO_HOME%\..\Publishing\base\temp
  %JAVA_HOME%\bin\jar xf %DYNAMO_HOME%\..\Publishing\WebAppRef\j2ee-apps\webappref\webappref.war index.jsp index2.jsp .  

  cd %DYNAMO_HOME%

  call bin\startRepositoryLoader.bat -m Publishing.base -m Publishing.WebAppRefVer -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\Publishing\base\temp -update -filemapping /atg/epub/file/typemappers/WebAppRefJavaServerPageTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

  rmdir /S /Q %DYNAMO_HOME%\..\Publishing\base\temp


  del %DYNAMO_HOME%\bin\startRepositoryLoader.bat

  call ..\Publishing\base\bin\executeSQL.bat -m Publishing.base -f %DYNAMO_HOME%\..\Publishing\base\install\install-cleanup.sql

  goto :endofscript

:pauseexit
pause

:endofscript
if exist endofscript rmdir endofscript

endlocal
