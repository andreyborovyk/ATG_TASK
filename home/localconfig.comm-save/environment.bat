REM environment.bat
REM Modify your DYNAMO_MODULES settings in this file
REM Modify your CLASSPATH, CONFIGPATH, and JAVA_ARGS"
REM settings in the postEnvironment.sh file in your"
REM <ATGCascadedir>/home/localconfig directory."
REM See the VERSION documentation for more information

REM <installed_module>
set DYNAMO_MODULES=%DYNAMO_MODULES%;DSS
REM </installed_module>

set DYNAMO_RMI_PORT=8860
set DYNAMO_LICENSED_MODULES=Portal.paf.framework;Publishing.base;DPS;DSS;
set DYNAMO_CLASSPATH_MODULES=PubPortlet;DSS;DSSJ2EEDemo
