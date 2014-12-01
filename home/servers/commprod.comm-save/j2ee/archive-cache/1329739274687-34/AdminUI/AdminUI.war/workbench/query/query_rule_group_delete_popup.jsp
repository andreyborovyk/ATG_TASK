<%--
JSP, used to be popup with delete query ruleset

@author Andrei Tsishkouski
@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_group_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="queryRuleGroupId" var="queryRuleGroupId" scope="page"/>

  <queryrule:queryRuleGroupFindByPrimaryKey queryRuleGroupId="${queryRuleGroupId}" var="queryRuleGroup"/>
  <c:set var="parent" value="${queryRuleGroup.parentNode}" />
  <c:choose>
    <c:when test="${parent.nodeType eq 'queryRuleSet'}">
      <c:url value="${queryPath}/queryset.jsp" var="targetURL">
        <c:param name="mode" value="edit"/>
        <c:param name="querySetId" value="${parent.id}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url value="${queryPath}/query_rule_group.jsp" var="targetURL">
        <c:param name="queryGroupId" value="${parent.id}"/>
      </c:url>
    </c:otherwise>
  </c:choose>
  <c:url var="errorURL" value="${queryPath}/query_rule_group_delete_popup.jsp">
    <c:param name="queryRuleGroupId" value="${queryRuleGroupId}" />
  </c:url>
  <d:form action="${errorURL}" method="POST">
    <%-- Id of query rule set, used to be deleted --%>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleGroupFormHandler.queryRuleGroupId"
             value="${queryRuleGroupId}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleGroupFormHandler.successURL"
             value="${targetURL}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleGroupFormHandler.errorURL"
             value="${errorURL}"/>
    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="query_rule_group_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="query_rule_group_delete_popup.question2">
                <fmt:param>
                  <c:out value="${queryRuleGroup.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="query_rule_group_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="query_rule_group_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQueryRuleGroupFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='query_rule_group_delete_popup.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='query_rule_group_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/query_rule_group_delete_popup.jsp#2 $$Change: 651448 $--%>
