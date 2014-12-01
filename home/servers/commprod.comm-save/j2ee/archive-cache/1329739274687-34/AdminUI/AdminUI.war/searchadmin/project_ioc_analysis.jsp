<%--
Index Output Config Analysis page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_ioc_analysis.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <d:getvalueof var="projectId" param="projectId" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <admin-beans:getIOCAnalysisData var="iocData" projectId="${projectId}" environment="environment"/>
      <c:choose>
        <c:when test="${empty environment}">
          <p><fmt:message key="project_ioc_analysis.unavailable" /></p>
          <p><fmt:message key="project_index_review.unavailable" /></p>
        </c:when>
        <c:when test="${empty iocData}">
          <p><fmt:message key="project_ioc_analysis.no_content.label"/></p>
        </c:when>
        <c:when test="${not empty iocData}">
          <br>
          <table class="form" cellspacing="0" cellpadding="0">
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.index"/></td>
              <td>
                <admin-beans:getSyncTaskByEnvironment environment="${environment}" var="currentSyncTask"/>
                <fmt:message var="timeFormat" key="timeFormat"/>
                <fmt:formatDate value="${currentSyncTask.currentEndDate}" pattern="${timeFormat}"/>
                (<fmt:message key="synchronization.task.${currentSyncTask.syncTaskType}" />)
              </td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.environment"/></td>
              <td><c:out value="${environment.envName}"/></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.total"/></td>
              <td><c:out value="${iocData.total}"/></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.analyzed"/></td>
              <td><c:out value="${iocData.reviewed}"/></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.properties_analyzed"/></td>
              <td><c:out value="${iocData.properties}"/></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="project_ioc_analysis.text_analyzed"/></td>
              <td><c:out value="${iocData.text}"/></td>
            </tr>
          </table>
          
          <d:form method="POST" action="project_ioc_analysis.jsp">
            <c:forEach items="${iocData.checkGroups}" var="checkGroup">
              <c:if test="${not empty checkGroup.checks}">
                <d:include page="project_ioc_analysis_table.jsp">
                  <d:param name="checkGroup" value="${checkGroup}"/>
                </d:include>
              </c:if>
            </c:forEach>
            <d:input type="submit" bean="SortFormHandler.sort" iclass="formsubmitter" style="display:none" id="sortIocChecksInput"/>
          </d:form>
        </c:when>
      </c:choose>
    </div>
  </div>
  <script type="text/javascript">
    function tableOnSort(tableId, columnName, sortDirection) {
      var postfix = tableId.split("iocChecks")[1];
      var sortFieldValue = document.getElementById('hiddenCheckSortValue' + postfix);
      if (sortFieldValue) {
        sortFieldValue.value = columnName + " " + sortDirection;
      }
      document.getElementById('sortIocChecksInput').click();
    }
    
    function showHideTable (checkType, show){
      document.getElementById('hiddenCheckSortValue_' + checkType + '_show').value = show;
      return showHideSettings('iocChecksDiv_' + checkType, show);
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_ioc_analysis.jsp#1 $$Change: 651360 $--%>
