<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer</h2><p>
<blockquote>
The ATG Database Initializer loads data from files and populates the
database, creating tables and doing data loads or other operations
against them.<p>

These data files are associated with each module through entries in 
the module's MANIFEST.MF file. There are three different types of data 
files:<br>
<ul>
  <li>DDLGen XML files, which encode table and index information in an XML format.
  <li>Repository template files, identical to the files used by the startSQLRepository tool, 
   which hold information on data items to be loaded into repositories.
  <li>SQL script files, which hold raw SQL and are sometimes database-specific.
</ul><p>

This tool has three distinct steps:<br>
<ol>
  <li>Locating the DataSource that connects to the database where the data should be stored.
    The DataSource itself must first be configured through the application server's native 
    interface or admin tools.
  <li>Choosing the modules and particular resource files to load, along with picking Repositories
    for the Repository template files.
  <li>Confirming the job, starting it, and monitoring its progress.
</ol><p>

This tool does not pre-check for most database errors, so taking actions that would
cause database errors (such as re-loading a single file multiple times) may well 
result in SQLExceptions.  Also, be careful when picking modules.  Many default modules (such as DCS and DCS.Versioned) have conflicting tables.  Please inspect any errors and adjust the resource files you 
are loading appropriately.<p>

If you already have a job running, you can <dsp:a href="monitor-jobs.jsp">skip directly
to the job monitoring page</dsp:a>.
</blockquote>

<!-- begin start form -->

<dsp:form action="choose-datasource.jsp" method="POST">

<!-- generate an initial jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" beanvalue="JobIdFormHandler.newJobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="JobIdFormHandler" />
</dsp:include>

<!-- button to go to next page -->

<input type="submit" value=" Start Initializer "/>

</dsp:form>

<!-- end start form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/start.jsp#2 $$Change: 651448 $--%>
