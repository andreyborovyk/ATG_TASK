# -------------------------------------------------------------------
# $Id: //product/PublishingAgent/version/10.0.3/web-agent/bin/docrootConfig.sh#2 $
# $Author: rbarbier $
# $Change: 651448 $
# deploymentConfig -- add the deployment agent's deploymentconfig directory
#                   to the config path
# -------------------------------------------------------------------

ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR=${DYNAMO_HOME}/${SERVERNAME}/PublishingWebAgent/deploymentdocroot
if [ ! -d ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR} ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}
fi
if [ ! -d ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/staging ] ; then
  mkdir -p ${ATG_PUB_AGENT_DEPLOYMENTCONFIG_DIR}/staging
fi

