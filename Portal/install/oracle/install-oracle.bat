@echo off
setlocal

REM Create the tables
echo Creating tables...
sqlplus %1/%2@%3 @.\oracle_tables.sql

REM Import the minimal data needed by Portals.  This data will allow you into admin,
REM where you can then begin building your communities, adding gears, etc.
REM the strange path for minimal-data.xml is due to startSQLRepository running from ..\home
echo Importing data...
call ..\..\..\home\bin\startSQLRepository.bat -m Portal.paf -database oracle -repository /atg/portal/framework/PortalRepository -import ..\Portal\install\minimal-data.xml
pause

