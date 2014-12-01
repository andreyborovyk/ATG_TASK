#!/bin/sh

#
# Run the Repository Loader RLClient module with supplied args.
#
# To see usage, issue ./RLClient.sh and if DYNAMO_HOME is set properly, 
# the usage should print.
# 
# $Change: 651448 $
#

if [ "x${DYNAMO_HOME}" = "x" ]; then
   echo "DYNAMO_HOME has not been set."
   exit_failure
fi

java -classpath ${DYNAMO_HOME}/../RL/lib/RLClient.jar atg.repository.loader.RLClient $1 $2 $3 $4 $5 $6 $7 $8 $9

# =====================================
# Exit because of error
# =====================================
exit_failure() { 
 exit 1 
}
