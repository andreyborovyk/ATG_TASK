@echo off

call .\search-dw-env.bat

@echo on

rem ********* Imports data into the Versioned Personalization Repository *********

call %DYNAMO_HOME%\..\DPS\Search\Base\install\importDPSVersioned.bat
call %DYNAMO_HOME%\..\DPS\InternalUsers\Search\Base\install\importDPSVersioned.bat

@echo off


