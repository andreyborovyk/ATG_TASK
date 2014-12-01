@echo off

call .\search-dw-env.bat

rem ********* Creates the Search Datawarehouse tables - assumes ARF/DW tables were created when the platform tables were created.
rem ********* Imports data into the Versioned Personalization Repository

call .\create-search-dw.bat
call .\import-DPSVersioned.bat

exit
