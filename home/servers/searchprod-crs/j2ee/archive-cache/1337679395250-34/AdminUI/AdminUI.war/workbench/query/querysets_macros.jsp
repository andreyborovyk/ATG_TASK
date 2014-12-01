
<%--
  JSP,  allow add, edit macros for Query Rules Sets.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_macros.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="queryRuleId" var="queryRuleId"/>
  <d:getvalueof param="mode" var="mode"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/QueryRulePatternMacrosFormHandler" var="macrosFormHandler" />
  <c:set var="formHandlerName" value="/atg/searchadmin/workbenchui/formhandlers/QueryRulePatternMacrosFormHandler"/>
  <c:url var="backURL" value="${queryPath}/querysets_general.jsp" />

  <admin-ui:initializeFormHandler handler="${macrosFormHandler}">
    <admin-ui:param name="queryRuleId" value="${queryRuleId}"/>
  </admin-ui:initializeFormHandler>

  <c:url var="errorURL" value="${queryPath}/querysets_macros.jsp" />
  <c:url var="sortURL" value="${queryPath}/querysets_macros.jsp" />

  <c:if test="${queryRuleId != null and queryRuleId != ''}">
    <c:url var="backURL" value="${queryPath}/queryrule.jsp">
      <c:param name="queryRuleId" value="${queryRuleId}"/>
    </c:url>
    <c:url var="errorURL" value="${queryPath}/querysets_macros.jsp">
      <c:param name="mode" value="edit"/>
      <c:param name="queryRuleId" value="${queryRuleId}"/>
    </c:url>
    <c:url var="sortURL" value="${queryPath}/querysets_macros.jsp">
      <c:param name="mode" value="edit"/>
      <c:param name="queryRuleId" value="${queryRuleId}"/>
    </c:url>
  </c:if>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">
      <div id="paneContent">
        <admin-validator:validate beanName="QueryRulePatternMacrosFormHandler"/>
        <p><fmt:message key="querysets_macros.description"/></p>

        <d:include src="/workbench/macros_edit.jsp">
          <d:param name="formHandlerName" value="${formHandlerName}"/>
          <d:param name="macroMessage" value="querysets_macros.title"/>
          <d:param name="level" value="globalQueryRuleSetMacro"/>
        </d:include>
      </div>

      <div id="paneFooter">
        <d:input bean="QueryRulePatternMacrosFormHandler.successURL" type="hidden" value="${backURL}"  name="successURL" id="successURL"/>
        <d:input bean="QueryRulePatternMacrosFormHandler.errorURL"   type="hidden" value="${errorURL}" name="errorURL" id="errorURL"/>
        <d:input bean="QueryRulePatternMacrosFormHandler.sortURL"   type="hidden" value="${sortURL}" name="sortURL" id="sortURL"/>
        <d:input bean="QueryRulePatternMacrosFormHandler.queryRuleId"  type="hidden" value="${queryRuleId}" name="queryRuleId" id="queryRuleId"/>

        <fmt:message key="querysets_macros.button.save"    var="updateButtonTitle"/>
        <fmt:message key="querysets_macros.tooltip.save"   var="updateButtonToolTip"/>
        <fmt:message key="querysets_macros.button.cancel"  var="cancelButtonTitle"/>
        <fmt:message key="querysets_macros.tooltip.cancel" var="cancelButtonToolTip"/>

        <d:input type="submit" bean="QueryRulePatternMacrosFormHandler.saveMacros" onclick="return checkForm();" 
                 iclass="formsubmitter" value="${updateButtonTitle}" title="${updateButtonToolTip}"/>
        <d:input type="submit" bean="QueryRulePatternMacrosFormHandler.cancel" 
                 iclass="formsubmitter" value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>

    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_macros.jsp#2 $$Change: 651448 $--%>
