<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1156534371510" enabled="true" last-modified-by="admin" modification-time="1228946084550">
  <segment migrate-subjects="true">
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
        <constant>/Content Administration/import.wdl</constant>
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
          <constant>Profile$role$epubManager:execute;Profile$role$epubUser:execute;Profile$role$epubSuperAdmin:execute;Admin$role$administrators-group:execute;Admin$role$managers-group:execute;Profile$role$epubAdmin:execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>A late model workflow to be used for automated imports</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Import Workflow (late model)</constant>
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
    <!--== Import  -->
    <!--================================-->
    <label id="3">
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">false</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubUser:write,execute;Profile$role$epubAdmin:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$administrators-group:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Import</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>Import</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Import</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="4">
      <branch id="4.1">
        <!--================================-->
        <!--== Import Complete  -->
        <!--================================-->
        <event id="4.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Content Administration/import.wdl</constant>
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
              <constant>Import Complete</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>Import Complete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Import Complete</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Check assets are up to date  -->
        <!--================================-->
        <action id="4.1.2">
          <action-name>assetsUpToDate</action-name>
        </action>
        <!--================================-->
        <!--== Change Project's Current project's Editable to false  -->
        <!--================================-->
        <action id="4.1.3">
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
        <!--== Approve and deploy project to target Production  -->
        <!--================================-->
        <action id="4.1.4">
          <action-name>approveAndDeployProject</action-name>
          <action-param name="target">
            <constant>Production</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== waitForProductionDeploymentToComplete  -->
        <!--================================-->
        <label id="4.1.5">
          <attributes>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">false</constant>
            </attribute>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Waits for the production deployment to complete</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>waitForProductionDeploymentToComplete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Wait for Production Deployment to Complete</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.6">
          <branch id="4.1.6.1">
            <!--================================-->
            <!--== Wait for deployment to complete on target Production  -->
            <!--================================-->
            <event id="4.1.6.1.1">
              <event-name>atg.deployment.DeploymentStatus</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>targetId</property-name>
                </event-property>
                <constant>Production</constant>
              </filter>
            </event>
            <fork id="4.1.6.1.2">
              <branch id="4.1.6.1.2.1">
                <!--================================-->
                <!--== Deployment completed event status is success on target Production  -->
                <!--================================-->
                <condition id="4.1.6.1.2.1.1">
                  <filter operator="deploymentCompleted">
                    <constant>1</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== Validate project is deployed on target Production  -->
                <!--================================-->
                <action id="4.1.6.1.2.1.2">
                  <action-name>validateProjectDeployed</action-name>
                  <action-param name="target">
                    <constant>Production</constant>
                  </action-param>
                </action>
              </branch>
              <branch id="4.1.6.1.2.2">
                <!--================================-->
                <!--== Deployment completed event status is failure on target Production  -->
                <!--================================-->
                <condition id="4.1.6.1.2.2.1">
                  <filter operator="deploymentCompleted">
                    <constant>0</constant>
                    <constant>Production</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== retryProductionDeployment  -->
                <!--================================-->
                <label id="4.1.6.1.2.2.2">
                  <attributes>
                    <attribute name="atg.workflow.assignable">
                      <constant type="java.lang.Boolean">false</constant>
                    </attribute>
                    <attribute name="atg.workflow.elementType">
                      <constant>task</constant>
                    </attribute>
                    <attribute name="atg.workflow.acl">
                      <constant>Profile$role$epubUser:write,execute;Profile$role$epubAdmin:write,execute;Admin$role$managers-group:write,execute;Admin$role$administrators-group:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
                    </attribute>
                    <attribute name="atg.workflow.description">
                      <constant>Allows production deployment to be retried</constant>
                    </attribute>
                    <attribute name="atg.workflow.name">
                      <constant>retryProductionDeployment</constant>
                    </attribute>
                    <attribute name="atg.workflow.displayName">
                      <constant>Retry Production Deployment</constant>
                    </attribute>
                  </attributes>
                </label>
                <fork exclusive="true" id="4.1.6.1.2.2.3">
                  <branch id="4.1.6.1.2.2.3.1">
                    <!--================================-->
                    <!--== retryProductionDeploymentNow  -->
                    <!--================================-->
                    <event id="4.1.6.1.2.2.3.1.1">
                      <event-name>atg.workflow.TaskOutcome</event-name>
                      <filter operator="eq">
                        <event-property>
                          <property-name>processName</property-name>
                        </event-property>
                        <constant>/Content Administration/import.wdl</constant>
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
                        <constant>4.1.6.1.2.2.3.1.1</constant>
                      </filter>
                      <attributes>
                        <attribute name="atg.workflow.elementType">
                          <constant>outcome</constant>
                        </attribute>
                        <attribute name="atg.workflow.description">
                          <constant>Allow for retrying the deployment to production</constant>
                        </attribute>
                        <attribute name="atg.workflow.name">
                          <constant>retryProductionDeploymentNow</constant>
                        </attribute>
                        <attribute name="atg.workflow.displayName">
                          <constant>Retry Production Deployment Now</constant>
                        </attribute>
                      </attributes>
                    </event>
                    <!--================================-->
                    <!--== Approve and deploy project to target Production  -->
                    <!--================================-->
                    <action id="4.1.6.1.2.2.3.1.2">
                      <action-name>approveAndDeployProject</action-name>
                      <action-param name="target">
                        <constant>Production</constant>
                      </action-param>
                    </action>
                    <jump id="4.1.6.1.2.2.3.1.3" target="4.1.5"/>
                  </branch>
                  <branch id="4.1.6.1.2.2.3.2">
                    <!--================================-->
                    <!--== retryProductionDeploymentLater  -->
                    <!--================================-->
                    <event id="4.1.6.1.2.2.3.2.1">
                      <event-name>atg.workflow.TaskOutcome</event-name>
                      <filter operator="eq">
                        <event-property>
                          <property-name>processName</property-name>
                        </event-property>
                        <constant>/Content Administration/import.wdl</constant>
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
                        <constant>4.1.6.1.2.2.3.2.1</constant>
                      </filter>
                      <attributes>
                        <attribute name="atg.workflow.elementType">
                          <constant>outcome</constant>
                        </attribute>
                        <attribute name="atg.workflow.description">
                          <constant>Allow for retrying the deployment to production</constant>
                        </attribute>
                        <attribute name="atg.workflow.name">
                          <constant>retryProductionDeploymentLater</constant>
                        </attribute>
                        <attribute name="atg.workflow.displayName">
                          <constant>Retry Production Deployment Later</constant>
                        </attribute>
                      </attributes>
                    </event>
                    <!--================================-->
                    <!--== Approve project for target Production  -->
                    <!--================================-->
                    <action id="4.1.6.1.2.2.3.2.2">
                      <action-name>approveProject</action-name>
                      <action-param name="target">
                        <constant>Production</constant>
                      </action-param>
                    </action>
                    <jump id="4.1.6.1.2.2.3.2.3" target="4.1.5"/>
                  </branch>
                  <branch id="4.1.6.1.2.2.3.3">
                    <!--================================-->
                    <!--== returnToImportTask  -->
                    <!--================================-->
                    <event id="4.1.6.1.2.2.3.3.1">
                      <event-name>atg.workflow.TaskOutcome</event-name>
                      <filter operator="eq">
                        <event-property>
                          <property-name>processName</property-name>
                        </event-property>
                        <constant>/Content Administration/import.wdl</constant>
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
                        <constant>4.1.6.1.2.2.3.3.1</constant>
                      </filter>
                      <attributes>
                        <attribute name="atg.workflow.elementType">
                          <constant>outcome</constant>
                        </attribute>
                        <attribute name="atg.workflow.description">
                          <constant>This will return the Staging and Target branches to their state prior to these deployments, and return the project to the Import task.</constant>
                        </attribute>
                        <attribute name="atg.workflow.name">
                          <constant>returnToImportTask</constant>
                        </attribute>
                        <attribute name="atg.workflow.displayName">
                          <constant>Return To Import Task</constant>
                        </attribute>
                      </attributes>
                    </event>
                    <!--================================-->
                    <!--== Reopen project  -->
                    <!--================================-->
                    <action id="4.1.6.1.2.2.3.3.2">
                      <action-name>reopenProject</action-name>
                    </action>
                    <jump id="4.1.6.1.2.2.3.3.3" target="3"/>
                  </branch>
                </fork>
              </branch>
            </fork>
          </branch>
        </fork>
        <!--================================-->
        <!--== Check in project's workspace  -->
        <!--================================-->
        <action id="4.1.7">
          <action-name>checkInProject</action-name>
        </action>
        <!--================================-->
        <!--== Complete project  -->
        <!--================================-->
        <action id="4.1.8">
          <action-name>completeProject</action-name>
        </action>
        <!--================================-->
        <!--== Complete process  -->
        <!--================================-->
        <action id="4.1.9">
          <action-name>completeProcess</action-name>
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
            <constant>/Content Administration/import.wdl</constant>
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
              <constant>Delete Project</constant>
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
  </segment>
</process>
