@echo off

call .\search-dw-env.bat

rem ********* Drops the Search Datawarehouse tables *********
rem ********* Drops the ARF Datawarehouse tables *********

call .\drop-search-dw.bat
call .\drop-arf.bat
