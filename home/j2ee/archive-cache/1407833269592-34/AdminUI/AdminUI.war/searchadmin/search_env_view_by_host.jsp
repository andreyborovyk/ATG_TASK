<%--
  Search environment view by host JSP. Show hosts with linked search engines and partitions.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_view_by_host.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:include page="search_env_tab_control.jsp">
    <d:param name="active" value="first"/>
  </d:include>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <d:getvalueof param="projectId" var="projectId" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <br/>
      <admin-beans:getSearchEnvironment environmentId="${environmentId}" var="enviroment"/>
      <c:choose>
        <c:when test="${fn:length(enviroment.hosts) == 0}">
          <fmt:message key="search_env_view_by_partition.no.hosts"/>
        </c:when>
        <c:otherwise>
          <table class="data" cellspacing="0" cellpadding="0">
            <thead>
              <tr>
                <th><fmt:message key="search_env_view_by_partition.table.machine.name"/></th>
                <th><fmt:message key="search_env_view_by_partition.table.engine.name"/></th>
                <th><fmt:message key="search_env_view_by_partition.table.partition.name"/></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${enviroment.hosts}" var="currentHost">
                <admin-beans:getSearchEngineSet hostId="${currentHost.id}" var="searchEngines" count="numSearchEngines"/>
                  <c:set var="hostClass"><c:forEach items="${searchEngines}" var="engine"
                      ><admin-beans:getLogicalPartitionByPhysical var="logicalPartition" physicalPartition="${engine.physicalPartition}"
                      />${logicalPartition.id} </c:forEach></c:set>
                  <c:forEach items="${searchEngines}" var="engine" varStatus="enginesIteratorStatus">
                    <tr>
                      <c:if test="${enginesIteratorStatus.first}">
                        <td id="${currentHost.id}_td1" class="${hostClass}" rowspan="${numSearchEngines}">
                          <fmt:message key="search_env_view_by_partition.table.machine.name.tooltip" var="hostMashineToolTip"/>
                          <c:url value="/searchadmin/search_env_host_machine_config.jsp" var="hostUrl">
                            <c:if test="${!empty projectId}">
                              <c:param name="projectId" value="${projectId}"/>
                            </c:if>
                            <c:param name="environmentId" value="${environmentId}"/>
                            <c:param name="hostId" value="${currentHost.id}"/>
                          </c:url>
                          <a href="${hostUrl}" onclick="return loadRightPanel(this.href);">
                            <c:out value="${currentHost.searchMachine.hostname}" />
                          </a>
                        </td>
                      </c:if>
                      <admin-beans:getLogicalPartitionByPhysical var="logicalPartition" physicalPartition="${engine.physicalPartition}"/>
                      <td id="${currentHost.id}_${engine.id}_td2" class="${logicalPartition.id}">
                        <fmt:message key="search_env_view.searchengine"/> ${engine.id}<br/>
                        <div class="info">
                          <fmt:formatNumber pattern="####" value="${engine.port}" var="portNumberMask" />
                          <fmt:message key="search_env_view.port">
                            <fmt:param value="${portNumberMask}"/>
                          </fmt:message><br/>
                          <fmt:message key="search_env_view.status">
                            <fmt:param value="${engine.serverState}" />
                          </fmt:message>
                        </div>
                      </td>
                      <td id="${currentHost.id}_${engine.id}_${logicalPartition.id}_td3" class="${logicalPartition.id}"
                          onMouseOver="onContentSetOver('${logicalPartition.id}');" onMouseOut="onContentSetOut('${logicalPartition.id}');">
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
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_view_by_host.jsp#2 $$Change: 651448 $--%>
