<%--
  JSP included to project_custpmizations_selection.jsp, showing all Language existing in system.
  Also used for selections language, for including to current project.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_language.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/CustomizationsLanguageFormHandler" var="handler"/>

  <c:set var="customizationType" value="language"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="customizationType" value="${customizationType}"/>
  </admin-ui:initializeFormHandler>

  <%-- retrieving current customization item --%>
  <d:getvalueof var="currentCustomizationItem"
                bean="/atg/searchadmin/adminui/navigation/CustomizationTypeComponent.customizationTypesMap.${customizationType}"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="project_customizations_language.jsp" method="POST">
      <c:url var="backURL" value="/searchadmin/project_manage_index.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>

      <div id="paneContent">
        <p>
          <fmt:message key="project_customizations.${customizationType}.text"/>
        </p>

        <script type="text/javascript">
          function viewLink(pId) {
            var pEl = (typeof pId == "string") ? document.getElementById(pId + "_input") : pId;
            pId = (typeof pId == "string") ? pId : pEl.id.substr(0, pEl.id.length - "_input".length);
            document.getElementById("link_" + pId).style.display = "none";
            document.getElementById("dlink_" + pId).style.display = "none";
            if (pEl.checked) {
              var v = getLanguageDictionaries(pId);
              if (v == "") {
                document.getElementById("link_" + pId).style.display = "inline";
              } else {
                var l = document.getElementById("dlink_" + pId);
                l.innerHTML = v;
                l.style.display = "inline";
              }
            }
          }
          function getLanguageDictionaries(pLan) {
            return document.getElementById('id_' + pLan).value;
          }
          function setLanguageDictionaries(pLan, pDicts) {
            document.getElementById('id_' + pLan).value = pDicts;
            viewLink(pLan);
          }
        </script>

        <admin-beans:getCustomizationsByType varItems="customizations" projectId="${projectId}"
                                             customizationType="${currentCustomizationItem.customizationTypeEnumName}"/>

        <c:set var="itemsHeaderContentValue">
          <input style="margin-left:2px;" type="checkbox" id="itemsAll" class="selectAll"
                 onclick="setChildCheckboxesState('itemsTable', 'selectedItemIds', this.checked, viewLink);"/>
        </c:set>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="currentCustomization" items="${customizations}"
                        tableId="itemsTable">
          <admin-ui:column type="checkbox" headerContent="${itemsHeaderContentValue}">
            <d:input type="checkbox" bean="CustomizationsLanguageFormHandler.selectedItemIds"
                     name="selectedItemIds" value="${currentCustomization.id}" id="${currentCustomization.nameValue}_input"
                     onclick="viewLink('${currentCustomization.nameValue}');
                              document.getElementById('itemsAll').checked =
                                getChildCheckboxesState('itemsTable', 'selectedItemIds');"/>
          </admin-ui:column>
          <admin-ui:column title="project_customizations.table.language" type="static">
            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentCustomization.nameValue}"/>
            <label for="${currentCustomization.nameValue}_input"><c:out value="${localizedLanguage}"/></label>
          </admin-ui:column>
          <admin-ui:column title="project_customizations.table.dictionary" type="static">
            <c:url value="/searchadmin/project_customizations_adapter_popup.jsp" var="popUrl">
              <c:param name="language" value="${currentCustomization.nameValue}"/>
              <c:param name="projectId" value="${projectId}"/>
            </c:url>

            <fmt:message var="linkSelectAdaptorTooltip"  key="project_customizations.table.adaptors.title.tooltip"/>

            <a href="${popUrl}" onclick="return showPopUp(this.href);" style="display:none" id="link_${currentCustomization.nameValue}" title="${linkSelectAdaptorTooltip}">
              <fmt:message key="project_customizations.table.adaptors.title"/>
            </a>
            <a href="${popUrl}" onclick="return showPopUp(this.href);" style="display:none" id="dlink_${currentCustomization.nameValue}" title="${linkSelectAdaptorTooltip}">
            </a>

            <d:input type="hidden" id="id_${currentCustomization.nameValue}"
                     bean="CustomizationsLanguageFormHandler.dictionaries.${currentCustomization.nameValue}"/>
            <script type="text/javascript">viewLink('${currentCustomization.nameValue}');</script>
          </admin-ui:column>
        </admin-ui:table>
        <script type="text/javascript">
          document.getElementById('itemsAll').checked = getChildCheckboxesState('itemsTable', 'selectedItemIds');
        </script>
      </div>

      <div id="paneFooter">
        <d:input type="hidden" bean="CustomizationsLanguageFormHandler.projectId" name="projectId"/>
        <d:input type="hidden" bean="CustomizationsLanguageFormHandler.customizationItemType" name="customizationItemType"/>
        <d:input type="hidden" bean="CustomizationsLanguageFormHandler.successURL" value="${backURL}"/>
        <d:input type="hidden" bean="CustomizationsLanguageFormHandler.cancelURL" value="${backURL}"/>
        <d:input type="hidden" bean="CustomizationsLanguageFormHandler.needInitialization" value="false"
                 name="needInitialization"/>
        <fmt:message key="project_footer.buttons.update" var="updateButtonTitle"/>
        <fmt:message key="project_footer.buttons.update.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsLanguageFormHandler.update" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}"/>
        <fmt:message key="project_footer.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_footer.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="CustomizationsLanguageFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_language.jsp#1 $$Change: 651360 $--%>
