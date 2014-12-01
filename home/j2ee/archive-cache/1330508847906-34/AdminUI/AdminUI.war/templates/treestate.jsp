<%--
  This page involves reading link for right pane area.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/treestate.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="areaId" var="areaId"/>
  <d:getvalueof var="navComponent" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent"/>
  <c:redirect url="${navComponent.lastUrlByAreaId[areaId]}" />
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/treestate.jsp#2 $$Change: 651448 $--%>
