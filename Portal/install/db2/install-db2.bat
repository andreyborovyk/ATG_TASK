@echo off
setlocal

REM create the tables
db2cmd -c -w db2cmds.bat %1 %2 %3

REM Import the minimal data needed by Portals.  This data will allow you into admin,
REM where you can then begin building your communities, adding gears, etc.
REM the strange path for minimal-data.xml is due to startSQLRepository running from ..\home
echo Importing data...
call ..\..\..\home\bin\startSQLRepository.bat -m Portal.paf -database db2 -repository /atg/portal/framework/PortalRepository -import ..\Portal\install\minimal-data.xml
pause

