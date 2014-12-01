@echo off

:: -------------------------------------------------------------------
:: $Id: //product/PublishingAgent/version/10.0.3/web-agent/bin/docrootConfig.bat#2 $
:: $Author: rbarbier $
:: $Change: 651448 $
:: deploymentConfig -- add the deployment agent's deploymentconfig directory
::                   to the config path
:: -------------------------------------------------------------------
set ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR=%DYNAMO_HOME%\%SERVERNAME%\PublishingWebAgent\deploymentdocroot
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR% mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging


