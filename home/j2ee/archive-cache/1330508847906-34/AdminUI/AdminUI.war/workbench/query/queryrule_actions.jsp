<%--
 * TODO: Description here
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_actions.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleFormHandler"/>
  <d:getvalueof bean="QueryRuleFormHandler.actionsSort" var="actionSortValue"/>

  <h3>
    <fmt:message key="queryrule_actions.title.actions"/>
    <span class="ea"><tags:ea key="embedded_assistant.queryrule_actions" /></span>
  </h3>

  <fmt:message key="queryrule_actions.text.li1" var="action1"/>
  <fmt:message key="queryrule_actions.text.li2" var="action2"/>
  <fmt:message key="queryrule_actions.text.li3" var="action3"/>
  <c:if test="${action1 != '' || action2 != '' || action3 != ''}">
    <p>
      <fmt:message key="queryrule_actions.text.title"/>
    </p>

    <p>
      <ul class="simpleBullets">
        <c:if test="${action1 != ''}">
          <li>
            <fmt:message key="queryrule_actions.text.li1"/>
          </li>
        </c:if>
        <c:if test="${action2 != ''}">
          <li>
            <fmt:message key="queryrule_actions.text.li2"/>
          </li>
        </c:if>
        <c:if test="${action3 != ''}">
          <li>
            <fmt:message key="queryrule_actions.text.li3"/>
          </li>
        </c:if>
      </ul>
    </p>
  </c:if>

  <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                var="allLanguages"/>
  <%-- table for show query rule actions--%>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="action" items="${handler.actions}"
                  sort="${actionSortValue}" onSort="tableOnSortActions"
                  tableId="editRuleActionsTable" emptyRow="true">

    <admin-ui:column type="checkbox simple" title="queryrule_actions.table.e">
      <input type="checkbox" onclick="addEmptyField(this);"
          <c:if test="${action.actionEnabled}">checked="true"</c:if> />
      <d:input type="hidden" value="${action.actionEnabled}" bean="QueryRuleFormHandler.actionEnabled" name="actionEnabled"/>
    </admin-ui:column>

    <admin-ui:column title="queryrule_actions.table.id" type="static">
      <c:if test="${empty action.actionId}">
        <fmt:message key="queryrule_actions.table.new_action"/>
      </c:if>
      <c:if test="${not empty action.actionId}">
        <c:out value="${action.actionId}"/>
      </c:if>
      <d:input bean="QueryRuleFormHandler.actionId" value="${action.actionId}" type="hidden" name="actionId"/>
    </admin-ui:column>

    <admin-ui:column title="queryrule_actions.table.action" type="sortable" width="90%" name="action">
      <d:input bean="QueryRuleFormHandler.action" value="${action.action}" name="action"
               type="text" iclass="textField" style="width:95%"
               onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="queryrule_actions.table.language" name="actionLanguage">
      <d:select bean="QueryRuleFormHandler.actionLanguage" iclass="small" name="actionLanguage"
                onchange="addEmptyField(this);">
        <c:forEach items="${allLanguages}" var="currentLanguage">
          <option value="${fn:escapeXml(currentLanguage)}" <c:if test="${currentLanguage == action.actionLanguage}">selected="true"</c:if>>
            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
            ${localizedLanguage}
          </option>
        </c:forEach>
      </d:select>
    </admin-ui:column>
    <admin-ui:column type="icon" width="20">
      <a class="icon propertyDelete" href="#" onclick="return deleteField(this);">del</a>
    </admin-ui:column>
  </admin-ui:table>
  <d:input type="submit" bean="QueryRuleFormHandler.sortActions" iclass="formsubmitter" style="display:none"
           id="actionSortInput" onclick="return checkForm();"/>
  <d:input type="hidden" bean="QueryRuleFormHandler.actionSortDirection" id="actionSortDirection"/>
  <d:input type="hidden" bean="QueryRuleFormHandler.actionSortColumn" id="actionSortColumn"/>
  <d:input type="hidden" bean="QueryRuleFormHandler.actionTab" id="actionTab" value="false"/>

  <script type="text/javascript">
    initTable(document.getElementById("editRuleActionsTable"));

    function tableOnSortActions(tableId, columnName, sortDirection) {
      var sortButton = document.getElementById('actionSortInput');
      var sortTab = document.getElementById('actionTab');
      var sortDirectionField = document.getElementById('actionSortDirection');
      var sortColumn = document.getElementById('actionSortColumn');
      sortDirectionField.value = sortDirection;
      sortColumn.value = columnName;

      sortTab.value = true;
      sortButton.click();
    }
  </script>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_actions.jsp#2 $$Change: 651448 $--%>
