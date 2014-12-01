@echo off

call .\search-dw-env.bat

rem ********* Only drops the Search Datawarehouse tables *********

call %SEARCH_DW_DB_TYPE% ..\ARF\DW\Search\sql\uninstall\%SEARCH_DW_DB_TYPE%\drop_search_datawarehouse_ddl.sql
