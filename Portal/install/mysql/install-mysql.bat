@echo off
setlocal

REM Create tables
echo connect to %3 user %1 using %2
echo Creating tables...

mysql -u%1 -p%2 %3 < ..\..\..\DAS\sql\install\mysql\das_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\..\DPS\sql\install\mysql\dps_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\..\DSS\sql\install\mysql\dss_ddl.sql

mysql -u%1 -p%2 %3 < ..\..\paf\sql\install\mysql\portal_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\paf\sql\install\mysql\profile_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\paf\sql\install\mysql\alert_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\paf\sql\install\mysql\paf_mappers_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\paf\sql\install\mysql\membership_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\bookmarks\sql\install\mysql\bookmarks_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\calendar\sql\install\mysql\calendar_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\communities\sql\install\mysql\communities_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\discussion\sql\install\mysql\discussion_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\docexch\sql\install\mysql\docexch_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\poll\sql\install\mysql\poll_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\soapclient\sql\install\mysql\soapclient_ddl.sql
mysql -u%1 -p%2 %3 < ..\..\ppa\sql\install\mysql\ppa_ddl.sql



REM Import the minimal data needed by Portals.  This data will allow you into admin,
REM where you can then begin building your communities, adding gears, etc.
REM the strange path for appsdata.xml is due to startSQLRepository running from ..\home
echo Importing data...
call ..\..\..\home\bin\startSQLRepository.bat -m Portal.paf -database mysql -repository \atg\portal\framework\PortalRepository -import ..\Portal\install\minimal-data.xml
pause

