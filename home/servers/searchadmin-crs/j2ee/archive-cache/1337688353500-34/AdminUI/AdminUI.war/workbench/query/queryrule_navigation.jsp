<%--
 * JSP contain tabs for query rule.
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_navigation.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
--%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="actionTab" var="actionTab"/>
  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <li <c:if test="${!actionTab}">class="current"</c:if>>
        <a href="#" onclick="switchContent(1,'content');top.focusFirstFormElement(window);return false;">
           <fmt:message key="queryrule_navigation.tab.patterns"/>
        </a>
      </li>
      <li <c:if test="${actionTab}">class="current"</c:if>>
        <a href="#" onclick="return switchContent(2,'content')">
          <fmt:message key="queryrule_navigation.tab.actions"/>
        </a>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_navigation.jsp#2 $$Change: 651448 $--%>
