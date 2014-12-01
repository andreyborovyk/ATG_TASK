@echo off
setlocal

:: This script calls the assembler tool to assemble an ATG application into
:: an ear file

:: Set up the appropriate directories
set PROGRAM_DIR=%~dp0
set OWD=%cd%

:: Ensure that DYNAMO_HOME is set. checkDynamoHome.bat defines the
:: %CHECK_DYNAMO_HOME% and %DYNAMO_HOME% variables
::
call %PROGRAM_DIR%\checkDynamoHome.bat
if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript
set DYNAMO_ROOT=%DYNAMO_HOME%\..

:: Allow extra environment variables to be specified.
::
if EXIST %DYNAMO_HOME%\localconfig\environment.bat call %DYNAMO_HOME%\localconfig\environment.bat

:: Use dasEnv.bat to set ATGJRE and app-server specific variables
::
if EXIST %DYNAMO_HOME%\localconfig\dasEnv.bat call %DYNAMO_HOME%\localconfig\dasEnv.bat

:: Use "java" as fallback value for ATGJRE.
::
if NOT DEFINED ATGJRE set ATGJRE=java

:: Unfortunately, WebLogic also uses the JAVA_VM variable, but in a 
:: different way.
::
if NOT DEFINED WL_HOME goto :notwl
  if "%JAVA_VM%" == "-client" (
    set JAVA_VM=%ATGJRE%
  )
:notwl

:: Supply default values for required variables that haven't been set.
::
if NOT DEFINED JAVA_VM set JAVA_VM=%ATGJRE%

:: Note - %THIRD_PARTY_APP_SERVER% is declared in checkDynamoHome.bat
::
set EXTRA_JAVA_ARGS=-Datg.dynamo.root="%DYNAMO_ROOT%" -Datg.dynamo.appserver="%THIRD_PARTY_APP_SERVER%"

"%JAVA_VM%" -Xms96m -Xmx160m %EXTRA_JAVA_ARGS% -cp "%DYNAMO_HOME%\lib\launcher.jar;%DYNAMO_HOME%\lib\assembler.jar" atg.appassembly.Main -owd "%OWD%" %* DafEar

:endofscript

endlocal
