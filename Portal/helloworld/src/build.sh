#!/bin/sh
if [ "${DYNAMO_HOME}" = "" ]; then 
    echo Please set DYNAMO_HOME.
	exit 1
elif [ "${JAVA_HOME}" = "" ]; then
	echo Please set JAVA_HOME.
	exit 1
else

	# clean up previous builds
	echo Cleaning up previous builds.
	rm -rf build

	# Assemble the WAR file
	echo Assembling the WAR file.
	mkdir build
	mkdir build/helloworld_war
	mkdir build/helloworld_war/WEB-INF
	mkdir build/helloworld_war/WEB-INF/lib

	echo ..Copying files
	cp -R helloworld.war/WEB-INF/* build/helloworld_war/WEB-INF
	cp -R helloworld.war/META-INF/* build/helloworld_war/META-INF
	cp -R helloworld.war/* build/helloworld_war

	echo ...Copying taglibs
	cp ${DYNAMO_HOME}/../Portal/taglib/pafTaglib/1.2/lib/pafTaglib1_2.jar build/helloworld_war/WEB-INF/lib
	cp ${DYNAMO_HOME}/../Portal/taglib/pafTaglib/1.2/tld/pafTaglib1_2.tld build/helloworld_war/WEB-INF
	cp ${DYNAMO_HOME}/../DAS/taglib/coreTaglib/1.0/lib/coreTaglib1_0.jar build/helloworld_war/WEB-INF/lib
	cp ${DYNAMO_HOME}/../DAS/taglib/coreTaglib/1.0/tld/coreTaglib1_0.tld build/helloworld_war/WEB-INF
	cp ${DYNAMO_HOME}/../DAS/taglib/dspjspTaglib/1.0/lib/dspjspTaglib1_0.jar build/helloworld_war/WEB-INF/lib
	cp ${DYNAMO_HOME}/../DAS/taglib/dspjspTaglib/1.0/tld/dspjspTaglib1_0.tld build/helloworld_war/WEB-INF/

	echo ...Jarring helloworld.war
	${JAVA_HOME}/bin/jar cf build/helloworld.war -C build/helloworld_war/ .
	echo ....Deleting working directories
	rm -rf build/helloworld_war

	echo Done creating helloworld.war.  
	echo
	echo Now follow the instructions from step two of \"Deploying helloworld Into Dynamo\" in INSTALL.html

fi
exit 0
