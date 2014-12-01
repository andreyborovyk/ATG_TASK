<%@ page errorPage="/error.jsp" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ProjectHistory's index.jsp -->

<fmt:setBundle var="projectHistoryBundle" basename="atg.epub.portlet.ProjectHistoryPortlet.Resources"/>

<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dspel:tomap var="currentUser" value="${profile.dataSource}"/>

<c:set var="actionURL"><portlet:actionURL/></c:set>

<h4 class="home">
  <fmt:message key="product-name" bundle="${projectHistoryBundle}"/>
</h4>
<table border="0" cellpadding="0" cellspacing="0" width="100%" id="portletRightBottom">
  <tr>
    <th class="homeLeftWidth">&nbsp;</th>
    <th class="homeCenterWidth"><fmt:message key="portlet-name" bundle="${projectHistoryBundle}"/></th>
  </tr>
</table>

<div class="homePanelContent">
  <table border="0" cellpadding="0" cellspacing="0" width="100%" id="portletRightBottom">

    <c:set var="itemCount" value="${1}"/>
    <c:forEach var="projectId" items="${currentUser.projectHistory}">
      <pws:getProject var="project" projectId="${projectId}">
        <c:if test="${project.workflow ne null}">      
          <dspel:tomap var="workflow" value="${project.workflow}"/>
          <%-- Let the include create the project URL --%>
          <dspel:include page="/includes/createProjectURL.jsp">
            <dspel:param name="outputAttributeName" value="projectURL"/>
            <dspel:param name="inputProjectId" value="${projectId}"/>
            <dspel:param name="workflowPath" value="${workflow.processName}"/>
          </dspel:include>             

          <tr>
            <td class="homeLeftWidth"><c:out value="${itemCount}"/></td>
            <td class="homeCenterWidth"><a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;"><c:out value="${project.displayName}"/></a></td>
          </tr>
          <c:set var="itemCount" value="${itemCount + 1}"/>
        </c:if>
      </pws:getProject>
    </c:forEach>

  </table>
  <table border="0" cellpadding="0" width="100%" id="portletRightBottom">
    <tr>
      <td>
        <dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>
        <c:url context="/atg" var="createProjectURL" value="/bcc/process?projectView=4&processPortlet=${bizuiPortletConfig.defaultProcessPortletID}"/>
        <a href="<c:out value="${createProjectURL}"/>" onmouseover="status='';return true;">Create a new process &raquo;</a>
      </td>
    </tr>
  </table>
</div>

</dsp:page>

<!-- End ProjectHistory's index.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectHistoryPortlet/index.jsp#2 $$Change: 651448 $--%>
