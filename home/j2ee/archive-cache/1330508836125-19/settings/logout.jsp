<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>


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
    <title><i18n:message key="logout-title"/></title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
  </head>
  <body <%= bodyTagData %> >
  <br /><br />
  <blockquote>
  <dsp:droplet name="Switch">
    <dsp:param name="value" bean="ProfileFormHandler.profile.transient"/>
    <dsp:oparam name="false">

    <dsp:form action="<%= portalServletResponse.encodePortalURL(portalServletRequest.getPortalRequestURI()) %>" method="post" target="_top">
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutSuccessURL" value="<%= successURL %>"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutErrorURL" value="<%= portalServletResponse.encodePortalURL(portalServletRequest.getPortalRequestURI()) %>"/>
<%--        <dsp:input type="hidden" bean="ProfileFormHandler.expireSessionOnLogout" value="false"/>--%>

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

       <i18n:message key="logout-message-goodbye"/>
       <br /><br />
       <i18n:message id="logoutbutton" key="logout-button-submit"/>
       <dsp:input type="submit" value="<%= logoutbutton %>" bean="ProfileFormHandler.logout"/>
          

      </dsp:form>


    </dsp:oparam>
    <dsp:oparam name="default">

       
      
    </dsp:oparam>
  </dsp:droplet>
 <blockquote>
  </body>
</html>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/logout.jsp#2 $$Change: 651448 $--%>
