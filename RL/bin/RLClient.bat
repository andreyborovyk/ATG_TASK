@echo off
setlocal

REM
REM Script to run the Repostory Loader RLClient
REM with supplied args
REM
REM To see usage: run RLClient.bat with no args with DYNAMO_HOME set correctly
REM

REM ====================================================
REM Make sure DYNAMO_HOME and/or DYNAMO_ROOT are set
REM ====================================================
if not "%DYNAMO_HOME%" == "" goto :not_empty_dynamo_home
 if not "%DYNAMO_ROOT%" == "" goto :after_dynamo_root_home_checks
   echo "Neither DYNAMO_HOME nor DYNAMO_ROOT is set... exiting."
   goto :end_of_script
:has_root
   set DYNAMO_HOME=%DYMAMO_ROOT%\home

:not_empty_dynamo_home
if not "%DYNAMO_ROOT%" == "" goto :after_dynamo_root_home_checks
  set DYNAMO_ROOT=%DYNAMO_HOME%\..

:after_dynamo_root_home_checks
echo .
echo ---------------------------------
echo DYNAMO_HOME=%DYNAMO_HOME%
echo DYNAMO_ROOT=%DYNAMO_ROOT%
echo ---------------------------------
echo .

java -classpath %DYNAMO_HOME%\..\RL\lib\RLClient.jar atg.repository.loader.RLClient %1 %2 %3 %4 %5 %6 %7 %8 %9

:end_of_script
  
