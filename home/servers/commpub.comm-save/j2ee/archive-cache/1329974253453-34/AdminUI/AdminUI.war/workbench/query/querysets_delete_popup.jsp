<%--
JSP, used to be popup with delete query ruleset

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="querySetId" var="querySetId" />

  <c:url var="targetURL" value="${queryPath}/querysets_general.jsp"/>
  <c:url var="errorURL" value="${queryPath}/querysets_delete_popup.jsp">
    <c:param name="querySetId" value="${querySetId}" />
  </c:url>
  <d:form action="${errorURL}" method="POST">
    <%-- Id of query rule set, used to be deleted --%>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQuerySetFormHandler.querySetId"
             value="${querySetId}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQuerySetFormHandler.successURL"
             value="${targetURL}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQuerySetFormHandler.errorURL"
             value="${errorURL}"/>

    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="querysets_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="querysets_delete_popup.question2">
                <fmt:param>
                  <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${querySetId}" var="querySet"/>
                  <c:out value="${querySet.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
      <p>
        <fmt:message key="querysets_delete_popup.used_in_search_projects"/>
      </p>
      <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${querySetId}" itemType="query_rules"/>
      <ul>
        <c:choose>
          <c:when test="${empty projects}">
            <li>
              <fmt:message key="querysets_delete_popup.used_in_search_projects.none"/>
            </li>
          </c:when>
          <c:otherwise>
            <c:forEach items="${projects}" var="currentProject">
              <li>
                <c:out value="${currentProject.name}"/>
              </li>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="querysets_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="querysets_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/DeleteQuerySetFormHandler.delete" type="submit"
               value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}"
               iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='querysets_delete_popup.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='querysets_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_delete_popup.jsp#2 $$Change: 651448 $--%>
