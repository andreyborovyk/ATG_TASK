@echo off

set PROGRAM_DIR=%~dp0
set NOPAUSE=true

if "%DEVTOOLS_PROJECTROOT%" == "" goto :nondevtoolsenv

  REM echo +++ devtools env setup +++
  REM +++ override CIM_HOME and EVAL_CLASSPATH +++
  set DYNAMO_ROOT=%DEVTOOLS_PROJECTROOT%/Dynamo
  set DYNAMO_HOME=%DYNAMO_ROOT%/home
  set CIM_HOME=%DEVTOOLS_PROJECTROOT%/CIM
  set EVAL_CLASSPATH=%DEVTOOLS_PROJECTROOT%\devtools\lib\ant.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\ant-nodeps.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\ant-launcher.jar;
  goto :start


:nondevtoolsenv

REM echo +++ non-devtools env setup +++
if "%JAVA_HOME%" == "" goto :javahomeerror

if "%DYNAMO_HOME%" == "" goto :dynamohomeerror

set DYNAMO_ROOT=%DYNAMO_HOME%/..
set CIM_HOME=%DYNAMO_ROOT%/CIM
set EVAL_CLASSPATH=%CIM_HOME%\lib\ant.jar;%CIM_HOME%\lib\ant-nodeps.jar;%CIM_HOME%\lib\ant-launcher.jar;


:start
if NOT EXIST %DYNAMO_HOME%/localconfig/dasEnv.bat goto :dasrooterror
call %DYNAMO_HOME%/localconfig/dasEnv.bat


set CIM_OUT_SCRIPT=%PROGRAM_DIR%\cimOut.cim

set CIMBATCH_APPSERVER=%PROGRAM_DIR%\cimbatch-jboss.cim
set CIMBATCH_DB=%PROGRAM_DIR%\cimbatch-mysqldb.cim
set CIMBATCH_ATGSERVER=%PROGRAM_DIR%\cimbatch-atgserver.cim
set CIMBATCH_DEPLOY=%PROGRAM_DIR%\cimbatch-jbossdeploy.cim


if DEFINED WL_HOME goto :wlerror
if DEFINED WAS_HOME goto :waserror

if DEFINED JBOSS_HOME goto :setjbossenv

:appserverdone

set ANT_CMD_LINE_ARGS=-Dcim.out=%CIM_OUT_SCRIPT% -Dcim.appserver=%CIMBATCH_APPSERVER% -Dcim.db=%CIMBATCH_DB% -Dcim.atgserver=%CIMBATCH_ATGSERVER% -Dcim.deploy=%CIMBATCH_DEPLOY% -Ddynamo.home=%DYNAMO_HOME% -Ddynamo.root=%DYNAMO_ROOT% -Dcim.home=%CIM_HOME%
set ANT_ARGS=-f "%PROGRAM_DIR%/evalbuild.xml"


SET /P LOAD_DATA=Do you wish to load evaluation data (Y/N)?
if %LOAD_DATA:~0,1% == Y goto :continue
if %LOAD_DATA:~0,1% == y goto :continue
goto :done
:continue

"%ATGJRE%" %ANT_OPTS% -classpath "%EVAL_CLASSPATH%" -Xms64m -Xmx256m org.apache.tools.ant.Main %ANT_ARGS% %ANT_CMD_LINE_ARGS% %APP_SERVER_ARGS%
call %DYNAMO_HOME%/bin/cim.bat -batch %CIM_OUT_SCRIPT%
goto done

:setjbossenv
set CIMBATCH_APPSERVER=%PROGRAM_DIR%\cimbatch-jboss.cim
set CIMBATCH_DEPLOY=%PROGRAM_DIR%\cimbatch-jbossdeploy.cim
set APP_SERVER_ARGS=-Djboss.home=%JBOSS_HOME%
goto :appserverdone

:javahomeerror
echo The JAVA_HOME environment variable must exist and point to the jdk1.6 directory.
pause
exit

:dynamohomeerror
echo You must set the DYNAMO_HOME environment variable to point to your Dynamo install directory.
pause
exit

:wlerror
echo Evaluation Configuration is not available with a Weblogic application server.
pause
exit

:waserror
echo Evaluation Configuration is not available with a Websphere application server.
pause
exit

:setwlenv
call %DYNAMO_HOME%\localconfig\environment.bat
for /F "eol=# tokens=1,2 delims==" %%i in (%DYNAMO_HOME%\localconfig\atg\devtools\ContainerAppDirectoryAgent.properties) do (
    if "%%i"=="directoryRoots" set DOMAIN_PATH=%%j/../
)
set CIMBATCH_APPSERVER=%PROGRAM_DIR%/cimbatch-wl.cim
set CIMBATCH_DEPLOY=%PROGRAM_DIR%/cimbatch-wldeploy.cim
set APP_SERVER_ARGS=-Dwl.home="%WL_HOME%" -Dwl.version="%WL_VERSION%" -Dwl.domain="%DOMAIN_PATH%" -Dbea.home="%BEA_HOME%"
goto :appserverdone

:setwasenv
for /F "eol=# tokens=1,2 delims==" %%i in (%DYNAMO_HOME%\localconfig\atg\devtools\ContainerAppDirectoryAgent.properties) do (
    if "%%i"=="directoryRoots" set DIRECTORY_ROOTS="%%j"
)
for /F "tokens=* delims=\\" %%i in ('echo %DIRECTORY_ROOTS%') do (
  set WAS_NODE=%%~nxi
)

set CIMBATCH_APPSERVER=%PROGRAM_DIR%/cimbatch-was.cim
set CIMBATCH_DEPLOY=%PROGRAM_DIR%/cimbatch-wasdeploy.cim
set APP_SERVER_ARGS="-Dwas.home=%WAS_HOME% -Dwas.node=%WAS_NODE%"

goto :appserverdone

:dasrooterror
echo File %DYNAMO_HOME%/localconfig/dasEnv.bat is missing

:done
