@echo off
setlocal

goto :startscript
  
:usage
  echo Usage startNucleus [-h] [-c opt-configpath ] [-m opt-modules ]

  echo Options
  echo   -m opt-module;opt-module:...           Run with the additionally specified modules
  echo   -c opt-configpath;opt-configpath:...   Run with the additionally specified configpath
  echo   -h                                     Display command usage

  goto :exit

:modules
  shift
  if "%1"=="" goto :parseDone
  set OPT_MODULES=%OPT_MODULES%;%1
  set FIRSTCHAR=%2
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :shiftparseswitch
  goto :modules
:configpaths
  shift
  if "%1"=="" goto :parseDone
  set OPT_CONFIGPATH=%OPT_CONFIGPATH%;%1
  set FIRSTCHAR=%2
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :shiftparseswitch
  goto :configpaths

:startscript
 goto :parseswitch
:shiftparseswitch
 shift
 goto :parseswitch
:parseswitch
  if "%1"=="-h" goto :usage
  if "%1"=="-m" goto :modules
  if "%1"=="-c" goto :configpaths
  goto :parseDone
:parseDone
  goto :initHome

:initHome
  if NOT DEFINED NUCLEUS_HOME if EXIST .\startNucleus.bat set NUCLEUS_HOME=..
  if NOT DEFINED NUCLEUS_HOME if EXIST .\bin\startNucleus.bat set NUCLEUS_HOME=.
  if NOT DEFINED NUCLEUS_HOME goto :homeError
  goto :initHomeDone
:homeError
 echo Error Unable to determine NUCLEUS_HOME
 goto :exit
:initHomeDone
  goto :initRoot

:initRoot
  if NOT DEFINED NUCLEUS_ROOT set NUCLEUS_ROOT=%NUCLEUS_HOME%\..
  goto :initRootDone
:initRootDone
  goto :initModules

:initModules
  if NOT DEFINED NUCLEUS_MODULES set NUCLEUS_MODULES=Initial
  if DEFINED OPT_MODULES set NUCLEUS_MODULES=%NUCLEUS_MODULES%;%OPT_MODULES%
  if DEFINED MODULES set NUCLEUS_MODULES=%NUCLEUS_MODULES%;%MODULES%
  goto :initModulesDone
:initModulesDone
  goto :initConfigPath

:initConfigPath
  if NOT DEFINED NUCLEUS_CONFIGPATH set NUCLEUS_CONFIGPATH=%NUCLEUS_HOME%\localconfig
  if DEFINED CONFIGPATH set NUCLEUS_CONFIGPATH=%CONFIGPATH%;%NUCLEUS_CONFIGPATH%
  if DEFINED OPT_CONFIGPATH set NUCLEUS_CONFIGPATH=%OPT_CONFIGPATH%;%NUCLEUS_CONFIGPATH%
  goto :initConfigPathDone
:initConfigPathDone
  goto :initJVM

:initJVM
  if NOT DEFINED JAVA_VM if DEFINED JAVA_HOME if EXIST %JAVA_HOME%\jre\sh\java.exe set JAVA_VM=%JAVA_HOME%\jre\sh\java.exe
  if NOT DEFINED JAVA_VM if DEFINED JAVA_HOME if EXIST %JAVA_HOME%\bin\java.exe set JAVA_VM=%JAVA_HOME%\bin\java.exe
  if NOT DEFINED JAVA_VM set JAVA_VM=java.exe

  if NOT EXIST %JAVA_VM% goto :jvmError  

  goto :initJVMDone
:jvmError
  echo Error JAVA_HOME is not defined correctly.
  echo   We cannot execute %JAVA_VM%
  goto :exit
:initJVMDone
  goto :initEnv

:initEnv
  set TEMP_ENVIRONMENT_SCRIPT=.\defaultCommand.bat
  %JAVA_VM% -classpath %NUCLEUS_HOME%\locallib;%NUCLEUS_HOME%\lib\launcher.jar -Datg.dynamo.home=%NUCLEUS_HOME% -Datg.dynamo.root=%NUCLEUS_ROOT% -Datg.dynamo.server.home=%NUCLEUS_HOME% -Datg.dynamo.modulepath=%NUCLEUS_ROOT% atg.applauncher.dynamo.DynamoServerLauncher -environment %TEMP_ENVIRONMENT_SCRIPT% %NUCLEUS_MODULES% %NUCLEUS_CONFIGPATH%
  call %TEMP_ENVIRONMENT_SCRIPT%
  goto :initEnvDone
:initEnvDone
  goto :dumpEnv

:dumpEnv
  echo JAVA_VM			%JAVA_VM%
  echo JAVA_ARGS		%JAVA_ARGS%
  echo NUCLEUS_ROOT		%NUCLEUS_ROOT%
  echo NUCLEUS_HOME		%NUCLEUS_HOME%
  echo MODULES			%DYNAMO_MODULES%
  echo CONFIGPATH		%CONFIGPATH%
  echo CLASSPATH		%CLASSPATH%
  goto :dumpEnvDone
:dumpEnvDone
 goto :startNucleus

:startNucleus
rem JPDA options. Uncomment and modify as appropriate to enable remote debugging.
rem set JAVA_ARGS=%JAVA_ARGS% -Xdebug -Xrunjdwp:transport=dt_socket,address=23000,server=y,suspend=y

%JAVA_VM% -classpath %CLASSPATH% -Datg.dynamo.home=%NUCLEUS_HOME% -Datg.dynamo.root=%NUCLEUS_ROOT% -Datg.dynamo.server.home=%NUCLEUS_HOME% -Datg.dynamo.modulepath=%NUCLEUS_ROOT% %JAVA_ARGS% atg.applauncher.dynamo.DynamoServerLauncher %DYNAMO_MODULES% %CONFIGPATH%

:exit
endlocal
