#!/bin/sh -x

. search-dw-env.sh

# ********* Imports data into the Versioned Personalization Repository *********

$DYNAMO_HOME/../DCS/Versioned/install/importDCSVersioned.sh

exit 0
