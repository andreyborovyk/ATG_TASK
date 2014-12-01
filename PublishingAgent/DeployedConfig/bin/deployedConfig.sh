# -------------------------------------------------------------------
# $Id: //product/PublishingAgent/version/10.0.3/agent/DeployedConfig/bin/deployedConfig.sh#2 $
# $Author: rbarbier $
# $Change: 651448 $
# deployedConfig -- add the deployment agent's deployedconfig directory
#                   to the config path
# -------------------------------------------------------------------

ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR=${DYNAMO_HOME}/${SERVERNAME}/PublishingAgent/deployedconfig
if [ ! -d ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR} ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}
fi
if [ ! -d ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/live ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/live
  mkdir -p ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/live/config
fi
if [ ! -d ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/staging ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/staging
  mkdir -p ${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/staging/config
fi

CONFIGPATH=${CONFIGPATH}:${ATG_PUB_AGENT_DEPLOYEDCONFIG_DIR}/live/config
