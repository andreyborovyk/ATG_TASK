@echo off

REM ---------------------------------------------------------
REM Sets the classpath for compilation given a set of Application
REM Module names.
REM usage:
REM compileEnv.bat [module_a module_b]
REM ---------------------------------------------------------

set ENVFILE=antCompileEnv.bat

if NOT "%1" == "" goto :checkdynamohome
echo Please specify a module or modules separated by a space.
goto :usage

:checkdynamohome
if NOT "%DYNAMO_HOME%" == "" goto :parsemodules
  echo You must set the DYNAMO_HOME variable to point to your Dynamo
  echo install directory

:parsemodules
set MODULES=%1
:modules
  shift
  if "%1"=="" goto :invoke
  set MODULES=%MODULES%;%1
goto :modules


:usage
  echo usage: compileEnv.bat [module_a module_b]
goto :end

:invoke
  java -classpath %DYNAMO_HOME%\lib\launcher.jar -Datg.dynamo.root=%DYNAMO_HOME%\.. atg.applauncher.dynamo.DynamoServerLauncher -environment %ENVFILE% %MODULES%
  if EXIST %ENVFILE% call %ENVFILE%

:end
  if EXIST %ENVFILE% del %ENVFILE%










