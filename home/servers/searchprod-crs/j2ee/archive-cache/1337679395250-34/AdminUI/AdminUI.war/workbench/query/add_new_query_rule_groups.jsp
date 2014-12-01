<%--
JSP allows to add new query rule groups.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/add_new_query_rule_groups.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="parentId" var="parentId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleGroupFormHandler" var="handler"/>

  <%-- Trying to find a set by ID first. If impossible then parent is a group. --%>
  <c:catch var="exception">
    <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${parentId}" var="parent" />
    <fmt:message var="title" key="add_new_query_rule_groups.under.query_rule_group">
      <fmt:param><c:out value="${parent.name}"/></fmt:param>
    </fmt:message>
    <c:url value="${queryPath}/queryset.jsp" var="successURL">
      <c:param name="querySetId" value="${parentId}"/>
    </c:url>
  </c:catch>
  <c:if test="${not empty exception}">
    <queryrule:queryRuleGroupFindByPrimaryKey queryRuleGroupId="${parentId}" var="parent" />
    
    <fmt:message var="title" key="add_new_query_rule_groups.under.query_rule_set">
      <fmt:param><c:out value="${parent.name}"/></fmt:param>
    </fmt:message>
    
    <c:url value="${queryPath}/query_rule_group.jsp" var="successURL">
      <c:param name="queryGroupId" value="${parentId}"/>
    </c:url>
  </c:if>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">

    <d:form action="${formActionUrl}" method="post">
      <div id="paneContent">

      <p>
        ${title}
        <span class="ea"><tags:ea key="embedded_assistant.add_new_query_rule_groups.group" /></span>
      </p>

        <c:url value="${queryPath}/add_new_query_rule_groups.jsp" var="errorURL">
          <c:param name="parentId" value="${parentId}"/>
        </c:url>

        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                        var="group" items="${handler.groupName}" tableId="tableQueryRuleGroups">
          <admin-ui:column title="add_new_query_rule_groups.table.name" type="static" width="100%" name="groupName">
            <d:input bean="QueryRuleGroupFormHandler.groupName" value="${group}" name="groupName"
                     type="text" iclass="textField" style="width:95%"
                     onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <a class="icon propertyDelete" href="#" onclick="return deleteField(this);">del</a>
          </admin-ui:column>
        </admin-ui:table>
        <script type="text/javascript">
          initTable(document.getElementById("tableQueryRuleGroups"));
        </script>
      </div>
      <div id="paneFooter">
        <fmt:message var="createButtonTooltip" key="add_new_query_rule_groups.tooltip.create"/>
        <fmt:message var="cancelButtonTooltip" key="add_new_query_rule_groups.tooltip.cancel"/>
        <fmt:message var="createButton" key="add_new_query_rule_groups.button.create"/>
        <fmt:message var="cancelButton" key="add_new_query_rule_groups.button.cancel"/>
        <d:input type="hidden" bean="QueryRuleGroupFormHandler.successURL" value="${successURL}" name="successURL"/>
        <d:input type="hidden" bean="QueryRuleGroupFormHandler.errorURL" value="${errorURL}" name="errorURL"/>
        <d:input type="hidden" bean="QueryRuleGroupFormHandler.parentId" value="${parentId}" name="parentId"/>
        <d:input type="submit" value="${createButton}" bean="QueryRuleGroupFormHandler.createGroups"
                 title="${createButtonTooltip}" onclick="return checkForm();" iclass="formsubmitter"/>

        <%-- Cancel button --%>
        <d:input type="submit" value="${cancelButton}" bean="QueryRuleGroupFormHandler.cancel"
                 title="${cancelButtonTooltip}" iclass="formsubmitter"/>
      </div>
      <%-- Validation --%>
      <admin-validator:validate beanName="QueryRuleGroupFormHandler"/>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/add_new_query_rule_groups.jsp#2 $$Change: 651448 $--%>
