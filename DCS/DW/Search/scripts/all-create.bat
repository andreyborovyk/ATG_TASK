@echo off

call .\search-dw-env.bat

rem ********* Creates the ARF Datawarehouse tables *********
rem ********* Creates the Search Datawarehouse tables *********
rem ********* Creates the DCS Datawarehouse tables *********
rem ********* Creates the DCS Search Datawarehouse tables *********
rem ********* Imports data into the Versioned Personalization Repository *********

call .\create-arf.bat
call .\create-search-dw.bat
call .\create-dcs-dw.bat
call .\create-dcs-search-dw.bat

call .\import-DPSVersioned.bat
call .\import-DCSVersioned.bat

exit
