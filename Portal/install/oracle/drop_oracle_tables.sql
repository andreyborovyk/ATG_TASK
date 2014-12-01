
@../../ppa/sql/uninstall/oracle/drop_ppa_ddl.sql
@../../soapclient/sql/uninstall/oracle/drop_soapclient_ddl.sql
@../../poll/sql/uninstall/oracle/drop_poll_ddl.sql
@../../docexch/sql/uninstall/oracle/drop_docexch_ddl.sql
@../../discussion/sql/uninstall/oracle/drop_discussion_ddl.sql
@../../communities/sql/uninstall/oracle/drop_communities_ddl.sql
@../../calendar/sql/uninstall/oracle/drop_calendar_ddl.sql
@../../bookmarks/sql/uninstall/oracle/drop_bookmarks_ddl.sql
@../../paf/sql/uninstall/oracle/drop_membership_ddl.sql
@../../paf/sql/uninstall/oracle/drop_paf_mappers_ddl.sql
@../../paf/sql/uninstall/oracle/drop_alert_ddl.sql
@../../paf/sql/uninstall/oracle/drop_portal_ddl.sql
@../../paf/sql/uninstall/oracle/drop_profile_ddl.sql

-- These lines are commented out for safety
-- If you actually want to remove all Dynamo related
-- tables, not just the portal ones, uncomment these lines.
-- @../../../DSS/sql/install/oracle/drop_dss_ddl.sql
-- @../../../DPS/sql/install/oracle/drop_dps_ddl.sql

-- @../../../DAS/sql/install/oracle/drop_das_ddl.sql

commit;
exit
