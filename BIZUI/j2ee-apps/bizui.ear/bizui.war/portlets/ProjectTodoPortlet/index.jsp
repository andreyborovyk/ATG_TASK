<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ProjectTodoPortlet's index.jsp -->
<fmt:setBundle var="projectTodoBundle" basename="atg.epub.portlet.ProjectTodoPortlet.Resources"/>

<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dspel:importbean var="selectState" bean="/atg/bizui/ui/state/SelectState"/>

<dspel:getvalueof var="userTasks" param="userTasks"/>
<dspel:getvalueof var="unowned" param="unowned"/>
<dspel:getvalueof var="creator" param="creator"/>
<dspel:getvalueof var="pagingIndex" param="pagingIndex"/>

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

<c:if test='${empty selectState.selectElement}'>
  <c:set target="${selectState}" property="selectElement" value="my"/>
</c:if>

<c:if test='${userTasks == null}'>
  <c:set var="userTasks" value='${selectState.selectElement == "my"}'/>
</c:if>
<c:if test='${unowned == null}'>
  <c:set var="unowned" value='${selectState.selectElement == "unowned"}'/>
</c:if>
<c:if test='${creator == null && selectState.selectElement == "creator"}'>
  <c:set var="creator" value='${profile.repositoryId}'/>
</c:if>
<c:if test='${creator == null}'>
  <c:set var="creator" value='-1'/>
</c:if>

<c:if test='${userTasks == "true" && unowned == "false"}'>
  <c:set target="${selectState}" property="selectElement" value="my"/>
</c:if>
<c:if test='${userTasks == "false" && unowned == "false"}'>
  <c:set target="${selectState}" property="selectElement" value="all"/>
</c:if>
<c:if test='${userTasks == "false" && unowned == "true"}'>
  <c:set target="${selectState}" property="selectElement" value="unowned"/>
</c:if>
<c:if test='${creator != "-1"}'>
  <c:set target="${selectState}" property="selectElement" value="creator"/>
</c:if>

<c:if test='${pagingIndex == null}'>
  <c:set var="pagingIndex" value='0' scope="session"/>
</c:if>
<c:if test='${pagingIndex != null}'>
  <c:set var="pagingIndex" value='${pagingIndex}' scope="session"/>
</c:if>
<c:if test='${prevPagingIndex == null}'>
  <c:set var="prevPagingIndex" value='0' scope="session"/>
</c:if>
<c:if test='${prevPagingIndex != null}'>
  <c:set var="prevPagingIndex" value='${prevPagingIndex}' scope="session"/>
</c:if>


<div id="projectResults">
  <table border="0" cellspacing="0" cellpadding="0" width="100%" class="BCCContentTable">

    <tr id="tdlHeaderMain">
      <td><h2><fmt:message key="portlet-name" bundle="${projectTodoBundle}"/></h2></td>

      <td align="right">

        <c:url var="closeAllUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="operateAll" value="false"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <fmt:message var="closeAllString" key="close-all" bundle="${projectTodoBundle}"/>
        <dspel:a id="tdlCloseAll" href="#" title="${closeAllString}" onclick="issueRequest('${closeAllUrl}', 'projectResults')"> </dspel:a>


        <c:url var="openAllUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="operateAll" value="true"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <fmt:message var="openAllString" key="open-all" bundle="${projectTodoBundle}"/>
        <dspel:a id="tdlOpenAll" href="#" title="${openAllString}" onclick="issueRequest('${openAllUrl}', 'projectResults')"> </dspel:a>

      </td>
    </tr>


    <tr>
      <td class="whatToShow">
        <fmt:message key="selector-label" bundle="${projectTodoBundle}"/>

        <c:url var="myTasksUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="userTasks" value="true"/>
          <c:param name="unowned" value="false"/>
          <c:param name="creator" value="-1"/>
          <c:param name="pagingIndex" value="0"/>
          <c:param name="prevPagingIndex" value="0"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <c:url var="allTasksUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="userTasks" value="false"/>
          <c:param name="unowned" value="false"/>
          <c:param name="creator" value="-1"/>
          <c:param name="pagingIndex" value="0"/>
          <c:param name="prevPagingIndex" value="0"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <c:url var="unownedTasksUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="userTasks" value="false"/>
          <c:param name="unowned" value="true"/>
          <c:param name="creator" value="-1"/>
          <c:param name="pagingIndex" value="0"/>
          <c:param name="prevPagingIndex" value="0"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <c:url var="creatorUrl" value="/portlets/ProjectTodoPortlet/index.jsp">
          <c:param name="userTasks" value="false"/>
          <c:param name="unowned" value="false"/>
          <c:param name="creator" value="${profile.repositoryId}"/>
          <c:param name="pagingIndex" value="0"/>
          <c:param name="prevPagingIndex" value="0"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <script type="text/javascript">
          function changeTaskSelection() {
            selectElement = document.getElementById("opsShow");
            if (selectElement.value == "my") 
              issueRequest('<c:out value="${myTasksUrl}" escapeXml="false"/>', 'projectResults');
            else if (selectElement.value == "all")
              issueRequest('<c:out value="${allTasksUrl}" escapeXml="false"/>', 'projectResults');
            else if (selectElement.value == "unowned")
              issueRequest('<c:out value="${unownedTasksUrl}" escapeXml="false"/>', 'projectResults');
            else if (selectElement.value == "creator")
              issueRequest('<c:out value="${creatorUrl}" escapeXml="false"/>', 'projectResults');
          }
        </script>

        <select id="opsShow" name="projects" onchange="changeTaskSelection()">
          <option value="my" <c:if test='${selectState.selectElement == "my"}'>selected</c:if> >
            <fmt:message key="my-projects" bundle="${projectTodoBundle}"/>
          </option>

          <option value="all" <c:if test='${selectState.selectElement == "all"}'>selected</c:if> >
            <fmt:message key="all-projects" bundle="${projectTodoBundle}"/>
          </option>

          <option value="creator" <c:if test='${selectState.selectElement == "creator"}'>selected</c:if> >
            <fmt:message key="creator-projects" bundle="${projectTodoBundle}"/>
          </option>

          <option value="unowned" <c:if test='${selectState.selectElement == "unowned"}'>selected</c:if> >
            <fmt:message key="unowned-projects" bundle="${projectTodoBundle}"/>
          </option>
        </select>
      </td>

      <td class="whatToShow">
      </td>
    </tr>

    <dspel:include page="getProjects.jsp" flush="true">
      <c:choose>
        <c:when test='${selectState.selectElement == "my"}'>
          <dspel:param name="userTasks" value="true"/>
          <dspel:param name="unowned" value="false"/>
        </c:when>
        <c:when test='${selectState.selectElement == "all"}'>
          <dspel:param name="userTasks" value="false"/>
          <dspel:param name="unowned" value="false"/>
        </c:when>
        <c:when test='${selectState.selectElement == "unowned"}'>
          <dspel:param name="userTasks" value="false"/>
          <dspel:param name="unowned" value="true"/>
        </c:when>
        <c:when test='${selectState.selectElement == "creator"}'>
          <dspel:param name="userTasks" value="false"/>
          <dspel:param name="unowned" value="false"/>
          <dspel:param name="creator" value="${profile.repositoryId}"/>
        </c:when>
      </c:choose>
      <dspel:param name="pagingIndex" value="${pagingIndex}"/>
    </dspel:include>

    <td colspan="2" class="whatToShow">
      <%@ include file="paging.jspf" %>
    </td>
  </table>
</div>

</dsp:page>

<!-- End ProjectTodoPortlet's index.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ProjectTodoPortlet/index.jsp#2 $$Change: 651448 $--%>
