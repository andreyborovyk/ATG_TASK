<%--
Indexing detail log page fragment.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/view_indexing_detail.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="syncStatusId" var="syncStatusId"/>
  <d:getvalueof param="syncStatusEntryId" var="syncStatusEntryId"/>
  <admin-beans:getIndexingDetailLog varIndexingDetailLog="indexingDetailLog"
    syncTaskId="${syncTaskId}" syncStatusId="${syncStatusId}" syncStatusEntryId="${syncStatusEntryId}"/>
  <c:if test="${indexingDetailLog.contentId != null}">
    <fmt:message key="sync_error_log.table.data.content_item"/>
    <c:out value="${indexingDetailLog.contentId}"/>
  </c:if>
  <c:if test="${indexingDetailLog.errorDataSet != null}">
    <h3>
      <fmt:message key="sync_error_log.customizations_header">
        <fmt:param value="${indexingDetailLog.errorDataSet.type}"/>
      </fmt:message>
      <c:if test="${not empty indexingDetailLog.errorDataSet.name}">
         &nbsp;&quot;<c:out value="${indexingDetailLog.errorDataSet.name}"/>&quot;
      </c:if>
    </h3>
    <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table" var="error" items="${indexingDetailLog.errorDataSet.errors}">
      <admin-ui:column title="sync_error_log.customizations_table.identifier" type="static">
        <c:out value="${error.name}"></c:out>
      </admin-ui:column>
      <admin-ui:column title="sync_error_log.customizations_table.type" type="static">
        <c:out value="${error.type}"></c:out>
      </admin-ui:column>
      <admin-ui:column title="sync_error_log.customizations_table.description" type="static">
        <c:out value="${error.description}"></c:out>
      </admin-ui:column>
      <admin-ui:column title="sync_error_log.customizations_table.other_info" type="static">
        <c:if test="${not empty error.correction}">
          <fmt:message key="sync_error_log.customizations_table.correction">
            <fmt:param value="${error.correction}"/>
          </fmt:message>
        </c:if>
        <c:if test="${not empty error.path}">
          <fmt:message key="sync_error_log.customizations_table.path">
            <fmt:param value="${error.path}"/>
          </fmt:message>
        </c:if>
      </admin-ui:column>
    </admin-ui:table>
  </c:if>
  <c:if test="${indexingDetailLog.errorDataSet == null and indexingDetailLog.cause != null}">
    <textarea class="textAreaField" rows="5" readonly="true"
      style="width:99%; overflow:auto; background:#f6f7f9;"><c:out value="${indexingDetailLog.cause}"/></textarea>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/view_indexing_detail.jsp#2 $$Change: 651448 $--%>
