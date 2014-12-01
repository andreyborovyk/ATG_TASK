<%--
JSP, used to be popup with delete content set functionality

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_set_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Id of project to show the warning message--%>
  <d:getvalueof param="projectId" var="projectId" />
  <%-- Id of content set to be deleted--%>
  <d:getvalueof param="contentSetId" var="contentSetId" />

  <admin-beans:getContentById var="content" id="${contentSetId}"/>

  <c:url value="/searchadmin/content_set_delete_popup.jsp" var="actionUrl">
    <c:param name="contentSetId" value="${contentSetId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">

    <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/DeleteContentSetFormHandler.errorURL"
       value="${actionUrl}"/>
    <%-- Id of content set, used to be deleted --%>
    <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/DeleteContentSetFormHandler.contentSetId"
             value="${contentSetId}"/>
    
    <div class="content">
      <%-- Display message if user is trying to delete content while indexing job is running --%>
      <admin-beans:getSynchronizationProjectStatus varCurrent="current" projectId="${projectId}"/>
      <c:if test="${!current.emptyBean}">
        <p>
          <span class="error"><fmt:message key="content_delete.warning.indexing.active"/></span>
        </p>
      </c:if>
        
      <span id="alertListPopup"></span>

      <p>
        <fmt:message key="content_delete.question1">
          <fmt:param>
            <strong>
              <fmt:message key="content_delete.question2">
                <fmt:param>
                  <c:out value="${content.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
      <d:input bean="/atg/searchadmin/adminui/formhandlers/DeleteContentSetFormHandler.deleteContentSources"
               value="true" type="hidden"/>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="content_delete.delete.button" var="deleteButtonTitle"/>
      <fmt:message key="content_delete.delete.button.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/adminui/formhandlers/DeleteContentSetFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='content_delete.cancel.button'/>"
             onclick="closePopUp()" title="<fmt:message key='content_delete.cancel.button.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_set_delete_popup.jsp#2 $$Change: 651448 $--%>
