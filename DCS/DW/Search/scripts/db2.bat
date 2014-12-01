db2.exe "connect to %SEARCH_DW_DB2_DB_NAME% user %SEARCH_DW_DB_USER% using %SEARCH_DW_DB_PWD%"
db2.exe -tvf %DYNAMO_HOME%\%1%
db2.exe "connect reset"
db2.exe "terminate"


