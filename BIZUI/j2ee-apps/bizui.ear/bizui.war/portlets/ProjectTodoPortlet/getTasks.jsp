<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="pafrt"   uri="http://www.atg.com/taglibs/portal/paf-rt1_3" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ProjectTodoPortlet's getTasks.jsp -->
<fmt:setBundle var="projectTodoBundle" basename="atg.epub.portlet.ProjectTodoPortlet.Resources"/>

<dspel:importbean var="activityManager" bean="/atg/bizui/activity/ActivityManager"/>
<dspel:importbean var="processState" bean="/atg/bizui/ui/state/ProcessState"/>
<dspel:importbean var="selectState" bean="/atg/bizui/ui/state/SelectState"/>
<dspel:importbean var="taskActionFormHandler" bean="/atg/epub/servlet/TaskActionFormHandler"/>
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dspel:importbean var="userDirectory" bean="/atg/userprofiling/ProfileUserDirectory"/>

<dspel:getvalueof var="processId" param="processId"/>
<dspel:getvalueof var="userTasks" param="userTasks"/>
<dspel:getvalueof var="unowned" param="unowned"/>

<c:if test='${!empty param["noCache"]}'>
  <!-- do not cache the AJAX request -->
  <%
    javax.servlet.http.HttpServletResponse pResponse = (javax.servlet.http.HttpServletResponse) response;
    pResponse.setHeader("Pragma", "no-cache");
    pResponse.addDateHeader("Expires", 0);
    pResponse.setHeader("Cache-Control", "no-cache");
    pResponse.setHeader("Cache-Control", "no-store");
  %>
</c:if>

<biz:elementState var="results" elementState="${processState}" key="${processId}"/>

<c:if test='${param["changeState"] == "true"}'>
  <biz:elementState var="results" elementState="${processState}" key="${processId}" value="${! results.expanded}"/>
</c:if>


<pws:getProcess var="process" processId="${processId}"/>
<pws:getTasks var="taskResults" processId="${processId}" userOnly='${userTasks}' unowned='${unowned}' active='${true}'/>

<c:if test='${taskResults.size > 0 || selectState.selectElement == "all"}'>

  <c:set var="processesDisplayed" value="${sessionScope.processesDisplayed + 1}" scope="session"/>

  <tr>
    <td colspan="2" class="innerElement" id='<c:out value="${process.id}"/>'>

      <table cellspacing="0" cellpadding="0" width="100%">

        <tr>

          <c:choose>
            <c:when test='${results.expanded}'><c:set var="expanded" value="tdlHeaderOpen"/></c:when>
            <c:otherwise><c:set var="expanded" value="tdlHeaderClose"/></c:otherwise>
          </c:choose>

          <c:url var="prjUrl" value="/portlets/ProjectTodoPortlet/getTasks.jsp">
            <c:param name="processId" value="${processId}"/>
            <c:param name="changeState" value="true"/>
            <c:param name="userTasks" value='${userTasks}'/>
            <c:param name="unowned" value='${unowned}'/>
            <c:param name="noCache" value='true'/>
          </c:url>

          <td class="<c:out value='${expanded}'/>" id="<c:out value='parent${processId}'/>"
              onclick="displayToggle('parent<c:out value="${processId}"/>', 'tdlHeaderOpen', 'tdlHeaderClose');displayToggle('parentGoto<c:out value="${processId}"/>', 'tdlHeaderOpen', 'tdlHeaderClose');issueRequest('<c:out value="${prjUrl}"/>', '<c:out value="${processId}"/>')">

            <dspel:a iclass="projectTitle" href="#">
              <c:out value="${process.displayName}"/>
            </dspel:a>

          </td>

          <td class="<c:out value='${expanded}'/> gotoproject" id="<c:out value='parentGoto${processId}'/>">
            <biz:getProcessURL var="processInfo" process="${process}"/>

            <c:choose>
              <c:when test='${processInfo.page != null}'>
                <paf:context var="portalContext"/>
                <c:set target="${portalContext}" property="page" value="${processInfo.page}"/>
                <paf:encodeURL var="prjURL" url="${processInfo.URL}" context="${portalContext}">
                  <pafrt:param name="projectView" value="<%=new Integer(5)%>"/> <%-- 5 is the project details page --%>
                </paf:encodeURL>
              </c:when>

              <c:otherwise>
                <c:set var="prjURL" value="${processInfo.URL}"/>
              </c:otherwise>
            </c:choose>

            <dspel:a href='${prjURL}' title="${process.description}">
              <fmt:message key="goto-project" bundle="${projectTodoBundle}"/> &raquo;
            </dspel:a>
          </td>

        </tr>

        <c:if test="${results.expanded}">
          <c:forEach var="task" items="${taskResults.tasks}" varStatus="taskLoop">

            <tr>
              <td colspan="2">
                <%@ include file="assignForm.jspf" %>
              </td>
            </tr>

          </c:forEach>
        </c:if>

      </table>

    </td>
  </tr>

</c:if>

</dsp:page>

<!-- End ProjectTodoPortlet's getTasks.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ProjectTodoPortlet/getTasks.jsp#2 $$Change: 651448 $--%>
