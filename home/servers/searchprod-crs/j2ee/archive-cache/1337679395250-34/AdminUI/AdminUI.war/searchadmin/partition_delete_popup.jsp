<%--
JSP, used to be popup with delete partition functionality

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/partition_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Id of project used--%>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <%-- Id of partition used to be deleted--%>
  <d:getvalueof param="partitionId" var="partitionId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeletePartitionFormHandler" var="handler" />

  <c:url value="/searchadmin/partition_delete_popup.jsp" var="actionUrl">
    <c:param name="projectId" value="${projectId}"/>
    <c:param name="partitionId" value="${partitionId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">
    <%-- id of partition, used to be deleted --%>
    <d:input type="hidden" bean="DeletePartitionFormHandler.projectId" value="${projectId}"/>
    <d:input type="hidden" bean="DeletePartitionFormHandler.logicalPartitionId" value="${partitionId}"/>
    <d:input type="hidden" bean="DeletePartitionFormHandler.errorURL" value="${actionUrl}"/>
    
    <admin-beans:getLogicalPartitionById var="logicalPartition" id="${partitionId}" />
    <div class="content">
      <%-- Display message if user is trying to delete content set while indexing job is running --%>
      <admin-beans:getSynchronizationProjectStatus varCurrent="current" projectId="${projectId}"/>
      <c:if test="${!current.emptyBean}">
        <p>
          <span class="error"><fmt:message key="partition_delete_popup.warning.indexing.active"/></span>
        </p>
      </c:if>
      <p>
        <fmt:message key="partition_delete_popup.warning"/>
      </p>
      <p>
        <fmt:message key="partition_delete_popup.question">
          <fmt:param><c:out value="${logicalPartition.name}" /></fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="partition_delete_popup.delete.button" var="deleteButtonTitle"/>
      <fmt:message key="partition_delete_popup.delete.button.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="DeletePartitionFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='partition_delete_popup.cancel.button'/>"
             onclick="closePopUp()" title="<fmt:message key='partition_delete_popup.cancel.button.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/partition_delete_popup.jsp#2 $$Change: 651448 $--%>
