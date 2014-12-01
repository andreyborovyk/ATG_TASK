<%--
Indexing error log popup window.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_error_log.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="stepStatusId" var="stepStatusId"/>
  <d:getvalueof param="isSuperStep" var="isSuperStep"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SyncStepErrorFormHandler" var="syncStepErrorFormHandler"/>
  <d:importbean bean="/atg/searchadmin/adminui/Configuration" var="configuration"/>
  
  <tags:separateWindow title="sync_error_log.title">

    <script type="text/javascript" src="${configuration.dojoBaseScriptUri}${configuration.dojoJS}" djConfig="isDebug: false, parseOnLoad: true, usePlainJson: true"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/indexing_detail_log.js"></script>
    
    <script type="text/javascript">
      dojo.require("dojox.layout.ContentPane");
      dojo.require("dojo.parser");
    </script>   
    <div class="content_sp">
      <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
      <admin-beans:getSyncErrorLog varIndexingErrorLog="errorLog" varCommonIndexingInfo="commonIndexingInfo"
                                   syncTaskId="${syncTaskId}" syncStatusId="${stepStatusId}" superStep="${isSuperStep}"/>
      <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.syncStepStatusErrorsComparator" var="comparator"/>
      <admin-ui:sort var="indexingErrorLog" items="${errorLog}" comparator="${comparator}" sortMode="undefined"/>
      
      <h3 class="overview">
        <fmt:message key="sync_error_log.indexing_error_log"/>
      </h3>
      <table class="form double" cellspacing="0" cellpadding="0">
      <tbody>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.search_project"/>
          </td>
          <td><c:out value="${project.name}"/></td>
        </tr>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.total_errors"/>
          </td>
          <td>${commonIndexingInfo.totalErrors}</td>
        </tr>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.fatal_errors"/>
          </td>
          <td>
            <fmt:message key="sync_error_log.fatal_errors.${commonIndexingInfo.fatalErrors ? 'yes' : 'no'}"/>
          </td>
        </tr>
      </tbody>
      </table>
      <table class="form double" cellspacing="0" cellpadding="0">
      <tbody>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.indexing_start"/>
          </td>
          <td>${commonIndexingInfo.indexingStart}</td>
        </tr>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.indexing_end"/>
          </td>
          <td>${commonIndexingInfo.indexingEnd}</td>
        </tr>
        <tr>
          <td class="label" nowrap="">
            <fmt:message key="sync_error_log.size"/>
          </td>
          <td>
            ${commonIndexingInfo.size}
            <fmt:message key="synchronization_index_summary.size.MB"/>
          </td>
        </tr>
      </tbody>
      </table>
      
      <c:url var="successURL" value="/searchadmin/sync_error_log.jsp">
        <c:param name="projectId" value="${projectId}"/>
        <c:param name="stepStatusId" value="${stepStatusId}"/>
        <c:param name="isSuperStep" value="${isSuperStep}"/>
        <c:param name="syncTaskId" value="${syncTaskId}"/>
      </c:url>
      <admin-ui:paging var="preparedIndexingErrorLog" items="${indexingErrorLog}"
              page="${syncStepErrorFormHandler.currentPage}" itemsPerPage="${syncStepErrorFormHandler.itemsPerPage}"
              varCurrentPage="currentPage" varTotalPages="totalPages" />

      <d:form method="post" action="sync_error_log.jsp">
        <d:input bean="SyncStepErrorFormHandler.paging" type="submit" id="pagingButton"
             style="display:none" iclass="formsubmitter" name="paging"/>
        <d:input bean="SyncStepErrorFormHandler.currentPage" type="hidden"
             name="currentPage" id="currentPageHidden" />
        <d:input type="hidden" bean="SyncStepErrorFormHandler.successURL" value="${successURL}"/>
      </d:form>
      
      <c:if test="${not empty indexingErrorLog}">
        <d:include page="/templates/paging.jsp">
          <d:param name="totalItems" value="${fn:length(indexingErrorLog)}" />
          <d:param name="currentPage" value="${currentPage}" />
          <d:param name="totalPages" value="${totalPages}" />
          <d:param name="itemsPerPage" value="${syncStepErrorFormHandler.itemsPerPage}" />
          <d:param name="onPage" value="doPaging" />
        </d:include>
      </c:if>
      
      <table id="logTable" class="data" cellspacing="0" cellpadding="0">
        <thead>
          <tr>
            <th></th>
            <th>
              <fmt:message key="sync_error_log.table.header.type"/>
            </th>
            <th>
              <fmt:message key="sync_error_log.table.header.error_message"/>
            </th>
            <th>
              <fmt:message key="sync_error_log.table.header.time"/>
            </th>
          </tr>
        </thead>
        <c:forEach items="${preparedIndexingErrorLog}" var="syncStepStatusError">
          <c:set var="idValue" value="${syncStepStatusError.id}"/>
          <c:if test="${empty isSuperStep || isSuperStep}">
            <c:set var="stepStatusIdValue" value="${syncStepStatusError.syncStepStatus.id}"/>
          </c:if>
          <c:if test="${not empty isSuperStep && not isSuperStep}">
            <c:set var="stepStatusIdValue" value="${stepStatusId}"/>
          </c:if>
          <c:set var="errorMessageValue" value="${syncStepStatusError.errorMessage}"/>
          <c:set var="fatalValue" value="${syncStepStatusError.type eq 'fatalError'}"/>
          <c:set var="occurredTimeValue" value="${syncStepStatusError.occurredTime}"/>
          <c:set var="errorType" value="${syncStepStatusError.type}"/>
          <tr>
            <td class="noneborderright" width="20">
              <fmt:message key="sync_error_log.detail.detail.tooltip" var="detail_tooltip"/>
              <span id="logToggle_${idValue}" class="indexingDetailLogClose" title="${detail_tooltip}"
                     onclick="return toggleLog(${idValue});">&nbsp;</span>
            </td>
            <c:set var="iconClass" value="${errorType}"/>
            <fmt:message var="iconTitle" key="sync_error_log.${errorType}.tooltip"/>
            <td class="aligncenter ${iconClass}" nowrap="" title="${iconTitle}">
            </td>
            <td class="noneborderrl">
              <div id="em_${idValue}">
                <c:out value="${fn:substring(errorMessageValue, 0, 150)}" />
              </div>
              <input type="hidden" id="full_em_${idValue}" value="${fn:escapeXml(errorMessageValue)}" />
            </td>
            <td class="noneborderleft" nowrap="">
              ${occurredTimeValue}
            </td>
          </tr>
          <tr class="alt closedetail" id="tr_${idValue}">
            <td colspan="4">
              <c:url var="cpUrl" value="/searchadmin/view_indexing_detail.jsp">
                <c:param name="syncTaskId" value="${syncTaskId}" />
                <c:param name="syncStatusId" value="${stepStatusIdValue}" />
                <c:param name="syncStatusEntryId" value="${idValue}" />
              </c:url>
              <div dojoType="dojox.layout.ContentPane" id="cp_${idValue}" style="display:none; width:100%;"
                  href="${cpUrl}" executeScripts="false" cacheContent="false" scriptSeparation="false"
                  loadingMessage='<fmt:message key="tree.dojo.loading" var="loading_message"/>'>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty indexingErrorLog}">
          <tr>
            <td colspan="4">
              <fmt:message key="sync_error_log.table.none"/>
            </td>
          </tr>
        </c:if>
      </table>
      
      <c:if test="${not empty indexingErrorLog}">
        <d:include page="/templates/paging.jsp">
          <d:param name="totalItems" value="${fn:length(indexingErrorLog)}" />
          <d:param name="currentPage" value="${currentPage}" />
          <d:param name="totalPages" value="${totalPages}" />
          <d:param name="itemsPerPage" value="${syncStepErrorFormHandler.itemsPerPage}" />
          <d:param name="onPage" value="doPaging" />
        </d:include>
      </c:if>
    </div>
  </tags:separateWindow>
  
  <script type="text/javascript">
    function doPaging(pValue) {
      if (pValue) {
        var pagingHidden = document.getElementById('currentPageHidden');
        var v = parseInt(pValue);
        if (isNaN(v) || v <= 0) {
          v = 1;
        }
        pagingHidden.value = v;
      }
      var pagingButton = document.getElementById('pagingButton');
      pagingButton.click();
      return false;
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_error_log.jsp#2 $$Change: 651448 $--%>

