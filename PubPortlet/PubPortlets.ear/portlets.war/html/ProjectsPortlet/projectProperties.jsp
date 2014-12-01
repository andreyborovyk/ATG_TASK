<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's projectProperties.jsp -->
<dspel:page>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<%-- GET THE CURRENT PROJECT & ID --%>
<pws:getCurrentProject var="projectContext"/>

<c:set var="project" value="${projectContext.project}"/>
<c:set var="currentProjectId" value="${project.id}"/>

<br />Project properties area - project ID: <c:out value="${currentProjectId}"/><br />
</dspel:page>
<!-- End ProjectsPortlet's projectProperties.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectProperties.jsp#2 $$Change: 651448 $--%>
