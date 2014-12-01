# -------------------------------------------------------------------
# $Id: //product/PublishingAgent/version/10.0.3/agent/bin/deploymentConfig.sh#2 $
# $Author: rbarbier $
# $Change: 651448 $
# deploymentConfig -- add the deployment agent's deploymentconfig directory
#                   to the config path
# -------------------------------------------------------------------

ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR=${DYNAMO_HOME}/${SERVERNAME}/PublishingAgent/deploymentconfig
if [ ! -d ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR} ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}
fi
if [ ! -d ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/live ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/live
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/live/config
fi
if [ ! -d ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/staging ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/staging
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/staging/config
fi

CONFIGPATH=${CONFIGPATH}:${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/live/config
