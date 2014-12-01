@echo off
setlocal

REM Create tables
echo Creating tables...
isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DAS\sql\install\mssql\das_ddl.sql

isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DPS\sql\install\mssql\dps_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DSS\sql\install\mssql\dss_ddl.sql

isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\install\mssql\portal_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\install\mssql\profile_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\install\mssql\alert_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\install\mssql\paf_mappers_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\install\mssql\membership_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\bookmarks\sql\install\mssql\bookmarks_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\calendar\sql\install\mssql\calendar_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\communities\sql\install\mssql\communities_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\discussion\sql\install\mssql\discussion_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\docexch\sql\install\mssql\docexch_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\poll\sql\install\mssql\poll_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\soapclient\sql\install\mssql\soapclient_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\ppa\sql\install\mssql\ppa_ddl.sql


REM Import the minimal data needed by Portals.  This data will allow you into admin,
REM where you can then begin building your communities, adding gears, etc.
REM the strange path for appsdata.xml is due to startSQLRepository running from ..\home
echo Importing data...
call ..\..\..\home\bin\startSQLRepository.bat -m Portal.paf -database mssql -repository \atg\portal\framework\PortalRepository -import ..\Portal\install\minimal-data.xml
pause

