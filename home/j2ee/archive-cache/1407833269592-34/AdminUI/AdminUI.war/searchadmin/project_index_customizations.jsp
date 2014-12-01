<%--
Outputs table which contains customization data assigned to the project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_customizations.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="custItems" var="custItems"/>
  <d:getvalueof param="preIndex" var="preIndex"/>
  <d:getvalueof param="popupId" var="popupId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>

  <%-- url definitions --%>
  <c:url value="/searchadmin/project_manage_index.jsp" var="successURL">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <fmt:message var="customTypeLanguage" key="project_index_customizations.template.language" />
  <fmt:message var="customTypeTPO" key="project_index_customizations.template.project.tpo" /> 

  <button dojoType="dijit.form.DropDownButton"
      templateString="<div class='atgInline' dojoAttachEvent='onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey'><button class='atgDropDownButton' xonclick='return false' type='button' dojoAttachPoint='focusNode,titleNode' waiRole='button' waiState='haspopup-true,labelledby-${id}_label'><span dojoAttachPoint='containerNode,popupStateNode' id='${'${'}id}_label'>${'${'}label}</span><span class='dijitA11yDownArrow'>&#9660;</span></button></div>">
    <span>
      <fmt:message key='project_cust.buttons.add_item'><fmt:param value="" /></fmt:message>
    </span>
    <div dojoType="dijit.Menu" id="${popupId}">
      <d:getvalueof var="types" bean="/atg/searchadmin/adminui/navigation/CustomizationTypeComponent"/>
      <c:forEach items="${types.enumerateCustomizationTypes}" var="typeNode">
        <c:if test="${typeNode.preIndex == preIndex and not empty typeNode.selectPage}">
          <c:url var="itemLink" value="${typeNode.selectPage}">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <div dojoType="dijit.MenuItem">
            <span onClick="destroyMenu();loadRightPanel('${itemLink}')">
              <fmt:message key='customization_type.${typeNode.id}'/>
            </span>
          </div>
        </c:if>
      </c:forEach>
    </div>
  </button>

  <d:input type="hidden" bean="SortFormHandler.successURL" value="${successURL}" name="successURL"/>
  <d:input id="hiddenCustValue${preIndex}" type="hidden" bean="SortFormHandler.sortTables.${preIndex}" />
  <d:input type="submit" iclass="formsubmitter" bean="SortFormHandler.sort" style="display:none" id="sortCustomizationItemsInput"/>
  <d:getvalueof bean="SortFormHandler.sortTables.${preIndex}" var="sortValue"/>
  <c:if test="${empty sortValue}">
    <c:set var="sortValue" value="name asc"/>
  </c:if>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.customizationItemComparator" var="comparator"/>
  <c:set target="${comparator}" property="locale" value="${pageContext.request.locale}" />
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/MessageResources" var="messageResources"/>
  <c:set target="${comparator}" property="messageResources" value="${messageResources}" />
  <admin-ui:sort var="sortedItems" items="${custItems}" comparator="${comparator}" sortMode="${sortValue}" />

  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
        var="currentCustomization" items="${sortedItems}"
        sort="${sortValue}" onSort="customizationItemsOnSort" tableId="${preIndex}">
    <admin-ui:column title="project_customizations.table.name" type="sortable" name="name" >
      <img src="${pageContext.request.contextPath}${currentCustomization.type.icon}"
           alt="<fmt:message key='customization_type.${currentCustomization.type.id}.single' />"/>
      <%-- Calculating customization item name --%>
      <c:choose>
        <c:when test="${not empty currentCustomization.nameKey}">
          <fmt:message var="name" key="${currentCustomization.nameKey}"/>
        </c:when>
        <c:otherwise>
          <c:set var="name"><c:out value="${currentCustomization.nameValue}"/></c:set>
        </c:otherwise>
      </c:choose>
      <%-- Adding red span around if it's not available --%>
      <c:if test="${not currentCustomization.available}">
        <c:set var="name"><span style="color:red"><fmt:message key="project_customizations.table.unavailable_item">
          <fmt:param value="${name}"/>
        </fmt:message></span></c:set>
      </c:if>
      <c:set var="namePopup" value="${name}" />
      <fmt:message key="project_index.item.name.tooltip" var="itemNameTooltip" />
      <%-- Displaying item name --%>
      <c:choose>
        <c:when test="${not empty currentCustomization.nameUrl}">
          <c:url value="${currentCustomization.nameUrl}" var="itemEditUrl"/>
          <a href="${itemEditUrl}" onclick="return loadRightPanel(this.href);" title="${itemNameTooltip}">${name}</a>
        </c:when>
        <c:when test="${currentCustomization.type.customizationTypeEnum eq customTypeLanguage}">
          <c:url var="itemUrl" value="project_customizations_language.jsp">
            <c:param name="projectId" value="${projectId}" />
          </c:url>
          <a href="${itemUrl}" onclick="return loadRightPanel(this.href);" title="${itemNameTooltip}">${name}</a>
        </c:when>
        <c:otherwise>
          ${name}
        </c:otherwise>
      </c:choose>
    </admin-ui:column>
    <admin-ui:column title="project_customizations.table.language" type="static" name="name" >
      <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentCustomization.language}"/>
      <c:if test="${currentCustomization.languageError}">
        <c:if test="${currentCustomization.type.customizationTypeEnum eq customTypeTPO}">
          <fmt:message key="project_cust.missing_language.alert" var="error_title">
            <fmt:param value="[${localizedLanguage}]"/>
          </fmt:message>
        </c:if>
        <c:if test="${currentCustomization.type.customizationTypeEnum ne customTypeTPO}">
          <fmt:message key="project_cust.missing_customization_language" var="error_title">
          <fmt:param value="${currentCustomization.type.customizationTypeEnum}"/>
          <fmt:param value="[${localizedLanguage}]"/>
        </fmt:message>
        </c:if>
        <span class="error" title="${error_title}"/>
      </c:if>
      <c:out value="${localizedLanguage}"/> 
    </admin-ui:column>
    <admin-ui:column title="project_customizations.table.source" type="sortable" name="location">
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
    <admin-ui:column type="icon">
      <c:if test="${not empty currentCustomization.id}">
        <fmt:message key="project_customizations.table.remove.title" var="removeTitle"/>
        <c:url value="/searchadmin/project_index_customization_remove_popup.jsp" var="popUrl">
          <c:param name="projectId" value="${projectId}"/>
          <c:param name="customItemId" value="${currentCustomization.id}"/>
          <c:param name="customItemName" value="${namePopup}"/>
          <c:param name="type" value="${currentCustomization.type.id}"/>
        </c:url>
        <a class="icon propertyRemove" title="${removeTitle}" href="${popUrl}"
           onclick="return showPopUp(this.href);">rem</a>
      </c:if>
    </admin-ui:column>
  </admin-ui:table>

  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_customizations.jsp#2 $$Change: 651448 $--%>
