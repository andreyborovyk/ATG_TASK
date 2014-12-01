<%--
  JSP, allow edit Query Rule.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_new.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="queryGroupId" var="queryGroupId"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleFormHandler"/>
  <d:getvalueof bean="QueryRuleFormHandler.actionTab" var="actionTab"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="queryGroupId" value="${queryGroupId}"/>
  </admin-ui:initializeFormHandler>

  <%-- Navigation tabs --%>
  <d:include src="queryrule_navigation.jsp">
    <d:param name="actionTab" value="${actionTab}"/>
  </d:include>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="post">
      <c:url value="${queryPath}/query_rule_group.jsp" var="successURL">
        <c:param name="queryGroupId" value="${queryGroupId}" />
      </c:url>
      <d:input type="hidden" bean="QueryRuleFormHandler.successURL" name="successURL" value="${successURL}"/>
      <c:url value="${queryPath}/queryrule_new.jsp" var="successAlternativeURL">
        <c:param name="queryGroupId" value="${queryGroupId}" />
      </c:url>
      <d:input type="hidden" bean="QueryRuleFormHandler.successAlternativeURL"
               name="successAlternativeURL" value="${successAlternativeURL}"/>
      <c:url value="${queryPath}/queryrule_new.jsp" var="errorURL" />
      <d:input type="hidden" bean="QueryRuleFormHandler.errorURL" name="errorURL" value="${errorURL}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.queryGroupId" name="queryGroupId" value="${queryGroupId}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.queryRuleId" name="queryRuleId" value="${queryRuleId}"/>
      <d:input type="hidden" bean="QueryRuleFormHandler.needInitialization" value="false"/>

      <div id="paneContent">
        <admin-validator:validate beanName="QueryRuleFormHandler"/>
        <%-- Patterns tab --%>
        <div id="content1" <c:if test="${actionTab}">style="display:none"</c:if>>
          <d:include src="queryrule_patterns.jsp">
            <d:param name="queryGroupId" value="${queryGroupId}"/>
          </d:include>
        </div>
        <%-- Actions tab --%>
        <div id="content2" <c:if test="${!actionTab}">style="display:none"</c:if>>
          <d:include src="queryrule_actions.jsp" />
        </div>
      </div>
    
      <div id="paneFooter">
        <%-- Create button--%>
        <fmt:message key="queryrule.button.create" var="createButtonTitle"/>
        <fmt:message key="queryrule.button.create.tooltip" var="createButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleFormHandler.create" iclass="formsubmitter"
                 value="${createButtonTitle}" title="${createButtonToolTip}" onclick="return checkForm();"/>
        <%-- Create and Add New Group button--%>
        <fmt:message key="queryrule.button.create_and_add" var="createButtonTitle"/>
        <fmt:message key="queryrule.button.create_and_add.tooltip" var="createButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleFormHandler.createAddNew" iclass="formsubmitter"
                 value="${createButtonTitle}" title="${createButtonToolTip}" onclick="return checkForm();"/>
        <%-- Cancel button--%>
        <fmt:message key="queryrule.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="queryrule.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryrule_new.jsp#2 $$Change: 651448 $--%>
