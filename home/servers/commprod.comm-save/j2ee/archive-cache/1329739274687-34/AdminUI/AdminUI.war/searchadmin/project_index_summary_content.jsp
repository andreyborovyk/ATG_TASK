<%--
Index summary statistics section (content table) page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary_content.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="syncTaskHistoryInfo" var="syncTaskHistoryInfo"/>
  <d:getvalueof param="popupView" var="popupView"/>
  
  <admin-beans:getIndexSummaryInfo taskId="${syncTaskHistoryInfo.syncTaskId}"
      var="indexSummaryInfo" varLanguages="languages" hasRunningIndex="running"/>
  <admin-beans:getSyncTaskGeneralStatus var="syncTaskGeneralStatus" syncTaskId="${syncTaskHistoryInfo.syncTaskId}"/>
  <table class="data autoWidth" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <th>&nbsp;</th>
        <c:forEach var="item" items="${indexSummaryInfo}">
          <th class="center">${item.partition.name}</th>
        </c:forEach>
        <th class="center"><fmt:message key="synchronization_index_summary.table.header.total"/></th>
      </tr>
    </thead>
    <tbody>
      <tr class="group">
        <td <c:if test="${not running}">colspan="${fn:length(indexSummaryInfo) + 2}"</c:if>>
          <fmt:message key="project_index_summary_content.table.overview"/>
        </td>
        <c:if test="${running}">
          <c:forEach var="item" items="${indexSummaryInfo}" varStatus="index">
            <td class="center">
              <c:if test="${not empty popupView}">
                <c:url value="/searchadmin/index_verification_overview_popup.jsp" var="viewPartitionUrl">
                  <c:param name="projectId" value="${projectId}"/>
                  <c:param name="partitionId" value="${item.partition.id}"/>
                  <c:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
                </c:url>
                <input type="button" value='<fmt:message key="synchronization_index_summary.table.column0.view_link"/>'
                       title='<fmt:message key="synchronization_index_summary.table.column0.view_link.tooltip"/>'
                       onclick="return showPopUp('${viewPartitionUrl}');"
                <c:if test="${item.emptyIndexedItems}">disabled="y"</c:if> />
              </c:if>
              <c:if test="${empty popupView}">
                <c:url value="/searchadmin/index_verification_overview.jsp" var="viewPartitionUrl">
                  <c:param name="projectId" value="${projectId}"/>
                  <c:param name="partitionId" value="${item.partition.id}"/>
                  <c:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
                </c:url>
                <input type="button" value='<fmt:message key="synchronization_index_summary.table.column0.view_link"/>'
                       title='<fmt:message key="synchronization_index_summary.table.column0.view_link.tooltip"/>'
                       onclick="return loadRightPanel('${viewPartitionUrl}');"
                <c:if test="${item.emptyIndexedItems}">disabled="y"</c:if> />
              </c:if>
            </td>
          </c:forEach>
          <td></td>
        </c:if>
      </tr>
      <tr>
        <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.content_sets"/></td>
        <c:set var="totalCS" value="0"/>
        <c:forEach var="item" items="${indexSummaryInfo}">
          <c:set var="totalCS" value="${totalCS + item.contentSets}"/>
          <td class="right">${item.contentSets}</td>
        </c:forEach>
        <td class="right">${totalCS}</td>
      </tr>
      <tr class="alt">
        <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.size_of_ind_cont"/></td>
        <c:set var="totalS" value="0"/>
        <c:forEach var="item" items="${indexSummaryInfo}">
          <td class="right">
            <c:choose>
              <c:when test="${syncTaskGeneralStatus eq 'Cancelled' or syncTaskGeneralStatus eq 'Failed'}">
                <fmt:message key="synchronization_index_summary.constant.na" />
              </c:when>
              <c:when test="${item.sizeOfIndexableContent < 0.1}">
                <fmt:message key="synchronization_index_summary.table.less.than">
                  <fmt:param>
                    <fmt:formatNumber value="0.1" maxFractionDigits="1"/>
                  </fmt:param>
                </fmt:message>
                <fmt:message key="synchronization_index_summary.size.MB"/>
                <c:set var="totalS" value="${totalS + item.sizeOfIndexableContent}"/>
              </c:when>              
              <c:otherwise>
                <fmt:formatNumber value="${item.sizeOfIndexableContent}" maxFractionDigits="1"/>
                <fmt:message key="synchronization_index_summary.size.MB"/>
                <c:set var="totalS" value="${totalS + item.sizeOfIndexableContent}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>
        <td class="right">
          <c:choose>
            <c:when test="${syncTaskGeneralStatus eq 'Cancelled' or syncTaskGeneralStatus eq 'Failed'}">
              <fmt:message key="synchronization_index_summary.constant.na" />
            </c:when>
            <c:when test="${totalS < 0.1}">
              <fmt:message key="synchronization_index_summary.table.less.than">
                <fmt:param>
                  <fmt:formatNumber value="0.1" maxFractionDigits="1"/>
                </fmt:param>
              </fmt:message>
              <fmt:message key="synchronization_index_summary.size.MB"/>
            </c:when>
            <c:otherwise>
              <fmt:formatNumber value="${totalS}" maxFractionDigits="1"/>
              <fmt:message key="synchronization_index_summary.size.MB"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>

      <tr>
        <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.partition_size"/></td>
        <c:set var="totalPartitionSize" value="0"/>
        <c:forEach var="item" items="${indexSummaryInfo}">
          <td class="right">
            <c:choose>
              <c:when test="${syncTaskGeneralStatus eq 'Cancelled' or syncTaskGeneralStatus eq 'Failed'}">
                <fmt:message key="synchronization_index_summary.constant.na" />
              </c:when>
              <c:otherwise>
                <c:out value="${item.partitionSize}"/>
                <fmt:message key="synchronization_index_summary.size.MB"/>
                <%--TODO: probably the rounding up is required --%>
                <c:set var="totalPartitionSize" value="${totalPartitionSize + item.partitionSize}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>
        <td class="right">
          <c:choose>
            <c:when test="${syncTaskGeneralStatus eq 'Cancelled' or syncTaskGeneralStatus eq 'Failed'}">
              <fmt:message key="synchronization_index_summary.constant.na" />
            </c:when>
            <c:otherwise>
              <c:out value="${totalPartitionSize}"/>
              <fmt:message key="synchronization_index_summary.size.MB"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>

      <tr class="alt">
        <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.percentage_max_size"/></td>
        <c:set var="totalPercentageOfMaxSize" value="0"/>
        <c:forEach var="item" items="${indexSummaryInfo}">
          <td class="right">
            <span <c:if test="${item.percentageOfMaxSize ge 100}">class="exceeded"</c:if>>
              <c:out value="${item.percentageOfMaxSize}"/><fmt:message key="synchronization_index_summary.percent"/>
              <c:set var="totalPercentageOfMaxSize" value="${totalPercentageOfMaxSize + item.percentageOfMaxSize}"/>
            </span>
          </td>
        </c:forEach>
        <td class="right">
          <c:out value="${totalPercentageOfMaxSize}"/>
          <fmt:message key="synchronization_index_summary.percent"/>
        </td>
      </tr>

      <c:if test="${running}">
        <tr>
          <td class="grouped"><fmt:message key="synchronization_index_summary.table.index_scheme"/></td>
          <c:forEach var="item" items="${indexSummaryInfo}">
            <td class="right">--</td>
          </c:forEach>
          <c:forEach var="item" items="${indexSummaryInfo}">
            <c:if test="${not empty item.compression}">
              <c:set var="compression" value="${item.compression}" />
            </c:if>
          </c:forEach>
          <td class="right"><fmt:message key="synchronization_index_summary.table.index_scheme.${compression}"/></td>
        </tr>
      </c:if>

      <c:if test="${running}">
        <tr class="group">
          <td colspan="${fn:length(indexSummaryInfo) + 2}"><fmt:message key="project_index_summary_content.table.content_items"/></td>
        </tr>
        <tr>
          <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.content_items"/></td>
          <c:set var="total" value="0"/>
          <c:forEach var="item" items="${indexSummaryInfo}">
            <c:set var="total" value="${total + item.totalItems}"/>
            <td class="right">${item.totalItems}</td>
          </c:forEach>
          <td class="right">${total}</td>
        </tr>
        <tr class="alt">
          <td class="grouped" title="<fmt:message key='synchronization_index_summary.table.column0.structured.help.description'/>">
            <fmt:message key="synchronization_index_summary.table.column0.structured"/>
          </td>
          <c:set var="total" value="0"/>
          <c:forEach var="item" items="${indexSummaryInfo}">
            <c:set var="total" value="${total + item.structured}"/>
            <td class="right">${item.structured}</td>
          </c:forEach>
          <td class="right">${total}</td>
        </tr>
        <tr>
          <td class="grouped"><fmt:message key="synchronization_index_summary.table.column0.unstructured"/></td>
          <c:set var="total" value="0"/>
          <c:forEach var="item" items="${indexSummaryInfo}">
            <c:set var="total" value="${total + item.unstructured}"/>
            <td class="right">${item.unstructured}</td>
          </c:forEach>
          <td class="right">${total}</td>
        </tr>
        <c:forEach var="language" items="${languages}" varStatus="languagesStatus">
          <tr class="${languagesStatus.index % 2 == 0 ? 'alt' : ''}">
            <td class="grouped">
              <fmt:message key="synchronization_index_summary.table.column0.language">
                <fmt:param><fmt:message key="${language}" /></fmt:param>
              </fmt:message>
            </td>
            <c:set var="total" value="0"/>
            <c:forEach var="item" items="${indexSummaryInfo}">
              <c:set var="lanCount" value="${item.languages[language]}"/>
              <c:set var="total" value="${total + lanCount}"/>
              <td class="right">${lanCount == null ? 0 : lanCount}</td>
            </c:forEach>
            <td class="right">${total}</td>
          </tr>
        </c:forEach>
      </c:if>
    </tbody>
  </table>
  <c:if test="${not running}">
    <fmt:message key="synchronization_index_summary.stopped" />
  </c:if>
  <br/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary_content.jsp#1 $$Change: 651360 $--%>
