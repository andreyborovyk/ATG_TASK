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

title ATG Dynamo Console 10.0 

set ORIGCLASS=%CLASSPATH%
set ORIGCONFIG=%CONFIGPATH%
set ORIGPATH=%PATH%
set ORIGARGS=%JAVA_ARGS%

  ::
  :: Check that DYNAMO_HOME and DAS_ROOT are set
  ::
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript

:homedone

  set DYNAMO_MODULES=DAS;DAS-UI

  call bin\dynamoEnv.bat
  if "%JAVA_VM%"=="" goto :endofscript

  %JAVA_VM% %JAVA_ARGS% %DYN_EXTRA_ARGS% atg.dynamometer.Dynamometer

:endofscript

endlocal
