<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/module/ModuleFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 2.1: Custom Module List</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 2.1: Custom Module List</h2><p>

<!-- begin choose custom modules form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="ModuleFormHandler.customModuleOptionSuccessURL" type="hidden" value="task-list.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="error.jsp">
  <dsp:param name="formHandler" bean="ModuleFormHandler" />
</dsp:include>

<!-- explanatory text -->

Choose a custom set of modules to load tasks from.<br>
Be aware that some
modules may contain conflicting tables, for example DCS and DCS.Versioned.<p>

<blockquote>

<!-- custom module list -->
<dsp:droplet name="ForEach">
  <dsp:param bean="ModuleFormHandler.moduleTaskInfos" name="array"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="element.hasTasks"/>
      <dsp:oparam name="true">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.previouslyRun"/>
          <dsp:oparam name="true">
            <dsp:input bean="ModuleFormHandler.customModuleNames"  type="checkbox" paramvalue="element.moduleName" checked="<%= false %>"/>
            <dsp:valueof param="element.moduleName"/> (previously run)<br>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:input bean="ModuleFormHandler.customModuleNames"  type="checkbox" paramvalue="element.moduleName"/>
            <dsp:valueof param="element.moduleName"/><br>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

</blockquote>
<p>


<!-- submit buttons -->

<dsp:input bean="ModuleFormHandler.customModuleList" type="submit" value=" Next "/><p><p>

The following modules do not contain database initializer tasks:<p>

<blockquote>

<!-- taskless module list -->
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
<p>

</dsp:form>

<!-- end choose custom modules form -->

</blockquote>
</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/custom-modules.jsp#2 $$Change: 651448 $--%>
