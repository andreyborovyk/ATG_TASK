#!/bin/sh

. search-dw-env.sh

# ********* Only drops the Search Datawarehouse tables *********

. ./$SEARCH_DW_DB_TYPE ../ARF/DW/Search/sql/uninstall/$SEARCH_DW_DB_TYPE/drop_search_datawarehouse_ddl.sql

