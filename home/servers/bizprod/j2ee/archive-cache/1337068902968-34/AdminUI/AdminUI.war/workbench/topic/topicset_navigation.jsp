<%--
  JSP, showing top navigation panel on create/edit topic set pages. This page is included into topic_set.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_navigation.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <li class="current">
        <a href="#" onclick="switchContent(1,'content');top.focusFirstFormElement(window);return false;">
           <fmt:message key="topic_sets.navigation.general"/>
        </a>
      </li>
      <li>
        <a href="#" onclick="return switchContent(2,'content')">
          <fmt:message key="topic_sets.navigation.search_projects"/>
        </a>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_navigation.jsp#2 $$Change: 651448 $--%>
