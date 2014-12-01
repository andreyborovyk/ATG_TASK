@echo off
setlocal

REM drop all tables to reset them
echo Deleting tables

isql -U%1 -P%2 -S%3 -d%4 -i..\..\ppa\sql\uninstall\mssql\drop_ppa_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\soapclient\sql\uninstall\mssql\drop_soapclient_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\poll\sql\uninstall\mssql\drop_poll_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\docexch\sql\uninstall\mssql\drop_docexch_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\discussion\sql\uninstall\mssql\drop_discussion_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\communities\sql\uninstall\mssql\drop_communities_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\calendar\sql\uninstall\mssql\drop_calendar_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\bookmarks\sql\uninstall\mssql\drop_bookmarks_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\uninstall\mssql\drop_membership_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\uninstall\mssql\drop_paf_mappers_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\uninstall\mssql\drop_alert_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\uninstall\mssql\drop_portal_ddl.sql
isql -U%1 -P%2 -S%3 -d%4 -i..\..\paf\sql\uninstall\mssql\drop_profile_ddl.sql

REM These lines are commented out for safety.
REM If you really want to delete all ATG tables, not just
REM the ones for Portal, then uncomment these lines.
REM isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DSS\sql\install\mssql\drop_dss_ddl.sql
REM isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DPS\sql\install\mssql\drop_dps_ddl.sql

REM isql -U%1 -P%2 -S%3 -d%4 -i..\..\..\DAS\sql\install\mssql\drop_das_ddl.sql

pause
