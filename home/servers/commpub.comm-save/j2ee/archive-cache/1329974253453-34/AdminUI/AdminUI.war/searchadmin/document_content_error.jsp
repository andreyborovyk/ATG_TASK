<%--
JSP, used to show error message in iframe in case document content is inaccessible.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/document_content_error.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <tags:separateWindow>
    <div id="paneContent">
      <p><fmt:message key="document_content_error.message" /></p>
    </div>
  </tags:separateWindow>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/document_content_error.jsp#2 $$Change: 651448 $--%>