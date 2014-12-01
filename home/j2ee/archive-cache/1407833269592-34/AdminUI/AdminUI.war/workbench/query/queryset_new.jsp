<%--
  JSP, allow edit Query Rule Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_new.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <%-- search project id. it is not empty is we came from manage project customizations page --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleSetFormHandler"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
  </admin-ui:initializeFormHandler>

  <%-- Navigation tabs --%>
  <d:include src="queryset_navigation.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <%-- Validation --%>
    <admin-validator:validate beanName="QueryRuleSetFormHandler"/>

    <c:url value="${queryPath}/add_new_query_rule_groups.jsp" var="add"/>
    <d:form action="${formActionUrl}" method="post">
      <div id="paneContent">
        <%-- General tab --%>
        <div id="content1">
          <d:include src="queryset_general.jsp" />
        </div>
        <%-- Used in search projects tab --%>
        <div id="content2" style="display:none">
          <d:include src="queryset_search_project.jsp"/>
        </div>
      </div>

      <div id="paneFooter">
        <c:set value="${queryPath}/queryset.jsp" var="successURL"/>
        <d:input bean="QueryRuleSetFormHandler.successURL" name="successURL" type="hidden" value="${successURL}"/>

        <c:set value="${queryPath}/add_new_query_rule_groups.jsp" var="successAlternativeURL"/>
        <d:input bean="QueryRuleSetFormHandler.successAlternativeURL" name="successAlternativeURL"
                 type="hidden" value="${successAlternativeURL}"/>

        <c:set value="${queryPath}/queryset_new.jsp" var="errorURL"/>
        <d:input bean="QueryRuleSetFormHandler.errorURL" name="errorURL" type="hidden" value="${errorURL}"/>

        <d:input type="hidden" bean="QueryRuleSetFormHandler.needInitialization" value="false" />

        <%-- Create button--%>
        <fmt:message key="queryset_general.button.create" var="createButtonTitle"/>
        <fmt:message key="queryset_general.button.create.tooltip" var="createButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleSetFormHandler.create" iclass="formsubmitter"
                 value="${createButtonTitle}" title="${createButtonToolTip}" onclick="return checkForm();"/>
        <%-- Create and Add New Group button--%>
        <fmt:message key="queryset_general.button.create_and_add" var="createButtonTitle"/>
        <fmt:message key="queryset_general.button.create_and_add.tooltip" var="createButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleSetFormHandler.createAddGroups" iclass="formsubmitter"
                 value="${createButtonTitle}" title="${createButtonToolTip}" onclick="return checkForm();"/>
        <%-- Cancel button--%>
        <fmt:message key="queryset_general.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="queryset_general.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="QueryRuleSetFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_new.jsp#2 $$Change: 651448 $--%>
