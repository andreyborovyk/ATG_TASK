#!/bin/sh

. search-dw-env.sh

# ********* Drops the DCS Search Datawarehouse tables *********
# ********* Drops the DCS Datawarehouse tables *********
# ********* Drops the Search Datawarehouse tables *********
# ********* Drops the ARF Datawarehouse tables *********

./drop-dcs-search-dw.sh
./drop-dcs-dw.sh
./drop-search-dw.sh
./drop-arf.sh

exit 0

