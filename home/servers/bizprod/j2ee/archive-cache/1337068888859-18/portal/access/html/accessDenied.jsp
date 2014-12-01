<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>


<fmt:setBundle var="accessbundle" basename="atg.portal.access"/>

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

<html>
  <head>
    <title><fmt:message key="access-denied-title" bundle="${accessbundle}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:out value="${cssURL}"/>" src="<c:out value="${cssURL}"/>"/>
  </head>
  <body <c:out value="${bodyTagData}" escapeXml="false"/> >
    <h1><fmt:message key="access-denied-heading" bundle="${accessbundle}"/></h1>
    <p><fmt:message key="access-denied-message" bundle="${accessbundle}"/></p>
  </body>
</html>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/access/html/accessDenied.jsp#2 $$Change: 651448 $--%>
