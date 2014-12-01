<%--
JSP provides popup for deleting term dictionary

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dict_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="dictId" var="dictId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTermDictFormHandler"/>

  <dictionary:termDictionaryFindByPrimaryKey termDictionaryId="${dictId}" var="dictionary" />
  <c:url value="${dictionaryPath}/term_dict_delete_popup.jsp" var="actionUrl">
    <c:param name="dictId" value="${dictId}"/>
  </c:url>
  <c:url value="${dictionaryPath}/termdicts_general.jsp" var="successUrl"/>

  <d:form action="${actionUrl}" method="POST">

    <d:input type="hidden" bean="DeleteTermDictFormHandler.errorURL" value="${actionUrl}"/>
    <d:input type="hidden" bean="DeleteTermDictFormHandler.successURL" value="${successUrl}"/>

    <div class="content">
      <p>
        <fmt:message key="term_dict_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="term_dict_delete_popup.question2">
                <fmt:param>
                  <c:out value="${dictionary.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
      <p>
        <fmt:message key="term_dict_delete_popup.used_in_search_projects"/>
      </p>
      <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${dictId}" itemType="term_dict"/>
      <ul>
        <c:choose>
          <c:when test="${empty projects}">
            <li>
              <fmt:message key="term_dict_delete_popup.used_in_search_projects.none"/>
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
      <d:input type="hidden" bean="DeleteTermDictFormHandler.dictionaryId" value="${dictId}" name="dictionaryId"/>
      <fmt:message key="term_dict_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="term_dict_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteTermDictFormHandler.deleteDictionary" type="submit" value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}" name="delete" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='term_dict_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='term_dict_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dict_delete_popup.jsp#2 $$Change: 651448 $--%>
