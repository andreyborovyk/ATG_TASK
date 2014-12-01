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

REM Set defaults...
set ARGS=
set SERVERCONFIGPATH=
set SERVERNAME=.
set PREFIX=

  ::
  :: Check that DYNAMO_HOME and DAS_ROOT are set
  ::
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript

:homedone

  goto :parse

:missingargumenterror
  echo Missing argument to option: %1  
  goto :usage

:unknownoptionerror
  echo Unknown option: %1  
  goto :usage

:unknownargumenterror
  echo Unknown argument: %1  
  goto :usage

:usage

  echo batchCompile [server-name] [-documentRoot dir] [-url url-prefix ]
  echo [server-name]  Compile files in the pagebuild directory of this server
  echo -documentRoot  Compile files for a server using this document root. 
  echo                The default is to use dynamo's document root.
  echo                Use this option to compile an admin server.
  echo                Do not use this to specify a sub-directory of the
  echo                document root.
  echo -url           Compile only files that start with this URL prefix.
  echo                For example, -url /demo will only compile files with the
  echo                prefix /demo. 
  goto :endofscript  	

:parse
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :parseswitch
  if "%SERVERCONFIGPATH%"=="" goto :namedserver
  goto :unknownargumenterror

:namedserver

  set SERVERCONFIGPATH=;servers\%1\localconfig
  set SERVERNAME=servers\%1
  set PREFIX=-prefix servers\%1
  shift
  goto :parse

:parseswitch
  if "%1"=="-help" goto :usage 
  if "%1"=="-documentRoot" goto :parsearg
  if "%1"=="-url" goto :parsearg
  goto unknownoptionerror

:parsearg
  if "%2"=="" goto :missingargumenterror
  set ARGS=%ARGS% %1 %2
  shift
  shift
  goto :parse

:parsedone
  set MAINCLASS=atg.service.sitewalker.BatchCompileSiteWalker
  set POSTCONFIGPATH=;localconfig%SERVERCONFIGPATH%;..\DAS\config\batchconfig.jar

  if "%DYN_BASE_CONFIGPATH%"=="" goto :loadenv
  set CONFIGPATH=%DYN_BASE_CONFIGPATH%

:loadenv
  REM don't load applications 
  set DYNAMO_BASE=true

  call bin\dynamoEnv.bat

:setconfigpath

  REM
  REM Always put this at the end - after a user's config path.
  REM
  set CONFIGPATH=%CONFIGPATH%%POSTCONFIGPATH%

  %JAVA_VM% %JAVA_ARGS% %MAINCLASS% %CONFIGPATH% %ARGS% %PREFIX%

:endofscript
endlocal
