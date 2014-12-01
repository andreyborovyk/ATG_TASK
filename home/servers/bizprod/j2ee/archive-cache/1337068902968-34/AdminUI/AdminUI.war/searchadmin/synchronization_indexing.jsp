<%--
  synchronization_indexing.jsp forwards to synchronization view according to the status of current
  synchronization process.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_indexing.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>

<d:page>
  <%-- Project id parameter --%>
  <d:getvalueof param="projectId" var="projectId"/>
  <%-- Take current synchronization project status bean --%>
  <admin-beans:getSynchronizationProjectStatus varCurrent="current" projectId="${projectId}"/>
  <%--
    Manual indexing page is displayed if there is no current
    synchronization project and synchronization status page otherwise
   --%>
  <c:choose>
    <c:when test="${!current.emptyBean}">
      <c:url var="url" value="/searchadmin/synchronization_status_monitor.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url var="url" value="/searchadmin/synchronization_manual.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
    </c:otherwise>
  </c:choose>
  <script>loadingStatus.setRedirectUrl("${url}");</script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_indexing.jsp#2 $$Change: 651448 $--%>
