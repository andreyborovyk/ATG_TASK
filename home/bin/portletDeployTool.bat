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
call %PROGRAM_DIR%\checkDynamoHome.bat
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

if "%DAS_ROOT%" == "" (
  set DYNAMO_MODULES=DAS;Portal.paf
) else (
  set DYNAMO_MODULES=DAS;Portal.paf
)
set SERVERNAME=.
set CREATE_JAR=
set SERVERCONFIGPATH=

:dynamoenv

  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :endofscript

  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%

REM  echo -------------------
REM  echo JAVA_ARGS:  %JAVA_ARGS% 
REM  echo -------------------
REM  echo DYNAMO_MODULES: %DYNAMO_MODULES%
REM  echo -------------------
REM  echo CONFIGPATH: %CONFIGPATH%
REM  echo -------------------
REM  echo CLASSPATH:  %CLASSPATH%
REM  echo -------------------
REM  echo PATH:       %PATH%

:shiftargs
  if "%1"=="" goto doneshift  
  set ALL_ARGS=%ALL_ARGS% %1
  shift
  goto shiftargs
:doneshift

%JAVA_VM% %JAVA_ARGS% atg.portal.deploy.PortletDeployTool %ALL_ARGS%

:endofscript

endlocal
