@echo off
setlocal

REM This script requires the full path to a manifest file. 
REM 
REM This script must be run from the dynamo installation directory or
REM set DYNAMO_HOME to point to the install directory and you can run it
REM from anywhere.
REM
REM It sets CLASSPATH, java command line options, and PATH to include the
REM directories for dlls that may need to be loaded.  These environment
REM variables are loaded from dynamoEnv.bat (which you can use to run javac
REM with Dynamo's CLASSPATH).
REM

REM figure out where the working directory is
call :setowd foo

if NOT "%DYNAMO_HOME%" == "" goto :checkhome

REM
REM Allow us to be started from anywhere
REM
REM %~dp0 is (d)rive and (p)ath of current script (the 0) under NT/2000
set DYNAMO_HOME=%~dp0\..

:checkhome
  cd /d %DYNAMO_HOME%
  if EXIST bin\startSQLRepository.bat goto :homedone

  echo - invalid setting of DYNAMO_HOME=%DYNAMO_HOME%
  goto :endofscript

:setowd
REM Call setowd with 1 argument (like "foo") to set working directory 
REM to the current directory
REM
  SET OWD=%~dp1
  goto :eof

:homedone
  if EXIST "%DYNAMO_HOME%"\..\Portal\paf\portal.war goto :foundportals

  echo Portals must be installed to run InstallManifest. 
  goto :endofscript

:foundportals
set DYNAMO_MODULES=DAS;DAS-UI;Portal.paf
set SERVERNAME=.
set SERVERCONFIGPATH=;..\DAS\config\dtmconfig.jar;localconfig
set CREATE_JAR=

:startswitch
  if "%1"=="" goto :dynamoenv
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  goto :dynamoenv

:parseswitch
  if "%1"=="-m" goto :modules
  if "%1"=="-s" goto :server
  if "%1"=="-j" goto :createJar
  goto :dynamoenv

:modules
  shift
  set DYNAMO_MODULES=%DYNAMO_MODULES%;%1
  shift
  goto :startswitch

:server
  shift
  set SERVERNAME=servers\%1
  set SERVERCONFIGPATH=;..\DAS\config\dtmconfig.jar;localconfig;servers\%1\localconfig
  shift
  goto :startswitch

:createJar
  set CREATE_JAR=-createJar
  shift
  goto :startswitch

:dynamoenv
set CLASSPATH=%DYNAMO_HOME%\..\Portal\PDK\ManifestLoader\ManifestLoader.jar;%CLASSPATH%

  call bin\dynamoEnv.bat %CREATE_JAR%
  if "%JAVA_VM%"=="" goto :endofscript

  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%

%JAVA_VM% %JAVA_ARGS% atg.portal.util.ManifestLoader InstallManifest -startDir %OWD% -configPath "%CONFIGPATH%" %*

:endofscript

endlocal
