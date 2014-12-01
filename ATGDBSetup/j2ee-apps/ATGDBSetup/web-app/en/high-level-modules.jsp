<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<%@ page import="atg.dbsetup.module.ModuleFormHandler"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/module/ModuleFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/datasource/DataSourceFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 2: Choose Modules</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 2: Choose Modules</h2><p>

<!-- begin choose module options form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="ModuleFormHandler.moduleOptionSuccessURL" type="hidden" value="task-list.jsp"/>
<dsp:input bean="ModuleFormHandler.moduleOptionCustomSuccessURL" type="hidden" value="custom-modules.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="ModuleFormHandler" />
</dsp:include>

<!-- explanatory text -->

Choose the set of modules to load data from:<p>

<blockquote>

<!-- module options -->

<!--<dsp:input bean="ModuleFormHandler.moduleOption" type="radio" value="<%= ModuleFormHandler.DBSETUP_INSTALLED_MODULES %>"/>
Create tables and data for all installed modules.<br>-->

<dsp:input bean="ModuleFormHandler.moduleOption" type="radio" value="<%= ModuleFormHandler.DBSETUP_RUNNING_MODULES %>"/>
Create tables and data for all running modules.<br><br>


<dsp:input bean="ModuleFormHandler.moduleOption" type="radio" value="<%= ModuleFormHandler.DBSETUP_CUSTOM_MODULES %>"/>
Choose a custom set of modules from available modules.<br>
<br>

</blockquote>

<!-- submit buttons -->

<dsp:input bean="ModuleFormHandler.chooseModuleOption" type="submit" value=" Next "/>

</dsp:form>

<!-- end choose module options form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/high-level-modules.jsp#2 $$Change: 651448 $--%>
