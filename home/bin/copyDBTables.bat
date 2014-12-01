@echo off
setlocal

REM =======================================================================
REM $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $ 
REM -----------------------------------------------------------------------
REM 
REM This script is intended to be run from the DCS_HOME\demo\copyData 
REM directory.
REM 
REM To run this from DCS_HOME\demo\copyData, you must make sure that
REM the DYNAMO_HOME environment variable is set to your Dynamo home
REM installation.  You must have DYNAMO_HOME\bin added to your PATH
REM environment variable.  You must have the jdbc classes added to
REM your CLASSPATH environment variable.
REM 
REM Issue the command:
REM copyDBTables CopyDCSDemoData.properties 
REM
REM =======================================================================

if NOT "%DYNAMO_HOME%" == "" goto :havepath
set DYNAMO_HOME=%~dp0..

:havepath
REM Make sure they specify a properties file
if "%1" == ""      set USAGE=y
if "%1" == "-help" set USAGE=y

REM Grab name of properties file
set PROPERTIESFILE=%1
shift

call %DYNAMO_HOME%\bin\dynamoEnv.bat

set PROGRAM=atg.common.util.CopyDBTables
if "%USAGE%" == "y" goto :usage

REM Run the program, NB: %* won't work here
%JAVA_VM% %JAVA_ARGS% %PROGRAM% %PROPERTIESFILE% 
goto :endofscript

:usage
echo copyDBTables copyDBTables.properties

:endofscript

endlocal
