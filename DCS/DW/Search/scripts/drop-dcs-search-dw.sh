#!/bin/sh

. search-dw-env.sh

# ********* Only drops the DCS Datawarehouse tables *********

. ./$SEARCH_DW_DB_TYPE ../DCS/DW/Search/sql/uninstall/$SEARCH_DW_DB_TYPE/drop_dcs_srch_ddl.sql

