#!/bin/sh -x

. search-dw-env.sh

# ********* Only creates the ARF Datawarehouse tables - assumes ARF/DW tables were already created

. ./$SEARCH_DW_DB_TYPE ../DCS/DW/sql/db_components/$SEARCH_DW_DB_TYPE/arf_dcs_ddl.sql
. ./$SEARCH_DW_DB_TYPE ../DCS/DW/sql/db_components/$SEARCH_DW_DB_TYPE/arf_dcs_init.sql
