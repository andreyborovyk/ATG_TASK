<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%--
     This page is a registration form, which uses the ProfileFormHandler to register a new 
     userid. This page is intended to serve as the default sample registration page, and can be
     updated or replaced for a given portal implementation.          
--%>
<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/InternalProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="InternalProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="bodyTagData"              value="${page.colorPalette.bodyTagData}"/>
<c:set var="cssURL"                   value="${community.style.CSSURL}"/>
<c:if test="${cssURL != null}">
 <paf:encodeURL var="cssURL" url="${cssURL}"/>
</c:if>

<c:choose>
 <c:when  test="${page != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${page.pageURI}"/>
 </c:when>
 <c:when  test="${community != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${community.communityURI}"/>
 </c:when>
 <c:otherwise>
  <c:set var="uri" value="${portalServletRequest.portalContextPath}"/>
 </c:otherwise>
</c:choose>
<c:set var="successURL" value="${param.successURL}"/>
<c:if test="${successURL == null}">
 <paf:encodeURL var="successURL" url="${uri}"/>
</c:if>
<paf:encodeURL var="errorURL" url="${uri}"/>
<paf:encodeURL var="actionURL" url="${uri}"/>

<html>
  <head>

    <title><fmt:message key="register-title" bundle="${userprofilingbundle}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:out value="${cssURL}"/>" src="<c:out value="${cssURL}"/>"/>  

  </head>
  <body <c:out value="${bodyTagData}" escapeXml="false"/> >
  <br /><br />
  <blockquote>
    <c:choose>
      <c:when test="${profile.transient == false}">

        <fmt:message key="login-message-logged-in" bundle="${userprofilingbundle}"/>&nbsp;<dsp:valueof bean="Profile.login"/>
      
      </c:when>   
      <c:otherwise>

        <dsp:form action="${actionURL}" method="post">
          <dsp:input type="hidden" bean="InternalProfileFormHandler.createSuccessURL" value="${successURL}"/>
          <dsp:input type="hidden" bean="InternalProfileFormHandler.createErrorURL" value="${errorURL}"/>
          <dsp:input bean="InternalProfileFormHandler.confirmPassword" type="HIDDEN" value="true"/>

          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="InternalProfileFormHandler.formError"/>
            <dsp:oparam name="true">
  
              <font color="cc0000"><strong><ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param name="exceptions" bean="InternalProfileFormHandler.formExceptions"/>
                <dsp:oparam name="output">
	          <li> <dsp:valueof param="message"/> </li>
                </dsp:oparam>
              </dsp:droplet>
              </ul></strong></font>

            </dsp:oparam>
          </dsp:droplet>

          <table border="0">
            <tr>
              <td valign="middle" align="right"><label for="username"><fmt:message key="register-label-username" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="username" type="text" size="20" maxsize="20" bean="InternalProfileFormHandler.value.login"/></td>
            </tr>

            <tr>
              <td valign="middle" align="right"><label for="password"><fmt:message key="register-label-password" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="password" type="password" size="35" maxsize="35" bean="InternalProfileFormHandler.value.password"/></td>
            </tr>

    	    <tr>
              <td valign="middle" align="right"><label for="confirmpassword"><fmt:message key="register-label-confirm-password" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="confirmpassword" type="password" size="35" maxsize="35" bean="InternalProfileFormHandler.value.confirmpassword"/></td>
            </tr>

            <fmt:message var="registerbutton" key="register-button-submit" bundle="${userprofilingbundle}"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="${registerbutton}" bean="InternalProfileFormHandler.create"/></td>
            </tr>
          </table>

        </dsp:form>

      </c:otherwise>
    </c:choose>
  </blockquote>
  </body>
</html>
</dsp:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/user/html/register.jsp#2 $$Change: 651448 $--%>
