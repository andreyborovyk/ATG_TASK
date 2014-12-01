#!/bin/sh

# Create the tables
echo "Creating tables..."
sqlplus $1/$2@$3 @./oracle_tables.sql

# Import the minimal data needed by Portals.  This data will allow you into admin,
# where you can then begin building your communities, adding gears, etc.
# the strange path for minimal-data.xml is due to startSQLRepository running from ..\home
echo "Importing data..."
( cd ../../../home; bin/startSQLRepository -m Portal.paf -database oracle -repository /atg/portal/framework/PortalRepository -import ../Portal/install/minimal-data.xml )

