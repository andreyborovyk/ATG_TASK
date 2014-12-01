<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1208186632423" enabled="true" last-modified-by="admin" modification-time="1212163842831">
  <segment migrate-from="1208188135492" migrate-subjects="true">
    <segment-name>main</segment-name>
    <!--================================-->
    <!--== startWorkflow  -->
    <!--================================-->
    <event id="1">
      <event-name>atg.workflow.StartWorkflow</event-name>
      <filter operator="eq">
        <event-property>
          <property-name>processName</property-name>
        </event-property>
        <constant>/Common/commonWorkflow.wdl</constant>
      </filter>
      <filter operator="eq">
        <event-property>
          <property-name>segmentName</property-name>
        </event-property>
        <constant>main</constant>
      </filter>
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>startWorkflow</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubUser:execute;Admin$role$administrators-group:execute;Profile$role$epubSuperAdmin:execute;Profile$role$epubManager:execute;Admin$role$managers-group:execute;Profile$role$epubAdmin:execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>A basic workflow which focuses on creating and editing arbitrary assets. Deploys to production target.</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Content Administration Project</constant>
        </attribute>
      </attributes>
    </event>
    <!--================================-->
    <!--== Create project without a workflow and process' project name  -->
    <!--================================-->
    <action id="2">
      <action-name>createProjectForProcess</action-name>
    </action>
    <!--================================-->
    <!--== author  -->
    <!--================================-->
    <label id="3">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Admin$role$administrators-group:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute;Profile$role$epubAdmin:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Create and modify assets for eventual deployment</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>author</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Author</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="4">
      <branch id="4.1">
        <!--================================-->
        <!--== review  -->
        <!--================================-->
        <event id="4.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Assets are ready for approval</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>review</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Ready for Review</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Change Project's Current project's Editable to false  -->
        <!--================================-->
        <action id="4.1.2">
          <action-name construct="modify-action">modify</action-name>
          <action-param name="modified">
            <subject-property>
              <property-name>project</property-name>
              <property-name>editable</property-name>
            </subject-property>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <constant type="java.lang.Boolean">false</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Check assets are up to date  -->
        <!--================================-->
        <action id="4.1.3">
          <action-name>assetsUpToDate</action-name>
        </action>
      </branch>
      <branch id="4.2">
        <!--================================-->
        <!--== delete  -->
        <!--================================-->
        <event id="4.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Delete the project</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>delete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Delete Project</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Delete project  -->
        <!--================================-->
        <action id="4.2.2">
          <action-name>deleteProject</action-name>
        </action>
      </branch>
    </fork>
    <!--================================-->
    <!--== contentReview  -->
    <!--================================-->
    <label id="5">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$managers-group:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Review and approve changes to project assets</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>contentReview</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Content Review</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="6">
      <branch id="6.1">
        <!--================================-->
        <!--== approve  -->
        <!--================================-->
        <event id="6.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>6.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>The content has been reviewed and appears good to proceed</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>approve</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Approve Content</constant>
            </attribute>
          </attributes>
        </event>
      </branch>
      <branch id="6.2">
        <!--================================-->
        <!--== reject  -->
        <!--================================-->
        <event id="6.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>6.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Reject the project</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>reject</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Reject</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Reopen project  -->
        <!--================================-->
        <action id="6.2.2">
          <action-name>reopenProject</action-name>
        </action>
        <jump id="6.2.3" target="3"/>
      </branch>
      <branch id="6.3">
        <!--================================-->
        <!--== delete  -->
        <!--================================-->
        <event id="6.3.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>6.3.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Delete the project</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>delete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Delete Project</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Delete project  -->
        <!--================================-->
        <action id="6.3.2">
          <action-name>deleteProject</action-name>
        </action>
      </branch>
    </fork>
    <!--================================-->
    <!--== productionApproval  -->
    <!--================================-->
    <label id="7">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Approve for deployment to Production target</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>productionApproval</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Approve for Production Deployment</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="8">
      <branch id="8.1">
        <!--================================-->
        <!--== approveAndDeploy  -->
        <!--================================-->
        <event id="8.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>8.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Approve for immediate deployment to Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>approveAndDeploy</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Approve and Deploy to Production</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Approve and deploy project to target Production  -->
        <!--================================-->
        <action id="8.1.2">
          <action-name>approveAndDeployProject</action-name>
          <action-param name="target">
            <constant>Production</constant>
          </action-param>
        </action>
      </branch>
      <branch id="8.2">
        <!--================================-->
        <!--== approve  -->
        <!--================================-->
        <event id="8.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>8.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Approve project for deployment to Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>approve</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Approve for Production Deployment</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Approve project for target Production  -->
        <!--================================-->
        <action id="8.2.2">
          <action-name>approveProject</action-name>
          <action-param name="target">
            <constant>Production</constant>
          </action-param>
        </action>
      </branch>
      <branch id="8.3">
        <!--================================-->
        <!--== reject  -->
        <!--================================-->
        <event id="8.3.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>8.3.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Reject project for deployment to Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>reject</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Reject Production Deployment</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Reopen project  -->
        <!--================================-->
        <action id="8.3.2">
          <action-name>reopenProject</action-name>
        </action>
        <!--================================-->
        <!--== Release asset locks  -->
        <!--================================-->
        <action id="8.3.3">
          <action-name>releaseAssetLocks</action-name>
        </action>
        <jump id="8.3.4" target="3"/>
      </branch>
    </fork>
    <!--================================-->
    <!--== waitForDeploymentToComplete  -->
    <!--================================-->
    <label id="9">
      <attributes>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">false</constant>
        </attribute>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubAdmin:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$administrators-group:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Wait for deployment to complete on Production target</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>waitForDeploymentToComplete</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Wait for Production Deployment Completion</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="10">
      <branch id="10.1">
        <!--================================-->
        <!--== Wait for deployment to complete on target Production  -->
        <!--================================-->
        <event id="10.1.1">
          <event-name>atg.deployment.DeploymentStatus</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>targetId</property-name>
            </event-property>
            <constant>Production</constant>
          </filter>
        </event>
        <fork id="10.1.2">
          <branch id="10.1.2.1">
            <!--================================-->
            <!--== Deployment completed event status is success on target Production  -->
            <!--================================-->
            <condition id="10.1.2.1.1">
              <filter operator="deploymentCompleted">
                <constant>1</constant>
                <constant>Production</constant>
              </filter>
            </condition>
          </branch>
          <branch id="10.1.2.2">
            <!--================================-->
            <!--== Deployment completed event status is failure on target Production  -->
            <!--================================-->
            <condition id="10.1.2.2.1">
              <filter operator="deploymentCompleted">
                <constant>0</constant>
                <constant>Production</constant>
              </filter>
            </condition>
            <!--================================-->
            <!--== Release asset locks  -->
            <!--================================-->
            <action id="10.1.2.2.2">
              <action-name>releaseAssetLocks</action-name>
            </action>
            <jump id="10.1.2.2.3" target="7"/>
          </branch>
        </fork>
      </branch>
    </fork>
    <!--================================-->
    <!--== verifyProduction  -->
    <!--================================-->
    <label id="11">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubUser:write,execute;Profile$role$epubManager:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Verify that the deployment to Production was successful and that all deployed assets look and function appropriately</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>verifyProduction</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Verify Production Deployment</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="12">
      <branch id="12.1">
        <!--================================-->
        <!--== accept  -->
        <!--================================-->
        <event id="12.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>12.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Accept the deployment to Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>accept</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Accept Production Deployment</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Validate project is deployed on target Production  -->
        <!--================================-->
        <action id="12.1.2">
          <action-name>validateProjectDeployed</action-name>
          <action-param name="target">
            <constant>Production</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Check in project's workspace  -->
        <!--================================-->
        <action id="12.1.3">
          <action-name>checkInProject</action-name>
        </action>
        <!--================================-->
        <!--== Complete project  -->
        <!--================================-->
        <action id="12.1.4">
          <action-name>completeProject</action-name>
        </action>
        <!--================================-->
        <!--== Complete process  -->
        <!--================================-->
        <action id="12.1.5">
          <action-name>completeProcess</action-name>
        </action>
      </branch>
      <branch id="12.2">
        <!--================================-->
        <!--== revertAssetsOnlyOnProductionNow  -->
        <!--================================-->
        <event id="12.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Common/commonWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>12.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Revert assets from Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>revertAssetsOnlyOnProductionNow</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Revert Assets on Production Immediately</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Revert assets immediately on target Production  -->
        <!--================================-->
        <action id="12.2.2">
          <action-name>revertAssetsOnTargetNow</action-name>
          <action-param name="target">
            <constant>Production</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== waitForRevertDeploymentToComplete  -->
        <!--================================-->
        <label id="12.2.3">
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">false</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Wait for revert deployment to complete on Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>waitForRevertDeploymentToComplete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Wait for Production Revert Deployment Completion</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="12.2.4">
          <branch id="12.2.4.1">
            <!--================================-->
            <!--== Wait for deployment to complete on target Production  -->
            <!--================================-->
            <event id="12.2.4.1.1">
              <event-name>atg.deployment.DeploymentStatus</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>targetId</property-name>
                </event-property>
                <constant>Production</constant>
              </filter>
            </event>
            <fork id="12.2.4.1.2">
              <branch id="12.2.4.1.2.1">
                <!--================================-->
                <!--== Deployment completed event status is success on target Production  -->
                <!--================================-->
                <condition id="12.2.4.1.2.1.1">
                  <filter operator="deploymentCompleted">
                    <constant>1</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== Reopen project  -->
                <!--================================-->
                <action id="12.2.4.1.2.1.2">
                  <action-name>reopenProject</action-name>
                </action>
                <!--================================-->
                <!--== Release asset locks  -->
                <!--================================-->
                <action id="12.2.4.1.2.1.3">
                  <action-name>releaseAssetLocks</action-name>
                </action>
                <jump id="12.2.4.1.2.1.4" target="3"/>
              </branch>
              <branch id="12.2.4.1.2.2">
                <!--================================-->
                <!--== Deployment completed event status is failure on target Production  -->
                <!--================================-->
                <condition id="12.2.4.1.2.2.1">
                  <filter operator="deploymentCompleted">
                    <constant>0</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
                <jump id="12.2.4.1.2.2.2" target="11"/>
              </branch>
            </fork>
          </branch>
        </fork>
      </branch>
    </fork>
  </segment>
</process>
