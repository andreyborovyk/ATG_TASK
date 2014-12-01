<%--
Displays document content by given document URL.
@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_preview.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <c:set var="env"><tags:i18GetParam paramName="env"/></c:set>
  <d:getvalueof param="documentUrl" var="documentUrl"/>

  <admin-beans:getContentItem varContent="content" searchEnvironmentName="${env}" documentUrl="${documentUrl}" />
  ${content}
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_preview.jsp#1 $$Change: 651360 $--%>
