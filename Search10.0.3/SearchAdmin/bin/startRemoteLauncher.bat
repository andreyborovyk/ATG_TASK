@echo off

if NOT DEFINED JAVA_VM goto :getfrompath
if NOT EXIST "%JAVA_VM%" goto :getfrompath
set L_JAVA_VM=%JAVA_VM%
goto :startprocess

:getfrompath
  if NOT DEFINED JAVA_HOME goto :assumeinpath
  if NOT EXIST "%JAVA_HOME%" goto :assumeinpath
  set L_JAVA_VM=%JAVA_HOME%\bin\java.exe
  if NOT EXIST "%L_JAVA_VM%" goto :assumeinpath
  goto :startprocess

:assumeinpath
  set L_JAVA_VM=java.exe
  goto :startprocess

:startprocess
  "%L_JAVA_VM%" -cp ..\common\lib\standalone-launcher.jar atg.search.routing.rmi.RemoteSearchEngineLaunchingService %1 %2 %3 %4 %5 %6

