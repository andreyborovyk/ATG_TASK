@echo off
setlocal

REM This script must be run from the EAI module directory or you must
REM set DYNAMO_HOME correctly and you can run it from anywhere.
REM
REM It sets CLASSPATH, java command line options, and PATH to include the
REM directories for dlls that may need to be loaded.  These environment
REM variables are loaded from dynamoEnv.bat (which you can use to run javac
REM with Dynamo's CLASSPATH).
REM


if NOT "%DYNAMO_HOME%" == "" goto :checkhome


REM
REM Allow us to be in the bin directory when we start
REM
  if EXIST generateSchema.bat cd ..\..\home
  if EXIST bin\generateSchema.bat ..\home
  set DYNAMO_HOME=.
  if NOT "%DYNAMO_HOME%" == "" goto :checkhome
  echo You must either run this script from the EAI module directory
  echo or you should set the variable DYNAMO_HOME
  goto :endofscript

:checkhome
  cd /d %DYNAMO_HOME%
  if EXIST ..\EAI\bin\generateSchema.bat goto :homedone

  echo Unable to find %DYNAMO_HOME%\bin\generateSchema.bat - invalid setting of DYNAMO_HOME
  goto :endofscript

:homedone


set DYNAMO_MODULES=EAI

if NOT "%1"=="-m" goto :dynamoenv

shift
set DYNAMO_MODULES=%DYNAMO_MODULES%;%1
shift


:dynamoenv
  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :endofscript

  set CONFIGPATH=%CONFIGPATH%;localconfig;.


REM specify start directory...otherwise relative output files will go into Dynamo home
REM
set STARTDIR=%DYNAMO_HOME%\bin


  %JAVA_VM% %JAVA_ARGS% atg.xml.schema.SchemaGeneratorUtility -configPath "%CONFIGPATH%" -startDir "%STARTDIR%" -generatorClass atg.xml.schema.XmlSchemaGenerator %*

:endofscript

endlocal
