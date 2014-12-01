@echo off
setlocal

REM This script must be run from the dynamo installation directory or
REM set DYNAMO_HOME to point to the install directory and you can run it
REM from anywhere.
REM
REM It sets CLASSPATH, java command line options, and PATH to include the
REM directories for dlls that may need to be loaded.  These environment
REM variables are loaded from dynamoEnv.bat (which you can use to run javac
REM with Dynamo's CLASSPATH).
REM

::
:: Check that DYNAMO_HOME and DAS_ROOT are set and do basic app-server
:: specific configuration.
::
set PROGRAM_DIR=%~dp0
call %PROGRAM_DIR%\..\..\..\home\bin\checkDynamoHome.bat
if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript
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

:homedone

set DYNAMO_MODULES=DAS
set SERVERNAME=.
set CREATE_JAR=
set SERVERCONFIGPATH=

REM Add appropriate dir or jar to config path for TransactionManager
if EXIST ..\DAS\config\dtmconfig     set SERVERCONFIGPATH=%SERVERCONFIGPATH%;..\DAS\config\dtmconfig
if EXIST ..\DAS\config\dtmconfig.jar set SERVERCONFIGPATH=%SERVERCONFIGPATH%;..\DAS\config\dtmconfig.jar

set SERVERCONFIGPATH=%SERVERCONFIGPATH%;localconfig

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
  set SERVERCONFIGPATH=%SERVERCONFIGPATH%;servers\%1\localconfig
  shift
  goto :startswitch

:createJar
  set CREATE_JAR=-createJar
  shift
  goto :startswitch

:dynamoenv
  if NOT "%ADDITIONAL_MODULES%"=="" set DYNAMO_MODULES=%ADDITIONAL_MODULES%;%DYNAMO_MODULES%
  call bin\dynamoEnv.bat %CREATE_JAR%
  if "%JAVA_VM%"=="" goto :endofscript

  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%

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

  "%JAVA_VM%" %JAVA_ARGS% atg.service.jdbc.ExecuteSQL -cmd executeSQL -configPath %CONFIGPATH% %1 %2 %3 %4 %5 %6 %7 %8 %9

:endofscript

endlocal
