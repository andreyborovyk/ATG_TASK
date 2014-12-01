#!/bin/sh

# UNIX type for this machine
UNIXFLAVOR=`uname`
    
# make sure we're adding on a platform we support
if [ $UNIXFLAVOR = SunOS ]; then
    LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${DYNAMO_ROOT}/PayFlowPro/sparc-sun-solaris2
    export LD_LIBRARY_PATH
fi
