#!/bin/sh

. search-dw-env.sh

# ********* Only drops the ARF Datawarehouse tables *********

. ./$SEARCH_DW_DB_TYPE ../ARF/DW/base/sql/uninstall/$SEARCH_DW_DB_TYPE/drop_arf_ddl.sql
