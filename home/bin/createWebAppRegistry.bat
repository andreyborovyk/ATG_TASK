@echo off
setlocal

REM Script to create WebAppRegistry property files
REM Usage: createWebAppRegistry file.{dar,ear,war} 
REM Creates property files in config/atg/registry/webappregistry
REM Example: createWebAppRegistry paf.dar
REM Example: createWebAppRegistry qiuncy.ear
REM Example: createWebAppRegistry QuincyFunds

REM Set defaults...
set ARGS=%*

if !%1 ==! goto :usage

set DYNAMO_HOME=%~dp0..

set MAINCLASS=atg.service.webappregistry.FileSystemWebAppRegistry

set DYNAMO_BASE=true

call %DYNAMO_HOME%\bin\dynamoEnv.bat

%JAVA_VM% %JAVA_ARGS% %MAINCLASS% %ARGS% -cl

goto :endofscript

:usage

  echo Usage: createWebAppRegistry [-cl] file.{dar,ear,war} or WebAppDirectory
  echo  Creates the Dynamo config tree for a StaticWebAppRegistry
  echo  in config/atg/registry/webappregistry
  echo  Where -cl = create a display-name=context-root map only
  echo  default is to create WebApp property files
  echo   examples:
  echo      createWebAppRegistry -cl paf.dar
  echo      createWebAppRegistry quincy.ear
  echo      createWebAppRegistry QuincyFunds
        
  goto :endofscript  	

:endofscript
endlocal
