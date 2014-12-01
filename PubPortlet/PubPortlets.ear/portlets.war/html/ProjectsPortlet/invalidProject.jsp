<%@ page import="javax.portlet.*,atg.portal.servlet.*,atg.portal.framework.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's projectDetail.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<dspel:page xml="true">

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>


 <portlet:renderURL  var="listURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LIST_VIEW")%>'/>
 </portlet:renderURL>

<%-- 
Redirect to default process page (the one you get by clicking the top-level tab) when there's
no process selected, since we shouldn't be here
--%>
  <script language="JavaScript" type="text/javascript">
    <!--
     document.location.replace('<c:out escapeXml="false" value="${listURL}"/>'); 
    // -->
  </script>

  <p class="path"><a href='<c:out value="${listURL}"/>' onmouseover="status='';return true;">&laquo;&nbsp;<fmt:message key="project-list-link-text" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;
   <p><span class="error"><fmt:message key="no-current-project-message" bundle="${projectsBundle}"/></span></p>
</dspel:page>
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/invalidProject.jsp#2 $$Change: 651448 $--%>
