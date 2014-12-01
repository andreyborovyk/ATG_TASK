::
:: This script verifies that DYNAMO_HOME and DAS_ROOT are set.  If either
:: is not set, it attempts to find its value.
::

:: set return value
set CHECK_DYNAMO_HOME=pass

::
:: Allow us to be started from anywhere
:: %~dp0 is (d)rive and (p)ath of current script (the 0) under NT/2000
::
set _SCRIPT_DIR=%~dp0

::
:: If DYNAMO_HOME is set by the user, attempt to use that value.  If it is
:: not valid, or not set at all, try to use the working directory.
::
if DEFINED DYNAMO_HOME goto :checkhome

if NOT "%CHECK_DYNAMO_HOME_DEBUG%" == "true" goto :findhome
echo ** DYNAMO_HOME is not set.
echo ** Attempting to locate a valid DYNAMO_HOME based on your working directory
echo **

goto :findhome

:: 
:: Convert forward slashes to backslashes in DYNAMO_HOME before trying to
:: cd there, just in case the user is running Cygwin or something like it 
:: and using Unix-style paths on a Windows machine.
:: 

:checkhome
  set DYNAMO_HOME=%DYNAMO_HOME:/=\%
  if EXIST %DYNAMO_HOME%\bin\dafEnv.bat goto :homedone

  echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
  echo ** Attempting to locate a valid DYNAMO_HOME based on your working directory
  echo **

::
:: Try to find DYNAMO_HOME by first looking in the current
:: directory, then the parent directory, then the script directory.
::
:: If it cannot find it tells the user and then exits the scripts.
::
:: If it does find it tells the user what it thinks DYNAMO_HOME is.
::
:findhome
  set DYNAMO_HOME=%~dp0..
  if EXIST %DYNAMO_HOME%\bin\dafEnv.bat goto :foundhome

  echo ** You can run this script from the from the home\bin subdirectory of the base 
  echo ** directory where you installed Dynamo (for example, C:\ATG\ATG10.0.3\home\bin).
  echo ** Alternatively, if you set the DYNAMO_HOME system environment variable to
  echo ** point to the home subdirectory, you can run this script from any directory.
  echo ** For information about setting DYNAMO_HOME, see the Dynamo Administration Guide.
  set CHECK_DYNAMO_HOME=fail
  goto :endofscript

:foundhome
  if NOT "%CHECK_DYNAMO_HOME_DEBUG%" == "true" goto :homedone
  echo ** Valid DYNAMO_HOME was located: "%DYNAMO_HOME%"
  echo **

::
:: Change to DYNAMO_HOME directory and then set DYNAMO_HOME to "."
::
:homedone
  cd /d %DYNAMO_HOME%
  set DYNAMO_HOME=.

::
:: If DAS_ROOT is not set by the user, attempt to find its value from
:: dasEnv.sh.  If it is not there and the user is not running a third
:: party app server then warn the user.
::
:finddasroot
  if DEFINED DAS_ROOT goto :endofscript
  if NOT EXIST %DYNAMO_HOME%\localconfig\dasEnv.bat goto :dasrooterror
  call %DYNAMO_HOME%\localconfig\dasEnv.bat
  if DEFINED DAS_ROOT goto :endofscript 
  if not defined CATALINA_HOME set CATALINA_HOME=%DYNAMO_HOME%\..\Tomcat\apache-tomcat-6.0.26
  	
  if DEFINED CATALINA_HOME set THIRD_PARTY_APP_SERVER=tomcat
  if DEFINED WL_HOME set THIRD_PARTY_APP_SERVER=weblogic
  if DEFINED WAS_HOME set THIRD_PARTY_APP_SERVER=websphere
  if DEFINED JBOSS_HOME set THIRD_PARTY_APP_SERVER=jboss
  goto :endofscript

:dasrooterror
  if NOT "%CHECK_DYNAMO_HOME_DEBUG%" == "true" goto :endofscript
  if NOT DEFINED THIRD_PARTY_APP_SERVER (
    echo ** WARNING: DAS_ROOT is not set.  If you are using the Dynamo Application
    echo ** Server then you must set the environment variable DAS_ROOT to point to
    echo ** the Dynamo Application Server installation directory.
    echo **
  )

:endofscript
  if not defined CATALINA_HOME set CATALINA_HOME=%DYNAMO_HOME%\..\Tomcat\apache-tomcat-6.0.26

  if DEFINED CATALINA_HOME set THIRD_PARTY_APP_SERVER=tomcat
  if DEFINED WL_HOME set THIRD_PARTY_APP_SERVER=weblogic
  if DEFINED WAS_HOME set THIRD_PARTY_APP_SERVER=websphere
  if DEFINED JBOSS_HOME set THIRD_PARTY_APP_SERVER=jboss
  
