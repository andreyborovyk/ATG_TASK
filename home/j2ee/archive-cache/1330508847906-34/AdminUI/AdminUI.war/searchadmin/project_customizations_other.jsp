<%--
  JSP included to project_custpmizations_selection.jsp, showing all Topic Taxomnomies existing in system.
  Also used for selections Topic Taxomnomies, for including to current project.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_other.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>
  <%-- Customization items type --%>
  <d:getvalueof param="customizationType" var="customizationType"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/CustomizationsOtherFormHandler" var="handler"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="customizationType" value="${customizationType}"/>
  </admin-ui:initializeFormHandler>

  <%-- retrieving current customization item --%>
  <d:getvalueof var="currentCustomizationItem"
                bean="/atg/searchadmin/adminui/navigation/CustomizationTypeComponent.customizationTypesMap.${customizationType}"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="project_customizations_other.jsp" method="POST">
      <c:url var="backURL" value="/searchadmin/project_manage_index.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
        
      <div id="paneContent">
        <c:if test="${currentCustomizationItem.localType}">
          <c:url value="${currentCustomizationItem.newItemPage}" var="newItemUrl">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <p>
            <input type="button" onclick="return loadRightPanel('${newItemUrl}');"
                   value="<fmt:message key='project_cust.buttons.new_item'/>"
                   title="<fmt:message key='project_cust.buttons.new_item.tooltip'/>" />
          </p>
        </c:if>
        <p>
          <fmt:message key="project_customizations.${customizationType}.text"/>
        </p>

        <admin-beans:getCustomizationsByType varItems="customizations" varAdapters="adapters" projectId="${projectId}"
                                             customizationType="${currentCustomizationItem.customizationTypeEnumName}"/>

        <c:set var="itemsHeaderContentValue">
          <input style="margin-left:2px;" type="checkbox" id="itemsAll" class="selectAll"
                 onclick="setChildCheckboxesState('itemsTable', 'selectedItemIds', this.checked);"/>
        </c:set>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="currentCustomization" items="${customizations}"
                        tableId="itemsTable">
          <admin-ui:column type="checkbox" headerContent="${itemsHeaderContentValue}">
            <d:input bean="CustomizationsOtherFormHandler.selectedItemIds" value="${currentCustomization.id}"
                     name="selectedItemIds" type="checkbox" id="${currentCustomization.id}_input"
                     onclick="document.getElementById('itemsAll').checked =
                                getChildCheckboxesState('itemsTable', 'selectedItemIds');"/>
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
        <c:if test="${not empty adapters}">
          <br/>
          <c:set var="adaptersHeaderContentValue">
            <input style="margin-left:2px;" type="checkbox" id="adaptersAll" class="selectAll"
                   onclick="setChildCheckboxesState('adaptersTable', 'selectedItemIds', this.checked);"/>
          </c:set>
          <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                          var="currentAdapter" items="${adapters}"
                          tableId="adaptersTable">
            <admin-ui:column type="checkbox" headerContent="${adaptersHeaderContentValue}">
              <d:input bean="CustomizationsOtherFormHandler.selectedItemIds" value="${currentAdapter.id}"
                       name="selectedItemIds" type="checkbox"
                       onclick="document.getElementById('adaptersAll').checked =
                                  getChildCheckboxesState('adaptersTable', 'selectedItemIds');"/>
            </admin-ui:column>
            <admin-ui:column title="customization_type.${customizationType}.adaptor" type="static">
              <img src="${pageContext.request.contextPath}${currentAdapter.type.icon}"
                   alt="<fmt:message key='customization_type.${currentAdapter.type.id}.single' />"/>
              <c:set var="name"><c:out value="${currentAdapter.nameValue}"/></c:set>
              <c:if test="${not empty currentAdapter.nameKey}">
                <fmt:message var="name" key="${currentAdapter.nameKey}"/>
                <%-- Hack to find out if ${currentAdapter.nameKey} was actually found in the
                     bundle. If not it will look like "$$$key$$$". If it wasn't found
                     then just use the key as the name so we can add adapters without
                     modifying the resource bundle, using the key name as their display name.
                  --%>
                <c:if test='${fn:contains(name,"??")}'>
                  <c:set var="name" value="${currentAdapter.nameKey}"/>
                </c:if>
              </c:if>
              <c:if test="${not currentAdapter.available}">
                <c:set var="name"><span style="color:red"><fmt:message key="project_customizations.table.unavailable_item">
                  <fmt:param value="${name}"/>
                </fmt:message></span></c:set>
              </c:if>
              <c:if test="${not empty currentAdapter.nameUrl}">
                <c:url value="${currentAdapter.itemEditUrl}" var="itemEditUrl"/>
                <fmt:message key="project_index.item.name.tooltip" var="itemNameTooltip" />
                <c:set var="name"><a href="${itemEditUrl}" title="${itemNameTooltip}">${name}</a></c:set>
              </c:if>
              ${name}
            </admin-ui:column>
            <admin-ui:column title="project_customizations.table.source" type="static">
              <c:if test="${not empty currentAdapter.locationKey}">
                <fmt:message key="${currentAdapter.locationKey}" var="location">
                  <c:if test="${not empty currentAdapter.locationParams}">
                    <c:forEach var="locationParam" items="${currentAdapter.locationParams}">
                      <fmt:param value="${locationParam}"/>
                    </c:forEach>
                  </c:if>
                </fmt:message>
              </c:if>
              <c:if test="${not currentAdapter.available}">
                <c:set var="location"><span style="color:red">${location}</span></c:set>
              </c:if>
              ${location}
            </admin-ui:column>
          </admin-ui:table>
          <script type="text/javascript">
            document.getElementById('adaptersAll').checked = getChildCheckboxesState('adaptersTable', 'selectedItemIds');
          </script>
        </c:if>
        <script type="text/javascript">
          document.getElementById('itemsAll').checked = getChildCheckboxesState('itemsTable', 'selectedItemIds');
        </script>
      </div>

      <div id="paneFooter">
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.projectId" name="projectId"/>
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.customizationItemType" name="customizationItemType"/>
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.successURL" value="${backURL}" name="successURL"/>
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.needInitialization" value="false"
                 name="needInitialization"/>
        
        <fmt:message key="project_footer.buttons.update" var="updateButtonTitle"/>
        <fmt:message key="project_footer.buttons.update.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsOtherFormHandler.update" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}"/>
        <fmt:message key="project_footer.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_footer.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsOtherFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_other.jsp#2 $$Change: 651448 $--%>