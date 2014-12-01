<%--
Index Output Config Analysis page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_ioc_analysis_table.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof var="checkGroup" param="checkGroup" />
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <c:set var="checkType" value="${checkGroup.type}"/>
  <c:set var="checks" value="${checkGroup.checks}"/>
  <c:set var="checksCount" value="${fn:length(checkGroup.checks)}"/>
  
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.IOCCheckComparator" var="comparator"/>
  <d:getvalueof bean="SortFormHandler.sortTables.iocChecks_${checkType}" var="sortValue"/>
  <d:getvalueof bean="/atg/searchadmin/audit/IndexReviewService.iocChecksCountPerGroupToShow" var="iocChecksCount"/>
  <c:if test="${empty sortValue}">
    <c:set var="sortValue" value="check_level desc"/>
  </c:if>
  <d:getvalueof bean="SortFormHandler.sortTables.iocChecks_${checkType}_show" var="show"/>
  <c:if test="${empty show}">
    <c:if test="${checksCount <= iocChecksCount}">
      <c:set var="show" value="true"/>
    </c:if>
    <c:if test="${checksCount > iocChecksCount}">
      <c:set var="show" value="false"/>
    </c:if>
  </c:if>
  <admin-ui:sort var="sortedChecks" items="${checks}" comparator="${comparator}" sortMode="${sortValue}"/>

  <fmt:message key="project_ioc_analysis.group.${checkType}" var="headerLabel"/>
  <h3>
    <c:out value="${headerLabel}"/>
    <span id="iocChecksDiv_${checkType}:hideLink" class="headerLink" <c:if test="${not show}">style="display:none;"</c:if>>
      [<a href="#" onclick="return showHideTable('${checkType}', false);">
        <fmt:message key="project_ioc_analysis.hide_link">
          <fmt:param value="${checksCount}"/>
        </fmt:message>
       </a>]
    </span>
    <span id="iocChecksDiv_${checkType}:showLink" class="headerLink" <c:if test="${show}">style="display:none;"</c:if>>
      [<a href="#" onclick="return showHideTable('${checkType}', true);">
        <fmt:message key="project_ioc_analysis.show_link">
          <fmt:param value="${checksCount}"/>
        </fmt:message>
      </a>]
    </span>
    <span class="ea"><tags:ea key="embedded_assistant.project_ioc_analysis.${checkType}" /></span>
  </h3>

  <div id="iocChecksDiv_${checkType}" <c:if test="${not show}">style="display:none;"</c:if>>
    <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
              var="check" items="${sortedChecks}"
              sort="${sortValue}" onSort="tableOnSort" tableId="iocChecks_${checkType}">
      <admin-ui:column type="sortable" title="project_ioc_analysis.table.type" name="check_type">
        <c:out value="${check.type}"/>
      </admin-ui:column>
      <admin-ui:column type="sortable" title="project_ioc_analysis.table.description" name="check_desc">
        <c:out value="${check.desc}"/>
      </admin-ui:column>
      <admin-ui:column type="sortable" title="project_ioc_analysis.table.count" name="check_count">
        <c:out value="${check.count}"/>
      </admin-ui:column>
        <admin-ui:column type="sortable" title="project_ioc_analysis.table.average" name="check_average">
        <c:out value="${check.average}"/>
      </admin-ui:column>
      <admin-ui:column type="sortable" title="project_ioc_analysis.table.name" name="check_name">
        <c:if test="${empty check.name}"><fmt:message key="project_ioc_analysis.empty_value"/></c:if>
        <c:out value="${check.name}"/>
      </admin-ui:column>
      <admin-ui:column type="sortable" title="project_ioc_analysis.table.value" name="check_value">
        <c:if test="${empty check.value}"><fmt:message key="project_ioc_analysis.empty_value"/></c:if>
        <c:out value="${check.value}"/>
      </admin-ui:column>
    </admin-ui:table>
  </div>
  <d:input id="hiddenCheckSortValue_${checkType}" type="hidden" bean="SortFormHandler.sortTables.iocChecks_${checkType}"/>
  <d:input id="hiddenCheckSortValue_${checkType}_show" type="hidden" bean="SortFormHandler.sortTables.iocChecks_${checkType}_show"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_ioc_analysis_table.jsp#2 $$Change: 651448 $--%>
