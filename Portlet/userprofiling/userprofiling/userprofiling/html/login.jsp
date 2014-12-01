<%@ page import="javax.portlet.*,atg.portal.servlet.*" errorPage="/error.jsp"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>


<%--
     This page is a login form, which uses the ProfileFormHandler to authenticate a userid
     and password. This page is intended to serve as the default sample login page, and can be
     updated or replace for a given portal implementation.          
--%>

<fmt:setBundle basename="atg.portlet.userprofiling.userprofiling"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<%
  RenderRequest renderRequest = (RenderRequest)request.getAttribute("javax.portlet.request");
  RenderResponse renderResponse = (RenderResponse)request.getAttribute("javax.portlet.response");
  PortalServletResponse portalServletResponse = (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);  
  String actionURL = response.encodeURL(request.getRequestURI());
  String successURL = portalServletResponse.encodePortalURL(portalServletRequest.getPortalContextPath() + portalServletRequest.getPage().getPageURI());  
  String errorURL = portalServletResponse.encodePortalURL(portalServletRequest.getPortalContextPath() + portalServletRequest.getPage().getPageURI());

%>
  <br /><br />
  <blockquote>
  <dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
    <core:exclusiveIf>
      <core:if value="<%= !profile.isTransient() %>">

        <fmt:message key="login-message-logged-in"/>&nbsp;<dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login"><%= login %></dsp:getvalueof>
      
      </core:if>   
      <core:defaultCase>

        <dsp:form action="<%= actionURL %>" method="post">
          <dsp:input type="hidden" bean="ProfileFormHandler.loginSuccessURL" value="<%= successURL %>"/>
          <dsp:input type="hidden" bean="ProfileFormHandler.loginErrorURL" value="<%= errorURL %>"/>
          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="ProfileFormHandler.formError"/>
            <dsp:oparam name="true">
  
              <font color="cc0000"><strong><ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
                <dsp:oparam name="output">
	          <li><dsp:getvalueof id="msg" idtype="java.lang.String" param="message"><%= msg %></dsp:getvalueof></li>
                </dsp:oparam>
              </dsp:droplet>
              </ul></strong></font>

            </dsp:oparam>
          </dsp:droplet>

          <table width="100%" border="0">
            <tr>
              <td valign="middle" align="right"><fmt:message key="login-label-username"/>:</td>
              <td><dsp:input type="text" size="20" maxsize="20" bean="ProfileFormHandler.value.login"/></td>
            </tr>

            <tr>
              <td valign="middle" align="right"><fmt:message key="login-label-password"/>:</td>
              <td><dsp:input type="password" size="35" maxsize="35" bean="ProfileFormHandler.value.password" /></td>
            </tr>
            <fmt:message var="loginbutton" key="login-button-submit"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="<%= pageContext.findAttribute("loginbutton") %>" bean="ProfileFormHandler.login"/></td>
            </tr>
          </table>

        </dsp:form>
      </core:defaultCase>
    </core:exclusiveIf>
  </dsp:getvalueof>
  </blockquote>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/portlet/userprofiling/userprofiling/html/login.jsp#2 $$Change: 651448 $--%>
