<%--
JSP, used to be included into project_index.jsp. Show information about current index partition.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_partition.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Current project --%>
  <d:getvalueof param="searchProject" var="project" scope="page"/>
  <%-- Current partition--%>
  <d:getvalueof param="partition" var="partition" scope="page"/>
  <d:getvalueof param="duplicatedProjects" var="duplicatedProjects" />
  <%--
    Current status of project:
    1. Project is not created yet.
    2. Project created but have no content set.
    3. Project has not been synchronized yet.
    4. Project successfully created.
    If status is empty, it means, that we are on manage project index page.
  --%>
  <d:getvalueof param="status" var="status" scope="page"/>
  <%-- Current project --%>
  <d:getvalueof param="isProduction" var="isProduction" />

  <%-- Define sorting property --%>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <d:getvalueof bean="SortFormHandler.sortTables.${partition.id}" var="sortValue"/>
  <c:if test="${empty sortValue}">
    <c:set var="sortValue" value="content_source_set_name asc"/>
  </c:if>
  <d:input id="hiddenContentSetSortValue${partition.id}" type="hidden" bean="SortFormHandler.sortTables.${partition.id}"/>

  <%-- Get sorted list of content sources --%>
  <common:contentSourceFindByLogicalPartition var="content" logicalPartition="${partition.id}" />
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.contentSourceSetComparator" var="comparator"/>
  <admin-ui:sort var="sortedContentSourceSet" items="${content}" comparator="${comparator}" sortMode="${sortValue}"/>

  <%-- new Add Content button for the Content Set (well, partition) --%>
  <c:url value="/searchadmin/content_manage.jsp" var="addContentUrl">
    <c:param name="projectId" value="${project.id}"/>
    <c:param name="logicalPartitionId" value="${partition.id}"/>
    <c:param name="action" value="add"/>
  </c:url>

  <c:if test="${status eq 2}">
    <input type="button" onclick="return loadRightPanel('${addContentUrl}');"
           value="<fmt:message key='project_index.buttons.new_content'/>"
           title="<fmt:message key='project_index.buttons.new_content.tooltip'/>"/>
    <br/>&nbsp;
  </c:if>

  <c:set var="advancedMode" value="${empty status and not empty sortedContentSourceSet}" />
  <c:set var="moveAllowed" value="${advancedMode and fn:length(project.index.logicalPartitions) gt 1}" />

  <c:set var="checkAllContentSourcesCheckbox">
  <input id="checkall_${partition.id}" style="margin-left:2px;" type="checkbox" checked="checked"
         onclick="checkAllMove_setChildren('${partition.id}', 'contentSetId')"/>
  </c:set>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="contentSet" items="${sortedContentSourceSet}"
                  sort="${sortValue}" onSort="contentSourceSetOnSort" tableId="${partition.id}">
    <admin-ui:header>
      <d:include page="project_index_partition_header.jsp">
        <d:param name="searchProject" value="${project}"/>
        <d:param name="partition" value="${partition}"/>
        <d:param name="status" value="${status}"/>
        <d:param name="isProduction" value="${isProduction}"/>
        <d:param name="duplicatedProjects" value="${duplicatedProjects}"/>
        <d:param name="sortedContentSourceSet" value="${sortedContentSourceSet}"/>
      </d:include>
    </admin-ui:header>

    <c:if test="${moveAllowed}">
      <admin-ui:column type="checkbox" headerContent="${checkAllContentSourcesCheckbox}">
        <d:input type="checkbox" bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.contentSetId"
            name="contentSetId" value="${contentSet.id}" iclass="unchanged"
            onclick="checkAllMove_setMain('${partition.id}', 'contentSetId')" />
      </admin-ui:column>
    </c:if>

    <admin-ui:column title="project_index.table.content_name" type="sortable" name="content_source_set_name" width="30%">
      <fmt:message key='project_index.edit_content.tooltip' var="tooltipEdit"/>
      <c:url var="contentManageUrl" value="/searchadmin/content_manage.jsp">
        <c:param name="projectId" value="${project.id}"/>
        <c:param name="logicalPartitionId" value="${partition.id}"/>
        <c:param name="setId" value="${contentSet.id}"/>
        <c:param name="action" value="edit"/>
      </c:url>
      <a href="${contentManageUrl}" title="${tooltipEdit}" onclick="return loadRightPanel(this.href);">
        <c:out value="${contentSet.name}"/>
      </a>
    </admin-ui:column>

    <admin-ui:column title="project_index.table.content_location" type="sortable" name="content_source_name" width="70%">
      <admin-beans:getContentLocation var="contentLocation" contentSource="${contentSet}" />
      <c:out value="${contentLocation}"/>
    </admin-ui:column>

    <c:if test="${advancedMode}">
      <admin-ui:column type="icon">
        <a href="${contentManageUrl}" class="icon propertyEdit" onclick="return loadRightPanel(this.href);"
           title="${tooltipEdit}">edit</a>
      </admin-ui:column>
      <admin-ui:column type="icon">
        <fmt:message key="project_index.copy_to_new_content.tooltip" var="tooltipCopy"/>
        <c:url var="contentCopyUrl" value="/searchadmin/content_manage.jsp">
          <c:param name="projectId" value="${project.id}"/>
          <c:param name="logicalPartitionId" value="${partition.id}"/>
          <c:param name="setId" value="${contentSet.id}"/>
          <c:param name="action" value="copy"/>
        </c:url>
        <a href="${contentCopyUrl}" class="icon propertyCopy" onclick="return loadRightPanel(this.href);"
           title="${tooltipCopy}">copy</a>
      </admin-ui:column>
      <admin-ui:column type="icon">
        <%-- Icon, used like link to popup to delete content set --%>
        <fmt:message key="project_index.delete_content.tooltip" var="deleteContentSetTitle"/>
        <c:url value="/searchadmin/content_set_delete_popup.jsp" var="popUrl">
          <c:param name="projectId" value="${project.id}"/>
          <c:param name="contentSetId" value="${contentSet.id}"/>
        </c:url>
        <a class="icon propertyDelete" title="${deleteContentSetTitle}" href="${popUrl}" 
           onclick="return showPopUp(this.href);">del</a>
      </admin-ui:column>
    </c:if>

    <c:if test="${moveAllowed}">
      <admin-ui:footer>
        <fmt:message key="project_index.table_move_to"/>
        <select id="moveSelector${partition.id}" class="small unchanged">
          <%--c:if test="${isProduction}">
            <option value="new" selected="true">
              <fmt:message key="project_index.content.create.new"/>
            </option>
          </c:if--%>
          <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.logicalPartitionComparator"
                        var="comparator"/>
          <admin-ui:sort var="sortedPartitions" items="${project.index.logicalPartitions}" comparator="${comparator}"
                         sortMode="default"/>
          <c:forEach items="${sortedPartitions}" var="currentPartition" varStatus="partitionNumber" >
            <c:if test="${currentPartition.id != partition.id}">
              <option value="${currentPartition.id}">
                <c:out value="${currentPartition.name}" />
              </option>
            </c:if>
          </c:forEach>
        </select>
        <fmt:message key="project_index.buttons.move" var="moveButton"/>
        <fmt:message key="project_index.buttons.move.tooltip" var="moveButtonToolTip"/>
        <d:input bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.move" type="button"
                 iclass="tableButton" value="${moveButton}" id="move_${partition.id}"
                 title="${moveButtonToolTip}" onclick="moveContent('${partition.id}');"/>
      </admin-ui:footer>
    </c:if>
  </admin-ui:table>
  <c:if test="${moveAllowed}">
    <script type="text/javascript">checkAllMove_setMain('${partition.id}', 'contentSetId');</script>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_partition.jsp#2 $$Change: 651448 $--%>
