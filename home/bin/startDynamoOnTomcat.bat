@echo off
setlocal

title ATG 10.0 on Tomcat

goto :startscript

:startscript
  set ADDITIONAL_FLAGS=
  set SERVER_NAME=
  set LIVECONFIG=
  set BASEFILE_NAME=
  set ADDITIONAL_MODULES=
  set CLEANSTART=
  set TOMCAT_SERVER=atgdefault
  set DEBUG_FLAG=
  set PROGRAM_DIR=%~dp0
  set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MaxPermSize=128m -XX:MaxNewSize=128m %JAVA_OPTS%
  call %PROGRAM_DIR%\checkDynamoHome.bat
  if "%CHECK_DYNAMO_HOME%" == "fail" goto :pauseexit

  rem Provide a default value for CATALINA_HOME.
  if not defined CATALINA_HOME set CATALINA_HOME=%DYNAMO_HOME%\..\Tomcat\apache-tomcat-6.0.26
        
  set DAS_ENV=%DYNAMO_HOME%\localconfig\dasEnv.bat
  if EXIST %DAS_ENV% goto :dasenvset
  goto :parseswitchfirst        

:dasenvset
  call %DAS_ENV%
  goto :parseswitchfirst


:parseswitchfirst
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  goto :parseswitch

:parseswitch
  if "%1"=="-help" goto :usage 
  if "%1"=="-l" goto :liveconfig
  if "%1"=="-m" goto :modules
  if "%1"=="-t" goto :tomcatdir
  if "%1"=="-clean" goto :cleanstart
  if "%1"=="-base" goto :basename
  if "%1"=="-s" goto :serverconfig
  if "%1"=="-f" goto :addflag
  if "%1"=="-layer" goto :firstlayer
  if "%1"=="-debug" goto :enableDebug
  if "%1"=="-debugPort" goto :debugPort
  if "%FIRSTCHAR%"=="-" goto :unknownoptionerror
  goto :usage

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

:tomcatdir
  shift
  set CATALINA_HOME=%1
  goto :parseswitchdone

:cleanstart
  set CLEANSTART=-tomcat-clean
  if NOT DEFINED DYNAMO_HOME GOTO :parseswitchdone
  if EXIST %DYNAMO_HOME%\tm.out.lck DEL "%DYNAMO_HOME%\tm.out.lck"
  if EXIST %DYNAMO_HOME%\tm.out DEL "%DYNAMO_HOME%\tm.out"
  for %%F in (%DYNAMO_HOME%\*.epoch) do DEL "%%F"
  for %%F in (%DYNAMO_HOME%\tmlog*.log) do DEL "%%F"
  goto :parseswitchdone

:basename
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :emptyearname
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :emptyearname
  if "%NEXT_FIRSTCHAR%"=="-" goto :emptyearname
  set BASEFILE_NAME=%1
  goto :parseswitchdone

:serverconfig
  shift
  set NEXT_FIRSTCHAR=%1
  if NOT DEFINED NEXT_FIRSTCHAR goto :emptyatgserver
  set NEXT_FIRSTCHAR=%NEXT_FIRSTCHAR:~0,1%
  if "%NEXT_FIRSTCHAR%"=="" goto :emptyatgserver
  if "%NEXT_FIRSTCHAR%"=="-" goto :emptyatgserver
  set SERVER_NAME=%1
  echo %DYNAMO_HOME%\servers\%SERVER_NAME%
  if NOT EXIST %DYNAMO_HOME%\servers\%SERVER_NAME% goto :nonexistantserver
  set TOMCAT_SERVER=%SERVER_NAME%
  set SERVER_NAME=-server %SERVER_NAME%
  goto parseswitchdone


:addflag
  shift
  set ADDITIONAL_FLAGS=%1 %ADDITIONAL_FLAGS%
  goto :parseswitchdone

:enableDebug
  set DEBUG_FLAG=jpda
  goto :parseswitchdone

:debugPort 
  shift
  set JPDA_ADDRESS=%1
  goto :parseswitchdone

:parseswitchdone
  shift
  if "%1"=="" goto :parsedone
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  goto :parseswitch

:parsedone
  rem check to make sure that the tomcat home is set
  if NOT DEFINED CATALINA_HOME goto :unknowntomcathome
  if NOT EXIST %CATALINA_HOME% goto :nonexistanttomcathome
  goto :copydatasources        

:copydatasources
  echo copydatasources

  if EXIST "%CATALINA_HOME%\lib\mysql-connector-java-5.1.15-bin.jar" goto :aftercopy

  REM Ah, if only set statments worked in multiline ifs.
  SET MYSQL_SRC=%DYNAMO_HOME%\..\DAS\mysql\mysql-connector-java-5.1.15-bin.jar
  if NOT EXIST "%MYSQL_SRC%" SET MYSQL_SRC=%DYNAMO_HOME%\..\..\devtools\lib\mysql-connector-java-5.1.15-bin.jar
  echo "MYSQL_SRC %MYSQL_SRC%"
  if EXIST "%MYSQL_SRC%" (
     copy "%MYSQL_SRC%" "%CATALINA_HOME%/lib/mysql-connector-java-5.1.15-bin.jar"
  )

:aftercopy
  if "%BASEFILE_NAME%" == "" set CATALINA_BASE=%CATALINA_HOME%\atgbases\%TOMCAT_SERVER%
  if not "%BASEFILE_NAME%" == "" set CATALINA_BASE=%BASEFILE_NAME%

:runassembler
  call bin\runAssembler.bat -tomcat %BASEFILE_NAME% %ADDITIONAL_FLAGS% -tomcat-initial-resources-file "%DYNAMO_HOME%\..\DafEar\Tomcat\mysql-resources\context-btm.xml" %SERVER_NAME% %LIVECONFIG% %EAR_FILE% %ADDITIONAL_LAYERS% %CLEANSTART% -m DafEar.Admin %ADDITIONAL_MODULES%
  if %ERRORLEVEL% == 0 goto :starttomcat
  echo Errors encountered during ear file assembly.
  goto :endofscript        

:starttomcat
  
  if NOT EXIST "%CATALINA_BASE%\conf\resources.properties" copy "%DYNAMO_HOME%\..\DafEar\Tomcat\mysql-resources\btm-resources.properties" "%CATALINA_BASE%\conf\resources.properties"

  echo +++ %CATALINA_HOME%\bin\catalina.bat %DEBUG_FLAG% run
  call %CATALINA_HOME%\bin\catalina.bat %DEBUG_FLAG% run
  goto :endofscript        

:tomcathomenotset
  set FIRSTCHAR=%1
  set FIRSTCHAR=%FIRSTCHAR:~0,1%
  if "%FIRSTCHAR%"=="-" goto :unknowntomcathome
  if "%1"=="" goto :unknowntomcathome
  goto :tomcatdir
                
:unknowntomcathome
  echo ***        
  echo *** CATALINA_HOME is not defined. You must specify the CATALINA_HOME dir.
  echo ***
  goto :usage         

:nonexistanttomcathome
  echo ***        
  echo *** The CATALINA_HOME: %CATALINA_HOME% does not exist. 
  echo *** You must specify a valid path for the CATALINA_HOME dir.
  echo ***
  goto :usage         

:nonexistantserver
  echo ***        
  echo *** The server directory for server: %SERVER_NAME% does not exist. 
  echo *** You must specify a valid server.
  echo ***
  goto :usage         


:emptyatgserver
  echo ***        
  echo *** No ATG server configuration name specified.
  echo *** You must specify a valid ATG server name
  echo *** when using the -s option.
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
  echo *** startDynamoOnTomcat  [-s atg-server-name] [-clean] [-l] [-base tomcat-base-dir] [-f additional-flag] [-t tomcat-home-dir] [-layer config layers] [-debug -debugPort] [-m additional-module-list]
  echo ***
  echo *** server-name is the name of the Dynamo server to use.
  echo *** -clean Delete existing ear file and data source xml files.
  echo *** -l Run with the liveconfig config layer.
  echo *** -s The ATG server to use.
  echo *** -base The destination Tomcat base directory name. Default is 
  echo *** -     CATALINA_HOME\atgbases\{ server | atgdefault }
  echo *** -f Run additionally with the specified runAssembler flag.
  echo *** -t The location of the Tomcat installation (home) directory.
  echo *** -layer Run additionally with the specified configuration layers
  echo *** -m Run additionally with the specified modules.
  echo *** -debug Run with Java remote debugging enabled 
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
