<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ProjectTodoPortlet's getProjects.jsp -->
<fmt:setBundle var="projectTodoBundle" basename="atg.epub.portlet.ProjectTodoPortlet.Resources"/>

<dspel:importbean var="processState" bean="/atg/bizui/ui/state/ProcessState"/>
<dspel:importbean var="selectState" bean="/atg/bizui/ui/state/SelectState"/>
<dspel:importbean var="bizuiConfig" bean="/atg/bizui/Configuration"/>

<dspel:getvalueof var="userTasks" param="userTasks"/>
<dspel:getvalueof var="unowned" param="unowned"/>
<dspel:getvalueof var="creator" param="creator"/>
<dspel:getvalueof var="pagingIndex" param="pagingIndex"/>


<c:if test='${!empty param["operateAll"]}'>
  <biz:elementState var="unused" elementState="${processState}" value='${param["operateAll"]}'/>
</c:if>

<c:set var="pagingCount" value="10"/>

<c:choose>
  <c:when test='${creator == "-1"}'>
    <pws:getProcesses var="processResults" 
      status="${bizuiConfig.processDisplayStatus}" 
      hiddenWorkflowTypes="${bizuiConfig.hiddenWorkflowTypes}"
      sortProperties="displayName"/>
  </c:when>
  <c:otherwise>
    <pws:getProcesses var="processResults" 
      createdByUser="${creator}" 
      status="${bizuiConfig.processDisplayStatus}" 
      hiddenWorkflowTypes="${bizuiConfig.hiddenWorkflowTypes}"
      sortProperties="displayName"/>
  </c:otherwise>
</c:choose>

<c:set var="prevPagingIndex" value="${pagingIndex}" scope="session"/>
<c:set var="prevProcessCount" value="0" scope="session"/>

<c:if test='${selectState.selectElement == "all"}'>
  <c:set var="prevPagingIndex" value="${sessionScope.prevPagingIndex - pagingCount}" scope="session"/>
  <c:if test='${sessionScope.prevPagingIndex < 0}'>
    <c:set var="prevPagingIndex" value="0" scope="session"/>
  </c:if>
</c:if>

<c:if test='${selectState.selectElement != "all" && pagingIndex > 0}'>
  <%
    int pagingCount = Integer.parseInt(pageContext.findAttribute("pagingCount").toString());
    int pagingIndex = Integer.parseInt(pageContext.findAttribute("pagingIndex").toString());
    for (int i = pagingIndex - 1; i >= 0; i--) {
      atg.epub.pws.taglib.GetProcessesTag.Results results =
            (atg.epub.pws.taglib.GetProcessesTag.Results) pageContext.findAttribute("processResults");
      java.util.List processes = (java.util.List) results.getProcesses();
      pageContext.setAttribute("process", processes.get(i));
  %>

    <pws:getTasks var="taskResults" processId="${process.id}" userOnly='${userTasks}' unowned='${unowned}' active='${true}'/>
    <c:set var="prevPagingIndex" value="${sessionScope.prevPagingIndex - 1}" scope="session"/>
    <c:if test='${taskResults.size > 0}'>
      <c:set var="prevProcessCount" value="${sessionScope.prevProcessCount + 1}" scope="session"/>
    </c:if>

  <%
      int prevProcessCount = Integer.parseInt(pageContext.findAttribute("prevProcessCount").toString());
      if (prevProcessCount == pagingCount)
        break;
    }
  %>
</c:if>


<c:set var="processesDisplayed" value="0" scope="session"/>
<c:set var="nextPagingIndex" value="${pagingIndex}" scope="session"/>

<c:choose>
  <c:when test='${selectState.selectElement == "all"}'>
    <c:forEach var="process" items="${processResults.processes}" begin="${pagingIndex}" end="${pagingIndex + pagingCount}">
      <c:if test='${sessionScope.processesDisplayed < pagingCount}'>
        <c:set var="nextPagingIndex" value="${sessionScope.nextPagingIndex + 1}" scope="session"/>
        <dspel:include page="getTasks.jsp" flush="true">
          <dspel:param name="processId" value="${process.id}"/>
          <dspel:param name="changeState" value="false"/>
          <dspel:param name="userTasks" value='${userTasks}'/>
          <dspel:param name="unowned" value='${unowned}'/>
        </dspel:include>
      </c:if>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <c:forEach var="process" items="${processResults.processes}" begin="${pagingIndex}">
      <c:if test='${sessionScope.processesDisplayed < pagingCount}'>
        <c:set var="nextPagingIndex" value="${sessionScope.nextPagingIndex + 1}" scope="session"/>
        <dspel:include page="getTasks.jsp" flush="true">
          <dspel:param name="processId" value="${process.id}"/>
          <dspel:param name="changeState" value="false"/>
          <dspel:param name="userTasks" value='${userTasks}'/>
          <dspel:param name="unowned" value='${unowned}'/>
        </dspel:include>
      </c:if>
    </c:forEach>
  </c:otherwise>
</c:choose>

</dsp:page>



<!-- End ProjectTodoPortlet's getProjects.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ProjectTodoPortlet/getProjects.jsp#2 $$Change: 651448 $--%>
