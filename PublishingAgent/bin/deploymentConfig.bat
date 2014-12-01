@echo off

:: -------------------------------------------------------------------
:: $Id: //product/PublishingAgent/version/10.0.3/agent/bin/deploymentConfig.bat#2 $
:: $Author: rbarbier $
:: $Change: 651448 $
:: deploymentConfig -- add the deployment agent's deploymentconfig directory
::                   to the config path
:: -------------------------------------------------------------------
set ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR=%DYNAMO_HOME%\%SERVERNAME%\PublishingAgent\deploymentconfig
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR% mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\live mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\live
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\live\config mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\live\config
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging
if NOT EXIST %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging\config mkdir %ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\staging\config

set CONFIGPATH=%CONFIGPATH%;%ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR%\live\config
