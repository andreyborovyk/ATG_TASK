rem ********* Defines variables needed by the scripts that create the datawarehouse schema
rem ********* Set the following environment variables to override the default values defined here.

REM 
REM Convert forward slashes to backslashes in DYNAMO_HOME before trying to
REM cd there, just in case the user is running Cygwin or something like it 
REM and using Unix-style paths on a Windows machine.
REM 
set DYNAMO_HOME=%DYNAMO_HOME:/=\%

rem ********* Check DYNAMO_HOME Environment variable
if EXIST %DYNAMO_HOME%\META-INF\MANIFEST.MF goto :homeok
  echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
  exit
:homeok

rem ********* Search version
if "%SEARCH_VERSION%" == "" set SEARCH_VERSION=@PRODUCT_VERSION@

rem ********* Possible values for SEARCH_DW_DB_TYPE are: "db2", "mssql", "mysql", "oracle"
if "%SEARCH_DW_DB_TYPE%" == "" set SEARCH_DW_DB_TYPE=mysql
echo SEARCH_DW_DB_TYPE is %SEARCH_DW_DB_TYPE%

rem ********* Common parameters
if "%SEARCH_DW_DB_USER%" == "" set SEARCH_DW_DB_USER=admin
if "%SEARCH_DW_DB_PWD%" == "" set SEARCH_DW_DB_PWD=admin

rem ********* MS SQL specific parameters
if "%SEARCH_DW_MSSQL_DB_NAME%" == "" set SEARCH_DW_MSSQL_DB_NAME=admin
if "%SEARCH_DW_MSSQL_DB_HOST%" == "" set SEARCH_DW_MSSQL_DB_HOST=localhost

rem ********* Oracle specific parameters
if "%SEARCH_DW_ORACLE_TNS%" == "" set SEARCH_DW_ORACLE_TNS=EXAMPLE@TEST.COM

rem ********* MySQL specific parameters
if "%SEARCH_DW_MYSQL_DB_NAME%" == "" set SEARCH_DW_MYSQL_DB_NAME=SearchAdmin
if "%SEARCH_DW_MYSQL_DB_HOST%" == "" set SEARCH_DW_MYSQL_DB_HOST=localhost

rem ********* DB2 specific parameters
if "%SEARCH_DW_DB2_DB_NAME%" == "" set SEARCH_DW_DB2_DB_NAME=SearchAdmin

