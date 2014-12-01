#!/bin/sh -x

. search-dw-env.sh

# ********* Creates the ARF Datawarehouse tables *********
# ********* Creates the Search Datawarehouse tables *********
# ********* Creates the DCS Datawarehouse tables *********
# ********* Creates the DCS Search Datawarehouse tables *********
# ********* Imports data into the Versioned Personalization Repository *********

./create-arf.sh
./create-search-dw.sh
./create-dcs-dw.sh
./create-dcs-search-dw.sh

./import-DPSVersioned.sh
./import-DCSVersioned.sh

exit 0


