@echo off
setlocal

REM drop all tables to reset them
echo Deleting and recreating tables...
db2 -t -v -o "connect to %3 user %1 using %2"

db2 -t -v -o -f../../ppa/sql/uninstall/db2/drop_ppa_ddl.sql
db2 -t -v -o -f../../soapclient/sql/uninstall/db2/drop_soapclient_ddl.sql
db2 -t -v -o -f../../poll/sql/uninstall/db2/drop_poll_ddl.sql
db2 -t -v -o -f../../docexch/sql/uninstall/db2/drop_docexch_ddl.sql
db2 -t -v -o -f../../discussion/sql/uninstall/db2/drop_discussion_ddl.sql
db2 -t -v -o -f../../communities/sql/uninstall/db2/drop_communities_ddl.sql
db2 -t -v -o -f../../calendar/sql/uninstall/db2/drop_calendar_ddl.sql
db2 -t -v -o -f../../bookmarks/sql/uninstall/db2/drop_bookmarks_ddl.sql
db2 -t -v -o -f../../paf/sql/uninstall/db2/drop_membership_ddl.sql
db2 -t -v -o -f../../paf/sql/uninstall/db2/drop_paf_mappers_ddl.sql
db2 -t -v -o -f../../paf/sql/uninstall/db2/drop_alert_ddl.sql
db2 -t -v -o -f../../paf/sql/uninstall/db2/drop_portal_ddl.sql
db2 -t -v -o -f../../paf/sql/uninstall/db2/drop_profile_ddl.sql

REM These lines are commented out for safety.
REM If you really want to delete all ATG tables, not just
REM the tables for Portals, uncomment them.
REM db2 -t -v -o -f../../../DSS/sql/install/db2/drop_dss_ddl.sql
REM db2 -t -v -o -f../../../DPS/sql/install/db2/drop_dps_ddl.sql

REM db2 -t -v -o -f../../../DAS/sql/install/db2/drop_das_ddl.sql


db2 -t -v -o "commit;"


