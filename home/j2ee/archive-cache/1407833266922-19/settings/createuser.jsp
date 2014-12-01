<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>

<dsp:page>


<paf:setFrameworkLocale />

<%--
     This page is an edit form, which uses the ProfileFormHandler to update an existing 
     userid. This page is intended to serve as the default sample edit  page, and can be
     updated or replace for a given portal implementation.          
--%>

<i18n:bundle baseName="atg.portal.admin.userprofiling" localeAttribute="userLocale" changeResponseLocale="false"/>
<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

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
    <title><i18n:message key="createuser-title"/></title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
  </head>
  <body <%= bodyTagData %> >
  <br /><br />
  <blockquote>
  <dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
        <dsp:form target="_top" action="<%= portalServletResponse.encodePortalURL(successURL) %>" method="post">

          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="ProfileAdminFormHandler.formError"/>
            <dsp:oparam name="true">
  
              <font color="cc0000"><strong><ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param name="exceptions" bean="ProfileAdminFormHandler.formExceptions"/>
                <dsp:oparam name="output">
	          <li> <dsp:valueof param="message"></dsp:valueof> </li>
                </dsp:oparam>
              </dsp:droplet>
              </ul></strong></font>

            </dsp:oparam>
          </dsp:droplet>

          <table border="0">

            <%-- This form should not show what the current profile attributes are so we will
                 disable the ability to extract default values from the profile. --%>
            <dsp:setvalue bean="ProfileAdminFormHandler.extractDefaultValuesFromProfile" value="false"/>

            <dsp:input bean="ProfileAdminFormHandler.confirmPassword" type="hidden" value="true"/>
            <tr>
              <td valign="middle" align="right"><label for="username"><i18n:message key="createuser-label-username"/>*:</label></td>
              <td><dsp:input id="username" type="text" size="20" maxsize="20" bean="ProfileAdminFormHandler.value.login"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="password"><i18n:message key="createuser-label-password"/>*:</label></td>
              <td><dsp:input id="password" type="password" size="30" maxsize="35" bean="ProfileAdminFormHandler.value.password"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="confirmPassword"><i18n:message key="createuser-label-confirmPassword"/>*:</label></td>
              <td><dsp:input id="confirmPassword" type="password" size="30" maxsize="35" bean="ProfileAdminFormHandler.value.confirmpassword"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="firstName"><i18n:message key="createuser-label-firstName"/>:</label></td>
              <td><dsp:input id="firstName" type="text" size="30" maxsize="35" bean="ProfileAdminFormHandler.value.firstName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="lastName"><i18n:message key="createuser-label-lastName"/>:</label></td>
              <td><dsp:input id="lastName" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.lastName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="email"><i18n:message key="createuser-label-email"/>:</label></td>
              <td><dsp:input id="email" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.email"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="address1"><i18n:message key="createuser-label-address1"/>:</label></td>
              <td><dsp:input id="address1" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.address1"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="address2"><i18n:message key="createuser-label-address2"/>:</label></td>
              <td><dsp:input id="address2" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.address2"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="city"><i18n:message key="createuser-label-city"/>:</label></td>
              <td><dsp:input id="city" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.city"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="state"><i18n:message key="createuser-label-state"/>:</label></td>
              <td><dsp:input id="state" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.state"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="postalCode"><i18n:message key="createuser-label-postalCode"/>:</label></td>
              <td><dsp:input id="postalCode" type="text" size="10" maxsize="10" bean="ProfileAdminFormHandler.value.homeAddress.postalCode"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="country"><i18n:message key="createuser-label-country"/>:</label></td>
              <td><dsp:input id="country" type="text" size="30" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.country"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="phoneNumber"><i18n:message key="createuser-label-phoneNumber"/>:</label></td>
              <td><dsp:input id="phoneNumber" type="text" size="20" maxsize="30" bean="ProfileAdminFormHandler.value.homeAddress.phoneNumber"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="dateOfBirth"><i18n:message key="createuser-label-dateOfBirth"/>:</label></td>
              <td><dsp:input id="dateOfBirth" type="text" size="10" maxsize="10" date="M/dd/yyyy" bean="ProfileAdminFormHandler.value.dateOfBirth"/></td>
            </tr>
            <i18n:message id="createuserbutton" key="createuser-button-submit"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="<%= createuserbutton %>" bean="ProfileAdminFormHandler.create"/></td>
            </tr>
          </table>   
        </dsp:form>
      
  </dsp:getvalueof>
  </blockquote>
  </body>
</html>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/createuser.jsp#2 $$Change: 651448 $--%>
