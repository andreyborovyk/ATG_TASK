@echo off

call .\search-dw-env.bat

@echo on

rem ********* Imports data into the Versioned Personalization Repository *********

call %DYNAMO_HOME%\..\DCS\Versioned\install\importDCSVersioned.bat

@echo off


