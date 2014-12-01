<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/module/ModuleFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 2.3: Choose Repositories for Template Tasks</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 2.3: Choose Repositories for Template Tasks</h2><p>

<!-- begin repository choice form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="ModuleFormHandler.databaseTemplateRepositorySuccessURL" type="hidden" value="start-job.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="ModuleFormHandler" />
</dsp:include>

<!-- explanatory text -->

For each Repository template resource, choose a repository
from the list provided or type in the Nucleus name:<p>

<blockquote>

<!-- repository choices for each template task -->

<dsp:droplet name="ForEach">
  <dsp:param bean="ModuleFormHandler.templateDatabaseTasks" name="array"/>
  <dsp:oparam name="output">
    <b><dsp:valueof param="element.moduleName"/>/<dsp:valueof param="element.resourceName"/></b><p>
    <blockquote>
    <!--Pick a known SQL Repository:<br>
    <dsp:param name="taskIndex" value="param:index"/>
    <dsp:droplet name="ForEach" >
      <dsp:param bean="ModuleFormHandler.knownRepositories" name="array"/>
      <dsp:param name="indexName" value="repositoryIndex"/>
      <dsp:oparam name="output">
        <dsp:input type="radio" bean="ModuleFormHandler.templatePickedRepositoryNames[param:index]" paramvalue="element"/><dsp:valueof param="element"/><BR>
      </dsp:oparam>
    </dsp:droplet>
    <p>-->
    Enter the Nucleus component name:<br>
    <dsp:input type="text" bean="ModuleFormHandler.templateTypedRepositoryNames[param:index]" paramvalue="element.repositoryName"/>
    <dsp:input type="hidden" bean="ModuleFormHandler.templateTaskNames[param:index]" paramvalue="element.key" size="70"/>
    </blockquote>
    <p>
  </dsp:oparam>
</dsp:droplet>

<!-- submit buttons -->

</blockquote>

<dsp:input bean="ModuleFormHandler.repositoriesForTemplates" type="submit" value=" Next "/>

</dsp:form>

<!-- end choose repository choice form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/repositories.jsp#2 $$Change: 651448 $--%>
