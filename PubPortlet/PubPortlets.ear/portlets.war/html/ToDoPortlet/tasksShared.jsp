<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>

<!-- Begin ToDoPortlet's tasksShared.jsp -->

<dspel:page>

<!-- BEGIN TASKS GEAR DISPLAY -->

<fmt:setBundle var="todoBundle" basename="atg.epub.portlet.ToDoPortlet.Resources"/>
<fmt:message var="claimButtonLabel" key="claim-action" bundle="${todoBundle}"/>
<fmt:message var="releaseButtonLabel" key="release-action" bundle="${todoBundle}"/>

<portlet:defineObjects/>

<%-- for claim/release actions --%>
<dspel:importbean bean="/atg/epub/servlet/TaskActionFormHandler" var="taskActionFormHandler"/>
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>


<%-- Current User ID --%>
<c:set var="currentUserId" value="${profile.repositoryId}"/>

<%-- for project views --%>
<%@ include file="../ProjectsPortlet/projectConstants.jspf" %>

<%
  String showTaskPoolParam = portletConfig.getInitParameter("showTaskPool");
  if (showTaskPoolParam==null){ showTaskPoolParam="false";}

  String listSizeParam = portletConfig.getInitParameter("taskCountShared");
  if (listSizeParam==null){ listSizeParam="10";}
%>

<c:set var="showTaskPool"><%= showTaskPoolParam %></c:set>
<c:set var="userListSize"><%= listSizeParam %></c:set>

<%-- RANGE/PAGING VARIABLES --%>
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<%--
<dspel:getvalueof var="userListSize" bean="Profile.defaultListing"/>
--%>

<c:set var="listIndex" value="${0}"/>
<c:if test="${param.taskIndex ne null}"><c:set var="listIndex" value="${param.taskIndex}"/></c:if>
<c:set var="rangeEnd" value="${listIndex + userListSize}"/>


<%-- MY TASKS / ALL TASKS LINKS --%>
<%-- NOTE: disabling for now, since this portlet is currently displayed in two instances, with the 
     my tasks/all tasks switch controlled by an initParamater, showTaskPool  --%>
<%--

<c:set var="myTasks" value="${false}"/>
<c:if test="${param.myTasks eq true}"><c:set var="myTasks" value="${true}"/></c:if>

<c:choose>
  <c:when test="${showTaskPool}">
    <portlet:renderURL  var="myTasksURL">
      <portlet:param name="myTasks" value="true"/>
    </portlet:renderURL>
    <a href='<c:out value="${myTasksURL}"/>'><fmt:message key="my-tasks" bundle="${todoBundle}"/></a>
  </c:when>
  <c:otherwise>
    <portlet:renderURL  var="allTasksURL">
      <portlet:param name="myTasks" value="false"/>
    </portlet:renderURL>
    <a href='<c:out value="${allTasksURL}"/>'><fmt:message key="all-tasks" bundle="${todoBundle}"/></a>
  </c:otherwise>
</c:choose>
<br />
<br />
--%>

<!-- begin content -->
<table cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td style="border-right: solid 1px #949494;">

      <!-- Begin My Tasks -->

      <c:choose>
        <c:when test="${showTaskPool}">
          <h3 class="homeDisplayResults"><fmt:message key="all-tasks-title" bundle="${todoBundle}"/></h3>
  </c:when>
  <c:otherwise>
          <h3 class="homeDisplayResults"><fmt:message key="my-tasks-title" bundle="${todoBundle}"/></h3>
  </c:otherwise>
      </c:choose>


<%-- GET ALL TASKS --%>
<c:catch var="taskExc">
<pws:getTasks var="results" userOnly="${!showTaskPool}" unowned="${showTaskPool}" index="${listIndex}" count="${userListSize}"/>

</c:catch>

  <c:if test="${taskExc ne null}">
    <span class="error">
      <c:out value="the following error occurred on this request:"/><br /><br />
      <c:out value="${taskExc}"/>
    </span>
  </c:if>
  <%-- display message if no tasks are found --%>
  <c:if test="${results.projectSize le 0}">
      <c:choose>
        <c:when test="${showTaskPool}">
          <p><fmt:message key="no-tasks-in-pool-msg" bundle="${todoBundle}"/></p>
  </c:when>
  <c:otherwise>
          <p><fmt:message key="no-tasks-message" bundle="${todoBundle}"/></p>
  </c:otherwise>
      </c:choose>
<%-- fill in the space so the page doesn't squish down --%>
    <c:forEach begin="${0}" end="${9}">
      <br />
    </c:forEach>
  </c:if>

<c:if test="${results.projectSize gt 0}">

<div id="colapsablePanelContainer">
 <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr><td nowrap align="right">

<%-- PAGING LINKS --%>
<c:if test="${results.projectSize gt userListSize}">
<c:choose>
  <c:when test="${results.projectSize gt rangeEnd}">
    <p>
    <fmt:message key="range-label" bundle="${todoBundle}">
      <fmt:param value="${listIndex + 1}"/>
      <fmt:param value="${rangeEnd}"/>
      <fmt:param value="${results.projectSize}"/>
    </fmt:message>&nbsp;&nbsp;&nbsp;
    <%-- PREV --%>
    <c:choose>
     <c:when test="${listIndex gt 0}">
      <c:set var="prevIndex" value="${listIndex-userListSize}"/>
      <% String prevIndexParam = pageContext.getAttribute("prevIndex").toString(); %>
      <portlet:renderURL  var="prevRangeURL">
        <portlet:param name="taskIndex" value="<%=prevIndexParam%>"/>
      </portlet:renderURL>
      <a href='<c:out value="${prevRangeURL}"/>'><fmt:message key="prev-link" bundle="${todoBundle}"/></a>&nbsp;&nbsp;
     </c:when>
     <c:otherwise>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     </c:otherwise>
    </c:choose>
    <%-- NEXT --%>
    <% String nextIndexParam = pageContext.getAttribute("rangeEnd").toString(); %>
    <portlet:renderURL  var="nextRangeURL">
      <portlet:param name="taskIndex" value="<%=nextIndexParam%>"/>
    </portlet:renderURL>
    <a href='<c:out value="${nextRangeURL}"/>'><fmt:message key="next-link" bundle="${todoBundle}"/></a>&nbsp;&nbsp;&nbsp;
    </p>
  </c:when>
  <c:otherwise>
    <p>
    <fmt:message key="range-label" bundle="${todoBundle}">
      <fmt:param value="${listIndex + 1}"/>
      <fmt:param value="${results.projectSize}"/>
      <fmt:param value="${results.projectSize}"/>
    </fmt:message>&nbsp;&nbsp;&nbsp;
    <%-- PREV --%>
    <c:if test="${listIndex gt 0}">
    <c:set var="prevIndex" value="${listIndex-userListSize}"/>
    <% String prevIndexParam = pageContext.getAttribute("prevIndex").toString(); %>
    <portlet:renderURL  var="prevRangeURL">
      <portlet:param name="taskIndex" value="<%=prevIndexParam%>"/>
    </portlet:renderURL>
    <a href='<c:out value="${prevRangeURL}"/>'><fmt:message key="prev-link" bundle="${todoBundle}"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    </c:if>
    </p>
  </c:otherwise>
</c:choose>
</c:if>
    </td>
  </tr>
        
  <tr>
   <td colspan="3" class="tasksBorder">

    <%-- TASK LIST --%>
    <div id="myprocess_1" style="display: block; width: 100%">
     <table cellpadding="0" cellspacing="0" border="0" width="100%" class="colapsablePanelData">
  <tr>
    <c:choose>
        <c:when test="${showTaskPool}">
	  <th class="centerAligned checkbox" style="width: 8%">
             <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ToDoPortlet/images/checkMark1.gif")%>' alt='<c:out value="${claimButtonLabel}"/>' title='<c:out value="${claimButtonLabel}"/>' width="7" height="8" border="0" />
	  </th>
        </c:when>
        <c:otherwise>
	  <%--
	  <th class="info" style="width: 8%"><c:out value="${releaseButtonLabel}"/></th>
	  --%>
	  <th class="centerAligned checkbox" style="width: 8%">
             <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ToDoPortlet/images/checkMark1.gif")%>' alt='<c:out value="${releaseButtonLabel}"/>' title='<c:out value="${releaseButtonLabel}"/>' width="7" height="8" border="0" />
	  </th>
        </c:otherwise>
    </c:choose>
    <th class="info" style="width: 45%"><fmt:message key="task-label" bundle="${todoBundle}"/></th>
    <th class="info" style="width: 45%"><fmt:message key="process-label" bundle="${todoBundle}"/></th>
  </tr>

  <c:forEach items="${results.projectTasks}" var="taskInfo" varStatus="loopInfo">
      <c:catch var="e">
      <pws:getProcess var="process" processId="${taskInfo.subjectId}"/>
      <pws:getWorkflowDescriptor var="workflow" processId="${process.id}"/>

             <%-- Let the include create the project and task detail URL --%>
             <dspel:include page="/includes/createProjectURL.jsp">
              <dspel:param name="outputAttributeName" value="projectURL"/>
              <dspel:param name="inputProjectId" value="${process.project.id}"/>
              <dspel:param name="workflowPath" value="${workflow.processWorkflow.processName}"/>
            </dspel:include>             
             <dspel:include page="/includes/createProjectURL.jsp">
              <dspel:param name="outputAttributeName" value="taskDetailURL"/>
              <dspel:param name="inputProjectId" value="${process.project.id}"/>
              <dspel:param name="workflowPath" value="${workflow.processWorkflow.processName}"/>
              <dspel:param name="inputTaskId" value="${taskInfo.taskDescriptor.taskElementId}"/>
            </dspel:include>             

           <tr class="rowHighlight" onmouseover="this.className='rowHighlightHover';" onmouseout="this.className='rowHighlight';" >
    <%-- no image just yet...
    <td><img class="leftArrow" src="images/icon_go.gif" alt="" /><c:out value="${taskInfo.taskDescriptor.displayName}"/></td>
    --%>
        
            <%-- CLAIM/RELEASE FORM --%>
    <portlet:actionURL var="actionURL"/>
          <c:set var="formName" value="${renderResponse.namespace}taskForm${loopInfo.count}"/>
                <dspel:form  name="${formName}" method="post" action="${actionURL}">
                <td class="centerAligned checkbox" style="width: 8%">
             <c:if test="${taskInfo.active}">
                <c:if test="${taskInfo.taskDescriptor.assignable}"> 
                
                       <!-- TaskAction form -->
                         <dspel:input type="hidden" bean="TaskActionFormHandler.processId" value="${process.id}"/>
                         <dspel:input type="hidden" bean="TaskActionFormHandler.projectId" value="${process.project.id}"/>
                         <dspel:input type="hidden" bean="TaskActionFormHandler.taskElementId" value="${taskInfo.taskDescriptor.taskElementId}"/>

                   <c:choose>
                     <c:when test="${currentUserId eq taskInfo.owner.primaryKey}" >
                         <dspel:input type="hidden" priority="-1" bean="TaskActionFormHandler.releaseTask" value="1"/>
     <input type="checkbox" class="checkbox"  onclick='javascript:document.<c:out value="${formName}"/>.submit()' checked>
                     </c:when>
                     <c:otherwise>
                         <dspel:input type="hidden" priority="-1" bean="TaskActionFormHandler.claimTask" value="1"/>
     <input type="checkbox"  class="checkbox" onclick='javascript:document.<c:out value="${formName}"/>.submit()'>
                     </c:otherwise>
                   </c:choose>

                </c:if>
             </c:if>
                </td>
                </dspel:form>
          <td onmousedown='javascript:doRedirect("<c:out value='${taskDetailURL}'/>");'><a href='<c:out value="${taskDetailURL}"/>'><c:out value="${taskInfo.taskDescriptor.displayName}"/></a></td>
          <td onmousedown='javascript:doRedirect("<c:out value='${projectURL}'/>");'><a href="<c:out value='${projectURL}'/>"><c:out value="${process.displayName}"/></a></td>

       </tr>
    </c:catch>
  </c:forEach>
     </table>
    </div>

   </td>
  </tr>

 </table>

    
</div>

</c:if>  <%-- results > 0 --%>

    </td>
  </tr>
</table>

<!-- END TASKS GEAR DISPLAY -->

</dspel:page>
<!-- End ToDoPortlet's tasksShared.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ToDoPortlet/tasksShared.jsp#2 $$Change: 651448 $--%>
