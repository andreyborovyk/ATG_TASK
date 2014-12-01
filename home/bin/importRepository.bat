@echo off
setlocal

:: -------------------------------------------------------------------
:: $Id: //product/DAS/version/10.0.3/release/home/bin/importRepository.bat#1 $
:: $Author: rbarbier $
:: $Change: 651360 $
:: importRepository -- import repository data
:: -------------------------------------------------------------------

:: This script must be run from the dynamo installation directory or
:: set DYNAMO_HOME to point to the install directory and you can run it
:: from anywhere.
::
:: It sets CLASSPATH, java command line options, and PATH to include the
:: directories for dlls that may need to be loaded.  These environment
:: variables are loaded from dynamoEnv.bat (which you can use to run javac
:: with Dynamo's CLASSPATH).
::

:: we pass the current directory to the Java app
set STARTDIR=%CD%

::
:: Check that DYNAMO_HOME and DAS_ROOT are set
::
set PROGRAM_DIR=%~dp0
call %PROGRAM_DIR%\checkDynamoHome.bat
if "%THIRD_PARTY_APP_SERVER%" == "" (
  echo ** WARNING: HOME variable of your application server is not set.
  goto :endofscript
)
if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript

:homedone

set DYNAMO_MODULES=DAS
set SKIP_PUB=
set SERVERNAME=.
set SERVERCONFIGPATH=;localconfig

:startswitch
  if "%1"=="" goto :dynamoenv
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  goto :dynamoenv

:parseswitch
  if "%1"=="-m" goto :modules
  if "%1"=="-s" goto :server
  if "%1"=="-noworkspace" set SKIP_PUB=Y
  goto :dynamoenv

:modules
  shift
  set DYNAMO_MODULES=%DYNAMO_MODULES%;%1
  shift
  goto :startswitch

:server
  shift
  set SERVERNAME=servers\%1
  set SERVERCONFIGPATH=;localconfig;servers\%1\localconfig
  shift
  goto :startswitch

:dynamoenv

:: this let's us pass all the arguments instead of just 9
set args=%1

:nextarg
 shift
 if "%~1" EQU "" goto :argsdone
 if "%~1"=="-noworkspace" set SKIP_PUB=Y
 set args=%args% %1
 goto :nextarg
:argsdone

:: add in publishing unless directed to skip it 
if NOT "%SKIP_PUB%"=="Y" set DYNAMO_MODULES=%DYNAMO_MODULES%;Publishing

:: the DataLoader overrides the configuration of the Publishing module
:: so the DataLoader module must come after Publishing
set DYNAMO_MODULES=%DYNAMO_MODULES%;DataLoader

  if NOT "%ADDITIONAL_MODULES%"=="" set DYNAMO_MODULES=%ADDITIONAL_MODULES%;%DYNAMO_MODULES%
  echo DYNAMO_MODULES: %DYNAMO_MODULES%
  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :endofscript

  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%;..\DAF\config\dafconfig.jar

  if NOT "%ATG_CHECK_DEPLOY_TARGETS%"=="" set JAVA_ARGS=-Datg.check.deploy.targets=%ATG_CHECK_DEPLOY_TARGETS% %JAVA_ARGS%

  echo -------------------
  echo JAVA_ARGS:  %JAVA_ARGS% 
  echo -------------------
  "%JAVA_VM%" %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -expand DYNAMO_MODULES: %DYNAMO_MODULES%
  echo -------------------
  "%JAVA_VM%" %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher -expand CONFIGPATH: %CONFIGPATH%
  echo -------------------
  echo CLASSPATH:  %CLASSPATH%
  echo -------------------
  echo PATH:       %PATH%

set MAIN_CLASS=atg.deployment.loader.RepositoryDataLoader
"%JAVA_VM%" %JAVA_ARGS% %MAIN_CLASS% -import -configPath %CONFIGPATH% -startDir %STARTDIR% %args%

:endofscript

endlocal
