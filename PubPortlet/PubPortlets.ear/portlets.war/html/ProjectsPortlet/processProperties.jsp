<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's processProperties.jsp -->

<dspel:page>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<%-- GET THE CURRENT PROJECT & ID --%>
<pws:getCurrentProject var="projectContext"/>

<c:set var="project" value="${projectContext.project}"/>
<c:set var="process" value="${projectContext.process}"/>
<c:set var="currentProjectId" value="${project.id}"/>

<c:set var="processData" value="${process.processData}"/>

<pws:createVersionManagerURI var="assetURI" repositoryItem="${processData}"/>

<c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

<c:set target="${assetInfo.context.attributes}" property="assetURI" value="${assetURI}"/>

<dspel:include page="assetEditPage.jsp"/>

</dspel:page>

<!-- End ProjectsPortlet's processProperties.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/processProperties.jsp#2 $$Change: 651448 $--%>
