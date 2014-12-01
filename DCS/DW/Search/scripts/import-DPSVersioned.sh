#!/bin/sh -x

. search-dw-env.sh

# ********* Imports data into the Versioned Personalization Repository *********

$DYNAMO_HOME/../DPS/Search/Base/install/importDPSVersioned.sh
$DYNAMO_HOME/../DPS/InternalUsers/Search/Base/install/importDPSVersioned.sh

exit 0
