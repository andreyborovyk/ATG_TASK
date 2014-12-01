@echo off
IF NOT "%DYNAMO_HOME%"=="" goto checkjava
echo Please set DYNAMO_HOME before running this script
goto end

:checkjava
IF NOT "%JAVA_HOME%"=="" goto process
echo Please set JAVA_HOME before running this script
goto end

:process
REM clean up previous builds
rmdir /s /q build

REM Assemble the WAR file
mkdir build
mkdir build\helloworld_war
mkdir build\helloworld_war\WEB-INF
mkdir build\helloworld_war\WEB-INF\lib

xcopy helloworld.war\WEB-INF\*.* build\helloworld_war\WEB-INF /E
xcopy helloworld.war\META-INF\*.* build\helloworld_war\META-INF /E
copy helloworld.war\*.* build\helloworld_war
copy %DYNAMO_HOME%\..\Portal\taglib\pafTaglib\1.2\lib\pafTaglib1_2.jar build\helloworld_war\WEB-INF\lib
copy %DYNAMO_HOME%\..\Portal\taglib\pafTaglib\1.2\tld\pafTaglib1_2.tld build\helloworld_war\WEB-INF
copy %DYNAMO_HOME%\..\DAS\taglib\coreTaglib\1.0\lib\coreTaglib1_0.jar build\helloworld_war\WEB-INF\lib
copy %DYNAMO_HOME%\..\DAS\taglib\coreTaglib\1.0\tld\coreTaglib1_0.tld build\helloworld_war\WEB-INF
copy %DYNAMO_HOME%\..\DAS\taglib\dspjspTaglib\1.0\lib\dspjspTaglib1_0.jar build\helloworld_war\WEB-INF\lib
copy %DYNAMO_HOME%\..\DAS\taglib\dspjspTaglib\1.0\tld\dspjspTaglib1_0.tld build\helloworld_war\WEB-INF\

%JAVA_HOME%\bin\jar cf build\helloworld.war -C build\helloworld_war\ .
rmdir /s /q build\helloworld_war

echo
echo helloworld.war should now be in the build directory.
echo Now follow the instructions from step two of \"Deploying helloworld Into Dynamo\" in INSTALL.html
:end
