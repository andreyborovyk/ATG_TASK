@echo off

call .\search-dw-env.bat

rem ********* Only drops the DCS Datawarehouse tables *********

call %SEARCH_DW_DB_TYPE% ..\DCS\DW\sql\uninstall\%SEARCH_DW_DB_TYPE%\drop_arf_dcs_ddl.sql
