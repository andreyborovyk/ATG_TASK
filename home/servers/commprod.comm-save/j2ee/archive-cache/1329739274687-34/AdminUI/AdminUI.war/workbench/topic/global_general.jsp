<%--
  JSP,  showing all Topic Sets.
  Used for change oder of Topic Set, allow import/export Topic Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/global_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Retrieving current value for table sorting --%>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/SortTopicSetsFormHandler.sort" var="sortValue"/>

  <topic:topicSetFindAll var="topicSets"/>

  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.topicSetComparator" var="comparator"/>
  <admin-ui:sort var="sortedTopicSets" items="${topicSets}" comparator="${comparator}" sortMode="${sortValue}"/>

  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                var="activeProjectId"/>

  <fmt:message key="topic_sets.table.partOfActiveProject" var="activeSearchProjectTitle"/>

  <c:url value="${topicPath}/topicset_new.jsp" var="newTopicURL"/>
  <c:url value="${topicPath}/topicset_import.jsp" var="importURL"/>
  <c:url var="backUrl" value="${topicPath}/global_general.jsp"/>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <d:form action="${backUrl}" method="POST">
        <p>
          <input type="button"
            value="<fmt:message key='topic_topicset.new.topicset'/>"
            title="<fmt:message key='topic_topicset.new.topicset'/>"
            onclick="return loadRightPanel('${newTopicURL}');"/>
          <input type="button"
            value="<fmt:message key='topic_topicset.import.topic'/>"
            title="<fmt:message key='topic_topicset.import.topic'/>"
            onclick="return loadRightPanel('${importURL}');"/>
          <nobr>
            <%-- Pattern macros and link --%>
            <topic:globalMacroFindAll var="globalMacros"/>
            <fmt:message key="edit_topic_set.pattern_macros"/> ${fn:length(globalMacros)}
            <c:url value="${topicPath}/global_macros.jsp" var="globalMacrosUrl" >
              <c:param name="macrosType" value="head"/>
              <c:param name="topicId" value=""/>
              <c:param name="topicSetId" value=""/>
            </c:url>
            [<a href="${globalMacrosUrl}" onclick="return loadRightPanel(this.href);"
              title="<fmt:message key='edit_topic_set.edit_macros.tooltip'/>" ><fmt:message key="edit_topic_set.edit_macros"/></a>]
          </nobr>
          <span class="ea"><tags:ea key="embedded_assistant.topic.global_general" /></span>
        </p>

        <%-- table --%>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="topicSet" items="${sortedTopicSets}"
                        sort="${sortValue}" onSort="tableOnSort">
          <admin-ui:column title="topic_sets.table.name" type="sortable" name="name">
            <c:url var="topicSetUrl" value="${topicPath}/topicset.jsp">
              <c:param name="topicSetId" value="${topicSet.id}"/>
            </c:url>
            <a href="${topicSetUrl}" onclick="return loadRightPanel(this.href);">
              <c:out value="${topicSet.name}"/>
            </a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <c:set var="display" value="none"/>
            <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${topicSet.id}" itemType="topics"/>
            <c:set var="allUsedProjects"/>
            <c:forEach items="${projects}" var="currentProject">
              <c:set var="allUsedProjects" value="${allUsedProjects} ${currentProject.id}"/>
              <c:if test="${currentProject.id eq activeProjectId}">
                <c:set var="display" value="inline"/>
              </c:if>
            </c:forEach>
            <a class="icon partOfActiveSearchProjectAsset ${allUsedProjects}" title="${activeSearchProjectTitle}" href="#"
               style="display:${display}" onclick="return false;">proj</a>
          </admin-ui:column>

          <admin-ui:column title="topic_sets.table.description" type="sortable" name="description">
            <c:out value="${topicSet.description}"/>
          </admin-ui:column>

          <admin-ui:column title="topic_sets.table.language" type="sortable" name="language">
            <c:if test="${empty topicSet.language}">
              <fmt:message key="topic_sets.table.language.epmty"/>
            </c:if>
            <c:if test="${not empty topicSet.language}">
              <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${topicSet.language}"/>
              <c:out value="${localizedLanguage}"/>
            </c:if>
          </admin-ui:column>

          <admin-ui:column title="topic_sets.table.modify" type="sortable" name="modified">
            <c:if test="${empty topicSet.lastModified}">
              <fmt:message key="topic_sets.last_modified.empty"/>
            </c:if>
            <c:if test="${not empty topicSet.lastModified}">
              <fmt:message var="timeFormat" key="timeFormat"/>
              <fmt:formatDate value="${topicSet.lastModified}" type="both" pattern="${timeFormat}"/>
            </c:if>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="topic_sets.table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
               onclick="return submitTopicSetForm(${topicSet.id}, 'copyInput');">copy</a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="topic_sets.table.export" var="exportTitle"/>
            <c:url var="exportSetUrl" value="${topicPath}/topicset_export.jsp">
              <c:param name="topicSetId" value="${topicSet.id}"/>
            </c:url>
            <a class="icon propertyExport" title="${exportTitle}" href="${exportSetUrl}"
               onclick="return loadRightPanel(this.href);">exp</a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="topic_sets.table.delete" var="deleteTitle"/>
            <c:url value="${topicPath}/topicset_delete_popup.jsp" var="popUrl">
              <c:param name="topicSetId" value="${topicSet.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>

        </admin-ui:table>

        <c:url var="successURL" value="${topicPath}/global_general.jsp"/>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/SortTopicSetsFormHandler.successURL"
                 value="${successURL}"
                 type="hidden"/>
        <script type="text/javascript">
          function tableOnSort(tableId, columnName, sortDirection){
            var sortButton = document.getElementById('sortInput');
            sortButton.value = columnName + " " + sortDirection;
            sortButton.click();
          }
        </script>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/SortTopicSetsFormHandler.sort"
                 value="field mode"
                 type="submit"
                 id="sortInput"
                 iclass="formsubmitter" style="display:none"/>

        <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler.topicSetId"
                 value="${topicSetId}" id="tpSetId"/>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler.copy"
                 value="field mode" type="submit" iclass="formsubmitter"
                 id="copyInput" style="display:none"/>
        
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler.successURL"
                 type="hidden" value="${successURL}" name="successURL"/>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler.errorURL"
                 type="hidden" value="${successURL}" name="errorURL"/>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler.cancelURL"
                 type="hidden" value="${successURL}" name="cancelURL"/>
      </d:form>
    </div>
  </div>
  <script type="text/javascript">
    function submitTopicSetForm(id, mode) {
      document.getElementById('tpSetId').value = id;
      document.getElementById(mode).click();
      return false;
    }
    function refreshRightPane(obj) {
      var icons = getElementsByClassName('partOfActiveSearchProjectAsset');
      for (var i = 0; i < icons.length; i++) {
        icons[i].style.display = 'none';
      }
      if (obj.activeProjectId) {
        icons = getElementsByClassName(obj.activeProjectId);
        for (var i = 0; i < icons.length; i++) {
          icons[i].style.display = 'inline';
        }
      }
    }
  </script>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTopicSetNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/global_general.jsp#2 $$Change: 651448 $--%>
