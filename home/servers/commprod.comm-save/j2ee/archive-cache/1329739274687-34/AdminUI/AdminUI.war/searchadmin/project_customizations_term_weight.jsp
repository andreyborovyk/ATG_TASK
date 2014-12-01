<%--
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_term_weight.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId" />
  <script type="text/javascript">
    function doCheck(group_name, checked, current_term) {
      if (!checked) {
        return false;
      } else {
        var group = getElementsByClassName(group_name);
        for (var i = 0; i < group.length; i++) {
          var term = group[i];
          if (term.value != current_term) {
            term.checked = false;
          }
        }
        return false;
      }
    }
    function doToggle(current_img_id, group_name) {
      var current_img = document.getElementById(current_img_id);
      if (current_img.className == 'icon termsClose') {
        current_img.className = 'icon termsOpen';
        var group = getElementsByClassName('tr_' + group_name);
        for (var i = 0; i < group.length; i++) {
          group[i].style.display = "";
        }
      } else {
        current_img.className = 'icon termsClose';
        var group = getElementsByClassName('tr_' + group_name);
        for (var i = 0; i < group.length; i++) {
          group[i].style.display = "none";
        }
      }
      return false;
    }
  </script>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/CustomizationsOtherFormHandler" var="handler" />
  <c:set var="customizationType" value="term_weight" />
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}" />
    <admin-ui:param name="customizationType" value="${customizationType}" />
  </admin-ui:initializeFormHandler>
  <%-- retrieving current customization item --%>
  <d:getvalueof var="currentCustomizationItem"
    bean="/atg/searchadmin/adminui/navigation/CustomizationTypeComponent.customizationTypesMap.${customizationType}" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="project_customizations_term_weight.jsp" method="POST">
      <c:url var="backURL" value="/searchadmin/project_manage_index.jsp">
        <c:param name="projectId" value="${projectId}" />
      </c:url>
      <div id="paneContent">
        <c:url value="${currentCustomizationItem.newItemPage}" var="newItemUrl">
          <c:param name="projectId" value="${projectId}" />
        </c:url>
        <p>
          <input type="button" value="<fmt:message key='project_cust.buttons.new_item'/>"
            title="<fmt:message key='project_cust.buttons.new_item.tooltip'/>"
            onclick="return loadRightPanel('${newItemUrl}');" />
        </p>
        <p>
          <fmt:message key="project_customizations.${customizationType}.text" />
        </p>
        <admin-beans:getCustomizationsByType varItemsByLanguage="customizations" projectId="${projectId}"
          customizationType="${currentCustomizationItem.customizationTypeEnumName}" />
        <table class="data" cellspacing="0" cellpadding="0">
          <tr class="th">
            <th class="rowSelector">
            </th>
            <th>
              <fmt:message key="customization_type.${customizationType}.single" />
            </th>
            <th>
              <fmt:message key="project_customizations.table.source" />
            </th>
            <th>
              <fmt:message key="project_customizations.table.description" />
            </th>
          </tr>
          <c:forEach items="${customizations}" var="customization">
            <tr onclick="doToggle('img_${customization.key}', '${customization.key}');" style="cursor: hand;">
              <td colspan="4" bgcolor="#F3F3F3">
                <fmt:message var="lang"
                  key="${customization.key == '' ? 'project_customizations.term_weight.language.unavailable' : customization.key}" />
                <fmt:message var="imgTitle" key="project_customizations.term_weight.checkbox.tooltip" />
                <d:a title="${imgTitle}" id="img_${customization.key}" iclass="icon termsOpen" href="#" />
                ${lang}
              </td>
            </tr>
            <c:forEach items="${customization.value}" var="term">
              <tr class="tr_${customization.key}">
                <td class="noneborderrl">
                  <c:choose>
                    <c:when test="${customization.key != ''}">
                      <d:input bean="CustomizationsOtherFormHandler.selectedItemIds" name="term_weights" type="checkbox"
                        value="${term.id}" iclass="${customization.key}" onclick="doCheck('${customization.key}', this.checked, '${term.id}');" />
                    </c:when>
                    <c:otherwise>
                      <d:input disabled="true" checked="false" bean="CustomizationsOtherFormHandler.selectedItemIds" name="term_weights" type="checkbox"
                        value="${term.id}" iclass="${customization.key}" />
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="noneborderrl">
                  <img src="${pageContext.request.contextPath}${term.type.icon}"
                    alt="<fmt:message key='customization_type.${term.type.id}.single' />" />
                  <c:set var="name">
                    <c:out value="${term.nameValue}" />
                  </c:set>
                  <c:if test="${not empty term.nameKey}">
                    <fmt:message var="name" key="${term.nameKey}" />
                  </c:if>
                  <c:if test="${not term.available}">
                    <c:set var="name">
                      <span style="color: red">
                        <fmt:message key="project_customizations.table.unavailable_item">
                          <fmt:param value="${name}" />
                        </fmt:message> 
                      </span>
                    </c:set>
                  </c:if>
                  <label for="${term.id}_input">
                    ${name}
                  </label>
                </td>
                <td class="noneborderrl">
                  <c:if test="${not empty term.locationKey}">
                    <fmt:message key="${term.locationKey}" var="location">
                      <c:if test="${not empty term.locationParams}">
                        <c:forEach var="locationParam" items="${term.locationParams}">
                          <fmt:param value="${locationParam}" />
                        </c:forEach>
                      </c:if>
                    </fmt:message>
                  </c:if>
                  <c:if test="${not term.available}">
                    <c:set var="location">
                      <span style="color: red">${location}</span>
                    </c:set>
                  </c:if>
                  ${location}
                </td>
                <td class="noneborderrl">
                  <c:choose>
                    <c:when test="${not term.available}">
                      <span style="color: red">
                        <fmt:message key="project_customizations.table.unavailable_item.description" />
                      </span>
                    </c:when>
                    <c:otherwise>
                      <c:out value="${term.description}" />
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
          </c:forEach>
          <c:if test="${empty customizations}">
            <tr>
              <td colspan="4">
                <fmt:message key="error_message.empty_table" />
              </td>
            </tr>
          </c:if>
        </table>
      </div>
      <div id="paneFooter">
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.projectId" name="projectId" />
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.customizationItemType" name="customizationItemType" />
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.successURL" value="${backURL}" name="successURL" />
        <d:input type="hidden" bean="CustomizationsOtherFormHandler.needInitialization" value="false"
          name="needInitialization" />
          
        <fmt:message key="project_footer.buttons.update" var="updateButtonTitle" />
        <fmt:message key="project_footer.buttons.update.tooltip" var="updateButtonToolTip" />
        <d:input type="submit" bean="CustomizationsOtherFormHandler.update" iclass="formsubmitter" 
          value="${updateButtonTitle}" title="${updateButtonToolTip}" />
        <fmt:message key="project_footer.buttons.cancel" var="cancelButtonTitle" />
        <fmt:message key="project_footer.buttons.cancel.tooltip" var="cancelButtonToolTip" />
        <d:input type="submit" bean="CustomizationsOtherFormHandler.cancel" iclass="formsubmitter" 
          value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_term_weight.jsp#2 $$Change: 651448 $--%>