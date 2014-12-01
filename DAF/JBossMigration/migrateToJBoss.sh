#!/bin/sh

#
# Check that DYNAMO_HOME and DAS_ROOT are set
#
OWD=`pwd`
PROGRAM_DIR=`dirname $0`

ATGJRE=java

#
# Set the JAVA_VM - could be overridden by environment.sh
#
if [ "x$JAVA_VM" = "x" ] 
then
  JAVA_VM="$ATGJRE"
fi

#
# runAssembler
#
${JAVA_VM} -Xms96m -Xmx160m \
           -cp jbossmigration.jar \
           atg.jbossmigration.JBossMigrationMain $OWD $*
