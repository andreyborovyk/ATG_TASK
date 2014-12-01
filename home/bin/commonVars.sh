#!/bin/sh

#
# This is a common variable file for all dynamo unix shell scripts
# Changes made to here are global in nature, so be careful
#

############################################################
# Email notification

EMAILDEST="foo@example.com"


############################################################
# PID files
# These are stored in each servers /log directory

# The pid file for the Dynamo server process
DYNAMO_PIDFILE_NAME=dynamo.pid

# Temporary store for status result of the Java process
STATUS_FILE_NAME=status.dat

############################################################
# LOG files
# These are stored in each servers /log directory

# The name of the logfile used to log each dynamo that was launched
# by launchDynamo
DYNAMO_LOGFILE_NAME=dynamo.log

############################################################
# Machine particular stuff

MAILER=/usr/bin/mailx 
DATE=`/bin/date +%m-%d-%Y_%H-%M-%S`

#
############################################################

