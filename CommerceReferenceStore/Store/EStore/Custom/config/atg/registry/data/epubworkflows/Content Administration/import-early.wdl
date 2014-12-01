<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1156534360401" enabled="true" last-modified-by="admin" modification-time="1156535224640">
  <segment migrate-from="1156534605601" migrate-subjects="true">
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
        <constant>/Content Administration/import-early.wdl</constant>
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
          <constant>An early model workflow to be used for automated imports</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Import Workflow  (early model)</constant>
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
            <constant>/Content Administration/import-early.wdl</constant>
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
        <!--================================-->
        <!--== Check in project's workspace  -->
        <!--================================-->
        <action id="4.1.4">
          <action-name>checkInProject</action-name>
        </action>
        <!--================================-->
        <!--== Approve and deploy project to all targets  -->
        <!--================================-->
        <action id="4.1.5">
          <action-name>approveAndDeployProjectForAllTargets</action-name>
        </action>
        <fork id="4.1.6" synchronized="true">
          <branch id="4.1.6.1">
            <!--================================-->
            <!--== waitForStagingDeploymentToComplete  -->
            <!--================================-->
            <label id="4.1.6.1.1">
              <attributes>
                <attribute name="atg.workflow.assignable">
                  <constant type="java.lang.Boolean">false</constant>
                </attribute>
                <attribute name="atg.workflow.elementType">
                  <constant>task</constant>
                </attribute>
                <attribute name="atg.workflow.description">
                  <constant>Waits for the deployment to staging to complete</constant>
                </attribute>
                <attribute name="atg.workflow.name">
                  <constant>waitForStagingDeploymentToComplete</constant>
                </attribute>
                <attribute name="atg.workflow.displayName">
                  <constant>Wait for Staging Deployment to Complete</constant>
                </attribute>
              </attributes>
            </label>
            <fork exclusive="true" id="4.1.6.1.2">
              <branch id="4.1.6.1.2.1">
                <!--================================-->
                <!--== Wait for deployment to complete on target Production  -->
                <!--================================-->
                <event id="4.1.6.1.2.1.1">
                  <event-name>atg.deployment.DeploymentStatus</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>targetId</property-name>
                    </event-property>
                    <constant></constant>
                  </filter>
                </event>
                <fork id="4.1.6.1.2.1.2">
                  <branch id="4.1.6.1.2.1.2.1">
                    <!--================================-->
                    <!--== Deployment completed event status is success on target Production  -->
                    <!--================================-->
                    <condition id="4.1.6.1.2.1.2.1.1">
                      <filter operator="deploymentCompleted">
                        <constant>1</constant>
                        <constant></constant>
                      </filter>
                    </condition>
                  </branch>
                  <branch id="4.1.6.1.2.1.2.2">
                    <!--================================-->
                    <!--== Deployment completed event status is failure on target Production  -->
                    <!--================================-->
                    <condition id="4.1.6.1.2.1.2.2.1">
                      <filter operator="deploymentCompleted">
                        <constant>0</constant>
                        <constant></constant>
                      </filter>
                    </condition>
                    <!--================================-->
                    <!--== retryStagingDeployment  -->
                    <!--================================-->
                    <label id="4.1.6.1.2.1.2.2.2">
                      <attributes>
                        <attribute name="atg.workflow.assignable">
                          <constant type="java.lang.Boolean">false</constant>
                        </attribute>
                        <attribute name="atg.workflow.elementType">
                          <constant>task</constant>
                        </attribute>
                        <attribute name="atg.workflow.acl">
                          <constant>Admin$role$administrators-group:write,execute;Profile$role$epubAdmin:write,execute;Profile$role$epubUser:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
                        </attribute>
                        <attribute name="atg.workflow.description">
                          <constant>Allows staging deployment to be retried</constant>
                        </attribute>
                        <attribute name="atg.workflow.name">
                          <constant>retryStagingDeployment</constant>
                        </attribute>
                        <attribute name="atg.workflow.displayName">
                          <constant>Retry Staging Deployment</constant>
                        </attribute>
                      </attributes>
                    </label>
                    <fork exclusive="true" id="4.1.6.1.2.1.2.2.3">
                      <branch id="4.1.6.1.2.1.2.2.3.1">
                        <!--================================-->
                        <!--== revertStaging  -->
                        <!--================================-->
                        <event id="4.1.6.1.2.1.2.2.3.1.1">
                          <event-name>atg.workflow.TaskOutcome</event-name>
                          <filter operator="eq">
                            <event-property>
                              <property-name>processName</property-name>
                            </event-property>
                            <constant>/Content Administration/import-early.wdl</constant>
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
                            <constant>4.1.6.1.2.1.2.2.3.1.1</constant>
                          </filter>
                          <attributes>
                            <attribute name="atg.workflow.elementType">
                              <constant>outcome</constant>
                            </attribute>
                            <attribute name="atg.workflow.description">
                              <constant>Reverts the deployment from staging</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>revertStaging</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Revert Staging</constant>
                            </attribute>
                          </attributes>
                        </event>
                        <!--================================-->
                        <!--== Revert assets immediately on target Production  -->
                        <!--================================-->
                        <action id="4.1.6.1.2.1.2.2.3.1.2">
                          <action-name>revertAssetsOnTargetNow</action-name>
                          <action-param name="target">
                            <constant></constant>
                          </action-param>
                        </action>
                        <!--================================-->
                        <!--== waitForStagingRevert  -->
                        <!--================================-->
                        <label id="4.1.6.1.2.1.2.2.3.1.3">
                          <attributes>
                            <attribute name="atg.workflow.assignable">
                              <constant type="java.lang.Boolean">false</constant>
                            </attribute>
                            <attribute name="atg.workflow.elementType">
                              <constant>task</constant>
                            </attribute>
                            <attribute name="atg.workflow.acl">
                              <constant>Profile$role$epubAdmin:write,execute;Profile$role$epubUser:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubManager:write,execute;Admin$role$administrators-group:write,execute</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>waitForStagingRevert</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Wait For Staging Revert to Complete</constant>
                            </attribute>
                          </attributes>
                        </label>
                        <fork exclusive="true" id="4.1.6.1.2.1.2.2.3.1.4">
                          <branch id="4.1.6.1.2.1.2.2.3.1.4.1">
                            <!--================================-->
                            <!--== Wait for deployment to complete on target Production  -->
                            <!--================================-->
                            <event id="4.1.6.1.2.1.2.2.3.1.4.1.1">
                              <event-name>atg.deployment.DeploymentStatus</event-name>
                              <filter operator="eq">
                                <event-property>
                                  <property-name>targetId</property-name>
                                </event-property>
                                <constant></constant>
                              </filter>
                            </event>
                            <fork id="4.1.6.1.2.1.2.2.3.1.4.1.2">
                              <branch id="4.1.6.1.2.1.2.2.3.1.4.1.2.1">
                                <!--================================-->
                                <!--== Deployment completed event status is success on target Production  -->
                                <!--================================-->
                                <condition id="4.1.6.1.2.1.2.2.3.1.4.1.2.1.1">
                                  <filter operator="deploymentCompleted">
                                    <constant>1</constant>
                                    <constant></constant>
                                  </filter>
                                </condition>
                                <!--================================-->
                                <!--== Hide project from target Production  -->
                                <!--================================-->
                                <!--<action id="4.1.6.1.2.1.2.2.3.1.4.1.2.1.2">
                                  <action-name>hideProject</action-name>
                                  <action-param name="target">
                                    <constant></constant>
                                  </action-param>
                                </action>-->
                              </branch>
                              <branch id="4.1.6.1.2.1.2.2.3.1.4.1.2.2">
                                <!--================================-->
                                <!--== Deployment completed event status is failure on target Production  -->
                                <!--================================-->
                                <condition id="4.1.6.1.2.1.2.2.3.1.4.1.2.2.1">
                                  <filter operator="deploymentCompleted">
                                    <constant>0</constant>
                                    <constant></constant>
                                  </filter>
                                </condition>
                                <jump id="4.1.6.1.2.1.2.2.3.1.4.1.2.2.2" target="4.1.6.1.2.1.2.2.2"/>
                              </branch>
                            </fork>
                          </branch>
                          <branch id="4.1.6.1.2.1.2.2.3.1.4.2">
                            <!--================================-->
                            <!--== returnToPreviousTask  -->
                            <!--================================-->
                            <event id="4.1.6.1.2.1.2.2.3.1.4.2.1">
                              <event-name>atg.workflow.TaskOutcome</event-name>
                              <filter operator="eq">
                                <event-property>
                                  <property-name>processName</property-name>
                                </event-property>
                                <constant>/Content Administration/import-early.wdl</constant>
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
                                <constant>4.1.6.1.2.1.2.2.3.1.4.2.1</constant>
                              </filter>
                              <attributes>
                                <attribute name="atg.workflow.elementType">
                                  <constant>outcome</constant>
                                </attribute>
                                <attribute name="atg.workflow.description">
                                  <constant>Returns to the previous task. Note the revert operation may still be running.</constant>
                                </attribute>
                                <attribute name="atg.workflow.name">
                                  <constant>returnToPreviousTask</constant>
                                </attribute>
                                <attribute name="atg.workflow.displayName">
                                  <constant>Return to Previous Task</constant>
                                </attribute>
                              </attributes>
                            </event>
                            <jump id="4.1.6.1.2.1.2.2.3.1.4.2.2" target="4.1.6.1.2.1.2.2.2"/>
                          </branch>
                        </fork>
                      </branch>
                      <branch id="4.1.6.1.2.1.2.2.3.2">
                        <!--================================-->
                        <!--== retryStagingDeployment  -->
                        <!--================================-->
                        <event id="4.1.6.1.2.1.2.2.3.2.1">
                          <event-name>atg.workflow.TaskOutcome</event-name>
                          <filter operator="eq">
                            <event-property>
                              <property-name>processName</property-name>
                            </event-property>
                            <constant>/Content Administration/import-early.wdl</constant>
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
                            <constant>4.1.6.1.2.1.2.2.3.2.1</constant>
                          </filter>
                          <attributes>
                            <attribute name="atg.workflow.elementType">
                              <constant>outcome</constant>
                            </attribute>
                            <attribute name="atg.workflow.description">
                              <constant>Allow for retrying the deployment to staging</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>retryStagingDeployment</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Retry Staging Deployment</constant>
                            </attribute>
                          </attributes>
                        </event>
                        <!--================================-->
                        <!--== Approve and deploy project to target Production  -->
                        <!--================================-->
                        <action id="4.1.6.1.2.1.2.2.3.2.2">
                          <action-name>approveAndDeployProject</action-name>
                          <action-param name="target">
                            <constant></constant>
                          </action-param>
                        </action>
                        <jump id="4.1.6.1.2.1.2.2.3.2.3" target="4.1.6.1.1"/>
                      </branch>
                    </fork>
                  </branch>
                </fork>
              </branch>
              <branch id="4.1.6.1.2.2">
                <!--================================-->
                <!--== returnToImportTask  -->
                <!--================================-->
                <event id="4.1.6.1.2.2.1">
                  <event-name>atg.workflow.TaskOutcome</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>processName</property-name>
                    </event-property>
                    <constant>/Content Administration/import-early.wdl</constant>
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
                    <constant>4.1.6.1.2.2.1</constant>
                  </filter>
                  <attributes>
                    <attribute name="atg.workflow.elementType">
                      <constant>outcome</constant>
                    </attribute>
                    <attribute name="atg.workflow.description">
                      <constant>Returns to the author task. Note that the deployment may still be running if this outcome is selected</constant>
                    </attribute>
                    <attribute name="atg.workflow.name">
                      <constant>returnToImportTask</constant>
                    </attribute>
                    <attribute name="atg.workflow.displayName">
                      <constant>Return to Import Task</constant>
                    </attribute>
                  </attributes>
                </event>
                <jump id="4.1.6.1.2.2.2" target="3"/>
              </branch>
            </fork>
          </branch>
          <branch id="4.1.6.2">
            <!--================================-->
            <!--== waitForProductionDeploymentToComplete  -->
            <!--================================-->
            <label id="4.1.6.2.1">
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
            <fork exclusive="true" id="4.1.6.2.2">
              <branch id="4.1.6.2.2.1">
                <!--================================-->
                <!--== Wait for deployment to complete on target Production  -->
                <!--================================-->
                <event id="4.1.6.2.2.1.1">
                  <event-name>atg.deployment.DeploymentStatus</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>targetId</property-name>
                    </event-property>
                    <constant></constant>
                  </filter>
                </event>
                <fork id="4.1.6.2.2.1.2">
                  <branch id="4.1.6.2.2.1.2.1">
                    <!--================================-->
                    <!--== Deployment completed event status is success on target Production  -->
                    <!--================================-->
                    <condition id="4.1.6.2.2.1.2.1.1">
                      <filter operator="deploymentCompleted">
                        <constant>1</constant>
                        <constant></constant>
                      </filter>
                    </condition>
                  </branch>
                  <branch id="4.1.6.2.2.1.2.2">
                    <!--================================-->
                    <!--== Deployment completed event status is failure on target Production  -->
                    <!--================================-->
                    <condition id="4.1.6.2.2.1.2.2.1">
                      <filter operator="deploymentCompleted">
                        <constant>0</constant>
                        <constant></constant>
                      </filter>
                    </condition>
                    <!--================================-->
                    <!--== retryProductionDeployment  -->
                    <!--================================-->
                    <label id="4.1.6.2.2.1.2.2.2">
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
                    <fork exclusive="true" id="4.1.6.2.2.1.2.2.3">
                      <branch id="4.1.6.2.2.1.2.2.3.1">
                        <!--================================-->
                        <!--== revertProduction  -->
                        <!--================================-->
                        <event id="4.1.6.2.2.1.2.2.3.1.1">
                          <event-name>atg.workflow.TaskOutcome</event-name>
                          <filter operator="eq">
                            <event-property>
                              <property-name>processName</property-name>
                            </event-property>
                            <constant>/Content Administration/import-early.wdl</constant>
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
                            <constant>4.1.6.2.2.1.2.2.3.1.1</constant>
                          </filter>
                          <attributes>
                            <attribute name="atg.workflow.elementType">
                              <constant>outcome</constant>
                            </attribute>
                            <attribute name="atg.workflow.description">
                              <constant>Reverts the deployment from production</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>revertProduction</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Revert Production</constant>
                            </attribute>
                          </attributes>
                        </event>
                        <!--================================-->
                        <!--== Revert assets immediately on target Production  -->
                        <!--================================-->
                        <action id="4.1.6.2.2.1.2.2.3.1.2">
                          <action-name>revertAssetsOnTargetNow</action-name>
                          <action-param name="target">
                            <constant></constant>
                          </action-param>
                        </action>
                        <!--================================-->
                        <!--== waitForProductionRevert  -->
                        <!--================================-->
                        <label id="4.1.6.2.2.1.2.2.3.1.3">
                          <attributes>
                            <attribute name="atg.workflow.assignable">
                              <constant type="java.lang.Boolean">false</constant>
                            </attribute>
                            <attribute name="atg.workflow.elementType">
                              <constant>task</constant>
                            </attribute>
                            <attribute name="atg.workflow.acl">
                              <constant>Profile$role$epubSuperAdmin:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubAdmin:write,execute;Profile$role$epubUser:write,execute;Admin$role$administrators-group:write,execute;Profile$role$epubManager:write,execute</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>waitForProductionRevert</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Wait for Production Revert</constant>
                            </attribute>
                          </attributes>
                        </label>
                        <fork exclusive="true" id="4.1.6.2.2.1.2.2.3.1.4">
                          <branch id="4.1.6.2.2.1.2.2.3.1.4.1">
                            <!--================================-->
                            <!--== Wait for deployment to complete on target Production  -->
                            <!--================================-->
                            <event id="4.1.6.2.2.1.2.2.3.1.4.1.1">
                              <event-name>atg.deployment.DeploymentStatus</event-name>
                              <filter operator="eq">
                                <event-property>
                                  <property-name>targetId</property-name>
                                </event-property>
                                <constant></constant>
                              </filter>
                            </event>
                            <fork id="4.1.6.2.2.1.2.2.3.1.4.1.2">
                              <branch id="4.1.6.2.2.1.2.2.3.1.4.1.2.1">
                                <!--================================-->
                                <!--== Deployment completed event status is success on target Production  -->
                                <!--================================-->
                                <condition id="4.1.6.2.2.1.2.2.3.1.4.1.2.1.1">
                                  <filter operator="deploymentCompleted">
                                    <constant>1</constant>
                                    <constant></constant>
                                  </filter>
                                </condition>
                                <!--================================-->
                                <!--== Hide project from target Production  -->
                                <!--================================-->
                                <!--<action id="4.1.6.2.2.1.2.2.3.1.4.1.2.1.2">
                                  <action-name>hideProject</action-name>
                                  <action-param name="target">
                                    <constant></constant>
                                  </action-param>
                                </action>-->
                              </branch>
                              <branch id="4.1.6.2.2.1.2.2.3.1.4.1.2.2">
                                <!--================================-->
                                <!--== Deployment completed event status is failure on target Production  -->
                                <!--================================-->
                                <condition id="4.1.6.2.2.1.2.2.3.1.4.1.2.2.1">
                                  <filter operator="deploymentCompleted">
                                    <constant>0</constant>
                                    <constant></constant>
                                  </filter>
                                </condition>
                                <jump id="4.1.6.2.2.1.2.2.3.1.4.1.2.2.2" target="4.1.6.2.2.1.2.2.2"/>
                              </branch>
                            </fork>
                          </branch>
                          <branch id="4.1.6.2.2.1.2.2.3.1.4.2">
                            <!--================================-->
                            <!--== returnToPreviousTask  -->
                            <!--================================-->
                            <event id="4.1.6.2.2.1.2.2.3.1.4.2.1">
                              <event-name>atg.workflow.TaskOutcome</event-name>
                              <filter operator="eq">
                                <event-property>
                                  <property-name>processName</property-name>
                                </event-property>
                                <constant>/Content Administration/import-early.wdl</constant>
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
                                <constant>4.1.6.2.2.1.2.2.3.1.4.2.1</constant>
                              </filter>
                              <attributes>
                                <attribute name="atg.workflow.elementType">
                                  <constant>outcome</constant>
                                </attribute>
                                <attribute name="atg.workflow.description">
                                  <constant>Returns to the previous task. Note the revert operation may still be running.</constant>
                                </attribute>
                                <attribute name="atg.workflow.name">
                                  <constant>returnToPreviousTask</constant>
                                </attribute>
                                <attribute name="atg.workflow.displayName">
                                  <constant>Return to Previous Task</constant>
                                </attribute>
                              </attributes>
                            </event>
                            <jump id="4.1.6.2.2.1.2.2.3.1.4.2.2" target="4.1.6.2.2.1.2.2.2"/>
                          </branch>
                        </fork>
                      </branch>
                      <branch id="4.1.6.2.2.1.2.2.3.2">
                        <!--================================-->
                        <!--== retryProductionDeployment  -->
                        <!--================================-->
                        <event id="4.1.6.2.2.1.2.2.3.2.1">
                          <event-name>atg.workflow.TaskOutcome</event-name>
                          <filter operator="eq">
                            <event-property>
                              <property-name>processName</property-name>
                            </event-property>
                            <constant>/Content Administration/import-early.wdl</constant>
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
                            <constant>4.1.6.2.2.1.2.2.3.2.1</constant>
                          </filter>
                          <attributes>
                            <attribute name="atg.workflow.elementType">
                              <constant>outcome</constant>
                            </attribute>
                            <attribute name="atg.workflow.description">
                              <constant>Allow for retrying the deployment to production</constant>
                            </attribute>
                            <attribute name="atg.workflow.name">
                              <constant>retryProductionDeployment</constant>
                            </attribute>
                            <attribute name="atg.workflow.displayName">
                              <constant>Retry Production Deployment</constant>
                            </attribute>
                          </attributes>
                        </event>
                        <!--================================-->
                        <!--== Approve and deploy project to target Production  -->
                        <!--================================-->
                        <action id="4.1.6.2.2.1.2.2.3.2.2">
                          <action-name>approveAndDeployProject</action-name>
                          <action-param name="target">
                            <constant></constant>
                          </action-param>
                        </action>
                        <jump id="4.1.6.2.2.1.2.2.3.2.3" target="4.1.6.2.1"/>
                      </branch>
                    </fork>
                  </branch>
                </fork>
              </branch>
              <branch id="4.1.6.2.2.2">
                <!--================================-->
                <!--== returnToImportTask  -->
                <!--================================-->
                <event id="4.1.6.2.2.2.1">
                  <event-name>atg.workflow.TaskOutcome</event-name>
                  <filter operator="eq">
                    <event-property>
                      <property-name>processName</property-name>
                    </event-property>
                    <constant>/Content Administration/import-early.wdl</constant>
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
                    <constant>4.1.6.2.2.2.1</constant>
                  </filter>
                  <attributes>
                    <attribute name="atg.workflow.elementType">
                      <constant>outcome</constant>
                    </attribute>
                    <attribute name="atg.workflow.description">
                      <constant>Returns to the author task. Note that the deployment may still be running if this outcome is selected</constant>
                    </attribute>
                    <attribute name="atg.workflow.name">
                      <constant>returnToImportTask</constant>
                    </attribute>
                    <attribute name="atg.workflow.displayName">
                      <constant>Return to Import Task</constant>
                    </attribute>
                  </attributes>
                </event>
                <jump id="4.1.6.2.2.2.2" target="3"/>
              </branch>
            </fork>
          </branch>
        </fork>
        <!--================================-->
        <!--== Complete project  -->
        <!--================================-->
        <action id="4.1.7">
          <action-name>completeProject</action-name>
        </action>
        <!--================================-->
        <!--== Complete process  -->
        <!--================================-->
        <action id="4.1.8">
          <action-name>completeProcess</action-name>
        </action>
      </branch>
    </fork>
  </segment>
</process>