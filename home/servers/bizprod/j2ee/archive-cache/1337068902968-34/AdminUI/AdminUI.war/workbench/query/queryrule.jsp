<%--
  JSP, allow edit Query Rule.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="queryRuleId" var="queryRuleId"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleFormHandler"/>
  <d:getvalueof bean="QueryRuleFormHandler.actionTab" var="actionTab"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="queryRuleId" value="${queryRuleId}"/>
  </admin-ui:initializeFormHandler>

  <admin-beans:getNextQueryRule var="nextQueryRule" queryRuleId="${queryRuleId}"/>
  <c:set var="updateEditButtonDisabled" value="${nextQueryRule eq null}"/>

  <%-- Navigation tabs --%>
  <d:include src="queryrule_navigation.jsp">
    <d:param name="actionTab" value="${actionTab}"/>
  </d:include>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="post">
      <c:url value="${queryPath}/query_rule_group.jsp" var="successURL">
        <c:param name="queryGroupId" value="${handler.queryGroupId}" />
      </c:url>
      <d:input type="hidden" bean="QueryRuleFormHandler.successURL" name="successURL" value="${successURL}"/>
      <c:url value="${queryPath}/queryrule.jsp" var="successAlternativeURL">
        <c:param name="queryRuleId" value="${nextQueryRule}" />
      </c:url>
      <d:input type="hidden" bean="QueryRuleFormHandler.successAlternativeURL"
               name="successAlternativeURL" value="${successAlternativeURL}"/>
      <c:url value="${queryPath}/queryrule.jsp" var="errorURL">
        <c:param name="queryRuleId" value="${queryRuleId}" />
      </c:url>
      <d:input type="hidden" bean="QueryRuleFormHandler.errorURL" name="errorURL" value="${errorURL}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.queryGroupId" name="queryGroupId" value="${handler.queryGroupId}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.queryRuleId" name="queryRuleId" value="${queryRuleId}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.needInitialization" value="false"/>

      <div id="paneContent">
        <admin-validator:validate beanName="QueryRuleFormHandler"/>
        <%-- Patterns tab --%>
        <div id="content1" <c:if test="${actionTab}">style="display:none"</c:if>>
          <d:include src="queryrule_patterns.jsp">
            <d:param name="queryRuleId" value="${queryRuleId}"/>
          </d:include>
        </div>
        <%-- Actions tab --%>
        <div id="content2" <c:if test="${!actionTab}">style="display:none"</c:if>>
          <d:include src="queryrule_actions.jsp" />
        </div>
      </div>

      <div id="paneFooter">
        <%-- Save button--%>
        <fmt:message key="queryrule.button.save" var="saveButtonTitle"/>
        <fmt:message key="queryrule.button.save.tooltip" var="saveButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleFormHandler.update" value="${saveButtonTitle}"
                 title="${saveButtonToolTip}" iclass="formsubmitter" onclick="return checkForm();"/>
        <%-- Save and Edit Next button--%>
        <fmt:message key="queryrule.button.save_and_edit_next" var="saveEditButtonTitle"/>
        <fmt:message key="queryrule.button.save_and_edit_next.tooltip" var="saveEditButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleFormHandler.updateEditNext" value="${saveEditButtonTitle}"
                 title="${saveEditButtonToolTip}" onclick="return checkForm();" iclass="formsubmitter" disabled="${updateEditButtonDisabled}" />
      </div>
    </d:form>
  </div>
  <%--dojo tree refresh. Selects current node in tree.--%>
  <admin-beans:getQueryGroupHierarchy queryRuleId="${queryRuleId}" var="hierarchy"/>
  <script type="text/javascript">
    top.hierarchy = new Array();
    top.hierarchy[0] = {id:"rootQrNode"};
    <c:forEach items="${hierarchy}" var="item" varStatus="status">
      <c:choose>
        <c:when test="${status.last}">
          top.hierarchy[${status.index + 1}] = {id:"<c:out value="${item.id}"/>", treeNodeType:"queryRule"};
        </c:when>
        <c:otherwise>
          top.hierarchy[${status.index + 1}] = {id:"<c:out value="${item.id}"/>", treeNodeType:"<c:out value="${item.nodeType}"/>"};
        </c:otherwise>
      </c:choose>
    </c:forEach>
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule.jsp#2 $$Change: 651448 $--%>
