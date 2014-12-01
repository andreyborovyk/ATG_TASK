#!/bin/sh -x

. search-dw-env.sh

# ********* Only creates the DCS Search Datawarehouse tables - assumes ARF/DW and DCS/DW tables were already created

. ./$SEARCH_DW_DB_TYPE ../DCS/DW/Search/sql/db_components/$SEARCH_DW_DB_TYPE/dcs_srch_ddl.sql