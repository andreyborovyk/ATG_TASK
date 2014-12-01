<%--
* Shows query rule patterns table
*
* @author Alexander Lutarevich
* @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_patterns.jsp#2 $$Change: 651448 $
* @updated $DateTime: 2011/06/07 13:55:45 $$$
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="queryRuleId" var="queryRuleId"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleFormHandler"/>
  <d:getvalueof bean="QueryRuleFormHandler.patternSort" var="sortValue"/>

  <h3>
    <fmt:message key="queryrule_patterns.message.basics"/>
  </h3>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="queryRuleNameAlert"><span class="required"><fmt:message key="project_general.required_field" /></span></span>
          <fmt:message key="queryrule_patterns.message.query_rule_name" />
        </td>
        <td>
          <d:input bean="QueryRuleFormHandler.queryRuleName" type="text" name="queryRuleName" iclass="textField"/>
        </td>
      </tr>
    </tbody>
  </table>

  <h3>
    <fmt:message key="queryrule_patterns.message.pattenrs"/>
    <span class="ea"><tags:ea key="embedded_assistant.queryrule_patterns" /></span>
  </h3>

  <p>
    <%-- Link to macros page --%>
    <queryrule:globalQueryRuleSetMacroFindAll var="globalMacros"/>
    <fmt:message key="queryrule_patterns.pattern_macros">
      <fmt:param value="${fn:length(globalMacros)}"/>
    </fmt:message>
    <c:url value="${queryPath}/querysets_macros.jsp" var="querysets_macros">
      <c:param name="mode" value="edit"/>
      <c:param name="queryRuleId" value="${queryRuleId}"/>
    </c:url>
    [
    <a href="${querysets_macros}" onclick="return loadRightPanel(this.href);"
       title="<fmt:message key='queryrule_patterns.link.edit_macros.tooltip'/>">
      <fmt:message key="queryrule_patterns.link.edit_macros"/>
    </a>
    ]
  </p>

  <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                var="allLanguages"/>
  <%-- query rule patterns table --%>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="pattern" items="${handler.patterns}"
                  sort="${sortValue}" onSort="tableOnSort"
                  tableId="editRulePatternsTable" emptyRow="true">

    <admin-ui:column type="checkbox simple" title="queryrule_patterns.table.e">
      <input type="checkbox" onclick="addEmptyField(this);"
          <c:if test="${pattern.patternEnabled}">checked="true"</c:if>/>
      <d:input type="hidden" value="${pattern.patternEnabled}" bean="QueryRuleFormHandler.patternEnabled"
               name="patternEnabled"/>
    </admin-ui:column>

    <admin-ui:column title="queryrule_patterns.table.id" type="static">
      <c:if test="${empty pattern.patternId}">
        <fmt:message key="queryrule_patterns.table.new_pattern"/>
      </c:if>
      <c:if test="${not empty pattern.patternId}">
        <c:out value="${pattern.patternId}"/>
      </c:if>
      <d:input type="hidden" bean="QueryRuleFormHandler.patternId" value="${pattern.patternId}" name="patternId"/>
    </admin-ui:column>

    <admin-ui:column title="queryrule_patterns.table.pattern" type="sortable" width="65%" name="pattern">
      <d:input bean="QueryRuleFormHandler.pattern" value="${pattern.pattern}" name="pattern"
               type="text" iclass="textField" style="width:95%"
               onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="queryrule_patterns.table.weight" width="10%" name="patternWeight">
      <d:input bean="QueryRuleFormHandler.patternWeight" value="${pattern.patternWeight}" name="patternWeight"
               type="text" iclass="textField" size="3" maxlength="3"
               onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="queryrule_patterns.table.group" width="25%" name="patternGroup">
      <d:input bean="QueryRuleFormHandler.patternGroup" value="${pattern.patternGroup}" name="patternGroup"
               type="text" iclass="textField" style="width:90%"
               onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="queryrule_patterns.table.language" name="patternLanguage">
      <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                    var="allLanguages"/>
      <d:select bean="QueryRuleFormHandler.patternLanguage" iclass="small" name="patternLanguage"
                onchange="addEmptyField(this);">
        <c:forEach items="${allLanguages}" var="currentLanguage">
          <option value="${fn:escapeXml(currentLanguage)}" <c:if test="${currentLanguage == pattern.patternLanguage}">selected="true"</c:if>>
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
  <d:input type="submit" bean="QueryRuleFormHandler.sortPatterns" style="display:none" id="patternSortInput"
           iclass="formsubmitter" onclick="return checkForm();"/>
  <d:input type="hidden" bean="QueryRuleFormHandler.patternSortDirection" id="patternSortDirection"/>
  <d:input type="hidden" bean="QueryRuleFormHandler.patternSortColumn" id="patternSortColumn"/>
  <script type="text/javascript">
    initTable(document.getElementById("editRulePatternsTable"));
    function tableOnSort(tableId, columnName, sortDirection) {
      var sortButton = document.getElementById('patternSortInput');
      document.getElementById('patternSortDirection').value = sortDirection;
      document.getElementById('patternSortColumn').value = columnName;
      sortButton.click();
    }
  </script>
</d:page><%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_patterns.jsp#2 $$Change: 651448 $--%>
