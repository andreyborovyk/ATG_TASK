@echo off
setlocal

title ATG 10.0.3 on JBOSS

goto :startscript

:startscript
  set ADDITIONAL_FLAGS=
  set SERVER_NAME=
  set LIVECONFIG=
  set EARFILE_NAME=ATG.ear
  set JBOSS_SERVER=atg
  set JBOSS_SERVER_ARG=-c %JBOSS_SERVER%
  set JBOSS_BIND_ARG=-b 0.0.0.0
  set JAVA_DEBUG_PORT=3000
  set PROGRAM_DIR=%~dp0
  call %PROGRAM_DIR%\checkDynamoHome.bat
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
  if "%1"=="-jprofiler" goto :enableJProfiler
  if "%1"=="-jbossBinding" goto :jbossBinding
  if "%1"=="-disableSessionIdCheck" goto :disableSessionIdCheck
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
  echo %DYNAMO_HOME%\servers\%SERVER_NAME%
  if NOT EXIST %DYNAMO_HOME%\servers\%SERVER_NAME% goto :nonexistantserver
  set SERVER_NAME=-server %SERVER_NAME%
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

:disableSessionIdCheck
  set DISABLE_SESSION_ID_CHECK=true
  goto :parseswitchdone        

:enableJProfiler
  set JPROFILER_ENABLED=true
  goto :parseswitchdone

:jbossBinding
  shift
  set JAVA_OPTS=-Djboss.service.binding.set=%1 %JAVA_OPTS%
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
  goto :removesoliddatasources    

:removesoliddatasources
  set ATG_SOLID_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-solid-ds.xml
  set ATG_SOLID_APPS_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-apps-solid-ds.xml

  if EXIST %ATG_SOLID_FILE% del /q /s /f %ATG_SOLID_FILE%
  if EXIST %ATG_SOLID_APPS_FILE% del /q /s /f %ATG_SOLID_APPS_FILE%
  goto :copydatasources         

:copydatasources
  set ATG_MYSQL_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-mysql-ds.xml
  set ATG_APPS_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\atg-apps-mysql-ds.xml
  set EAR_FILE=%JBOSS_HOME%\server\%JBOSS_SERVER%\deploy\%EARFILE_NAME%

  if NOT EXIST %JBOSS_HOME%\server\%JBOSS_SERVER% goto :nonexistantjbossserver

  if DEFINED CLEANSTART (
    if EXIST %ATG_MYSQL_FILE% del /q /s /f %ATG_MYSQL_FILE%
    if EXIST %ATG_APPS_FILE% del /q /s /f %ATG_APPS_FILE%
    if EXIST %EAR_FILE% del /q /s /f %EAR_FILE%
  )

  if NOT EXIST %ATG_MYSQL_FILE% copy %DYNAMO_HOME%\..\DafEar\JBoss\copy-to-jboss_home-your_server-deploy-dir\atg-mysql-ds.xml %ATG_MYSQL_FILE%
  if NOT EXIST %ATG_APPS_FILE% copy %DYNAMO_HOME%\..\DafEar\JBoss\copy-to-jboss_home-your_server-deploy-dir\atg-apps-mysql-ds.xml %ATG_APPS_FILE%
  goto :runassembler

:runassembler
  call bin\runAssembler.bat %ADDITIONAL_FLAGS% %SERVER_NAME% %LIVECONFIG% %EAR_FILE% %ADDITIONAL_LAYERS% -m DafEar.Admin %ADDITIONAL_MODULES%
  if %ERRORLEVEL% == 0 goto :startjboss
  echo Errors encountered during ear file assembly.
  goto :endofscript        

:startjboss
  if "%DISABLE_SESSION_ID_CHECK%"=="true" (
    set JAVA_OPTS=%JAVA_OPTS% -Dorg.apache.catalina.connector.Request.SESSION_ID_CHECK="false"
  )
  if "%DEBUG_ENABLED%"=="true" (
    set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=%JAVA_DEBUG_PORT%,server=y,suspend=n %JAVA_OPTS%
  )
  if "%JPROFILER_ENABLED%"=="true" (
    call %JBOSS_HOME%\bin\run_jprofiler.bat %JBOSS_SERVER_ARG% %JBOSS_BIND_ARG%
  ) else (
    echo +++ %JBOSS_HOME%\bin\run.bat %JBOSS_SERVER_ARG% %JBOSS_BIND_ARG%
  call %JBOSS_HOME%\bin\run.bat %JBOSS_SERVER_ARG% %JBOSS_BIND_ARG%
  )
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

:usage
  echo *** startDynamoOnJBOSS [server-name] [-clean] [-l] [-c server-config-name] [-ear ear-file-name] [-f additional-flag] [-j jboss-home-dir] [-layer config layers] [-debug -debugPort] [-m additional-module-list] [-jbossBinding]
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
  echo *** -jbossBinding Sets jboss service bindings to a set of ports (e.g. ports-01).
  echo *** -disableSessionIdCheck allow server to create new sessions based on session id in passed in cookie
  goto :pauseexit

:pauseexit
  REM Don't pause if we're running as an NT service: there's no console 
  REM for the user to respond in!
  goto :endofscript
  pause


:endofscript
  if exist endofscript rmdir endofscript
  title Command Prompt
