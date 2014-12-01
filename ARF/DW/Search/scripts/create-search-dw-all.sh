#!/bin/sh -x

. search-dw-env.sh

# ********* Creates the Search Datawarehouse tables - assumes ARF/DW tables were created when the platform tables were created.
# ********* Imports data into the Versioned Personalization Repository

./create-search-dw.sh
./import-DPSVersioned.sh

exit 0


