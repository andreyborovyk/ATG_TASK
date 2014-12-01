<%--
JSP, included to project.jsp, showing information about project index. Also used to be included into
project_manage_index.jsp.

We have request parameter, called "status", which shows us project stage:
status = 1: Project isn't created
status = 2: Project created, but has no content sets
status = 3: Project created, has at least one content set, but has not been synchronized yet.
status = 4: Project created, has at least one content set, and has been synchronized.
Also "status" parameter can be null, it can be used to show information for project_manage_index.jsp page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%--Status is always empty for content page and filled for project page.
  This fact is used here to check from what page(project/content) we've got here. --%>
  <d:getvalueof param="status" var="status"/>
  <d:getvalueof param="projectId" var="projectId"/>

  <%-- url definitions --%>
  <c:url value="/searchadmin/project_manage_index.jsp" var="successURL">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <%-- Retrieving search project by id into page scope --%>
  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
  <c:set var="noContent" value="${project.noContent}"/>
  <%-- retrieve partitions count --%>
  <c:if test="${!empty project and !empty project.index}">
    <c:set var="partitionsCount" value="${fn:length(project.index.logicalPartitions)}"/>
  </c:if>
  <p>
    <c:if test="${noContent}">
      <fmt:message key="project_index.title.no_content"/>
    </c:if>
    <c:if test="${!noContent}">
      <fmt:message key="project_index.title"/>
    </c:if>
  </p>
  
  <c:if test="${empty status or not noContent}">
    <c:if test="${empty status}">
      <admin-beans:getProjectCustomizations varPreIndex="preIndex" varPostIndex="postIndex" projectId="${projectId}"
          varHasLanguageErrors="hasLanguageErrors" showDefaultTPO="${true}" checkLanguages="${true}" />
    </c:if>

    <%--
      Loop through all index partitions, showing information about it.
      Here we include page, containing all needed information about index partition.
    --%>
    <c:url var="actionUrl" value="/searchadmin/project_index.jsp"/>
    <d:form formid="project_index_partition" action="${actionUrl}" method="POST">
      <%-- Url, used to return back to manage project index page--%>
      <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.successURL"
               value="${successURL}" name="successURL"/>
      <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.errorURL"
               value="${successURL}" name="errorURL"/>
      <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.projectId"
               value="${projectId}" name="projectId"/>
      <%-- Move functionality --%>
      <d:input id="fromPartitionId" type="hidden"
               bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.fromPartitionId"/>
      <d:input id="toPartitionId" type="hidden"
               bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.toPartitionId"/>
      <d:input bean="/atg/searchadmin/adminui/formhandlers/MoveContentSetFormHandler.move" type="submit" iclass="formsubmitter"
               style="display:none" id="move" name="move" />
      <fmt:message key="project_index.buttons.new_content_set" var="addContentSetButton"/>
      <fmt:message key="project_index.buttons.new_content_set.tooltip" var="addContentSetButtonTooltip"/>

      <c:set var="parentProject" value="${project.parentProject}" /><%-- parentProject is a calculated property so storing it to a variable --%>
      <c:if test="${empty status}">
        <%--span class="seperator"></span--%>
        <c:if test="${not noContent}">
          <h3 class="overview">
            <fmt:message key="project.content.bread.crumbs" />
          </h3>
        </c:if>
        <p>
          <c:if test="${empty parentProject and not noContent}">
            <%-- Create new content set button --%>
            <c:url value="/searchadmin/partition_rename_popup.jsp" var="addContentSetUrl">
              <c:param name="projectId" value="${project.id}"/>
            </c:url>
            <input type="button" value="${addContentSetButton}" title="${addContentSetButtonTooltip}"
                   onclick="return showPopUp('${addContentSetUrl}');" />
          </c:if>
          <c:if test="${not empty parentProject}">
            <fmt:message key="project_index.non_production.linked_to_project">
              <fmt:param><c:out value="${parentProject.name}" /></fmt:param>
            </fmt:message>
            <br/>
            <c:url var="parentContentUrl" value="/searchadmin/project_manage_index.jsp">
              <c:param name="projectId" value="${parentProject.id}" />
            </c:url>
            <fmt:message key="project_index.non_production.note">
              <fmt:param><a href="${parentContentUrl}" onclick="return loadRightPanel(this.href);"
                ><fmt:message key="project_index.non_production.note.link" /></a></fmt:param>
            </fmt:message>
          </c:if>
        </p>
      </c:if>
      <%-- Display sorted content sets --%>
      <admin-beans:getDuplicatedContentLabels projectId="${projectId}" varDuplContentLabelsMap="duplContentLabelsMap"/>
          
      <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.logicalPartitionComparator" var="comparator"/>
      <admin-ui:sort var="sortedPartitions" items="${project.index.logicalPartitions}" comparator="${comparator}"
                     sortMode="default"/>
      <c:forEach items="${sortedPartitions}" var="partition" varStatus="currentPartitionNumber">
        <c:choose>
          <c:when test="${noContent}">
            <table class="data" cellspacing="0" cellpadding="0">
              <thead><tr><td class="dataHeader">
                <d:include page="project_index_partition_header.jsp">
                  <d:param name="searchProject" value="${project}"/>
                  <d:param name="partition" value="${partition}"/>
                  <d:param name="status" value="${status}"/>
                  <d:param name="isProduction" value="${empty parentProject}"/>
                  <d:param name="duplicatedProjects" value="${duplContentLabelsMap[partition.contentLabel]}"/>
                </d:include>
              </td></tr></thead>
            </table>
          </c:when>
          <c:otherwise>
            <d:include page="project_index_partition.jsp">
              <d:param name="searchProject" value="${project}"/>
              <d:param name="partition" value="${partition}"/>
              <d:param name="status" value="${status}"/>
              <d:param name="isProduction" value="${empty parentProject}"/>
              <d:param name="duplicatedProjects" value="${duplContentLabelsMap[partition.contentLabel]}"/>
            </d:include>
          </c:otherwise>
        </c:choose>
      </c:forEach>
      <c:if test="${empty status and empty parentProject and not noContent}">
        <%-- Create new content set button --%>
        <input type="button" value="${addContentSetButton}" title="${addContentSetButtonTooltip}"
               onclick="return showPopUp('${addContentSetUrl}');" />
      </c:if>
      <c:if test="${not empty status}">
        <c:url var="refreshUrl" value="/searchadmin/project.jsp">
          <c:param name="projectId" value="${projectId}"/>
        </c:url>
      </c:if>
      <c:if test="${empty status}">
        <c:url var="refreshUrl" value="/searchadmin/project_manage_index.jsp">
          <c:param name="projectId" value="${projectId}"/>
        </c:url>
      </c:if>
      <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
      <d:input type="hidden" bean="SortFormHandler.successURL" value="${refreshUrl}"/>
      <d:input type="submit" bean="SortFormHandler.sort" iclass="formsubmitter" style="display:none" id="sortContentSetInput"/>

      <c:if test="${empty status}">
        <script type="text/javascript">
          function destroyMenu(){
            if (dijit.byId('addPreIndexPopup')){
              dijit.byId('addPreIndexPopup').destroy();
            }
            if (dijit.byId('addPostIndexPopup')){
              dijit.byId('addPostIndexPopup').destroy();
            }
          }
          destroyMenu();
        </script>

        <span class="seperator"></span>

        <h3 class="overview">
          <fmt:message key="project_cust.pre_index"/>
          <span class="ea"><tags:ea key="embedded_assistant.project_index.pre_index" /></span>
        </h3>

        <d:include page="project_index_customizations.jsp">
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="custItems" value="${preIndex}"/>
          <d:param name="preIndex" value="${true}"/>
          <d:param name="popupId" value="addPreIndexPopup"/>
        </d:include>

        <span class="seperator"></span>

        <h3 class="overview">
          <fmt:message key="project_cust.post_index"/>
        </h3>

        <d:include page="project_index_customizations.jsp">
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="custItems" value="${postIndex}"/>
          <d:param name="preIndex" value="${false}"/>
          <d:param name="popupId" value="addPostIndexPopup"/>
        </d:include>
      </c:if>
    </d:form>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index.jsp#2 $$Change: 651448 $--%>
