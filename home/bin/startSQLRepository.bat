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
if "%THIRD_PARTY_APP_SERVER%" == "" (
  echo ** WARNING: HOME variable of your application server is not set.
  goto :endofscript
)
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

if "%DAS_ROOT%" == "" (
  set DYNAMO_MODULES=DAS
) else (
  set DYNAMO_MODULES=DAS
)
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
  if "%1"=="-layer" goto :layers
  goto :dynamoenv

:modules
  shift
  set DYNAMO_MODULES=%DYNAMO_MODULES%;%1
  shift
  goto :startswitch

:layers
  shift
  set CONFIG_LAYERS=%CONFIG_LAYERS%;%1
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

  
  if "%THIRD_PARTY_APP_SERVER%" == "" (
    set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%
  ) else (
  
   
    if "%THIRD_PARTY_APP_SERVER%" == "websphere" (
      set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%;..\DAF\config\dafconfig.jar;..\DAF\config\wsconfig.jar
    ) else (
      set CONFIGPATH=%CONFIGPATH%%SERVERCONFIGPATH%;..\DAF\config\dafconfig.jar
    )
    
  )


  if NOT "%ATG_CHECK_DEPLOY_TARGETS%"=="" set JAVA_ARGS=-Datg.check.deploy.targets=%ATG_CHECK_DEPLOY_TARGETS% %JAVA_ARGS%
	
  echo -------------------
  echo JAVA_ARGS:  %JAVA_ARGS% 
  echo -------------------
  echo DYNAMO_MODULES: %DYNAMO_MODULES%
  echo -------------------
  echo CONFIGPATH: %CONFIGPATH%
  echo -------------------
  echo CLASSPATH:  %CLASSPATH%
  echo -------------------
  echo PATH:       %PATH%

:shiftargs
  if "%~1"=="" goto doneshift  
  set ALL_ARGS=%ALL_ARGS% %1
  shift
  goto shiftargs
:doneshift

  "%JAVA_VM%" %JAVA_ARGS% atg.adapter.gsa.xml.TemplateParser -cmd startSQLRepository  -configPath %CONFIGPATH% %ALL_ARGS%

:endofscript

endlocal
