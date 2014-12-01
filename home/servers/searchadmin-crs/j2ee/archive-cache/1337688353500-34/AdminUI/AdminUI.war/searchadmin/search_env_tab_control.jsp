<%--
  search env tab control JSP. Swith pages in search environment

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_tab_control.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="active" var="active" scope="page"/>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <c:if test="${!empty projectId}">
    <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
      <ul>
        <li <c:if test="${active eq 'first'}">class="current"</c:if>>
          <c:url var="viewByHostsURL" value="/searchadmin/search_env_view_by_host.jsp">
            <c:param name="projectId" value="${projectId}"/>
            <c:param name="environmentId" value="${environmentId}"/>
          </c:url>
          <a href="${viewByHostsURL}" onclick="return loadRightPanel(this.href);"
             title="<fmt:message key='search_env_configure_hosts.steps.first.tooltip'/>">
            <fmt:message key="search_env_configure_hosts.steps.first"/>
          </a>
        </li>
        <li <c:if test="${active eq 'second'}">class="current"</c:if>>
          <c:url var="viewByPartitionsURL" value="/searchadmin/search_env_view_by_partition.jsp">
            <c:param name="projectId" value="${projectId}"/>
            <c:param name="environmentId" value="${environmentId}"/>
          </c:url>
          <a href="${viewByPartitionsURL}" onclick="return loadRightPanel(this.href);"
             title="<fmt:message key='search_env_configure_hosts.steps.second.tooltip'/>">
            <fmt:message key="search_env_configure_hosts.steps.second"/>
          </a>
        </li>
        <li <c:if test="${active eq 'third'}">class="current"</c:if>>
          <c:url var="configureHostsURL" value="/searchadmin/search_env_configure_hosts.jsp">
            <c:param name="projectId" value="${projectId}"/>
            <c:param name="environmentId" value="${environmentId}"/>
          </c:url>
          <a href="${configureHostsURL}" onclick="return loadRightPanel(this.href);"
             title="<fmt:message key='search_env_configure_hosts.steps.third'/>">
            <fmt:message key="search_env_configure_hosts.steps.third.tooltip"/>
          </a>
        </li>
      </ul>
    </div>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_tab_control.jsp#2 $$Change: 651448 $--%>
