@echo off

call .\search-dw-env.bat

rem ********* Only creates the ARF Datawarehouse tables *********

call %SEARCH_DW_DB_TYPE% ..\ARF\DW\base\sql\db_components\%SEARCH_DW_DB_TYPE%\arf_ddl.sql
call %SEARCH_DW_DB_TYPE% ..\ARF\DW\base\sql\db_components\%SEARCH_DW_DB_TYPE%\arf_init.sql
