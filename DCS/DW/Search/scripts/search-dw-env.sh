#!/bin/sh -x

# ********* Defines variables needed by the scripts that create the datawarehouse schema
# ********* Set the following environment variables to override the default values defined here.

# ********* Set DYNAMO_HOME or comment it if it is already in the Environment variables
if [ "x$DYNAMO_HOME" = "x" ] ; then
 echo "** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%" ;
 exit
fi

# ********* Search version
if [ "x$SEARCH_VERSION" = "x" ] ; then
  SEARCH_VERSION=@PRODUCT_VERSION@
fi

# ********* Possible values for {SEARCH_DW_DB_TYPE} are: "db2", "mssql", "mysql" or "oracle"
if [ "x$SEARCH_DW_DB_TYPE" = "x" ] ; then
  SEARCH_DW_DB_TYPE=mysql
fi

# ********* Common parameters
if [ "x$SEARCH_DW_DB_USER" = "x" ] ; then
  SEARCH_DW_DB_USER=admin
fi
if [ "x$SEARCH_DW_DB_PWD" = "x" ] ; then
  SEARCH_DW_DB_PWD=admin
fi

# ********* MS SQL specific parameters
if [ "x$SEARCH_DW_MSSQL_DB_NAME" = "x" ] ; then
  SEARCH_DW_MSSQL_DB_NAME=SearchAdmin
fi
if [ "x$SEARCH_DW_MSSQL_DB_HOST" = "x" ] ; then
  SEARCH_DW_MSSQL_DB_HOST=localhost
fi

# ********* Oracle specific parameters
if [ "x$SEARCH_DW_ORACLE_TNS" = "x" ] ; then
  SEARCH_DW_ORACLE_TNS=EXAMPLE@TEST.COM
fi

#  ********* MySQL specific parameters
if [ "x$SEARCH_DW_MYSQL_DB_NAME" = "x" ] ; then
  SEARCH_DW_MYSQL_DB_NAME=SearchAdmin
fi
if [ "x$SEARCH_DW_MYSQL_DB_HOST" = "x" ] ; then
  SEARCH_DW_MYSQL_DB_HOST=localhost
fi

# ********* DB2 specific parameters
if [ "x$SEARCH_DW_DB2_DB_NAME" = "x" ] ; then
  SEARCH_DW_DB2_DB_NAME=SearchAdmin
fi
