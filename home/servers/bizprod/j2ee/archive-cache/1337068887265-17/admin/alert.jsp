<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
   
<dsp:importbean bean="/atg/portal/alert/AlertConfigurationFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<i18n:message key="imageroot" id="i18n_imageroot"/>
   
<html>
<head>
<title><i18n:message key="title-admin-alerts"/></title>
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>
   
<dsp:param name="thisNavHighlight" value="alert"/>
   
<% String thisNavHighlight = "alert"; %>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    thisNavHighlight     = "alerts";  // used for header_main
    String mode          = "1";          // used for sub page high light
    String currPid       = "3";          // used for sidebar sub selection
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available alerts
    }
    String greetingName = "";
%>



<%@ include file="nav_header_main.jspf"%>
<br>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>

<td width="150" valign="top"><font class="smaller">
 <%@ include file="nav_sidebar.jspf"%><br></td>

<td width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>
<td valign="top" width="90%" align="left">

<%@ include file="form_messages.jspf"%>

<!-- Display any errors processing form -->
  <dsp:droplet name="Switch">
  <dsp:param name="value" bean="AlertConfigurationFormHandler.formError"/>
   <dsp:oparam name="true">
    <font class="error">
     <dsp:droplet name="ErrorMessageForEach">
      <dsp:param name="exceptions" bean="AlertConfigurationFormHandler.formExceptions"/>
      <dsp:oparam name="output">
       <img src='<%= response.encodeURL("images/error.gif")%>'>&nbsp;&nbsp;<dsp:valueof param="message"/><br>
      </dsp:oparam>
    </dsp:droplet>
    </font>
   </dsp:oparam>
  </dsp:droplet>

<i18n:message id="deleteAlert" key="delete-yes"/>
<i18n:message id="cancelAlert" key="cancel"/>
<i18n:message id="createAlert" key="save"/>
<i18n:message id="resetAlert" key="reset"/>
<i18n:message id="updateAlert" key="save"/>
<i18n:message key="admin-html-bold" id="alert_i18n_boldb"/>
<i18n:message key="admin-html-end-bold" id="alert_i18n_end_boldb"/>




 <core:Switch value="<%= mode%>">

  <core:Case  value="1">
   <%@ include file="alert_admin.jspf" %>
  </core:Case>

  <core:Case value="3">
    <%@ include file="alert_framework.jspf" %>
  </core:Case>

  <core:Case value="5"><%-- about alerts --%>
  <font class="small" >
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-alerts-about-alerts"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
    <i18n:message key="admin-helpertext-alertsA"/><br><br>
    <i18n:message key="admin-helpertext-alertsB"/><br><br>
    <i18n:message key="admin-helpertext-alertsC"/>
   </font>
   </td></tr></table>
  </core:Case>
  <core:DefaultCase>
  </core:DefaultCase>
 </core:Switch>

  <%-- Edit  Alerts --%>
  <core:If  value='<%= mode.equals("7") || mode.equals("9")%>'>
   <%@ include file="alert_edit.jspf" %>
  </core:If>

  <%--  Delete  Alert --%>
  <core:If value='<%=  mode.equals("10") ||  mode.equals("8")%>'>
   <%@ include file="alert_delete.jspf" %>
  </core:If>

  <%-- New alert --%>
  <core:If value='<%= mode.equals("2") || mode.equals("4") %>'>
   <%@ include file="alert_new.jspf" %>
  </core:If>


</td></tr></table>

</body>
</html>


</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/alert.jsp#2 $$Change: 651448 $--%>
