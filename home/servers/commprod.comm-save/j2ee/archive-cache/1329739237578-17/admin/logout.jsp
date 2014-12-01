<%@page import="java.io.*,java.util.*,atg.servlet.ServletUtil,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>

<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />
<dsp:page>
<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  String successURL = request.getParameter("successURL");
  if(successURL == null) {
    if((portalServletResponse != null) &&
       (portalServletRequest != null))
  	successURL = portalServletResponse.encodePortalURL(portalServletRequest.getPortalRequestURI());
    if(successURL == null) {
      successURL = request.getRequestURI();
    }
    successURL = response.encodeURL(successURL);
  }    
   
%>

<core:demarcateTransaction id="demarcateXA">

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<i18n:message key="imageroot" id="i18n_imageroot"/>

<html>
<head>
<title><i18n:message key="title-admin-logout"/></title>

<link rel="STYLESHEET" type="text/css" href='<%= response.encodeURL("css/default.css") %>' src='<%= response.encodeURL("css/default.css") %>'>

</head>

<%-- <body>tag now lives in Nav include --%>

<% String thisNavHighlight = "";%>

<dsp:getvalueof id="thisMode" idtype="java.lang.String" param="mode">

<dsp:param name="thisNavHighlight" value="community"/>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    thisNavHighlight     = "logout";     // used for header_main
    String currPid       = "5";          // used for sidebar sub selection
    String mode          = "1";          // used for sub page with area
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to logout
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

    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-body-header-title"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
      <%-- Determine whether to show name, and what name to show (firstname if there, login name if not) --%>
          <dsp:getvalueof id="firstName" idtype="java.lang.String" bean="Profile.firstName">
            <core:ExclusiveIf>
               <core:IfNotNull value="<%=firstName%>">
                  <% greetingName = (String) firstName; %>
               </core:IfNotNull>
               <core:DefaultCase>
                  <dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">
                    <% greetingName= (String) login; %>
                  </dsp:getvalueof>
               </core:DefaultCase>
            </core:ExclusiveIf>
          </dsp:getvalueof>

 <dsp:form action="<%= request.getRequestURI() %>" method="POST">
 <dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="<%= successURL %>" />


  <font class="smaller">&nbsp;<%= greetingName %>,&nbsp;<i18n:message key="admin-logout-message"/></font>

<br>&nbsp;<br>

  <i18n:message id="logout03" key="logout" /><dsp:input type="SUBMIT" value="<%= logout03 %>" bean="ProfileFormHandler.logout"/>


 </dsp:form>

</td></tr></table>

  
</td>
</tr>
</table>



</dsp:getvalueof><%-- id="thisMode" --%>

</body>
</html>
</admin:InitializeAdminEnvironment>

</core:demarcateTransaction>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/logout.jsp#2 $$Change: 651448 $--%>
