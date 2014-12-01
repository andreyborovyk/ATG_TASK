@echo off
setlocal

REM set environment

  ::
  :: Check that DYNAMO_HOME and DAS_ROOT are set
  ::
	set PROGRAM_DIR=%~dp0
	call %PROGRAM_DIR%\checkDynamoHome.bat
	if "%CHECK_DYNAMO_HOME%" == "fail" goto :dynamoHomeError
  set DYNAMO_HOME=%CD%
	set DYNAMO_ROOT=%DYNAMO_HOME%\..
	
	if NOT EXIST %DYNAMO_HOME%\localconfig\dasEnv.bat goto :checkJava
	  call %DYNAMO_HOME%\localconfig\dasEnv.bat

	:checkJava
	if NOT "%ATGJRE%" == "" goto :javahomeok
	  echo You must set the ATGJRE variable to point to the
	  echo java executable
  pause
	GOTO :EOF
  
  :dynamoHomeError
  echo You must set the DYNAMO_HOME variable
  pause
	GOTO :EOF

	:javahomeok
	
	call %DYNAMO_HOME%\bin\j2eeEnv.bat

set CIM_HOME=%DYNAMO_ROOT%\CIM
set CIM_CLASSPATH=%CLASSPATH%;%CIM_HOME%\lib\ant.jar;%CIM_HOME%\lib\ant-nodeps.jar;%CIM_HOME%\lib\ant-launcher.jar;%CIM_HOME%\lib\commons-beanutils-1.7.0.jar;%CIM_HOME%\lib\commons-digester-1.8.jar;%DYNAMO_ROOT%\DAS\lib\commons-logging-1.1.1.jar;%CIM_HOME%\lib\hsqldb-1.8.0.7.jar;%CIM_HOME%\lib\jline-0.9.94.jar;%CIM_HOME%\lib\incanto-0.2.0.jar;%DYNAMO_ROOT%\DAS\lib\jaxb-api101.jar;%DYNAMO_ROOT%\DAS\lib\jaxb-impl104.jar;%DYNAMO_ROOT%\DAS\lib\jaxb-libs104.jar;%DYNAMO_ROOT%\DAS\src\lib\relaxngDatatype.jar;%DYNAMO_ROOT%\DAS\src\lib\xalan.jar;%DYNAMO_ROOT%\DAS\src\lib\xercesImpl.jar;%DYNAMO_ROOT%\DAS\src\lib\xml-apis.jar;%DYNAMO_ROOT%\DAS\src\lib\xsdlib.jar;%DYNAMO_HOME%\lib\assembler.jar;%DYNAMO_HOME%\lib\launcher.jar;%DYNAMO_ROOT%\DAS\lib\classes.jar;%DYNAMO_ROOT%\DAS\lib\resources.jar;%DYNAMO_ROOT%\ATGDBSetup\j2ee-apps\ATGDBSetup\web-app\WEB-INF\lib\classes.jar;%CIM_HOME%\lib\classes.jar;				
set DBSETUP_HOME=%DYNAMO_ROOT%\ATGDBSetup

if "%DEVTOOLS_PROJECTROOT%" == "" goto :nondevtoolsenv
  
 	REM echo +++ devtools env setup +++
 	REM +++ override CIM_HOME and CIM_CLASSPATH +++
	set DYNAMO_ROOT=%DEVTOOLS_PROJECTROOT%\Dynamo
	set DYNAMO_HOME=%DYNAMO_ROOT%\home
	set CIM_HOME=%DEVTOOLS_PROJECTROOT%\CIM
	set DBSETUP_HOME=%DEVTOOLS_PROJECTROOT%\ATGDBSetup\ATGDBSetupEar\_build
	set CIM_CLASSPATH=%CLASSPATH%;%DEVTOOLS_PROJECTROOT%\devtools\lib\ant.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\ant-nodeps.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\ant-launcher.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\commons-beanutils-1.7.0.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\commons-digester-1.8.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\commons-logging-1.1.1.jar;%CIM_HOME%\lib\hsqldb-1.8.0.7.jar;%CIM_HOME%\lib\jline-0.9.94.jar;%DEVTOOLS_PROJECTROOT%\devtools\lib\incanto-0.2.0.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\jaxb-api101.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\jaxb-impl104.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\jaxb-libs104.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\relaxngDatatype.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\xalan.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\xercesImpl.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\xml-apis.jar;%DEVTOOLS_PROJECTROOT%\DAS\src\lib\xsdlib.jar;%DYNAMO_HOME%\lib\assembler.jar;%DYNAMO_HOME%\lib\launcher.jar;%DEVTOOLS_PROJECTROOT%\DAS\buildtree\;%DEVTOOLS_PROJECTROOT%\ATGDBSetup\ATGDBSetupEar\_build\classes;%CIM_HOME%\build\classes\;				

:nondevtoolsenv

REM uncomment the following lines for environment debug
REM echo CIM_HOME is %CIM_HOME%
REM echo DYNAMO_ROOT is %DYNAMO_ROOT%
REM echo DYNAMO_HOME is %DYNAMO_HOME%
REM echo DEVTOOLS_PROJECTROOT is %DEVTOOLS_PROJECTROOT%
REM echo CIM_CLASSPATH is %CIM_CLASSPATH%

:setclasspath

set CLASSPATH=%CIM_CLASSPATH%

:parse

set CIM_ARGS=-Dcim.home=%CIM_HOME% -Ddynamo.root=%DYNAMO_ROOT% -Ddbsetup.home=%DBSETUP_HOME%

REM default MODE is console
set MODE_ARGS=-Dcim.console=true 

if "%1"=="" goto :parsedone
set FIRSTCHAR=%1
set FIRSTCHAR=%FIRSTCHAR:~0,1%
if "%FIRSTCHAR%"=="-" goto :parseswitch
goto :parsedone

:parseswitch
if "%1"=="-debug" goto :setdebug
if "%1"=="-mock" goto :setmockmode
if "%1"=="-record" goto :setrecord
if "%1"=="-batch" goto :batch
echo Unknown option: '%1'  

:usage
  echo cim.bat [-debug] [-record] [-batch inputFilePath]
  exit

:setdebug
  set DEBUG_ARGS=-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=3000
  echo Debug mode enabled on port 3000.  Waiting for connection to Attach.
  shift
  if "%1"=="" goto :parsedone
  goto :parse

:setrecord
  set RECORD_ARGS=-Dcim.record=true
  echo Batch Mode Recording Enabled
  shift 
  if "%1"=="" goto :parsedone
  goto :parse  

:setmockmode
  set MODE_ARGS=-Dcim.mock=true 
  shift
  if "%1"=="" goto :parsedone
  goto :parse
	
:DeQuote
REM This is used to remove the quotes from the batch file arg
SET _DeQuoteVar=%1
CALL SET _DeQuoteString=%%!_DeQuoteVar!%%
IF [!_DeQuoteString:~0^,1!]==[^"] (
IF [!_DeQuoteString:~-1!]==[^"] (
SET _DeQuoteString=!_DeQuoteString:~1,-1!
) ELSE (GOTO :EOF)
) ELSE (GOTO :EOF)
SET !_DeQuoteVar!=!_DeQuoteString!
SET _DeQuoteVar=
SET _DeQuoteString=
GOTO :EOF
	
:batch
  shift
  SetLocal EnableDelayedExpansion
  set BatchFile=%1
  CALL :dequote BatchFile
  if "x%BatchFile%"=="x" goto :usage
  set MODE_ARGS=-Dcim.batch=true "-Dcim.batch.inputFileName=%BatchFile%"
  goto :parsedone

:parsedone
"%ATGJRE%" -Xmx512m %DEBUG_ARGS% %MODE_ARGS% %RECORD_ARGS% %CIM_ARGS% atg.cim.Launcher

endlocal
