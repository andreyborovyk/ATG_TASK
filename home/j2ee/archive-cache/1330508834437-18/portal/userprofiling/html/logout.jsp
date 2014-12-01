<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

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

    <title><fmt:message key="logout-title" bundle="${userprofilingbundle}" /></title>
    <link rel="stylesheet" type="text/css" href="<c:out value="${cssURL}"/>" src="<c:out value="${cssURL}"/>"/>

  </head>
  <body <c:out value="${bodyTagData}" escapeXml="false"/> >
  <br /><br />
  <blockquote>
  <dsp:droplet name="Switch">
    <dsp:param name="value" bean="ProfileFormHandler.profile.transient"/>
    <dsp:oparam name="false">

      <dsp:form action="${actionURL}" method="post">
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutSuccessURL" value="${successURL}"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutErrorURL" value="${errorURL}"/>
<%--        <dsp:input type="hidden" bean="ProfileFormHandler.expireSessionOnLogout" value="false"/>--%>

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

       <fmt:message key="logout-message-goodbye" bundle="${userprofilingbundle}"/>
       <br /><br />
       <fmt:message var="logoutbutton" key="logout-button-submit" bundle="${userprofilingbundle}"/>
       <dsp:input type="submit" value="${logoutbutton}" bean="ProfileFormHandler.logout"/>
          

      </dsp:form>

    </dsp:oparam>    
  </dsp:droplet>
 <blockquote>
  </body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/userprofiling/html/logout.jsp#2 $$Change: 651448 $--%>
