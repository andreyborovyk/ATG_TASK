#!/bin/sh

# UNIX type for this machine
UNIXFLAVOR=`uname`

# make sure we're adding on a platform we support
if [ $UNIXFLAVOR = SunOS ] ; then
    LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${DYNAMO_ROOT}/Taxware/os_specific_files/sparc-sun-solaris2
    export LD_LIBRARY_PATH
fi
if [ $UNIXFLAVOR = AIX ] ; then
    LIBPATH=${LIBPATH}:${DYNAMO_ROOT}/Taxware/os_specific_files/powerpc-ibm-aix4.2
    export LIBPATH
fi
if [ $UNIXFLAVOR = HP ] ; then
    SHLIB_PATH=${SHLIB_PATH}:${DYNAMO_ROOT}/Taxware/os_specific_files/hppa1.0-hp-hpux11.00
    export SHLIB_PATH
fi
