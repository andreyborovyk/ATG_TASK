<%--
 * ISP contain tabs for query rule set.
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_navigation.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <li class="current">
        <a href="#" onclick="switchContent(1,'content');top.focusFirstFormElement(window);return false;">
           <fmt:message key="queryset_navigation.tab.general"/>
        </a>
      </li>
      <li>
        <a href="#" onclick="return switchContent(2,'content')">
          <fmt:message key="queryset_navigation.tab.used_in_project"/>
        </a>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_navigation.jsp#2 $$Change: 651448 $--%>
