@echo off
setlocal

echo Creating tables...
db2 -t -v -o "connect to %3 user %1 using %2"

db2 -t -v -o -f../../../DAS/sql/install/db2/das_ddl.sql

db2 -t -v -o -f../../../DPS/sql/install/db2/dps_ddl.sql
db2 -t -v -o -f../../../DSS/sql/install/db2/dss_ddl.sql

db2 -t -v -o -f../../paf/sql/install/db2/profile_ddl.sql
db2 -t -v -o -f../../paf/sql/install/db2/portal_ddl.sql
db2 -t -v -o -f../../paf/sql/install/db2/alert_ddl.sql
db2 -t -v -o -f../../paf/sql/install/db2/paf_mappers_ddl.sql
db2 -t -v -o -f../../paf/sql/install/db2/membership_ddl.sql
db2 -t -v -o -f../../bookmarks/sql/install/db2/bookmarks_ddl.sql
db2 -t -v -o -f../../calendar/sql/install/db2/calendar_ddl.sql
db2 -t -v -o -f../../communities/sql/install/db2/communities_ddl.sql
db2 -t -v -o -f../../discussion/sql/install/db2/discussion_ddl.sql
db2 -t -v -o -f../../docexch/sql/install/db2/docexch_ddl.sql
db2 -t -v -o -f../../poll/sql/install/db2/poll_ddl.sql
db2 -t -v -o -f../../soapclient/sql/install/db2/soapclient_ddl.sql
db2 -t -v -o -f../../ppa/sql/install/db2/ppa_ddl.sql

db2 -t -v -o "commit;"


