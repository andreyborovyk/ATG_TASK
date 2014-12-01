@echo off
setlocal

REM delete the tables
db2cmd -c -w db2-delcmds.bat %1 %2 %3

pause

