@echo off

call .\search-dw-env.bat

rem ********* Only drops the ARF Datawarehouse tables *********

call %SEARCH_DW_DB_TYPE% ..\ARF\DW\base\sql\uninstall\%SEARCH_DW_DB_TYPE%\drop_arf_ddl.sql
