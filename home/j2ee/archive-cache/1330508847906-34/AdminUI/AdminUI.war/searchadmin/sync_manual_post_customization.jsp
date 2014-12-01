<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler" var="handler"/>
  <c:set var="projectId" value="${handler.projectId}"/>
  <c:set value="${synchronizationManualFormHandler.postCustomizationsList}" var="postCustomizationsList"/>
  <admin-beans:getProjectCustomizations varPreIndex="preIndex" varPostIndex="postIndex" projectId="${projectId}"/>
  <c:set var="headerContentValue">
    <input style="margin-left:2px;" type="checkbox" id="customizationsAll" class="selectAll" onclick="setChildCheckboxesState('postCustomizationTable', 'postCustomizationsList', this.checked);"/>
  </c:set>
  <admin-ui:table renderer="/templates/table_data.jsp"
                  modelVar="table"
                  tableId="postCustomizationTable"
                  var="currentCustomization"
                  items="${postIndex}">
    <admin-ui:column type="checkbox" headerContent="${headerContentValue}">
      <input type="checkbox" value="${currentCustomization.type.id}_${currentCustomization.id}" name="postCustomizationsList" 
             onclick="document.getElementById('customizationsAll').checked = getChildCheckboxesState('postCustomizationTable', 'postCustomizationsList');"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="synchronization_manual.table.header.data.source.name">
      <img src="${pageContext.request.contextPath}${currentCustomization.type.icon}"
           alt="<fmt:message key='customization_type.${currentCustomization.type.id}.single' />"/>
      <c:set var="name"><c:out value="${currentCustomization.nameValue}"/></c:set>
      <c:if test="${not empty currentCustomization.nameKey}">
        <fmt:message var="name" key="${currentCustomization.nameKey}"/>
      </c:if>
      <c:if test="${not currentCustomization.available}">
        <c:set var="name"><span style="color:red"><fmt:message key="project_customizations.table.unavailable_item">
          <fmt:param value="${name}"/>
        </fmt:message></span></c:set>
      </c:if>
      ${name}
    </admin-ui:column>
    <admin-ui:column type="static" title="synchronization_manual.table.header.data.source.location">
      <c:if test="${not empty currentCustomization.locationKey}">
        <fmt:message key="${currentCustomization.locationKey}">
          <c:if test="${not empty currentCustomization.locationParams}">
            <c:forEach var="locationParam" items="${currentCustomization.locationParams}">
              <fmt:param value="${locationParam}"/>
            </c:forEach>
          </c:if>
        </fmt:message>
      </c:if>
    </admin-ui:column>
  </admin-ui:table>    
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_manual_post_customization.jsp#2 $$Change: 651448 $--%>
