@echo off

call .\search-dw-env.bat

rem ********* Only creates the DCS Datawarehouse tables - assumes ARF/DW tables were already created

call %SEARCH_DW_DB_TYPE% ..\DCS\DW\sql\db_components\%SEARCH_DW_DB_TYPE%\arf_dcs_ddl.sql
call %SEARCH_DW_DB_TYPE% ..\DCS\DW\sql\db_components\%SEARCH_DW_DB_TYPE%\arf_dcs_init.sql
