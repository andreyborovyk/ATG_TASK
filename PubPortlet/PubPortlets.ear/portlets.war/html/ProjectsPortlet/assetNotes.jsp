<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet' assetNotes.jsp -->
<dspel:page>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<c:catch var="ex">

<portlet:defineObjects/>


<%-- GET THE CURRENT PROJECT & ID --%>
<pws:getCurrentProject var="projectContext"/>
<c:set var="project" value="${projectContext.project}"/>
<c:set var="currentProjectId" value="${project.id}"/>

<BR>Asset Notes page - project ID: <c:out value="${currentProjectId}"/><br>

Asst URI: <c:out value="${param.assetURI}"/><br>

</c:catch>
<c:out value="${ex}"/>
</dspel:page>
<!-- End ProjectsPortlet' assetNotes.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetNotes.jsp#2 $$Change: 651448 $--%>
