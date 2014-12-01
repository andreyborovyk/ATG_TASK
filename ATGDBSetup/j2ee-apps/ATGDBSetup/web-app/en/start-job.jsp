<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobDataDroplet"/>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 3: Confirm Job Start</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 3: Confirm Job Start</h2><p>

<!-- begin start job form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="JobFormHandler.startJobSuccessURL" type="hidden" value="monitor-jobs.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.staticJobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="JobFormHandler" />
</dsp:include>

<!-- explanatory text -->

Confirm the details of the job you have assembled:<p>

<blockquote>
<!-- display the job -->

<dsp:droplet name="JobDataDroplet">
  <dsp:oparam name="output">
    <b>Job <dsp:valueof bean="JobIdFormHandler.jobId"/></b><p>
    DataSource: <dsp:valueof param="jobData.dataSourceName"/><br>
    Database Vendor: <dsp:valueof param="jobData.canonicalDatabaseVendorName"/><br>
    Internationalized: <dsp:valueof param="jobData.internationalizedDatabase"/><br>
    Transactional by Task: <dsp:valueof param="jobData.tasksTransactional"/><p>
    
    Tasks:<br>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="jobData.databaseTasks"/>
      <dsp:oparam name="output">
        <dsp:valueof param="element.moduleName"/>: <dsp:valueof param="element.baseResourceName"/><br>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet><p>

<!-- submit buttons -->

</blockquote>

<dsp:input bean="JobFormHandler.startJob" type="submit" value=" Start Database Job "/>

</dsp:form>

<!-- end start job form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/start-job.jsp#2 $$Change: 651448 $--%>
