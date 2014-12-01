<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<%@ page import="atg.dbsetup.job.JobThread"%>
<%@ page import="atg.dbsetup.job.DatabaseResult"%>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobProgressDroplet"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/ContainerExceptionDroplet"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/DatabaseJobManager"/>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>


<HTML><HEAD>
<dsp:droplet name="Switch">
  <dsp:param name="value" bean="DatabaseJobManager.hasUnfinishedJobs"/>
  <dsp:oparam name="true">
    <META HTTP-EQUIV="Refresh" CONTENT="5">
  </dsp:oparam>
</dsp:droplet>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer: Job Monitoring</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer: Job Monitoring</h2><p>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="JobFormHandler" />
</dsp:include>

<!-- explanatory text -->

This page displays a list of database loading 
jobs with the option to halt running jobs.<p>

<dsp:droplet name="Switch">
  <dsp:param name="value" bean="DatabaseJobManager.hasUnfinishedJobs"/>
  <dsp:oparam name="false">
     All jobs have finished.<p>
     <form action="/ATGDBSetup" method="GET">
       <input type="submit" value=" Return to Start "/>
     </form>
     <form action="/" method="GET">
       <input type="submit" value=" Return to Site Home "/>
     </form><p>
  </dsp:oparam>
</dsp:droplet>

<blockquote>
<!-- begin job monitoring display -->

<% boolean inRunningJobs = true; 
   boolean printedRunning = false; %>
<dsp:droplet name="JobProgressDroplet">
  <dsp:oparam name="output">

    <dsp:droplet name="Switch">
      <dsp:param name="value" param="job.state"/>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_NOT_STARTED %>">
        <% inRunningJobs = true; %>
        <% if (!printedRunning) {  %>
          <hr><h3>Running Jobs</h3><p>
        <% } 
           printedRunning = true; %>
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_RUNNING %>">
        <% inRunningJobs = true; %>
        <% if (!printedRunning) {  %>
          <hr><h3>Running Jobs</h3><p>
        <% } 
           printedRunning = true; %>
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_RUNNING_WITH_ERRORS %>">
        <% inRunningJobs = true; %>
        <% if (!printedRunning) {  %>
          <hr><h3>Running Jobs</h3><p>
        <% } 
           printedRunning = true; %>
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_ERROR %>">
        <% if (inRunningJobs) {  %>
          <hr><h3>Completed Jobs</h3><p>
        <% }
           inRunningJobs = false; %>      
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_SUCCESS %>">
        <% if (inRunningJobs) {  %>
          <hr><h3>Completed Jobs</h3><p>
        <% }
           inRunningJobs = false; %>
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_FINISHED_WITH_ERRORS %>">
        <% if (inRunningJobs) {  %>
          <hr><h3>Completed Jobs</h3><p>
        <% }
           inRunningJobs = false; %>
      </dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_HALTED %>">
        <% if (inRunningJobs) {  %>
          <hr><h3>Completed Jobs</h3><p>
        <% }
           inRunningJobs = false; %>
      </dsp:oparam>
    </dsp:droplet>    

    <!-- job display -->
    <b>Job <dsp:valueof param="job.jobId"/></b><p>
    <blockquote>
    Job Status:
    <dsp:droplet name="Switch">
		  <dsp:param name="value" param="job.state"/>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_NOT_STARTED %>">Not Started</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_RUNNING %>">Running</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_RUNNING_WITH_ERRORS %>">Encountered Errors, Still Running</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_ERROR %>">Halted Due to Errors</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_SUCCESS %>">Finished With No Errors</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_FINISHED_WITH_ERRORS %>">Finished, Encountered Errors</dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_HALTED %>">Halted by User Request</dsp:oparam>
      <dsp:oparam name="default">Unrecognized Status</dsp:oparam>
    </dsp:droplet><p>

    DataSource: <dsp:valueof param="job.dataSourceName"/><br>
    Database Vendor: <dsp:valueof param="job.databaseVendor"/><br>
    Internationalized: <dsp:valueof param="job.internationalized"/><br>
    Transactional by Task: <dsp:valueof param="job.tasksTransactional"/><p>

    
    <!-- current task display -->
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="currentTasks"/>
      <dsp:oparam name="outputStart">
        Currently Running Task:<br>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:valueof param="element.databaseTask.moduleName"/>: <dsp:valueof param="element.databaseTask.baseResourceName"/>
        <br>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        <p>
      </dsp:oparam>
    </dsp:droplet>
    
    <!-- error tasks display -->
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="errorTasks"/>
      <dsp:oparam name="outputStart">
        Tasks that caused errors:<br>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:valueof param="element.databaseTask.moduleName"/>: <dsp:valueof param="element.databaseTask.baseResourceName"/>
        <br>
        <dsp:droplet name="IsNull">
          <dsp:param name="value" param="element.exception"/>
          <dsp:oparam name="false">
            <blockquote>
            Error Message:
            <dsp:droplet name="ContainerExceptionDroplet">
              <dsp:param name="exception" param="element.exception"/>
              <dsp:oparam name="output">
                <dsp:valueof param="message"/>
              </dsp:oparam>
            </dsp:droplet>
            </blockquote>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        <p>
      </dsp:oparam>
    </dsp:droplet>

    <!-- not started tasks display -->
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="notStartedTasks"/>
      <dsp:oparam name="outputStart">
        Tasks that have not yet been run:<br>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:valueof param="element.databaseTask.moduleName"/>: <dsp:valueof param="element.databaseTask.baseResourceName"/>
        <br>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        <p>
      </dsp:oparam>
    </dsp:droplet>

    <!-- completed tasks display -->
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="completedTasks"/>
      <dsp:oparam name="outputStart">
        Completed Tasks:<br>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:valueof param="element.databaseTask.moduleName"/>: <dsp:valueof param="element.databaseTask.baseResourceName"/>
        <br>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
         <p>
      </dsp:oparam>
    </dsp:droplet>

    <!-- Only display stop job button for running tasks -->
    <dsp:droplet name="Switch">
		  <dsp:param name="value" param="job.state"/>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_NOT_STARTED %>"></dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_ERROR %>"></dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_SUCCESS %>"></dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_FINISHED_WITH_ERRORS %>"></dsp:oparam>
      <dsp:oparam name="<%= JobThread.DBSETUP_JOB_HALTED %>"></dsp:oparam>
      <dsp:oparam name="default">
        <!-- begin stop job form -->

        <dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

        <!-- save the jobId -->
        <dsp:input type="hidden" bean="JobIdFormHandler.jobId" paramvalue="job.jobId" priority="-5"/>

        <!-- submit buttons -->
        <dsp:input bean="JobFormHandler.startJob" type="submit" value=" Stop "/>

        </dsp:form>
        <!-- end stop job form -->
      </dsp:oparam>
    </dsp:droplet>
    
    </blockquote>
  </dsp:oparam>
</dsp:droplet>
</blockquote>
<!-- end job monitoring display -->

</blockquote>
</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/monitor-jobs.jsp#2 $$Change: 651448 $--%>
