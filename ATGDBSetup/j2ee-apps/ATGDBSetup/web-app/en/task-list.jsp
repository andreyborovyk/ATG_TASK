<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/module/ModuleFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 2.2: Verify Resource File List</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 2.2: Verify Resource File List</h2><p>

<!-- begin task list form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="ModuleFormHandler.databaseTaskListSuccessURL" type="hidden" value="start-job.jsp"/>
<dsp:input bean="ModuleFormHandler.databaseTaskListTemplateSuccessURL" type="hidden" value="repositories.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="ModuleFormHandler" />
</dsp:include>

<!-- explanatory text -->

Choose the particular tasks that you wish to run.
If you run a task that has already been run, or which tries to create
a table that already exists in the database, the job will halt with
an error.<p>

<blockquote>

<!-- custom module list -->

<dsp:droplet name="ForEach">
  <dsp:param bean="ModuleFormHandler.moduleTaskInfos" name="array"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="element.hasTasks"/>
      <dsp:oparam name="true">
        <b><dsp:valueof param="element.moduleName"/></b><br>
        <ul>
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.hasTableCreationTasks"/>
          <dsp:oparam name="true">
            <li>Table Creation Files:
            <dsp:include page="display-tasks.jsp">
              <dsp:param name="tasks" param="element.tableCreationTasks"/>
            </dsp:include></li>   
           </dsp:oparam>
        </dsp:droplet>
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.hasInitialDataTasks"/>
          <dsp:oparam name="true">
            <li>Repository Data Loading Files:
            <dsp:include page="display-tasks.jsp">
              <dsp:param name="tasks" param="element.initialDataTasks"/>
            </dsp:include></li>   
           </dsp:oparam>
        </dsp:droplet>
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.hasDatabaseSqlTasks"/>
          <dsp:oparam name="true">
            <li>SQL Script Files:
            <dsp:include page="display-tasks.jsp">
              <dsp:param name="tasks" param="element.databaseSqlTasks"/>
            </dsp:include></li>   
           </dsp:oparam>
        </dsp:droplet>
        </ul>
       </dsp:oparam>
     </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
<p>

</blockquote>

<!-- submit buttons -->

<dsp:input bean="ModuleFormHandler.taskList" type="submit" value=" Next "/>

<!--
The following modules do not contain database initializer tasks:<p>

<blockquote>

<!-- taskless module list --><!--
<dsp:droplet name="ForEach">
  <dsp:param bean="ModuleFormHandler.moduleTaskInfos" name="array"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="element.hasTasks"/>
      <dsp:oparam name="false">
        <dsp:valueof param="element.moduleName"/><br>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

</blockquote>
<p>-->

</dsp:form>

<!-- end choose custom modules form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/task-list.jsp#2 $$Change: 651448 $--%>
