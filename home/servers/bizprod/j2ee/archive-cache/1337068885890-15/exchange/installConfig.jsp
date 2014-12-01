<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.portal.framework.RequestAttributes" %>



<dsp:page>

<paf:PortalAdministratorCheck>

<paf:InitializeGearEnvironment id="pafEnv">

<%
   String gearDefId = (String) request.getParameter("paf_gear_def_id");
   request.setAttribute(RequestAttributes.GEAR_DEFINITION, gearDefId );

    //this is a temp. work around as right now this (along with some other things)
    // is null even after the paf:InitializeGearEnvironment
    request.setAttribute(RequestAttributes.PORTAL_REPOSITORY_LOCATION,
             "dynamo:/atg/portal/framework/PortalRepository");
    %>


<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<i18n:bundle id="bundle" baseName="atg.portal.gear.exchange.Exchange" changeResponseLocale="false" />

<html>
<head>
<title>
<i18n:message key="PAGE_TITLE_TAG"/>
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
<body bgcolor="#ffffff"> 
<br />


      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="GearConfigFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="GearConfigFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
                   <font class="error"><img src='<%= response.encodeURL("images/info.gif")%>'
                    >&nbsp;&nbsp;<%= errorMsg %></font><br/>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="GearConfigFormHandler.resetFormExceptions"/>
         </dsp:oparam>
      </dsp:droplet>

      <%-- display info messages if any --%> 
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="GearConfigFormHandler.formInfo"/>
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="GearConfigFormHandler.formMessages"/>
               <dsp:oparam name="output">
                  <font class="info"><img src='<%= response.encodeURL("images/info.gif")%>'
                  >&nbsp;&nbsp;<dsp:valueof param="element"/></font>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="GearConfigFormHandler.resetFormMessages"/> 
         </dsp:oparam>
      </dsp:droplet>
       
   <core:CreateUrl id="successURL" 
        url='<%= pafEnv.getGearDefinition().getServletContext() + "/installConfig.jsp" %>'>
        <core:UrlParam param="paf_dm"          value="<%=pafEnv.getDisplayMode() %>"/>
        <core:UrlParam param="paf_gm"          value="<%=pafEnv.getGearMode() %>"/>
        <core:UrlParam param="paf_gear_def_id" value="<%= gearDefId %>" />

     <dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>
     <dsp:form method="post" action='<%= pafEnv.getGearDefinition().getServletContext() + "/installConfig.jsp" %>'>
        <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
        <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
        <input type="hidden" name="paf_gear_def_id" value="<%= gearDefId %>" />
        <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl"
                                 value="<%= successURL.getNewUrl() %>" /> 
        <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"
                                 value="instance" />
        <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"
                                 value="true" />  


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit"><img src='images/write.gif' height="15" width="28" alt="" border="0">
    <i18n:message key="INSTALL_CONFIG_TITLE"> 
     <i18n:messageArg value="<%= pafEnv.getGearDefinition().getName(response.getLocale()) %>"/>
    </i18n:message>
</td></tr></table>
</td></tr></table>
      
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
  <font class="smaller"><i18n:message key="INSTALL_HELPER_TEXT" /></font>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
  <tr>
    <td><font class="smaller"><i18n:message key="MOWA_HOST_URL"/></font></td>
    <td>
     <dsp:input type="text" size="50" maxlength="254" bean="GearConfigFormHandler.values.hostURL"
                      value='<%= pafEnv.getGearInstanceDefaultValue("hostURL") %>'/>
    </td>
   </tr>
   <tr>
    <td>
      <font class="smaller"><i18n:message key="PROXY_HOST_URL"/></font>
    </td>
    <td>
       <dsp:input type="text" size="50" maxlength="254" bean="GearConfigFormHandler.values.proxyURL"
                            value='<%= pafEnv.getGearInstanceDefaultValue("proxyURL") %>'/>
    </td>
   </tr>
   <tr>
   <i18n:message key="DONE" id="doneStr"/>
  <td align="right">&nbsp;</td>
  <td align="left"><dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= doneStr %>"/>
<br /><br /><br />
</td>
  </tr>
 </table>
 </dsp:form> 
     </core:CreateUrl>
</body>
</html> 

</paf:InitializeGearEnvironment>

</paf:PortalAdministratorCheck>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/exchange-passthrough/exchange.war/installConfig.jsp#1 $$Change: 651360 $--%>
