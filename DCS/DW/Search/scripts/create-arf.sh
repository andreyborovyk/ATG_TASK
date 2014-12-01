#!/bin/sh -x

. search-dw-env.sh

# ********* Only creates the ARF Datawarehouse tables *********

. ./$SEARCH_DW_DB_TYPE ../ARF/DW/base/sql/db_components/$SEARCH_DW_DB_TYPE/arf_ddl.sql
. ./$SEARCH_DW_DB_TYPE ../ARF/DW/base/sql/db_components/$SEARCH_DW_DB_TYPE/arf_init.sql
