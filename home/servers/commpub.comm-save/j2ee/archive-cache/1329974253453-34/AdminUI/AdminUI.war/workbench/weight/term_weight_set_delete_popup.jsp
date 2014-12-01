<%--
JSP provides popup for deleting term weight set

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="weightSetId" var="weightSetId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteWeightSetFormHandler"/>

  <c:url var="targetUrl" value="${weightPath}/term_weight_sets.jsp"/>
  <c:url value="${weightPath}/term_weight_set_delete_popup.jsp" var="actionUrl">
    <c:param name="weightSetId" value="${weightSetId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">
    <d:input type="hidden" bean="DeleteWeightSetFormHandler.errorURL"     value="${actionUrl}"/>
    <d:input type="hidden" bean="DeleteWeightSetFormHandler.successURL"  value="${targetUrl}"/>

    <termweights:termWeightSetFindByPrimaryKey termWeightSetId="${weightSetId}" var="termWeightSet"/>

    <div class="content">
      <p>
        <fmt:message key="term_weight_set_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="term_weight_set_delete_popup.question2">
                <fmt:param>
                  <c:out value="${termWeightSet.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
      <p>
        <fmt:message key="term_weight_set_delete_popup.used_in_search_projects"/>
      </p>
      <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${weightSetId}" itemType="term_weight"/>
      <ul>
        <c:choose>
          <c:when test="${empty projects}">
            <li>
              <fmt:message key="term_weight_set_delete_popup.used_in_search_projects.none"/>
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
      <d:input type="hidden" bean="DeleteWeightSetFormHandler.weightSetId" value="${weightSetId}" name="weightSetId"/>
      <fmt:message key="term_weight_set_delete_popup.button.delete"         var="deleteButtonTitle"/>
      <fmt:message key="term_weight_set_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteWeightSetFormHandler.deleteWeightSet" type="submit" value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}" name="deleteWeightSet" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='term_weight_set_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='term_weight_set_delete_popup.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_delete_popup.jsp#2 $$Change: 651448 $--%>
