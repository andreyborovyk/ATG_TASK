@echo off

:: -------------------------------------------------------------------
:: $Id: //product/PublishingAgent/version/10.0.3/agent/DeployedConfig/bin/deployedConfig.bat#2 $
:: $Author: rbarbier $
:: $Change: 651448 $
:: deployedConfig -- add the deployment agent's deployedconfig directory
::                   to the config path
:: -------------------------------------------------------------------
set ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR=%DYNAMO_HOME%\%SERVERNAME%\PublishingAgent\deployedconfig
if NOT EXIST %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR% mkdir %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%
if NOT EXIST %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\live mkdir %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\live
if NOT EXIST %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\live\config mkdir %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\live\config
if NOT EXIST %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\staging mkdir %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\staging
if NOT EXIST %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\staging\config mkdir %ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\staging\config

set CONFIGPATH=%CONFIGPATH%;%ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR%\live\config
