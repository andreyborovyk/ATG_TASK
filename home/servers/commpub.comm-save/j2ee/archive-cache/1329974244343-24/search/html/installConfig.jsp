<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.portal.framework.RequestAttributes" %>

<dsp:page>
<paf:PortalAdministratorCheck>

<%

String gearDefId = (String) request.getParameter("paf_gear_def_id");
request.setAttribute(RequestAttributes.GEAR_DEFINITION, gearDefId );

%>


<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle id="bundle" baseName="atg.portal.gear.search.ConfigMessageResource" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButton" key="submitButton" />
<i18n:message id="updateButton" key="updateButton" />
<i18n:message id="resetButton" key="resetButton" />
<html>
<head>
<title>
</title> 
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
<body  bgcolor="#ffffff">
<br />


<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<% 
//String origURI= pafEnv.getOriginalRequestURI(); 
String successUrl = request.getParameter("paf_success_url");
String clearGif = response.encodeURL("../images/clear.gif");
String infoGif = response.encodeURL("../images/info.gif");
String errorGif = response.encodeURL("../images/error.gif");
String writeGif = response.encodeURL("../images/write.gif");
%>

<%-- put feedback message here it will be on a white back ground
     a br tag before and after 
--%>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearConfigFormHandler.formError"/>

  <dsp:oparam name="true">
    <br />
    <font class="error">

      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="GearConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
        &nbsp;<img src="<%= errorGif %>">&nbsp;&nbsp;<font class="smaller">
          <dsp:valueof param="message"></dsp:valueof></font><br />
        </dsp:oparam><br />
      </dsp:droplet>
     <%-- reset form errors --%>
     <dsp:setvalue bean="GearConfigFormHandler.resetFormExceptions" />
    </font>
  </dsp:oparam>


  <dsp:oparam name="false">

    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" bean="GearConfigFormHandler.formMessages" />
      <dsp:oparam name="output">
        &nbsp;<img src="<%= infoGif %>">&nbsp;&nbsp;<font class="smaller">
        <dsp:valueof param="element" /></font><br /><br />
      </dsp:oparam>
    </dsp:droplet> 
    
   <%-- reset form messages --%>
   <dsp:setvalue bean="GearConfigFormHandler.resetFormMessages" />

  </dsp:oparam>
</dsp:droplet> 

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit"><img src='<%= writeGif %>' height="15" width="28" alt="" border="0">
    <i18n:message key="INSTALL_CONFIG_TITLE"> 
     <i18n:messageArg value="<%= pafEnv.getGearDefinition().getName(response.getLocale()) %>"/>
    </i18n:message>
</td></tr></table>
</td></tr></table>
      
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
  <font class="smaller"><i18n:message key="INSTALL_HELPER_TEXT" /></font>
</td></tr></table>


<TABLE WIDTH="100%" bgcolor="#bad8ec" BORDER="0" CELLSPACING="0" CELLPADDING="4">
<tr><td>
<br>
<TABLE WIDTH="80%" bgcolor="#bad8ec" BORDER="0" CELLSPACING="0" CELLPADDING="0">

<%
 String currentPageUrl = pafEnv.getGearDefinition().getServletContext() +
                           "/html/installConfig.jsp";
%>

<dsp:form  method="post" action="<%= currentPageUrl %>">
  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="" value="<%=pafEnv.getGearMode() %>"/>

  <dsp:getvalueof id="thisPageUrl" bean="/OriginatingRequest.requestURIWithQueryString">
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= thisPageUrl%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= thisPageUrl%>"/>
  </dsp:getvalueof>
    <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"  value="instance" />
    <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"  value="true" />  
    <dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>


    <tr>
      <td colspan=2 ><font class="medium_bold"><i18n:message key="installConfigHeading"/></font>    </td>
    </tr>

    <tr>
      <td colspan=2 bgcolor="#000033"><img src="<%= clearGif %>" height=1 width=1 border=0></td>
    </tr>

    <tr>
      <td colspan=2><img src="<%= clearGif %>" height=15 width=1 border=0></td>
    </tr>

    <tr>
    <td width="50%" align="right"><font class="small"><i18n:message key="maxResultsPerRep" /></font></td>
<td width="50%">&nbsp;&nbsp;<dsp:input type="text" size="3"  maxlength="254" bean="GearConfigFormHandler.values.maxResultsPerRepository" value='<%= pafEnv.getGearInstanceDefaultValue("maxResultsPerRepository") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="<%= clearGif %>" height=10 width=1 border=0></td>
</tr>

    <tr>
      <td colspan=2><img src="<%= clearGif %>" height=15 width=1 border=0></td>
    </tr>

    <tr>
    <td width="50%" align="right"><font class="small"><i18n:message key="maxGearsPerRep" /></font></td>
<td width="50%">&nbsp;&nbsp;<dsp:input type="text" size="6"  maxlength="254" bean="GearConfigFormHandler.values.maxGearsPerRepository" value='<%= pafEnv.getGearInstanceDefaultValue("maxGearsPerRepository") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="<%= clearGif %>" height=10 width=1 border=0></td>
</tr>

 <td>&nbsp;</td>
<td align="left">
&nbsp;&nbsp;
 <dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= updateButton %>" />
&nbsp;&nbsp;&nbsp;
 <dsp:input type="submit"  bean="GearConfigFormHandler.cancel" value="<%= resetButton %>" />
<br><br><br><br>

 </td></tr>
</dsp:form> 
</TABLE>
</td></tr>
</TABLE>
 

</body>
</html>

</paf:InitializeGearEnvironment>
</paf:PortalAdministratorCheck>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/search/search.war/html/installConfig.jsp#2 $$Change: 651448 $--%>
