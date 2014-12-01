<%--
  JSP included to project_custpmizations_selection.jsp, showing all Customizations Text Proceccing Options existing
  in system.  Also used for selections Customizations Text Proceccing Options, for including to current project.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_tpo.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/CustomizationsTPOFormHandler" var="handler"/>

  <c:set var="customizationType" value="project_tpo"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="customizationType" value="${customizationType}"/>
  </admin-ui:initializeFormHandler>

  <%-- retrieving current customization item --%>
  <d:getvalueof var="currentCustomizationItem"
                bean="/atg/searchadmin/adminui/navigation/CustomizationTypeComponent.customizationTypesMap.${customizationType}"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="project_customizations_tpo.jsp" method="POST">
      <c:url var="backURL" value="/searchadmin/project_manage_index.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
      
      <div id="paneContent">
        <c:url value="${currentCustomizationItem.newItemPage}" var="newItemUrl"/>
          
        <p>
          <input type="button" onclick="return loadRightPanel('${newItemUrl}');"
                 value="<fmt:message key='project_cust.buttons.new_item'/>"
                 title="<fmt:message key='project_cust.buttons.new_item.tooltip'/>" />
        </p>
        <p>
          <fmt:message key="project_customizations.${customizationType}.text"/>
        </p>

        <fmt:message key="content.create.indexing_options.tpo.default" var="defaultTPOName"/>
        <admin-beans:getCustomizationsByType varItems="customizations" projectId="${projectId}" defaultItemName="${defaultTPOName}"
                                             customizationType="${currentCustomizationItem.customizationTypeEnumName}"/>

        <admin-ui:table renderer="/templates/table_simple.jsp"
                        modelVar="table"
                        var="currentCustomization"
                        items="${customizations}">
          <admin-ui:column type="checkbox">
            <d:input bean="CustomizationsTPOFormHandler.selectedItemId" value="${currentCustomization.id}"
                     name="selectedItemId" type="radio" id="${currentCustomization.id}_input"/>
          </admin-ui:column>
          <admin-ui:column title="customization_type.${customizationType}.single" type="static">
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
            <label for="${currentCustomization.id}_input">${name}</label>
          </admin-ui:column>
          <admin-ui:column title="project_customizations.table.source" type="static">
            <c:if test="${not empty currentCustomization.locationKey}">
              <fmt:message key="${currentCustomization.locationKey}" var="location">
                <c:if test="${not empty currentCustomization.locationParams}">
                  <c:forEach var="locationParam" items="${currentCustomization.locationParams}">
                    <fmt:param value="${locationParam}"/>
                  </c:forEach>
                </c:if>
              </fmt:message>
            </c:if>
            <c:if test="${not currentCustomization.available}">
              <c:set var="location"><span style="color:red">${location}</span></c:set>
            </c:if>
            ${location}
          </admin-ui:column>
          <admin-ui:column title="project_customizations.table.description" type="static">
            <c:choose>
              <c:when test="${not currentCustomization.available}">
                <span style="color:red"><fmt:message key="project_customizations.table.unavailable_item.description"/></span>
              </c:when>
              <c:otherwise>
                <c:out value="${currentCustomization.description}"/>
              </c:otherwise>
            </c:choose>
          </admin-ui:column>
        </admin-ui:table>
      </div>

      <div id="paneFooter">
        <d:input type="hidden" bean="CustomizationsTPOFormHandler.projectId" name="projectId"/>
        <d:input type="hidden" bean="CustomizationsTPOFormHandler.customizationItemType" name="customizationItemType"/>
        <d:input type="hidden" bean="CustomizationsTPOFormHandler.successURL" value="${backURL}" name="successURL"/>
        <d:input type="hidden" bean="CustomizationsTPOFormHandler.needInitialization" value="false"
                 name="needInitialization"/>
          
        <fmt:message key="project_footer.buttons.update" var="updateButtonTitle"/>
        <fmt:message key="project_footer.buttons.update.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsTPOFormHandler.update" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}"/>
        <fmt:message key="project_footer.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_footer.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsTPOFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_tpo.jsp#2 $$Change: 651448 $--%>