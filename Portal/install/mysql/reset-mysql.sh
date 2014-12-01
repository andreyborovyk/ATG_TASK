#!/bin/sh
# drop all tables to reset them
echo connect to $3 user $1 using $2
echo Deleting data and dropping portal tables...

mysql -u$1 -p$2 $3 < ../../ppa/sql/uninstall/mysql/drop_ppa_ddl.sql
mysql -u$1 -p$2 $3 < ../../soapclient/sql/uninstall/mysql/drop_soapclient_ddl.sql
mysql -u$1 -p$2 $3 < ../../poll/sql/uninstall/mysql/drop_poll_ddl.sql
mysql -u$1 -p$2 $3 < ../../docexch/sql/uninstall/mysql/drop_docexch_ddl.sql
mysql -u$1 -p$2 $3 < ../../discussion/sql/uninstall/mysql/drop_discussion_ddl.sql
mysql -u$1 -p$2 $3 < ../../communities/sql/uninstall/mysql/drop_communities_ddl.sql
mysql -u$1 -p$2 $3 < ../../calendar/sql/uninstall/mysql/drop_calendar_ddl.sql
mysql -u$1 -p$2 $3 < ../../bookmarks/sql/uninstall/mysql/drop_bookmarks_ddl.sql
mysql -u$1 -p$2 $3 < ../../paf/sql/uninstall/mysql/drop_membership_ddl.sql
mysql -u$1 -p$2 $3 < ../../paf/sql/uninstall/mysql/drop_paf_mappers_ddl.sql
mysql -u$1 -p$2 $3 < ../../paf/sql/uninstall/mysql/drop_alert_ddl.sql
mysql -u$1 -p$2 $3 < ../../paf/sql/uninstall/mysql/drop_portal_ddl.sql
mysql -u$1 -p$2 $3 < ../../paf/sql/uninstall/mysql/drop_profile_ddl.sql

# These lines are commented out for safety.
# If you really want to erase all ATG tables, not just the ones
# for Portals, then uncomment these lines.
# mysql -u$1 -p$2 $3 < ../../../DSS/sql/install/mysql/drop_dss_ddl.sql
# mysql -u$1 -p$2 $3 < ../../../DPS/sql/install/mysql/drop_dps_ddl.sql

# mysql -u$1 -p$2 $3 < ../../../DAS/sql/install/mysql/drop_das_ddl.sql

