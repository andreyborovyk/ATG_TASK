#!/bin/sh

. search-dw-env.sh

# ********* Drops the Search Datawarehouse tables *********
# ********* Drops the ARF Datawarehouse tables *********

./drop-search-dw.sh
./drop-arf.sh

exit 0

