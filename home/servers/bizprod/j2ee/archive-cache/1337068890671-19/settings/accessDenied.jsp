<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>

<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false"/>
<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  Style style = null;
  String cssURL = "";
  if(cm != null) {
   style = cm.getStyle();
   if(style != null)
     cssURL = style.getCSSURL();
  }
  
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  ColorPalette colorPalette = null;
  String bodyTagData = "";
  if(pg != null) {
    colorPalette = pg.getColorPalette();
    if(colorPalette != null)
      bodyTagData = colorPalette.getBodyTagData();
  }
%>
<html>
  <head>
    <title><i18n:message key="title-authen-access-denied"/></title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
  </head>
  <body <%= bodyTagData %> >
    <h1><i18n:message key="access_denied"/></h1>
    <p><i18n:message key="access_sorry_message"/></p>
    <dsp:form action='<%= request.getContextPath() + "/accessDenied.jsp"  %>' method="POST">
    <core:CreateUrl id="logoutSuccessURL" url='<%= request.getContextPath() +"/login.jsp"%>'>
      <dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="<%= logoutSuccessURL.getNewUrl() %>" />
      <i18n:message id="login01" key="access-denied-login-button-label"/>
        <dsp:input type="SUBMIT" value="<%= login01 %>" bean="ProfileFormHandler.logout"/>
    </core:CreateUrl>
    </dsp:form>
  </body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/accessDenied.jsp#2 $$Change: 651448 $--%>
