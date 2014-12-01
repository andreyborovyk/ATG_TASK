
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/soapclient-taglib" prefix="soap" %>

<%@ page import="java.io.*,java.util.*,atg.portal.framework.RequestAttributes" %>

<dsp:page>
<paf:PortalAdministratorCheck>
<i18n:bundle id="soapclient" baseName="atg.gears.soapclient.soapclient" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:bundle id="soaprep" baseName="atg.portal.gear.soapclient.SOAPRepositoryTemplateResources" localeAttribute="userLocale" changeResponseLocale="false" />


<html>
<head>
<title>
  <i18n:message bundle="<%= soapclient %>" key="soapclient-installconfig-title"/>
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

<%
String gearDefId = null;
if(request.getAttribute(RequestAttributes.GEAR_DEFINITION) == null) {
  gearDefId = (String) request.getParameter("paf_gear_def_id");
  request.setAttribute(RequestAttributes.GEAR_DEFINITION, gearDefId );
} else {
  gearDefId = (String)request.getAttribute(RequestAttributes.GEAR_DEFINITION);
}
%>

<paf:InitializeGearEnvironment id="pafEnv">

<dsp:importbean bean="/atg/portal/gear/soapclient/SOAPConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<%
   //String successUrl = request.getParameter("paf_success_url");
   String currentPageUrl = pafEnv.getGearDefinition().getServletContext() +
                           "/html/installConfig.jsp";
%>

<dsp:form method="post" action="<%= currentPageUrl %>" enctype="multipart/form-data">
 <br>
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
        <font class="pageheader_edit"><img src='/gear/soapclient/images/write.gif' height="15" width="28" alt="" border="0">
		     	<i18n:message bundle="<%= soapclient %>" key="soapclient-installconfig-pagetitle">
   	  <i18n:messageArg value="<%=pafEnv.getGearDefinition().getName(response.getLocale())%>" /> 
	</i18n:message> 
        </td></tr></table>
        </td></tr></table>
        
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller"><i18n:message key="installsoap-helper-text" /></font>
        </td></tr></table>
		
<img src="/gear/soapclient/images/clear.gif" height=1 width=1 border=0><br>
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">







<!-- Display any errors processing form -->
<dsp:droplet name="Switch">
   <dsp:param name="value" bean="SOAPConfigFormHandler.formError"/>
   <dsp:oparam name="true">
     <tr>
    <td colspan="2"><font class="smaller">
      <UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param name="exceptions" bean="SOAPConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          <LI><dsp:valueof param="message"/></li>
        </dsp:oparam>
       </dsp:droplet>
       </UL>
	   </td></tr>
    </dsp:oparam>

    <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>
     <tr>
    <td colspan="2"> <font class="info"><i18n:message key="feedback-msg"/></font></td></tr>
    </core:ifNotNull>
    </dsp:oparam>

</dsp:droplet>

<soap:GetInstalledWSDLValues id="WSDLValues" gearDefId="<%= gearDefId %>">

  <core:exclusiveIf>
    <core:If value="<%= WSDLValues.isWSDLLoaded() %>">
      <tr><td colspan=2  width="200"><font class="small">
      <i18n:message bundle="<%= soaprep %>" key="WSDLLoaded" /></td></tr>
      <tr><td><font class="smaller"><i18n:message bundle="<%= soaprep %>" key="targetServiceURL" />:</td>
      <td><font class="smaller"><%= WSDLValues.getTargetServiceURL() %></td></tr>
      <tr><td><font class="smaller"><i18n:message bundle="<%= soaprep %>" key="targetNameSpaceURI" />:</td>
      <td><font class="smaller"><%= WSDLValues.getTargetNameSpaceURI() %></td></tr>
      <tr><td><font class="smaller"><i18n:message bundle="<%= soaprep %>" key="targetMethodName" />:</td>
      <td><font class="smaller"><%= WSDLValues.getTargetMethodName() %></td></tr>
      <tr><td><font class="smaller"><i18n:message bundle="<%= soaprep %>" key="soapActionURI" />:</td>
      <td><font class="smaller"><%= WSDLValues.getSOAPActionURI() %></td></tr>
    </core:If>

    <core:defaultCase>
      <tr><td width="100"><font class="smaller"><i18n:message bundle="<%= soaprep %>" key="noWSDLLoaded" /></td><td>&nbsp;</td></tr>
    </core:defaultCase>
  </core:exclusiveIf>

</soap:GetInstalledWSDLValues>





<input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
<input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
<input type="hidden" name="paf_gear_def_id" value="<%= gearDefId %>" />
<input type="hidden" name="formSubmit" value="true"/>

<%--
<dsp:input type="hidden" bean="SOAPConfigFormHandler.UploadWSDLSuccessUrl"
                         value="<%= currentPageUrl %>" />  


  <dsp:input type="hidden" bean="SOAPConfigFormHandler.UploadWSDLErrorUrl"
                          value="<%= currentPageUrl %>" />  
--%>

<dsp:input type="hidden" bean="SOAPConfigFormHandler.installConfiguration"
                         value="true" />  
<dsp:input type="hidden" bean="SOAPConfigFormHandler.gearDefId"
                         value="<%= gearDefId %>" />  
<tr><td width="100">
	<font class="smaller"><i18n:message bundle="<%= soapclient %>" key="soapclient-installconfig-wsdlfile"/>
	</td>
	<td>
		<dsp:input type="file" bean="SOAPConfigFormHandler.WSDLFile"/>
	</td>
</tr>

<tr>
	<td>
		&nbsp;
	</td>
	<td>
		<img src="/gear/soapclient/images/clear.gif" height=10 width=1 border=0><br>
		
   		<i18n:message bundle="<%= soapclient %>" id="upload01" key="upload"/>
		<dsp:input type="submit" bean="SOAPConfigFormHandler.uploadWSDL" value="<%= upload01 %>" />&nbsp;
		
		<i18n:message bundle="<%= soapclient %>" id="reset01" key="reset"/>
		<input type="submit" value="<%= reset01 %>" />
	</td>
</tr>
</table>

</dsp:form> 
</paf:InitializeGearEnvironment>
</body>
</html>
</paf:PortalAdministratorCheck>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/soapclient/soapclient.war/html/installConfig.jsp#2 $$Change: 651448 $--%>
