<%--
JSP, used to be popup with delete query rule.

@author Andrei Tsishkouski
@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="queryRuleId" var="queryRuleId" scope="page"/>

  <queryrule:queryRuleFindByPrimaryKey queryRuleId="${queryRuleId}" var="queryRule"/>
  <c:set var="parent" value="${queryRule.queryRuleGroup}" />
  <c:url var="targetUrl" value="${queryPath}/query_rule_group.jsp">
    <c:param name="queryGroupId" value="${parent.id}" />
  </c:url>
  <c:url var="errorURL" value="${queryPath}/query_rule_delete_popup.jsp">
    <c:param name="queryRuleId" value="${queryRuleId}"/>
  </c:url>    
  <d:form action="${errorURL}" method="POST">
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleFormHandler.queryRuleId"
             value="${queryRuleId}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleFormHandler.successURL"
             value="${targetUrl}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleFormHandler.errorURL"
             value="${errorURL}"/>
    <div class="content">
      <span id="alertListPopup"></span>

      <p>
        <fmt:message key="query_rule_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="query_rule_delete_popup.question2">
                <fmt:param>
                  <c:out value="${queryRule.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="query_rule_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="query_rule_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='query_rule_delete_popup.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='query_rule_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_delete_popup.jsp#2 $$Change: 651448 $--%>
