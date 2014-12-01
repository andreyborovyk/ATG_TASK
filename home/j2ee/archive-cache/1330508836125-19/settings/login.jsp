<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>


<%--
     This page is a login form, which uses the ProfileFormHandler to authenticate a userid
     and password. This page is intended to serve as the default sample login page, and can be
     updated or replace for a given portal implementation.          
--%>

<i18n:bundle baseName="atg.portal.userprofiling" localeAttribute="userLocale" changeResponseLocale="false"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  Style style = null;
  String cssURL = "";
  if(cm != null) {
   style = cm.getStyle();
   if(style != null)
     cssURL = response.encodeURL(style.getCSSURL());
  }
  
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  ColorPalette colorPalette = null;
  String bodyTagData = "";
  if(pg != null) {
    colorPalette = pg.getColorPalette();
    if(colorPalette != null)
      bodyTagData = colorPalette.getBodyTagData();
  }
 
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  String successURL = request.getParameter("successURL");
  if(successURL == null) {
    if((portalServletResponse != null) &&
       (portalServletRequest != null)) {
          if(pg != null) {
            if(cm != null) {
               successURL = portalServletRequest.getPortalContextPath()+pg.getPageURI();
             } else { 
               successURL = portalServletRequest.getPortalContextPath()+pg.getPageURI();
             }
           }
    }
    if(successURL == null) {
      successURL = request.getRequestURI();
    }
    successURL = response.encodeURL(successURL);
  }
%>
<html>
  <head>
    <title><i18n:message key="login-title"/></title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
  </head>
  <body <%= bodyTagData %> >
  <br /><br />
  <blockquote>
  <dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
    <core:exclusiveIf>
      <core:if value="<%= !profile.isTransient() %>">

        <i18n:message key="login-message-logged-in"/>&nbsp;<dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login"><%= login %></dsp:getvalueof>
      
      </core:if>   
      <core:defaultCase>

        <dsp:form target="_top" action="<%= portalServletResponse.encodePortalURL(portalServletRequest.getPortalRequestURI()) %>" method="post">
          <dsp:input type="hidden" bean="ProfileFormHandler.loginSuccessURL" value="<%= successURL %>"/>
          <dsp:input type="hidden" bean="ProfileFormHandler.loginErrorURL" value="<%= portalServletResponse.encodePortalURL(portalServletRequest.getPortalRequestURI()) %>"/>

          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="ProfileFormHandler.formError"/>
            <dsp:oparam name="true">
  
              <font color="cc0000"><strong><ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
                <dsp:oparam name="output">
	          <li> <dsp:valueof param="message"></dsp:valueof> </li>
                </dsp:oparam>
              </dsp:droplet>
              </ul></strong></font>

            </dsp:oparam>
          </dsp:droplet>

          <table width="456" border="0">
            <tr>
              <td valign="middle" align="right"><i18n:message key="login-label-username"/>:</td>
              <td><dsp:input type="text" size="20" maxsize="20" bean="ProfileFormHandler.value.login"/></td>
            </tr>

            <tr>
              <td valign="middle" align="right"><i18n:message key="login-label-password"/>:</td>
              <td><dsp:input type="password" size="35" maxsize="35" bean="ProfileFormHandler.value.password"/></td>
            </tr>
            <i18n:message id="loginbutton" key="login-button-submit"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="<%= loginbutton %>" bean="ProfileFormHandler.login"/></td>
            </tr>
          </table>

        </dsp:form>

      </core:defaultCase>
    </core:exclusiveIf>
  </dsp:getvalueof>
  </blockquote>
  </body>
</html>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/login.jsp#2 $$Change: 651448 $--%>
