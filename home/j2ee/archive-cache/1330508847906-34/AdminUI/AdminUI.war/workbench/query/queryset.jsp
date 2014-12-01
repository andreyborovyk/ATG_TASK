<%--
  JSP, allow edit Query Rule Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="querySetId" var="querySetId"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleSetFormHandler"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="querySetId" value="${querySetId}"/>
  </admin-ui:initializeFormHandler>

  <%-- Navigation tabs --%>
  <d:include src="queryset_navigation.jsp"/>
    
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <%-- Validation --%>
    <admin-validator:validate beanName="QueryRuleSetFormHandler"/>

    <d:form action="${formActionUrl}" method="post">
      <div id="paneContent">
        <%-- General tab --%>
        <div id="content1">
          <d:include src="queryset_general.jsp" />

          <%-- Additional info about existing Query Set--%>
          <h3><fmt:message key="queryset_general.message.title.child"/></h3>

          <p>
            <c:url value="${queryPath}/add_new_query_rule_groups.jsp" var="addGroupURL">
              <c:param name="parentId" value="${handler.querySetId}"/>
            </c:url>
            <input type="button" value='<fmt:message key="queryset_general.button.add_groups"/>'
                   title='<fmt:message key="queryset_general.button.add_groups.tooltip"/>'
                   onclick="return loadRightPanel('${addGroupURL}');"/>
          </p>
          <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${handler.querySetId}" var="queryRuleSet"/>

          <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                          var="queryGroup" items="${queryRuleSet.childQueryRuleGroups}">
            <admin-ui:column title="queryset_general.table.child.name" type="sortable" name="name">
              <c:url value="${queryPath}/query_rule_group.jsp" var="ruleGroupUrl">
                <c:param name="queryGroupId" value="${queryGroup.id}"/>
              </c:url>
              <a href="${ruleGroupUrl}" onclick="return loadRightPanel(this.href);">
                <c:out value="${queryGroup.name}"/>
              </a>
            </admin-ui:column>

            <admin-ui:column title="queryset_general.table.child.description" type="sortable" name="query_rules">
              <queryrule:queryRuleGroupGetQueryRules queryRuleGroupId="${queryGroup.id}" var="queruRules"/>  
              <c:out value="${queruRules}"/>
            </admin-ui:column>

            <admin-ui:column type="icon">
              <fmt:message key="queryset_general.table.child.copy" var="copyTitle"/>
              <a class="icon propertyCopy" title="${copyTitle}" href="#"
                 onclick="return submitOnCopy(${queryGroup.id});">copy</a>
            </admin-ui:column>

            <admin-ui:column type="icon">
              <fmt:message key="queryset_general.table.child.delete" var="deleteTitle"/>
              <c:url value="${queryPath}/query_rule_group_delete_popup.jsp" var="popUrl">
                <c:param name="queryRuleGroupId" value="${queryGroup.id}"/>
              </c:url>
              <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
                 onclick="return showPopUp(this.href);">del</a>
            </admin-ui:column>
          </admin-ui:table>

          <script type="text/javascript">
            function submitOnCopy(id, mode){
              document.getElementById('copyQueryRuleGroupId').value = id;
              document.getElementById('copyInput').click();
              return false;
            }
          </script>
        </div>
        <%-- Used in search projects tab --%>
        <div id="content2" style="display:none">
          <d:include src="queryset_search_project.jsp"/>
        </div>
      </div>

      <div id="paneFooter">
        <c:set var="nextQuerySetId" value="${handler.nextQuerySetId}" />
        <c:url value="${queryPath}/queryset.jsp" var="successAlternativeURL">
          <c:param name="querySetId" value="${nextQuerySetId}" />
        </c:url>
        <d:input bean="QueryRuleSetFormHandler.successAlternativeURL" name="successAlternativeURL"
                 type="hidden" value="${successAlternativeURL}"/>

        <d:input type="hidden" bean="QueryRuleSetFormHandler.querySetId" />
        <d:input type="hidden" bean="QueryRuleSetFormHandler.needInitialization" value="false" />

        <%-- Save button--%>
        <fmt:message key="queryset_general.button.save" var="saveButtonTitle"/>
        <fmt:message key="queryset_general.button.save.tooltip" var="saveButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleSetFormHandler.update" iclass="formsubmitter"
                 value="${saveButtonTitle}" title="${saveButtonToolTip}" onclick="return checkForm();"/>
        <%-- Save and Edit Next button--%>
        <fmt:message key="queryset_general.button.save_edit_next" var="saveEditButtonTitle"/>
        <fmt:message key="queryset_general.button.save_edit_next.tooltip" var="saveEditButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleSetFormHandler.updateEditNext" iclass="formsubmitter"
                 value="${saveEditButtonTitle}" title="${saveEditButtonToolTip}" onclick="return checkForm();"
                 disabled="${nextQuerySetId == null}"/>
        <%-- copy query rule group --%>
        <d:input type="hidden" bean="QueryRuleSetFormHandler.copyQueryRuleGroupId"
                 id="copyQueryRuleGroupId" name="copyQueryRuleGroupId"/>
        <%-- copy action --%>
        <d:input bean="QueryRuleSetFormHandler.copyQueryRuleGroup" type="submit" id="copyInput" 
                 iclass="formsubmitter" style="display:none;"/>
      </div>
    </d:form>

    <c:if test="${not empty handler.querySetId}">
      <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${handler.querySetId}" var="queryRuleSet"/>
      <script type="text/javascript">
        //dojo tree refresh
        top.hierarchy = [{id:"rootQrNode"}, {id:"<c:out value="${queryRuleSet.id}"/>", treeNodeType:"<c:out value="${queryRuleSet.nodeType}"/>"}];
        top.syncTree();
      </script>
    </c:if>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset.jsp#2 $$Change: 651448 $--%>
