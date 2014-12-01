<!-- BEGIN FILE index.jsp -->
 <%--
  Deployment portlet index page 
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<%@ page import="atg.servlet.*" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
 <%@ include file="includes/paramstoattributes.jspf" %>
 <dspel:importbean scope="request" var="topoManager" bean="/atg/epub/deployment/TopologyManager"/>  
 <dspel:importbean scope="request" var="topologyEditFormHandler" bean="/atg/epub/deployment/TopologyEditFormHandler"/>  
 <dspel:importbean scope="request" var="deploymentFormHandler" bean="/atg/epub/deployment/DeploymentFormHandler"/>  
 <dspel:getvalueof var="userListSize" bean="/atg/userprofiling/Profile.defaultListing" scope="request"/>
 <c:set var="deploymentFormHandlerName" value="/atg/epub/deployment/DeploymentFormHandler" scope="request"/>
 <c:set var="topologyFormHandlerName" value="/atg/epub/deployment/TopologyEditFormHandler" scope="request"/>
	<c:choose>
	  <c:when test="${atgAdminMenuGroup == 'deployment'}">
	    <c:choose>    
        <c:when test="${atgAdminMenu1 == 'overview'}">
		      <dspel:include page="site/index.jsp" />
	      </c:when><%-- end overview--%>
	      <c:when test="${atgAdminMenu1 == 'configuration'}">
		      <dspel:include page="config/index.jsp" />
	      </c:when>
	    </c:choose>
	  </c:when>
	  <c:otherwise>
	    <dspel:include page="site/index.jsp" />
	  </c:otherwise>
</c:choose>
</dspel:page>
<!-- END FILE index.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/index.jsp#2 $$Change: 651448 $--%>
