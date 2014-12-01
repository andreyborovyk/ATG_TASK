@echo off

call .\search-dw-env.bat

rem ********* Drops the DCS Search Datawarehouse tables *********
rem ********* Drops the DCS Datawarehouse tables *********
rem ********* Drops the Search Datawarehouse tables *********
rem ********* Drops the ARF Datawarehouse tables *********

call .\drop-dcs-search-dw.bat
call .\drop-dcs-dw.bat
call .\drop-search-dw.bat
call .\drop-arf.bat
