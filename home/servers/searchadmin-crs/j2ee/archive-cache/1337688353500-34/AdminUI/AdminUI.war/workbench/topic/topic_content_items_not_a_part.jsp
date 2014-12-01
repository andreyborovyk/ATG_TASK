<%--
  JSP, showing message, when topic set is not a part of active search project on "Content Items" tab of
  Topic page. This page is included into topic_content_items.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_not_a_part.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="paneContent">
    <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                  var="activeProjectId"/>
    <common:searchProjectFindByPrimaryKey searchProjectId="${activeProjectId}" var="activeProject"/>
    <h3>
      <fmt:message key="topic_content_items.not_a_part.title"/>
    </h3>
    <p>
      <fmt:message key="topic_content_items.not_a_part.message">
        <fmt:param><c:out value="${activeProject.name}"/></fmt:param>
      </fmt:message>
    </p>
    <ul>
      <li>
        <fmt:message key="topic_content_items.not_a_part.choise1"/>
      </li>
      <li>
        <fmt:message key="topic_content_items.not_a_part.choise2"/>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_not_a_part.jsp#2 $$Change: 651448 $--%>
