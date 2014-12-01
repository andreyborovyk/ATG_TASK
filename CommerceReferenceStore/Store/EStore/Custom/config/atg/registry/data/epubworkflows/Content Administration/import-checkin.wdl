<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">

<process author="admin" creation-time="1195656757000" enabled="true" last-modified-by="admin" modification-time="1195658447598">
  <segment migrate-from="1195656827818" migrate-subjects="true">
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
        <constant>/Content Administration/import-checkin.wdl</constant>
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
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">false</constant>
        </attribute>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
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
            <constant>/Content Administration/import-checkin.wdl</constant>
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
      </branch>
    </fork>
  </segment>
</process>
