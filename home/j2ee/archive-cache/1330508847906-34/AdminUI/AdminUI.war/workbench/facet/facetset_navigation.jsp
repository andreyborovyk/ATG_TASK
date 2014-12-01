<%--
  JSP, showing top navigation panel on create/edit facet pages. This page is included into facet.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_navigation.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <li class="current">
        <a href="#" onclick="switchContent(1,'content');top.focusFirstFormElement(window);return false;">
           <fmt:message key="facetset_navigation.tab.general"/>
        </a>
      </li>
      <li>
        <a href="#" onclick="return switchContent(2,'content')">
          <fmt:message key="facetset_navigation.tab.search.projects"/>
        </a>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_navigation.jsp#1 $$Change: 651360 $--%>
