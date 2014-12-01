#!/bin/sh -x

. search-dw-env.sh

# ********* Only creates the Search Datawarehouse tables - assumes ARF/DW tables were created when the platform tables were created

. ./$SEARCH_DW_DB_TYPE ../ARF/DW/Search/sql/db_components/$SEARCH_DW_DB_TYPE/search_datawarehouse_ddl.sql
. ./$SEARCH_DW_DB_TYPE ../ARF/DW/Search/sql/db_components/$SEARCH_DW_DB_TYPE/search_datawarehouse_init.sql
