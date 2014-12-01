@echo off
setlocal

title ATG 9.0 on JBOSS

goto :startscript

:startscript
  set ADDITIONAL_FLAGS=
  set SERVER_NAME=
  set BASE_SERVER_NAME=
  set LIVECONFIG=
  set ADDITIONAL_MODULES=
  set ADDITIONAL_ARGS=
  set MODULE_ENV=
  set MODULE_LIST=
  set EARFILE_NAME=ATG.ear
  set JBOSS_SERVER=atg
  set JBOSS_SERVER_ARG=-c %JBOSS_SERVER%
  set JBOSS_BIND_ARG=-b 0.0.0.0
  set JAVA_DEBUG_PORT=3000
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\..\..\..\home\bin\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :pauseexit
        
  set DAS_ENV=%DYNAMO_HOME%\localconfig\dasEnv.bat
  if EXIST %DAS_ENV% goto :dasenvset
  goto :parseswitchfirst        

:dasenvset
  call %DAS_ENV%
  goto :parseswitchfirst

rem  goto :copydatasources

:parseswitchfirst
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  goto :parseswitch

:parseswitch
  if "%1"=="-help" goto :usage 
  if "%1"=="-l" goto :liveconfig
  if "%1"=="-m" goto :modules
  if "%1"=="-j" goto :jbossdir
  if "%1"=="-clean" goto :cleanstart
  if "%1"=="-ear" goto :earname
  if "%1"=="-c" goto :jbossconfig
  if "%1"=="-f" goto :addflag
  if "%1"=="-layer" goto :firstlayer
  if "%1"=="-debug" goto :enableDebug
  if "%1"=="-debugPort" goto :debugPort
  if "%FIRSTCHAR%"=="-" goto :unknownoptionerror
  goto :setservername

:modules
  shift
  if "%1"=="" goto :parsedone
  if "%ADDITIONAL_MODULES%"=="" goto :firstmod
  set ADDITIONAL_MODULES=%ADDITIONAL_MODULES% %1
  goto :modules

:firstmod
  set ADDITIONAL_MODULES=%1
  goto :modules

:firstlayer
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :nolayers
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :nolayers
  if "%NEXT_FIRSTCHAR%"=="-" goto :nolayers
  set ADDITIONAL_LAYERS=-layer %1
  goto :layers

:layers
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :parsedone
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :parsedone
  if "%NEXT_FIRSTCHAR%"=="-" goto :parseswitch
  set ADDITIONAL_LAYERS=%ADDITIONAL_LAYERS% %1
  goto :layers
  
:nolayers
  echo ***        
  echo *** No configuration layers have been specified. You must specify 
  echo *** one or more configuration layers when using the -layer option.     
  echo ***
  goto :usage         

:liveconfig
  set LIVECONFIG=-liveconfig
  goto :parseswitchdone

:jbossdir
  shift
  set JBOSS_HOME=%1
  goto :parseswitchdone

:cleanstart
  set CLEANSTART=true
  goto :parseswitchdone

:earname
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :emptyearname
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :emptyearname
  if "%NEXT_FIRSTCHAR%"=="-" goto :emptyearname
  set EARFILE_NAME=%1
  goto :parseswitchdone

:jbossconfig
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :emptyjbossserver
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :emptyjbossserver
  if "%NEXT_FIRSTCHAR%"=="-" goto :emptyjbossserver
  set JBOSS_SERVER=%1
  set JBOSS_SERVER_ARG=-c %JBOSS_SERVER%
  goto parseswitchdone

:setservername
  set SERVER_NAME=%1
  set BASE_SERVER_NAME=%1
  if NOT EXIST %DYNAMO_HOME%\servers\%SERVER_NAME% goto :nonexistantserver
  set SERVER_NAME=-server %SERVER_NAME%
  goto :getbasemodules

:getbasemodules
  set MODULE_ENV=%DYNAMO_HOME%\servers\%BASE_SERVER_NAME%\localconfig\moduleList.properties
  if NOT EXIST %MODULE_ENV% goto :missingmodulelist

  for /F "eol=# tokens=1,2 delims==" %%i in (%MODULE_ENV%) do (
    if "%%i"=="modules" set MODULE_LIST=%%j
  )
  
  set MODULE_LIST=%MODULE_LIST:,=%

  
  goto :parseswitchdone
  
:addflag
  shift
  set ADDITIONAL_FLAGS=%1 %ADDITIONAL_FLAGS%
  goto :parseswitchdone

:enableDebug
  set DEBUG_ENABLED=true
  goto :parseswitchdone

:debugPort 
  shift
  set JAVA_DEBUG_PORT=%1
  goto :parseswitchdone

:parseswitchdone
  shift
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  goto :parseswitch

:parsedone
  rem check to make sure that the jboss home is set
  if NOT DEFINED JBOSS_HOME goto :unknownjbosshome
  if NOT EXIST %JBOSS_HOME% goto :nonexistantjbosshome
  if NOT DEFINED SERVER_NAME goto :nonexistantserver
  if "%SERVER_NAME%" == "" goto :nonexistantserver
  goto :copydatasources        

:copydatasources
  set ATG_SOLID_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-solid-ds.xml
  set ATG_APPS_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-apps-solid-ds.xml
  set EAR_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\%EARFILE_NAME%

  if NOT EXIST %JBOSS_HOME%\server\%JBOSS_SERVER% goto :nonexistantjbossserver

  if DEFINED CLEANSTART (
    if EXIST %ATG_SOLID_FILE% del /q /s /f %ATG_SOLID_FILE%
    if EXIST %ATG_APPS_FILE% del /q /s /f %ATG_APPS_FILE%
    if EXIST %EAR_FILE% del /q /s /f %EAR_FILE%
  )

  if NOT EXIST %ATG_SOLID_FILE% copy %DYNAMO_HOME%\..\DafEar\Tomcat\copy-to-jboss_home-your_server-deploy-dir\atg-solid-ds.xml %ATG_SOLID_FILE%
  if NOT EXIST %ATG_APPS_FILE% copy %DYNAMO_HOME%\..\DafEar\Tomcat\copy-to-jboss_home-your_server-deploy-dir\atg-apps-solid-ds.xml %ATG_APPS_FILE%
  goto :runassembler

:runassembler
  call bin\runAssembler.bat %ADDITIONAL_FLAGS% %SERVER_NAME% %LIVECONFIG% %EAR_FILE% %ADDITIONAL_LAYERS% -m %MODULE_LIST% %ADDITIONAL_MODULES%
  if ERRORLEVEL==0 goto :startjboss
  echo Errors encountered during ear file assembly.
  goto :endofscript        

:startjboss
  if "%DEBUG_ENABLED%"=="true" (
    set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=%JAVA_DEBUG_PORT%,server=y,suspend=n %JAVA_OPTS%
  )
  call %JBOSS_HOME%\bin\run.bat %JBOSS_SERVER_ARG% %JBOSS_BIND_ARG%
  goto :endofscript        

:jbosshomenotset
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :unknownjbosshome
  if "%1"=="" goto :unknownjbosshome
  goto :jbossdir
                
:unknownjbosshome
  echo ***        
  echo *** JBOSS_HOME is not defined. You must specify the JBOSS_HOME dir.
  echo ***
  goto :usage         

:nonexistantjbosshome
  echo ***        
  echo *** The JBOSS_HOME: %JBOSS_HOME% does not exist. 
  echo *** You must specify a valid path for the JBOSS_HOME dir.
  echo ***
  goto :usage         

:nonexistantserver
  echo ***        
  echo *** The server directory for server: %SERVER_NAME% does not exist. 
  echo *** You must specify a valid server.
  echo ***
  goto :usage         

:nonexistantjbossserver
  echo ***        
  echo *** The JBoss server directory for server: %JBOSS_SERVER% does not exist. 
  echo *** You must specify a valid JBoss server when using the -c option.
  echo ***
  goto :usage         

:emptyjbossserver
  echo ***        
  echo *** No JBoss server configuration specified.
  echo *** You must specify a valid server configuration 
  echo *** when using the -c option.
  echo ***
  goto :usage         
        
:emptyearname
  echo ***        
  echo *** No ear file name specified.
  echo *** You must specify an ear file name when using the -ear option.
  echo ***
  goto :usage         

:unknownoptionerror
  echo Unknown option: %1  
  goto :usage

:missingmodulelist
  echo No module list provided, ensure that%DYNAMO_HOME%\servers\%SERVER_NAME%\moduleList.properties.
  goto :usage
  
:usage
  echo *** startStoreOnJBOSS server-name [-clean] [-l] [-c server-config-name] [-ear ear-file-name] [-f additional-flag] [-j jboss-home-dir] [-layer config layers] [-debug -debugPort] [-m additional-module-list]
  echo ***
  echo *** server-name is the name of the Dynamo server to use.
  echo *** -clean Delete existing ear file and data source xml files.
  echo *** -l Run with the liveconfig config layer.
  echo *** -c The jboss server to use. Default value is atg.
  echo *** -ear The destination ear file name. Default is ATG.ear.
  echo *** -f Run additionally with the specified runAssembler flag.
  echo *** -j The location of the jboss installation directory.
  echo *** -layer Run additionally with the specified configuration layers
  echo *** -m Run additionally with the specified modules.
  echo *** -debug Run with Java remote debugging enabled on port 3000
  echo *** -debugPort Specifies the port for remote debugging
  goto :pauseexit

:pauseexit
  REM Don't pause if we're running as an NT service: there's no console 
  REM for the user to respond in!
  goto :endofscript
  pause


:endofscript
  if exist endofscript rmdir endofscript
  title Command Prompt
