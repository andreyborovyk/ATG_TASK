@echo @%DYNAMO_HOME%\%1 > oracle_temp.sql
@echo exit >> oracle_temp.sql
sqlplus %SEARCH_DW_DB_USER%/%SEARCH_DW_DB_PWD%@%SEARCH_DW_ORACLE_TNS% @./oracle_temp.sql
@del oracle_temp.sql
