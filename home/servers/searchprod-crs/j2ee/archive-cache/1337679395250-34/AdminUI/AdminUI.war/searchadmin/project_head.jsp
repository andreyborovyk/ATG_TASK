<%--
  JSP, included to project.jsp, showing current project status.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_head.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<%--
  We have request parameter, called "status", which shows us project stage:
  status = 1:
  Project isn't created
  status = 2:
  Project created, but has no content sets
  status = 3:
  Project created, has at least one content set, but has not been synchronized yet.
  status = 4:
  Project created, has at least one content set, and has been synchronized.
--%>

<d:getvalueof param="status" var="status" scope="page"/>

<%-- In this case, if status equals "1", we show status header with first step active --%>
<c:if test="${status == 1}">
  <div id="steps" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <p>
      <fmt:message key="project_head.title"/>
    </p>
    <ol>
      <li>
        <fmt:message key="project_head.first_step"/>
      </li>
      <li>
        <fmt:message key="project_head.second_step"/>
      </li>
      <li>
        <fmt:message key="project_head.third_step"/>
      </li>
    </ol>
  </div>
</c:if>

<%-- In this case, if status equals "2", we show status header with second step active and first step striked --%>
<c:if test="${status == 2}">
  <div id="steps" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <p>
      <fmt:message key="project_head.title"/>
    </p>
    <ol>
      <li>
        <strike>
          <fmt:message key="project_head.first_step"/>
        </strike>
      </li>
      <li>
        <fmt:message key="project_head.second_step"/>
      </li>
      <li>
        <fmt:message key="project_head.third_step"/>
      </li>
    </ol>
  </div>
</c:if>

<%--
  In this case, if status equals "", we show status header with third step active,
  first and second steps are striked
--%>
<c:if test="${status == 3}">
  <div id="steps" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <p>
      <fmt:message key="project_head.title"/>
    </p>
    <ol>
      <li>
        <strike>
          <fmt:message key="project_head.first_step"/>
        </strike>
      </li>
      <li>
        <strike>
          <fmt:message key="project_head.second_step"/>
        </strike>
      </li>
      <li>
        <fmt:message key="project_head.third_step"/>
      </li>
    </ol>
  </div>
</c:if>

<%-- if status is "4", we shouldn't show header at all --%>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_head.jsp#2 $$Change: 651448 $--%>
