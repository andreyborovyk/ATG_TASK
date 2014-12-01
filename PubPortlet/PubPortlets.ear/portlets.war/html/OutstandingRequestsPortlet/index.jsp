<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<fmt:setBundle var="requestsBundle" basename="atg.epub.portlet.OutstandingRequestsPortlet.Resources"/>

<dspel:page>

<portlet:defineObjects/>

<div> <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/OutstandingRequestsPortlet/images/homeLogo_placeholder.gif") %>' alt="atg" width="300" height="200" /></div>

<br />
<p ><fmt:message key="intro-text" bundle="${requestsBundle}"/></p>


</dspel:page>
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/OutstandingRequestsPortlet/index.jsp#2 $$Change: 651448 $--%>
