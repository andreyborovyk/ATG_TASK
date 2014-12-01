#!/bin/sh

. search-dw-env.sh

# ********* Only drops the DCS Datawarehouse tables *********

. ./$SEARCH_DW_DB_TYPE ../DCS/DW/sql/uninstall/$SEARCH_DW_DB_TYPE/drop_arf_dcs_ddl.sql

