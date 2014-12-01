@echo off
setlocal

::
:: Ensure that DYNAMO_HOME is set.
::
set PROGRAM_DIR=%~dp0

call :setowd foo

rem echo OWD is %OWD%


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
if NOT DEFINED JAVA_VM set JAVA_VM=%ATGJRE%


"%JAVA_VM%" -Xms96m -Xmx160m  -cp "jbossmigration.jar" atg.jbossmigration.JBossMigrationMain "%OWD%\" %1 %2 %3 %4 %5 %6 %7

:setowd
  REM Call setowd with 1 argument (like "foo") to set OWD to the current
  REM working directory
  REM
  SET OWD=%~dp1
  goto :eof

:endofscript

endlocal
