<%-- 
Page:   	installConfig.jsp
Gear:  	 	Xmlfeed Gear
gearmode: 	InstallConfig
displayMode: 	full
DeviceOutput:   HTML
Author:      	Malay Desai
Description: 	This is the full view InstallConfig mode of the xmlfeed gear.
                The portal administrator can configure xml/xsl source 
                parameters and QueryMapping using this page.
--%>


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

  <i18n:bundle id="bundle" baseName="atg.portal.gear.xmlfeed.ConfigMessageResource"
             localeAttribute="userLocale" changeResponseLocale="false" />
  <i18n:message id="updateButton" key="updateButton"/>
  <i18n:message id="submitButton" key="submitButton" />
  <i18n:message id="resetButton" key="resetButton" />
  <i18n:message id="separator" key="separator" />

<html>
  <head>
    <title>
      <i18n:message key="pageTitle"/>
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

   <dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>

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
            <img src='<%= response.encodeURL("/gear/xmlfeed/images/info.gif")%>'/>&nbsp;&nbsp;<font class="info"><%=successMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- successMessages --%>

       <dsp:getvalueof id="failureMessages" idtype="java.util.List" bean="FailureMessageProcessor.messages">
         <core:ForEach id="failureIterator"
                       values="<%=failureMessages%>"
                       castClass="String"
                       elementId="failureMessage">
           <img src='<%= response.encodeURL("/gear/xmlfeed/images/error.gif")%>'/>&nbsp;&nbsp;<font class="error"><%=failureMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- failureMessages --%>

<%
  failureMessageProcessor.clear();
  successMessageProcessor.clear();
%>
     </dsp:getvalueof><%-- successMessages --%>
   </dsp:getvalueof><%-- failureMessages --%>

  <%
     //String origURI= pafEnv.getOriginalRequestURI();
     //String successUrl = request.getParameter("paf_success_url");
     String currentPageUrl = pafEnv.getGearDefinition().getServletContext() +
                           "/html/installConfig.jsp";

  %>
   <core:CreateUrl id="successURL" 
        url='<%= pafEnv.getGearDefinition().getServletContext() + "/html/installConfig.jsp" %>'>
        <core:UrlParam param="paf_dm"          value="<%=pafEnv.getDisplayMode() %>"/>
        <core:UrlParam param="paf_gm"          value="<%=pafEnv.getGearMode() %>"/>
        <core:UrlParam param="paf_gear_def_id" value="<%= gearDefId %>" />

    <dsp:form method="post" action="<%= currentPageUrl %>">
      <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
      <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
      <input type="hidden" name="paf_gear_def_id" value="<%= gearDefId %>" />
      <input type="hidden" name="formSubmit" value="true"/>
      <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl"
                             value="<%= successURL.getNewUrl() %>" />
      <dsp:input type="hidden" bean="GearConfigFormHandler.failureUrl"
                             value="<%= successURL.getNewUrl() %>" />

  
      <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"
                         value="instance" />
      <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"
                         value="true" />
      <dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>  
 
      <br>
      <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
      <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
        <font class="pageheader_edit"><img src='<%= response.encodeURL("/gear/xmlfeed/images/write.gif") %>' height="15" width="28" alt="" border="0">
		     <i18n:message key="installConfigHeading"/></font>
        </td></tr></table>
        </td></tr></table>
        
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">&nbsp;</font>
        </td></tr></table>
		
        <img src='<%= response.encodeURL("/gear/xmlfeed/images/clear.gif") %>' height=1 width=1 border=0><br>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">

  <tr>
    <td width="20%" align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="qpdisplay" /><%= separator %> &nbsp;</font></td>
    <td width="80%" align="left" valign="top"><font class="small">
    <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.queryProcessorClass"
               value='<%= pafEnv.getGearInstanceDefaultValue("queryProcessorClass") %>'/>
    </font></td>
  </tr>

  <tr>
    <td colspan=2><img src='<%= response.encodeURL("/gear/xmlfeed/images/clear.gif") %>' height=1 width=1 border=0></td>
  </tr>

  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="xmlsource" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
      <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.baseXmlSource"
                 value='<%= pafEnv.getGearInstanceDefaultValue("baseXmlSource") %>'/>
    </font></td>
  </tr>

  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
      <i18n:message key="qpmapping" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
      <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.queryParametersMapping"
           value='<%= pafEnv.getGearInstanceDefaultValue("queryParametersMapping") %>'/>
    </font></td>
  </tr>


  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="xslsharedhtml" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
    <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.sharedHtmlXsl"
               value='<%= pafEnv.getGearInstanceDefaultValue("sharedHtmlXsl") %>'/>
    </font></td>
  </tr>

  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="xslfullhtml" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
    <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.fullHtmlXsl"
               value='<%= pafEnv.getGearInstanceDefaultValue("fullHtmlXsl") %>'/>
    </font></td>
  </tr>

  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="xslsharedwml" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
    <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.sharedWmlXsl"
               value='<%= pafEnv.getGearInstanceDefaultValue("sharedWmlXsl") %>'/>
    </font></td>
  </tr>

  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="xslfullwml" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
    <dsp:input type="text" size="30"  maxlength="254" bean="GearConfigFormHandler.values.fullWmlXsl"
               value='<%= pafEnv.getGearInstanceDefaultValue("fullWmlXsl") %>'/>
    </font></td>
  </tr>


  <tr>
    <td align="left" valign="top" NOWRAP><font class="smaller">
    <i18n:message key="showlink" /><%= separator %> &nbsp;</font></td>
    <td align="left" valign="top"><font class="small">
    <dsp:select bean="GearConfigFormHandler.values.showLinkToFull">
<% 
  String showLinkToFullString =  pafEnv.getGearInstanceDefaultValue("showLinkToFull");

  if((showLinkToFullString != null) &&
     (showLinkToFullString.equals("true"))) {
%>
     <dsp:option value="true" selected="true"/>true
     <dsp:option value="false"/>false
<% } else { %>
     <dsp:option value="true" />true
     <dsp:option value="false" selected="true"/>false
<% } %>
    </dsp:select>
    
    </font></td>
  </tr>

 
  <tr>
    <td align="left">&nbsp;</td>

    <td align="left"><font class="small">
    <dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= updateButton %>" />
    &nbsp;&nbsp;&nbsp;
    <input type="submit" value="<%= resetButton %>" />
    </font></td>
  </tr>


</table>

</dsp:form> 
</core:CreateUrl>
</body>
</html>
</paf:InitializeGearEnvironment>
</paf:PortalAdministratorCheck>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/html/installConfig.jsp#2 $$Change: 651448 $--%>
