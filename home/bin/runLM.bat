@echo off
setlocal

REM You can run this script from the from the home\bin subdirectory of the base 
REM directory where you installed Dynamo (for example, C:\ATG\ATG10.0.3\home\bin).
REM Alternatively, if you set the DYNAMO_HOME system environment variable to
REM point to the home subdirectory, you can run this script from any directory.
REM For information about setting DYNAMO_HOME, see the Dynamo Administration Guide.
REM
REM This script sets CLASSPATH, java command line options, and PATH to include
REM the directories for dlls that may need to be loaded.  These environment
REM variables are loaded from dynamoEnv.bat (which you can use to run javac
REM with Dynamo's CLASSPATH).
REM
REM An optional command line argument specifies the server name to use.
REM If you run this script with no argument, it runs the "default" server.
REM
REM This bat file also handles server restart - if the server exits with
REM status code 17, it restarts the server process.

title ATG License Manager 10.0.3

set ORIGCLASS=%CLASSPATH%
set ORIGCONFIG=%CONFIGPATH%
set ORIGPATH=%PATH%
set ORIGARGS=%JAVA_ARGS%

goto :startscript

:usage
  echo runLM  [-v or -i][-m additional-module-list]
  echo -m  Run additionally with the specified modules
  echo -v  Print version info and exit
  echo -i  Print IP addresses of this machine and exit 
  goto :pauseexit

:unknownoptionerror
  echo Unknown option: %1  
  goto :usage

:incompatible
  echo ** 
  echo ** Incompatible options specified
  echo ** Only one of -w or -n may be specified
  echo ** Only one of -v or -i may be specified
  echo **
  goto :usage

:startscript
  ::
  :: Check that DYNAMO_HOME and DAS_ROOT are set
  ::
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :pauseexit

:homedone
  set DYNAMO_BASE=false
  set VERSION_ONLY=false
  set RUN_DCC=false
  set ALLOW_UI=true
  set IP_ONLY=false
  set SERVERCONFIGPATH=;localconfig
  set SERVERNAME=.

  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  goto :namedserver

:parseswitch
  if "%1"=="-help" goto :usage 
  if "%1"=="-b" goto :baseconfig
  if "%1"=="-m" goto :modules
  if "%1"=="-w" goto :rundcc
  if "%1"=="-n" goto :nolocalui
  if "%1"=="-v" goto :versiononly
  if "%1"=="-i" goto :iponly
  if "%FIRSTCHAR%"=="-" goto :unknownoptionerror

:namedserver
  title ATG 10.0.3 %1
  set SERVERCONFIGPATH=;localconfig;servers\%1\localconfig
  set SERVERNAME=servers\%1
  set SERVERCOMMAND=%1Command.bat
  goto :parseswitchdone

:modules
  shift
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  if "%ADDITIONAL_MODULES%"=="" goto :firstmod
  set ADDITIONAL_MODULES=%ADDITIONAL_MODULES%;%1
  goto :modules
:firstmod
  set ADDITIONAL_MODULES=%1
  goto :modules

:rundcc
  if "%ALLOW_UI%"=="false" goto :incompatible
  set RUN_DCC=true
  goto :parseswitchdone

:nolocalui
  if "%RUN_DCC%"=="true" goto :incompatible
  set ALLOW_UI=false
  goto :parseswitchdone

:versiononly
  if "%IP_ONLY%"=="true" goto :incompatible
  set VERSION_ONLY=true
  goto :parseswitchdone

:iponly
  if "%VERSION_ONLY%"=="true" goto :incompatible
  set IP_ONLY=true
  goto :parseswitchdone

:baseconfig
  set DYNAMO_BASE=true
  goto :parseswitchdone

:parseswitchdone
  shift
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  goto :parseswitch

:parsedone
  set DYNAMO_MODULES=DAS-UI
  if "%ALLOW_UI%"=="false" set DYNAMO_MODULES=
  if NOT "%ADDITIONAL_MODULES%"=="" set DYNAMO_LICENSED_MODULES=%DYNAMO_LICENSED_MODULES%;%ADDITIONAL_MODULES%

if EXIST %DYNAMO_HOME%\localconfig\environment.bat call %DYNAMO_HOME%\localconfig\environment.bat

  call bin\dynamoEnv.bat -m %DYNAMO_LICENSED_MODULES% -createJar
  if "%JAVA_VM%"=="" goto :pauseexit

  REM
  REM Always put this at the end - after a user's config path.
  REM
  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%
  
  if "%IP_ONLY%"=="true" goto :printip
  if "%VERSION_ONLY%"=="true" goto :printversion

  REM
  REM append DCC to modules list if we are to run it in this VM
  REM
  if "%RUN_DCC%"=="true" set DYNAMO_MODULES=%DYNAMO_MODULES%;DCC

  REM EnvironmentLogger tool
  %JAVA_VM% %JAVA_ARGS% atg.service.dynamo.EnvironmentLogger LM;%DYNAMO_LICENSED_MODULES% %CONFIGPATH%
  goto :firststart

:restart
  set CLASSPATH=%ORIGCLASS%
  set CONFIGPATH=%ORIGCONFIG%
  set PATH=%ORIGPATH%
  set JAVA_ARGS=%ORIGARGS%
  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :pauseexit
  set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%

:firststart
  REM
  REM If dynamo is being started as a service, do not echo config info to the console
  REM

  if not "%DYNAMOSERVICE%"=="" goto :afterstart

  %JAVA_VM% %JAVA_ARGS% atg.service.dynamo.Configuration -v
  %JAVA_VM% -version 2>&1
  %JAVA_VM% -fullversion 2>&1   
  echo -------------------
  echo JAVA_ARGS:  %JAVA_ARGS% 
  echo -------------------
  echo CONFIGPATH: %CONFIGPATH%
  echo -------------------
  echo CLASSPATH:  %CLASSPATH%
  echo -------------------
  echo PATH:       %PATH%
  :afterstart

  REM
  REM If dynamo is being started as a service, dcontrol must be used to launch
  REM the %JAVA_VM%
  REM

  if not "%DYNAMOSERVICE%"=="" goto :servicecommand

  "%JAVA_VM%" %JAVA_ARGS% %DYN_EXTRA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher LM LM;%DYNAMO_LICENSED_MODULES% %CONFIGPATH%

  goto :endcommand

:servicecommand
  dcontrol -n %DYNAMOSERVICE% -k "%JAVA_VM% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher %DYNAMO_LICENSED_MODULES% %CONFIGPATH% -prefix %SERVERNAME% -shellrestart"

:endcommand

  REM codes of 18 or greater just exit
  if ERRORLEVEL 18 goto :endofscript

  REM We restart if the java process exited with code 17
  if ERRORLEVEL 17 goto :restart

  if ERRORLEVEL 0 goto :endofscript

  echo Dynamo exited abnormally.  For troubleshooting information go to the URL:
  echo   http://www.atg.com/support
  goto :pauseexit

:printip
  "%JAVA_VM%" %JAVA_ARGS% atg.service.dynamo.Configuration -i
  goto :endofscript

:printversion
  "%JAVA_VM%" %JAVA_ARGS% atg.service.dynamo.Configuration -v
  goto :endofscript

:pauseexit
pause

:endofscript
title Command Prompt

endlocal
