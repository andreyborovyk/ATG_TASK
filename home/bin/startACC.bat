@echo off
setlocal

::
:: This script starts the ACC from either a shortcut or the command line.
:: 

::
:: Ensure that DYNAMO_HOME is set.
::
set PROGRAM_DIR=%~dp0
call %PROGRAM_DIR%\checkDynamoHome.bat
if "%CHECK_DYNAMO_HOME%" == "fail" goto :endofscript
set DYNAMO_ROOT=%DYNAMO_HOME%\..

::
:: Allow extra environment variables to be specified.
::
if EXIST %DYNAMO_HOME%\localconfig\environment.bat call %DYNAMO_HOME%\localconfig\environment.bat

::
:: Preserve current value of DAS_ROOT.
::
set ACTUAL_DAS_ROOT=%DAS_ROOT%

::
:: Use dasEnv.bat to set ATGJRE and (possbily) WL_HOME.
::
if EXIST %DYNAMO_HOME%\localconfig\dasEnv.bat call %DYNAMO_HOME%\localconfig\dasEnv.bat

::
:: Restore value of DAS_ROOT, if necessary.
::
if DEFINED ACTUAL_DAS_ROOT set DAS_ROOT=%ACTUAL_DAS_ROOT%

::
:: Use "java" as fallback value for ATGJRE.
::
if NOT DEFINED ATGJRE set ATGJRE=java

::
:: Unfortunately, WebLogic also uses the JAVA_VM variable, but in a different way.
::
if NOT DEFINED WL_HOME goto :notwl
  if "%JAVA_VM%" == "-client" (
    set JAVA_VM=%ATGJRE%
  )
:notwl

::
:: Supply default values for required variables that haven't been set.
::
if NOT DEFINED DYNAMO_RMI_PORT set DYNAMO_RMI_PORT=8860
if NOT DEFINED JAVA_VM set JAVA_VM=%ATGJRE%

::
:: Start the ACC.
::

if NOT DEFINED DAS_ROOT (
  set EXTRA_JAVA_ARGS= -Datg.dynamo.modulepath="%DYNAMO_ROOT%"
) else (
  set EXTRA_JAVA_ARGS= -Datg.j2eeserver.root="%DAS_ROOT%" -Datg.dynamo.modulepath="%DYNAMO_ROOT%;%DAS_ROOT%"
)

:: THIS IS THE PATCH TO HANDLE THE -server TYPE

echo %JAVA_VM% > JAVAVM
findstr /I /R ".*\\java(.exe)* [ ]*-server" JAVAVM > nul

if ERRORLEVEL 1 goto :notfound
set JAVA_VM=%JAVA_VM: -server=%
set JAVA_ARGS_PREPEND=-server

:notfound
"%JAVA_VM%" %JAVA_ARGS_PREPEND% -Xms96m -Xmx160m -Djava.security.policy=%DYNAMO_HOME%\lib\java.policy -Djava.protocol.handler.pkgs=atg.net.www.protocol -Djava.naming.factory.url.pkgs=atg.jndi.url %EXTRA_JAVA_ARGS% -cp "%DYNAMO_HOME%\lib\launcher.jar;%DYNAMO_HOME%\..\DAS-UI\lib\client-stubs.jar" atg.applauncher.dynamo.LocalACCLauncher -host localhost -port %DYNAMO_RMI_PORT% -root %DYNAMO_ROOT% -home %DYNAMO_HOME%

:endofscript
del JAVAVM

endlocal
