@echo off

call .\search-dw-env.bat

rem ********* Only drops the DCS Search Datawarehouse tables *********

call %SEARCH_DW_DB_TYPE% ..\DCS\DW\Search\sql\uninstall\%SEARCH_DW_DB_TYPE%\drop_dcs_srch_ddl.sql
