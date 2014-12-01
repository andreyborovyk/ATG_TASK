<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1093873202920" enabled="true" last-modified-by="admin" modification-time="1263567941703">
<segment migrate-from="1097691645555,1214401597538" migrate-subjects="true">
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
<constant>/ListImport/project/listImport.wdl</constant>
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
<constant>Profile$role$epubUser:execute;Profile$role$campaignUser:execute;Profile$role$campaignManager:execute;Profile$role$campaignAdmin:execute;Profile$role$epubSuperAdmin:execute;Profile$role$epubManager:execute;Admin$role$managers-group:execute;Admin$role$administrators-group:execute;Profile$role$epubAdmin:execute</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>List Import</constant>
</attribute>
</attributes>
</event>
<!--================================-->
<!--== Declare repository asset Email List from repository ProcessData and item type Email List Import as required  -->
<!--================================-->
<action id="2">
<action-name>declareAsset</action-name>
<attributes>
<attribute name="atg.workflow.elementType">
<constant>declareAsset</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Email List</constant>
</attribute>
<attribute name="atg.workflow.assetRepositoryPath">
<constant>/atg/epub/process/ProcessDataRepository</constant>
</attribute>
<attribute name="atg.workflow.assetItemType">
<constant>emailListImport</constant>
</attribute>
<attribute name="atg.workflow.assetRequired">
<constant type="java.lang.Boolean">true</constant>
</attribute>
</attributes>
</action>
<!--================================-->
<!--== ImportList  -->
<!--================================-->
<label id="3">
<attributes>
<attribute name="atg.workflow.assignable">
<constant type="java.lang.Boolean">true</constant>
</attribute>
<attribute name="atg.workflow.elementType">
<constant>task</constant>
</attribute>
<attribute name="atg.workflow.acl">
<constant>Profile$role$campaignManager:write,execute;Profile$role$campaignUser:write,execute;Profile$role$epubAdmin:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Profile$role$campaignAdmin:write,execute;Profile$role$epubSuperAdmin:write,execute</constant>
</attribute>
<attribute name="atg.workflow.description">
<constant>Preview import file, assign mappings from file to repository, and initiate file import</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>ImportList</constant>
</attribute>
<attribute name="atg.workflow.taskSubject">
<constant>Email List</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>Import List</constant>
</attribute>
</attributes>
</label>
<fork exclusive="true" id="4">
<branch id="4.1">
<!--================================-->
<!--== Confirm Import  -->
<!--================================-->
<event id="4.1.1">
<event-name>atg.workflow.TaskOutcome</event-name>
<filter operator="eq">
<event-property>
<property-name>processName</property-name>
</event-property>
<constant>/ListImport/project/listImport.wdl</constant>
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
<constant>Taking this action confirms that the list has been imported and will move the process to the next step to create a profile group for the imported users</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Confirm Import</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>Confirm Import</constant>
</attribute>
</attributes>
</event>
<!--================================-->
<!--== Create Import Segment  -->
<!--================================-->
<label id="4.1.2">
<attributes>
<attribute name="atg.workflow.elementType">
<constant>task</constant>
</attribute>
<attribute name="atg.workflow.assignable">
<constant type="java.lang.Boolean">true</constant>
</attribute>
<attribute name="atg.workflow.acl">
<constant>Profile$role$campaignManager:write,execute;Profile$role$campaignAdmin:write,execute;Profile$role$campaignUser:write,execute;Profile$role$epubManager:write,execute;Profile$role$epubSuperAdmin:write,execute;Profile$role$epubUser:write,execute;Profile$role$epubAdmin:write,execute</constant>
</attribute>
<attribute name="atg.workflow.description">
<constant>Create a Segment that groups all users from an import together</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Create Import Segment</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>Create Import Segment</constant>
</attribute>
</attributes>
</label>
<fork exclusive="true" id="4.1.3">
<branch id="4.1.3.1">
<!--================================-->
<!--== Create Segment  -->
<!--================================-->
<event id="4.1.3.1.1">
<event-name>atg.workflow.TaskOutcome</event-name>
<filter operator="eq">
<event-property>
<property-name>processName</property-name>
</event-property>
<constant>/ListImport/project/listImport.wdl</constant>
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
<constant>4.1.3.1.1</constant>
</filter>
<attributes>
<attribute name="atg.workflow.elementType">
<constant>outcome</constant>
</attribute>
<attribute name="atg.workflow.description">
<constant>Creates the Import Segment</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Create Segment</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>Create Segment</constant>
</attribute>
</attributes>
</event>
<!--================================-->
<!--== Generate Import RepositoryGroup  -->
<!--================================-->
<action id="4.1.3.1.2">
<action-name>generateImportRepositoryGroup</action-name>
</action>
<!--================================-->
<!--== Check in project's workspace  -->
<!--================================-->
<action id="4.1.3.1.3">
<action-name>checkInProject</action-name>
</action>
<!--================================-->
<!--== Approve and deploy project to target Production  -->
<!--================================-->
<action id="4.1.3.1.4">
<action-name>approveAndDeployProject</action-name>
<action-param name="target">
<constant>Production</constant>
</action-param>
</action>
</branch>
<branch id="4.1.3.2">
<!--================================-->
<!--== Don't Create Segment  -->
<!--================================-->
<event id="4.1.3.2.1">
<event-name>atg.workflow.TaskOutcome</event-name>
<filter operator="eq">
<event-property>
<property-name>processName</property-name>
</event-property>
<constant>/ListImport/project/listImport.wdl</constant>
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
<constant>4.1.3.2.1</constant>
</filter>
<attributes>
<attribute name="atg.workflow.elementType">
<constant>outcome</constant>
</attribute>
<attribute name="atg.workflow.description">
<constant>Does not create an Import Segment</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Don't Create Segment</constant>
</attribute>
<attribute name="atg.workflow.displayName">
<constant>Don't Create Segment</constant>
</attribute>
</attributes>
</event>
<!--================================-->
<!--== Check in project's workspace  -->
<!--================================-->
<action id="4.1.3.2.2">
<action-name>checkInProject</action-name>
</action>
</branch>
</fork>
</branch>
<branch id="4.2">
<!--================================-->
<!--== Delete Project  -->
<!--================================-->
<event id="4.2.1">
<event-name>atg.workflow.TaskOutcome</event-name>
<filter operator="eq">
<event-property>
<property-name>processName</property-name>
</event-property>
<constant>/ListImport/project/listImport.wdl</constant>
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
<constant>Taking this action indicates that the import was aborted and that this project should be deleted</constant>
</attribute>
<attribute name="atg.workflow.name">
<constant>Delete Project</constant>
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
<!--== Change Project's Current project's Editable to false  -->
<!--================================-->
<action id="5">
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
<!--== Complete project  -->
<!--================================-->
<action id="6">
<action-name>completeProject</action-name>
</action>
</segment>
</process>
