<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/datasource/DataSourceFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 1.1: Input Database and Job Details</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 1.1: Input Database and Job Details</h2><p>

<!-- begin database details form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="DataSourceFormHandler.databaseDetailsSuccessURL" type="hidden" value="high-level-modules.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="DataSourceFormHandler" />
</dsp:include>

<blockquote>

<!-- schema name -->
<!-- no schema name functionality for this
revision - too difficult to handle in for repository template objects
<h3>Schema Name</h3>
Schema name to load tables and data into:
<dsp:input bean="DataSourceFormHandler.schemaName"/><br>
Note that the schema must be writable from the chosen DataSource.<br>
-->

<!-- internationalization -->

<h3>Internationalization</h3>
<dsp:input bean="DataSourceFormHandler.internationalized" type="checkbox"/>
Should the loader produce internationalized (i.e. NVARCHAR)
tables?<br>

<!-- store progress -->

<h3>Save Database Initializer Progress</h3>
<dsp:input bean="DataSourceFormHandler.storeProgress" type="checkbox"/>
Should the loader store its progress?<br><br>
If you check this box, the tool will
automatically create a table named das_dbsetup_history (if it does not already
exist) and save information on which initializer tasks have been run.<br>

<!-- transactional -->

<h3>Transaction Granularity</h3>
<dsp:input bean="DataSourceFormHandler.tasksTransactional" type="checkbox"/>
Should the loader manage transactions at the resource level?<br><br>
If unchecked,
the statements will be run outside of transactions.  If checked, the loader
will run each task (that is, resource file) in a single transaction, 
ensuring that the task either completely succeeds or entirely fails.

If you have large data loads,
checking this box may result in lengthy transaction logs and database delays.<br>

</blockquote>
<!-- submit button -->

<dsp:input bean="DataSourceFormHandler.databaseDetails" type="submit" value=" Next "/>

<!-- end database details form -->
</dsp:form>

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/database-details.jsp#2 $$Change: 651448 $--%>
