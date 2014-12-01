<%--
Query rule group page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_group.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="queryGroupId" var="queryGroupId"/>

  <c:if test="${queryGroupId != null and queryGroupId != ''}">
    <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/EditQueryRuleGroupFormHandler" var="handler"/>
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="queryGroupId" value="${queryGroupId}"/>
    </admin-ui:initializeFormHandler>
  </c:if>
  <queryrule:queryRuleGroupFindByPrimaryKey var="queryRuleGroup" queryRuleGroupId="${queryGroupId}" />

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/EditQueryRuleGroupFormHandler"/>
  <c:set var="handlerName" value="EditQueryRuleGroupFormHandler"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="post">
      <div id="paneContent">
        <h3>
          <fmt:message key="query_rule_group.query_rule_group_basics"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <label for="queryRuleGroupName">
                  <span id="queryRuleGroupNameAlert"></span><fmt:message key="query_rule_group.name"/>
                </label>
              </td>
              <td>
                <d:input bean="${handlerName}.queryRuleGroupName" type="text"
                         id="queryRuleGroupName" iclass="textField" name="queryRuleGroupName"/>
              </td>
            </tr>
          </tbody>
        </table>
        <h3>
          <fmt:message key="query_rule_group.child_query_rule_groups"/>
        </h3>
        <p>
          <c:url var="addNewGroups" value="${queryPath}/add_new_query_rule_groups.jsp">
            <c:param name="parentId" value="${queryGroupId}"/>
          </c:url>
          <tags:buttonLink titleKey="query_rule_group.addNewGroups"
                           tooltipKey="query_rule_group.addNewGroupsTooltip" href="${addNewGroups}"/>
        </p>
        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                        var="childGroup" items="${queryRuleGroup.childQueryRuleGroups}">
          <admin-ui:column title="query_rule_group.table.child_group_name" type="static">
            <c:url value="${queryPath}/query_rule_group.jsp" var="queryGroupURL">
              <c:param name="queryGroupId" value="${childGroup.id}"/>
            </c:url>
            <a href="${queryGroupURL}" onclick="return loadRightPanel(this.href);">
              <c:out value="${childGroup.name}"/>
            </a>
          </admin-ui:column>
          <admin-ui:column title="query_rule_group.table.query_rules" type="static">
            <queryrule:queryRuleGroupGetQueryRules queryRuleGroupId="${childGroup.id}" var="queryRulesCount"/>
            <c:out value="${queryRulesCount}"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="query_rule_group.table.copy.tooltip" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
               onclick="return submitOnCopy(${childGroup.id});">copy</a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="query_rule_group.table.delete.tooltip" var="deleteTitle"/>
            <c:url value="${queryPath}/query_rule_group_delete_popup.jsp" var="popGroupUrl">
              <c:param name="queryRuleGroupId" value="${childGroup.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popGroupUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>
        <h3>
          <fmt:message key="query_rule_group.child_query_rules"/>
        </h3>
        <p>
          <c:url value="${queryPath}/queryrule_new.jsp" var="newQueryRuleURL">
            <c:param name="queryGroupId" value="${queryGroupId}"/>
          </c:url>
          <tags:buttonLink titleKey="query_rule_group.addNewRule" tooltipKey="query_rule_group.addNewRuleTooltip" href="${newQueryRuleURL}"/>
        </p>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="childQueryRule" items="${queryRuleGroup.childQueryRules}">
          <admin-ui:column title="query_rule_group.table.child_query_rule_name" type="static">
            <c:url var="qeryRuleURL" value="${queryPath}/queryrule.jsp">
              <c:param name="queryRuleId" value="${childQueryRule.id}"/>
            </c:url>
            <a href="${qeryRuleURL}" onclick="return loadRightPanel(this.href);">
              <c:out value="${childQueryRule.name}"/>
            </a>
          </admin-ui:column>
          <admin-ui:column title="query_rule_group.table.patterns" type="static">
             <c:forEach items="${childQueryRule.rulePatterns}" var="item" varStatus="index">
               <c:out value="${item.pattern}"/>
               <c:if test="${!index.last}">
                 <fmt:message key="project_cust.semicolon"/>
               </c:if>
             </c:forEach>
          </admin-ui:column>
          <admin-ui:column title="query_rule_group.table.actions" type="static">
            <c:forEach items="${childQueryRule.ruleActions}" var="item" varStatus="index">
              <c:out value="${item.action}"/>
              <c:if test="${!index.last}">
                <fmt:message key="project_cust.semicolon"/>
              </c:if>
            </c:forEach>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="query_rule_group.table.delete" var="deleteTitle"/>
            <c:url value="${queryPath}/query_rule_delete_popup.jsp" var="popUrl">
              <c:param name="queryRuleId" value="${childQueryRule.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>
      </div>

      <%-- copy query rule group --%>
      <d:input type="hidden" bean="${handlerName}.copyQueryRuleGroupId" id="copyQueryRuleGroupId" name="copyQueryRuleGroupId"/>
      <d:input bean="${handlerName}.copyQueryRuleGroup" value="field mode" type="submit" id="copyInput" iclass="formsubmitter" style="display:none"/>
         
      <div id="paneFooter">
        <d:input type="hidden" bean="${handlerName}.queryGroupId" value="${queryGroupId}" name="queryGroupId" />
        <d:input type="hidden" bean="${handlerName}.needInitialization" value="false" />

        <c:url var="errorURL" value="${queryPath}/query_rule_group.jsp">
          <c:param name="queryGroupId" value="${queryGroupId}" />
        </c:url>
        <d:input type="hidden" bean="${handlerName}.errorURL" value="${errorURL}" name="errorURL" />

        <c:set var="parentNode" value="${queryRuleGroup.parentNode}" />
        <c:if test="${parentNode.nodeType eq 'queryRuleSet'}">
          <c:url var="successURL" value="${queryPath}/queryset.jsp">
            <c:param name="querySetId" value="${parentNode.id}" />
          </c:url>
        </c:if>
        <c:if test="${parentNode.nodeType eq 'queryRuleGroup'}">
          <c:url var="successURL" value="${queryPath}/query_rule_group.jsp">
            <c:param name="queryGroupId" value="${parentNode.id}" />
          </c:url>
        </c:if>
        <d:input type="hidden" bean="${handlerName}.successURL" value="${successURL}" name="successURL" id="successURL"/>
        <fmt:message var="saveButton" key="query_rule_group.saveButton"/>
        <fmt:message var="saveButtonTooltip" key="query_rule_group.saveButtonTooltip"/>
        <d:input type="submit" value="${saveButton}" iclass="formsubmitter"
                 bean="${handlerName}.save" title="${saveButtonTooltip}" onclick="return checkForm()"/>

        <admin-beans:getNextQueryRuleGroup queryGroupId="${queryGroupId}" nextGroup="nextGroup" />

        <c:if test="${nextGroup != null}">
          <c:url var="successEditNextURL" value="${queryPath}/query_rule_group.jsp">
            <c:param name="queryGroupId" value="${nextGroup.id}" />
          </c:url>
          <d:input type="hidden" bean="${handlerName}.successEditNextURL" value="${successEditNextURL}" name="successEditNextURL" />
        </c:if>
        <fmt:message var="saveEditNextButton" key='query_rule_group.saveEditNextButton'/>
        <fmt:message var="saveEditNextButtonTooltip" key="query_rule_group.saveEditNextButtonTooltip"/>
        <d:input type="submit" value="${saveEditNextButton}" bean="${handlerName}.saveEditNext" iclass="formsubmitter"
                 title="${saveEditNextButton}" onclick="return checkForm()" disabled="${nextGroup == null}"/>

        <admin-validator:validate beanName="EditQueryRuleGroupFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    function submitOnCopy(id){
      <c:url var="copyGroupURL" value="${queryPath}/query_rule_group.jsp">
        <c:param name="queryGroupId" value="" />
      </c:url>
      document.getElementsByName('successURL')[0].value='${copyGroupURL}' + id;
      document.getElementById('copyQueryRuleGroupId').value = id;
      document.getElementById('copyInput').click();
      return false;
    }
  </script>
  <admin-beans:getQueryGroupHierarchy groupId="${queryGroupId}" var="hierarchy"/>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = new Array();
    top.hierarchy[0] = {id:"rootQrNode"};
    <c:forEach items="${hierarchy}" var="item" varStatus="status">
      top.hierarchy[${status.index + 1}] = {id:"<c:out value="${item.id}"/>", treeNodeType:"<c:out value="${item.nodeType}"/>"};
    </c:forEach>
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_group.jsp#2 $$Change: 651448 $--%>
