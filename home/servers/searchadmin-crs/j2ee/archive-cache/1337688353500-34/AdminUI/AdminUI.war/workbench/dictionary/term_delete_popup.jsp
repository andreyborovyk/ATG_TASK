<%--
JSP provides popup for deleting term dictionary

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="termId" var="termId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTermFormHandler"/>

  <dictionary:termFindByPrimaryKey termId="${termId}" var="term" />
  <c:set var="parent" value="${term.parentNode}" />
  <c:choose>
    <c:when test="${parent.nodeType eq 'term'}">
      <c:url value="${dictionaryPath}/term_edit.jsp" var="successURL">
        <c:param name="termId" value="${parent.id}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url value="${dictionaryPath}/term_dictionary.jsp" var="successURL">
        <c:param name="dictId" value="${parent.id}"/>
      </c:url>
    </c:otherwise>
  </c:choose>
  <c:url value="${dictionaryPath}/term_delete_popup.jsp" var="actionUrl">
    <c:param name="termId" value="${termId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">

    <d:input type="hidden" bean="DeleteTermFormHandler.errorURL"     value="${actionUrl}"/>
    <d:input type="hidden" bean="DeleteTermFormHandler.successURL"  value="${successURL}"/>

    <div class="content">
      <p>
        <fmt:message key="term_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="term_delete_popup.question2">
                <fmt:param>
                  <c:out value="${term.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="DeleteTermFormHandler.termId" value="${termId}" name="termId"/>
      <fmt:message key="term_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="term_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteTermFormHandler.deleteTerm" type="submit" value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}" name="delete" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='term_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='term_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_delete_popup.jsp#2 $$Change: 651448 $--%>
