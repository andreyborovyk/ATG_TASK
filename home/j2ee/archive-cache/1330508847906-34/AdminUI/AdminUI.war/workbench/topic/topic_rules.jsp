<%--
  JPS, showing rules tab on edit topic page. It is included into topic.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_rules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler" var="ManageTopicFormHandler"/>
  <d:getvalueof bean="ManageTopicFormHandler.topicId" var="currentTopicId"/>

  <br/>

  <h3>
    <fmt:message key="topic_rules.categorization_rules"/>
  </h3>

  <p>
    <fmt:message key="topic_rules.pattern_macros"/>
    <fmt:message key="topic_rules.pattern_macros.edit_global_macros.tooltip" var="editGlobalPatternMacrosTooltip"/>
    <c:url value="${topicPath}/global_macros.jsp" var="macrosGlobal" >
      <c:param name="macrosType" value="topic"/>
      <c:param name="topicId" value="${currentTopicId}"/>
      <c:param name="topicSetId" value=""/>
    </c:url>
    <a href="${macrosGlobal}" title="${editGlobalPatternMacrosTooltip}" onclick="return loadRightPanel(this.href);">
      <fmt:message key="topic_rules.pattern_macros.global">
        <topic:globalMacroFindAll var="globalMacros"/>
        <fmt:param value="${fn:length(globalMacros)}" />
      </fmt:message>
    </a>
    +
    <fmt:message key="topic_rules.pattern_macros.edit_local_macros.tooltip" var="editLocalPatternMacrosTooltip"/>
    <c:url value="${topicPath}/topic_pattern_macros.jsp" var="macrosLocal">
      <c:param name="topicId" value="${currentTopicId}"/>
    </c:url>
    <a href="${macrosLocal}" title="${editLocalPatternMacrosTooltip}" onclick="return loadRightPanel(this.href);">
      <fmt:message key="topic_rules.pattern_macros.local">
        <topic:topicFindByPrimaryKey topicId="${currentTopicId}" var="currentTopic"/>
        <fmt:param value="${fn:length(currentTopic.macros)}" />
      </fmt:message>
    </a>
  </p>

  <%-- Table, showing topic patterns--%>
  <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                  var="currentRule" items="${ManageTopicFormHandler.patternIterator}" tableId="editMacroTable" >
    <admin-ui:column type="checkbox simple" title="topic_rules.table.enabled">
      <input type="checkbox" onclick="addEmptyField(this)" <c:if test="${currentRule.enabled}">checked="true"</c:if> />
      <d:input type="hidden" value="${currentRule.enabled}" bean="ManageTopicFormHandler.patternEnabled" name="patternEnabled"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="topic_rules.table.id">
      <c:if test="${empty currentRule.patternId}">
        <fmt:message key="topic_rules.table.new_pattern"/>
      </c:if>
      <c:if test="${not empty currentRule.patternId}">
        <c:out value="${currentRule.patternId}"/>
      </c:if>
      <d:input bean="ManageTopicFormHandler.patternIds" value="${currentRule.patternId}"
               type="hidden" name="patternIds"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="topic_rules.table.pattern" width="65%">
      <d:input type="text" iclass="textField" value="${fn:escapeXml(currentRule.pattern)}"
               bean="ManageTopicFormHandler.patterns" name="patterns" onkeyup="addEmptyField(this);"
               onchange="addEmptyField(this);" style="width:95%"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="topic_rules.table.weight" width="10%">
      <d:input type="text" iclass="textField" value="${currentRule.weight}"
               bean="ManageTopicFormHandler.weights" name="weights" onkeyup="addEmptyField(this);"
               onchange="addEmptyField(this);" size="4"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="topic_rules.table.groups" width="25%">
      <d:input type="text" iclass="textField" value="${fn:escapeXml(currentRule.groups)}"
               bean="ManageTopicFormHandler.groups" name="groups" onkeyup="addEmptyField(this);"
               onchange="addEmptyField(this);" style="width:90%"/>
    </admin-ui:column>
    <admin-ui:column type="static" title="topic_rules.table.language">
      <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                    var="allLanguages"/>
      <d:select bean="ManageTopicFormHandler.languages" iclass="small" name="languages">
        <c:forEach items="${allLanguages}" var="currentLanguage">
          <option value="${fn:escapeXml(currentLanguage)}" <c:if test="${currentLanguage == currentRule.language}">selected="true"</c:if>>
            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
            ${localizedLanguage}
          </option>
        </c:forEach>
      </d:select>
    </admin-ui:column>
    <admin-ui:column type="icon" width="20">
      <fmt:message key="topic_rules.table.title.move_up" var="moveUpTitle"/>
      <a class="icon propertyUp" href="#" title="${moveUpTitle}" onclick="return moveField(this,true);">up</a>
    </admin-ui:column>
    <admin-ui:column type="icon" width="20">
      <fmt:message key="topic_rules.table.title.move_down" var="moveDownTitle"/>
      <a class="icon propertyDown" href="#" title="${moveDownTitle}" onclick="return moveField(this,false);">down</a>
    </admin-ui:column>
    <admin-ui:column type="icon" width="20">
      <fmt:message key="topic_rules.table.title.delete" var="deleteTitle"/>

      <c:if test="${!empty currentRule.patternId}">
        <c:url value="${topicPath}/topic_rule_delete_popup.jsp" var="popUrl">
          <c:param name="id" value="${currentRule.patternId}"/>
        </c:url>
        <a name="deleteRuleLink_${currentRule.patternId}" id="deleteRuleLink_${currentRule.patternId}"
           class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
           onclick="return showPopUp(this.href);">del</a>
      </c:if>
      <c:if test="${empty currentRule.patternId}">
        <a class="icon propertyDelete" title="${deleteTitle}" href="#" onclick="return deleteField(this);">del</a>
      </c:if>
    </admin-ui:column>
  </admin-ui:table>
  <script type="text/javascript">
    initTable(document.getElementById("editMacroTable"));
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_rules.jsp#2 $$Change: 651448 $--%>
