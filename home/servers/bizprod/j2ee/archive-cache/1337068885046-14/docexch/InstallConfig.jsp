<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>


<%-- 
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<paf:InitializeGearEnvironment id="pafEnv">
<paf:PortalAdministratorCheck>
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">

<html>
<head>
<title>Install Config</Title>
<style  type="text/css">
 <!--

body { font-family:verdana,arial,geneva,helvetica,sans-serif ; }
 font { font-family:verdana,arial,geneva,helvetica,sans-serif ; }

		
 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}

 a.portaladmin_nav       { color: #4D4E4F; text-decoration: none; }
 a.portaladmin_leftnav   { color: #4D4E4F; text-decoration: none; }
 
 a.breadcrumb   { font-size: 10px; text-decoration:none; }
 a.gear_nav     { font-weight:700; text-transform:lowercase; text-decoration:none; }
 a.gear_content { font-weight:300 }
 a.admin_link   { font-size: 10px; font-weight:300; text-decoration:none; }

 .smaller {font-size:10px; }
 .small   {font-size:12px; }
 .medium  {font-size:13px; }
 .large   {font-size:15px; }
 .larger  {font-size:17px; }

 .smaller_bold   {font-size:10px; font-weight:700 }
 .small_bold     {font-size:12px; font-weight:700 }
 .medium_bold    {font-size:13px; font-weight:700 }
 .large_bold     {font-size:15px; font-weight:700 }
 .larger_bold    {font-size:17px; font-weight:700 }
 .humungous_bold { font-size:22px;font-weight:700}
 
 .breadcrumb_link {font-size:10px; color : 0000cc}
 .small_link {font-size:12px; color : 0000cc}
 
 
 .info   {font-size:10px; color : 000000}
 .error  {font-size:10px; color : cc0000}
 
 .helpertext {font-size:10px; color : 333333}
 .adminbody  {font-size:10px; color : 333333}
 .subheader  {font-size:10px; color : 333333; font-weight:700}
 .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
 .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}

 -->
 </style>

</head>

<body bgcolor="#ffffff">
<br>


<i18n:bundle baseName="atg.portal.gear.docexch.DocExchInstallConfigResources" changeResponseLocale="false" />
<i18n:message id="i18n_link_mapping" key="config-repmapping-link"/>
<i18n:message id="i18n_link_limitation" key="config-replimitation-link"/>

<%
   String origURI= dexpage.getRelativeUrl("/InstallConfig.jsp");
   String geardefID = pafEnv.getGearDefinition().getId();
   String geardefName = pafEnv.getGearDefinition().getName(response.getLocale());

   String configPage = request.getParameter("config_page");
   if (configPage == null)
      configPage = "unset";

   String mappingStyle   = (configPage.equals("limitations"))   ? " class='smaller'" : 
       " style='text-decoration:none;color:#000000;font-weight:700'";
   String limitationStyle   = (configPage.equals("limitations"))   ?  
       " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
%>


  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit"><img src='images/write.gif' height="15" width="28" alt="" border="0">
      <i18n:message key="installConfigMainHeading">
        <i18n:messageArg value="<%= geardefName %>"/>
      </i18n:message>
    </td></tr></table>
  </td></tr></table>
        

  <table bgcolor="#cccccc" width="100%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td><font class="smaller">

    <%-- MAPPING --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<core:UrlParam param="config_page" value="Repository1"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= mappingStyle%> ><%= i18n_link_mapping %></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>
    
    <%-- LIMITATIONS --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<core:UrlParam param="config_page" value="limitations"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= limitationStyle%> ><%= i18n_link_limitation %></font></a>
    </core:CreateUrl>

     </td>
    </tr>
   </table>

   <!-- display errors if any -->
   <%-- Page fragment that renders form success and failure messages. --%>

   <dsp:importbean bean="/atg/portal/admin/SuccessMessageProcessor"/>
   <dsp:importbean bean="/atg/portal/admin/FailureMessageProcessor"/>

   <dsp:getvalueof id="failureMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="FailureMessageProcessor">
     <dsp:getvalueof id="successMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="SuccessMessageProcessor">
<%
 failureMessageProcessor.localizeMessages(request, response);
 successMessageProcessor.localizeMessages(request, response);
%>

       <%-- Previous submission success/failure reporting --%>
       <dsp:getvalueof id="successMessages" idtype="java.util.List" bean="SuccessMessageProcessor.messages">
         <core:ForEach id="successIterator"
                       values="<%=successMessages%>"
                       castClass="String"
                       elementId="successMessage">
            <img src='<%= response.encodeURL("images/info.gif")%>'/>&nbsp;&nbsp;<font class="info"><%=successMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- successMessages --%>

       <dsp:getvalueof id="failureMessages" idtype="java.util.List" bean="FailureMessageProcessor.messages">
         <core:ForEach id="failureIterator"
                       values="<%=failureMessages%>"
                       castClass="String"
                       elementId="failureMessage">
           <img src='<%= response.encodeURL("images/error.gif")%>'/>&nbsp;&nbsp;<font class="error"><%=failureMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- failureMessages --%>

<%
  failureMessageProcessor.clear();
  successMessageProcessor.clear();
%>
     </dsp:getvalueof><%-- successMessages --%>
   </dsp:getvalueof><%-- failureMessages --%>

  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" param="config_page" />

    <dsp:oparam name="default">
        <dsp:include page="/configRepository1.jsp" flush="false" />
    </dsp:oparam>
    <dsp:oparam name="Repository1">
	<dsp:include page="/configRepository1.jsp" flush="false" />
    </dsp:oparam>
    <dsp:oparam name="Repository2">
	<dsp:include page="/configRepository2.jsp" flush="false" />
    </dsp:oparam>
    <dsp:oparam name="Repository3">
	<dsp:include page="/configRepository3.jsp" flush="false" />
    </dsp:oparam>
    <dsp:oparam name="limitations">
	<dsp:include page="/configRepository4.jsp" flush="false" />
    </dsp:oparam>

  </dsp:droplet>

</blockquote>
</font>
</body>
</html>

</dex:DocExchPage>
</paf:PortalAdministratorCheck>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/InstallConfig.jsp#2 $$Change: 651448 $--%>
