@echo off
setlocal

REM drop all tables to reset them
echo dropping portal tables...
sqlplus %1/%2@%3 @.\drop_oracle_tables.sql

pause

