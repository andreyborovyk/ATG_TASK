<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<%--
     This page is an edit form, which uses the ProfileFormHandler to update an existing 
     userid. This page is intended to serve as the default sample edit  page, and can be
     updated or replace for a given portal implementation.          
--%>

<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />
<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should show what the current profile attributes are so we will
     enable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>

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

    <title><fmt:message key="edit-title" bundle="${userprofilingbundle}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:out value="${cssURL}"/>" src="<c:out value="${cssURL}"/>"/>

  </head>
  <body <c:out value="${bodyTagData}" escapeXml="false"/> >
  <br /><br />
  <blockquote>
        <dsp:form action="${actionURL}" method="post">
          <dsp:input type="hidden" bean="ProfileFormHandler.createSuccessURL" value="${successURL}"/>
          <dsp:input type="hidden" bean="ProfileFormHandler.createErrorURL" value="${errorURL}"/>

          <dsp:droplet name="Switch">
            <dsp:param name="value" bean="ProfileFormHandler.formError"/>
            <dsp:oparam name="true">
  
              <font color="cc0000"><strong><ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
                <dsp:oparam name="output">
	          <li> <dsp:valueof param="message"/> </li>
                </dsp:oparam>
              </dsp:droplet>
              </ul></strong></font>

            </dsp:oparam>
          </dsp:droplet>

          <table border="0">

<%--
           This ensures that we update the same profile that we edit.
           It guards against the session expiring while this form is
           displayed (and updating the anonymous profile) or the user
           logging out and in again as a different user in a different
           window 
--%>
          <dsp:input bean="ProfileFormHandler.updateRepositoryId" beanvalue="ProfileFormHandler.repositoryId" type="hidden"/>
            <tr>
              <td valign="middle" align="right"><label for="firstName"><fmt:message key="edit-label-firstName" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="firstName" type="text" size="30" maxsize="35" bean="ProfileFormHandler.value.firstName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="lastName"><fmt:message key="edit-label-lastName" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="lastName" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.lastName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="email"><fmt:message key="edit-label-email" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="email" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.email"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="address1"><fmt:message key="edit-label-address1" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="address1" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.address1"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="address2"><fmt:message key="edit-label-address2" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="address2" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.address2"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="city"><fmt:message key="edit-label-city" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="city" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.city"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="state"><fmt:message key="edit-label-state" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="state" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.state"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="postalCode"><fmt:message key="edit-label-postalCode" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="postalCode" type="text" size="10" maxsize="12" bean="ProfileFormHandler.value.homeAddress.postalCode"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="country"><fmt:message key="edit-label-country" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="country" type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.country"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="phoneNumber"><fmt:message key="edit-label-phoneNumber" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="phoneNumber" type="text" size="20" maxsize="30" bean="ProfileFormHandler.value.homeAddress.phoneNumber"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><label for="dateOfBirth"><fmt:message key="edit-label-dateOfBirth" bundle="${userprofilingbundle}"/>:</label></td>
              <td><dsp:input id="dateOfBirth" type="text" size="10" maxsize="10" date="M/dd/yyyy" bean="ProfileFormHandler.value.dateOfBirth"/></td>
            </tr>
            <fmt:message var="editbutton" key="edit-button-submit" bundle="${userprofilingbundle}"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="${editbutton}" bean="ProfileFormHandler.update"/></td>
            </tr>
          </table>   
        </dsp:form>
      
  </blockquote>
  </body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/userprofiling/html/edit.jsp#2 $$Change: 651448 $--%>
