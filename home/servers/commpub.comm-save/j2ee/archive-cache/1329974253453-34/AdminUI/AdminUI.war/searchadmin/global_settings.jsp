<%--
  JSP, used to show/edit content labels and target types.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_settings.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/table_modification.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/macros.js"></script>

    <d:importbean var="globalSettingsFormHandler" bean="/atg/searchadmin/adminui/formhandlers/GlobalSettingsFormHandler"/>
    <d:importbean var="projService" bean="/atg/searchadmin/repository/service/SearchProjectService"/>

    <c:url value="/searchadmin/global_settings.jsp" var="backURL" />

    <admin-ui:initializeFormHandler handler="${globalSettingsFormHandler}"/>

    <d:form action="global_settings.jsp" method="post">
      <div id="paneContent">
        <h3><fmt:message key="global_settings.content.labels" /></h3>
        <p><fmt:message key="global_settings.labels.intro.text"/></p>
        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                        var="contentLabel" items="${globalSettingsFormHandler.labelBeans}"
                        emptyRow="true" tableId="labelTable">
          <admin-ui:column title="global_settings.content.label" type="sortable" name="content_label" width="45%" 
                           messageEA="embedded_assistant.global_settings.content_label">
            <c:if test="${empty contentLabel.label}">
              <d:input type="text" style="width:95%" iclass="textField" name="contentLabel" bean="GlobalSettingsFormHandler.contentLabel"
                       maxlength="40" value="${contentLabel.label}" onkeyup="addEmptyField(this);" onchange="addEmptyField(this);" />
            </c:if>
            <c:if test="${not empty contentLabel.label}">
              <d:input type="hidden" name="contentLabel" bean="GlobalSettingsFormHandler.contentLabel" value="${contentLabel.label}" />
              <c:out value="${contentLabel.label}" />
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="global_settings.associated.content.sets" type="sortable" name="content_sets">
            <tags:join items="${contentLabel.contentSets}" var="contentSet" delimiter="; ">
              <c:out value="${contentSet}" />
            </tags:join>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message var="tooltipDelete" key="global_settings.buttons.delete.tooltip"/>
            <c:set var="isNew" value="${empty contentLabel.label}" />
            <c:set var="deletable" value="${empty contentLabel.contentSets}" />
            <a class="icon propertyDelete ${deletable ? '' : 'disabled inactive'}" href="#" title="${tooltipDelete}"
               onclick="return onDelete(this, ${deletable}, ${isNew});">del</a>
          </admin-ui:column>
        </admin-ui:table>

        <h3><fmt:message key="global_settings.target.types" /></h3>
        <p><fmt:message key="global_settings.target.intro.text" /></p>
        <fmt:message key="global_settings.default.target.type" var="defaultTargetType" />

        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                        var="targetType" items="${globalSettingsFormHandler.targetBeans}"
                        emptyRow="true" tableId="targetTable">
          <admin-ui:column title="global_settings.target.type" type="sortable" name="content_label" width="45%"
                           messageEA="embedded_assistant.global_settings.target_type">
            <c:if test="${empty targetType.target}">
              <d:input type="text" style="width:95%" iclass="textField" name="targetType" bean="GlobalSettingsFormHandler.targetType"
                       value="${targetType.target}" onkeyup="addEmptyField(this);" onchange="addEmptyField(this);" />
            </c:if>
            <c:if test="${not empty targetType.target}">
              <d:input type="hidden" name="targetType" bean="GlobalSettingsFormHandler.targetType" value="${targetType.target}" />
              <c:out value="${targetType.target}" />
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="global_settings.target.associated.projects" type="sortable" name="content_sets">
            <tags:join items="${targetType.projects}" var="project" delimiter="; ">
              <c:out value="${project}" />
            </tags:join>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <c:if test="${targetType.target ne projService.defaultTargetType}">
              <fmt:message var="tooltipDelete" key="global_settings.buttons.delete.tooltip"/>
              <c:set var="isNew" value="${empty targetType.target}" />
              <c:set var="deletable" value="${empty targetType.projects}" />
              <a class="icon propertyDelete ${deletable ? '' : 'disabled inactive'}" href="#" title="${tooltipDelete}"
                 onclick="return onDelete(this, ${deletable}, ${isNew});">del</a>
            </c:if>
          </admin-ui:column>
        </admin-ui:table>
        <fmt:message key="global_settings.note.text"/>
      </div>

      <div id="paneFooter">
        <d:input bean="GlobalSettingsFormHandler.successURL" value="${backURL}" type="hidden" name="successURL"/>
        <d:input bean="GlobalSettingsFormHandler.errorURL" value="${backURL}" type="hidden" name="errorURL"/>
        <fmt:message key="global_settings.buttons.save" var="saveButtonTitle"/>
        <fmt:message key="global_settings.buttons.save.tooltip" var="saveButtonTooltip"/>
        <d:input type="submit" bean="GlobalSettingsFormHandler.save"
                 value="${saveButtonTitle}" iclass="formsubmitter" title="${saveTooltipButton}"
                 name="update" onclick="return checkForm();"/>
      </div>
    </d:form>
    
    <c:url value="/searchadmin/global_settings_delete_popup.jsp" var="popUrl" />
    <script type="text/javascript">
      initTable(document.getElementById('labelTable'));
      initTable(document.getElementById('targetTable'));

      function onDelete(element, deletable, immediately) {
        if (deletable && element.className.indexOf("inactive") < 0) {
          var delRow = getParentByChildElement(element, "tr");
          var tableId = getParentByChildElement(delRow, "table").id;
          if (immediately) {
            deleteField(element);
          } else {
            return showPopUp("${popUrl}?index=" + delRow.rowIndex + "&tableId=" + tableId);
          }
        }
        return false;
      }

      function deleteRowByIndexFromPopup(tableId, index) {
        var table = document.getElementById(tableId);
        table.deleteRow(index);
        initTable(table);
      }

      function getNameByIndexFromPopup(tableId, index) {
        var table = document.getElementById(tableId);
        var delRow = table.rows[index];
        var inputs = delRow.getElementsByTagName("input");
        return inputs != null && inputs.length > 0 ? inputs[0].value : "";
      }
    </script>
    <%-- Validation --%>
    <admin-validator:validate beanName="GlobalSettingsFormHandler"/>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_settings.jsp#2 $$Change: 651448 $--%>
