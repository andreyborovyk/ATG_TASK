#!/bin/sh -x

. search-dw-env.sh

# ********* Creates the ARF Datawarehouse tables *********
# ********* Creates the Search Datawarehouse tables *********
# ********* Imports data into the Versioned Personalization Repository *********

./create-arf.sh
./create-search-dw.sh

./import-DPSVersioned.sh

exit 0


