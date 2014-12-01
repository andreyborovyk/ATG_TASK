#!/bin/sh

# drop all tables to reset them
echo "Deleting tables..."
sqlplus $1/$2@$3 @./drop_oracle_tables.sql

