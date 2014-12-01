<%--
JSP, rename partition popup

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/partition_rename_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Id of project used--%>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <%-- Id of partition --%>
  <d:getvalueof param="partitionId" var="partitionId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManagePartitionFormHandler"/>

  <d:getvalueof bean="ManagePartitionFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="partitionId" value="${partitionId}"/>
  </admin-ui:initializeFormHandler>

  <c:url value="/searchadmin/partition_rename_popup.jsp" var="actionUrl">
    <c:param name="partitionId" value="${partitionId}"/>
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">
    <%-- id of partition, used to be deleted --%>
    <d:input type="hidden" bean="ManagePartitionFormHandler.partitionId" />
    <d:input type="hidden" bean="ManagePartitionFormHandler.projectId" />
    <d:input type="hidden" bean="ManagePartitionFormHandler.errorURL" value="${actionUrl}"/>
    
    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="partition_rename_popup.title"/>
      </p>
      <d:include src="content_set_edit.jsp" />
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message var="updateButtonTitle" key="partition_rename_popup.${empty partitionId ? 'create' : 'save'}.button" />
      <fmt:message var="updateButtonToolTip" key="partition_rename_popup.${empty partitionId ? 'create' : 'save'}.button.tooltip" />
      <d:input bean="/atg/searchadmin/adminui/formhandlers/ManagePartitionFormHandler.update" type="submit"
               value="${updateButtonTitle}" iclass="formsubmitter"
               title="${updateButtonToolTip}" onclick="return checkForm();"/>
      <input type="button" value="<fmt:message key='partition_rename_popup.cancel.button'/>"
             onclick="closePopUp()" title="<fmt:message key='partition_rename_popup.cancel.button.tooltip'/>"/>
    </div>
    <%-- client side validator --%>
    <admin-validator:validate beanName="ManagePartitionFormHandler"/>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/partition_rename_popup.jsp#2 $$Change: 651448 $--%>
