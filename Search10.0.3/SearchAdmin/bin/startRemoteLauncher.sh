#!/bin/sh

args=$*

L_JAVA_VM=${JAVA_VM}

getfrompath()
{
    L_JAVA_VM=${JAVA_HOME}/bin/java
    if [ ! -f $L_JAVA_VM ]; then
        assumeinpath
    fi

    startprocess
}

assumeinpath()
{
    L_JAVA_VM=java
    startprocess
}

startprocess()
{
    HOSTNAME_ARG=
    if [ -x /bin/hostname ] ; then
        HOSTNAME_ARG=-Djava.rmi.server.hostname=`/bin/hostname`
    elif [ -x /usr/bin/hostname ] ; then
        HOSTNAME_ARG=-Djava.rmi.server.hostname=`/usr/bin/hostname`
    elif [ -x /usr/local/bin/hostname ] ; then
        HOSTNAME_ARG=-Djava.rmi.server.hostname=`/usr/local/bin/hostname`
    fi
    ${L_JAVA_VM} -cp ../common/lib/standalone-launcher.jar ${HOSTNAME_ARG} atg.search.routing.rmi.RemoteSearchEngineLaunchingService $args
    exit 1
}

if [ "${L_JAVA_VM}" = "" ] ; then
    getfrompath
elif [ ! -f $L_JAVA_VM ]; then
    getfrompath
fi

startprocess
