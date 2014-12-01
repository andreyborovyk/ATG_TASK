<%--
  Search environment view by partition JSP. Show patritions with linked search engines and hosts.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_view_by_partition.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:include page="search_env_tab_control.jsp">
    <d:param name="active" value="second"/>
  </d:include>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <d:getvalueof param="projectId" var="projectId" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <br/>
      <admin-beans:getEnginesByContentSet var="enginesByContentSet" environmentId="${environmentId}"/>
      <c:choose>
        <c:when test="${fn:length(enginesByContentSet) == 0}">
          <fmt:message key="search_env_view_by_host.no.hosts"/>
        </c:when>
        <c:otherwise>
          <table class="data" cellspacing="0" cellpadding="0">
            <thead>
              <tr>
                <th><fmt:message key="search_env_view_by_host.table.partition.name"/></th>
                <th><fmt:message key="search_env_view_by_host.table.engine.name"/></th>
                <th><fmt:message key="search_env_view_by_host.table.machine.name"/></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${enginesByContentSet}" var="contentSetEngines">
                <c:set var="contentSetClass"><c:forEach items="${contentSetEngines}" var="engine">${engine.searchEnvironmentHost.id} </c:forEach></c:set>
                <c:forEach items="${contentSetEngines}" var="engine" varStatus="enginesIteratorStatus">
                  <tr>
                    <c:if test="${enginesIteratorStatus.first}">
                      <td id="${engine.physicalPartition.id}_td1" class="${contentSetClass}" rowspan="${fn:length(contentSetEngines)}">
                        <admin-beans:getLogicalPartitionByPhysical var="logicalPartition" physicalPartition="${engine.physicalPartition}"/>
                        <div class="info">
                          <c:out value="${logicalPartition.name}" /><br/>
                          <fmt:message key="search_env_view.contentitems">
                            <fmt:param value="${logicalPartition.itemsIndexed}" />
                          </fmt:message><br/>
                          <fmt:message key="search_env_view.size">
                            <fmt:param value="${logicalPartition.sizeMB}" />
                          </fmt:message>
                        </div>
                      </td>
                    </c:if>
                    <td id="${engine.physicalPartition.id}_${engine.id}_td2" class="${engine.searchEnvironmentHost.id}">
                      <c:out value="${engine.id}" /><br/>
                      <fmt:formatNumber pattern="####" value="${engine.port}" var="portNumberMask" />
                      <fmt:message key="search_env_view.port">
                        <fmt:param value="${portNumberMask}"/>
                      </fmt:message><br/>
                      <fmt:message key="search_env_view.status">
                        <fmt:param value="${engine.serverState}" />
                      </fmt:message>
                    </td>
                    <td id="${engine.physicalPartition.id}_${engine.id}_${engine.searchEnvironmentHost.id}_td3" class="${engine.searchEnvironmentHost.id}"
                        onmousemove="onContentSetOver('${engine.searchEnvironmentHost.id}');" onmouseout="onContentSetOut('${engine.searchEnvironmentHost.id}');">
                      <fmt:message key="search_env_view_by_host.table.machine.name.tooltip" var="hostMashineToolTip"/>
                      <c:url value="/searchadmin/search_env_host_machine_config.jsp" var="hostUrl">
                        <c:if test="${!empty projectId}">
                          <c:param name="projectId" value="${projectId}"/>
                        </c:if>
                        <c:param name="environmentId" value="${environmentId}"/>
                        <c:param name="hostId" value="${engine.searchEnvironmentHost.id}"/>
                      </c:url>
                      <a href="${hostUrl}" onclick="return loadRightPanel(this.href);">
                        <c:out value="${engine.searchEnvironmentHost.searchMachine.hostname}" />
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:forEach>
            </tbody>
          </table>
          <%-- TODO move style to .css --%>
          <style>
            .highlighted {background-color:#E0E7F2;}
            .common {background-color:#ffffff;}
          </style>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_view_by_partition.jsp#2 $$Change: 651448 $--%>
