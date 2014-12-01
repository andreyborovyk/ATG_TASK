<%--
Main template tag. It wraps page content with header and footer sections.
It describes all required JS and CSS files.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/main.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="atg.searchadmin.page" var="page" />
  <d:getvalueof param="atg.searchadmin.skipMessages" var="skipMessages" />
  <%-- The following script shouldn't be merged with others to prevent JS errors when opening the app from BCC --%>
  <script>if (typeof dojo == "undefined") top.document.location = "${pageContext.request.contextPath}";</script>
  <d:getvalueof var="navigationState" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent" />
  <script>loadingStatus.setErrorUrl("${pageContext.request.contextPath}${navigationState.currentStateNode.parentSkipTab.link}");</script>
  <div id="viewPane" dojoType="dijit.layout.LayoutContainer" style="width:100%; height:100%;">
    <d:include page="/templates/bread_crumbs.jsp" />
    <d:include page="${page}" />
    <div id="paneFooterCopy" dojoType="dojox.layout.ContentPane" layoutAlign="bottom"></div>
  </div>
  <script>
    <c:if test="${not skipMessages}">
      var messagesData = <admin-dojo:jsonObject><admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue></admin-dojo:jsonObject>;
    </c:if>
    loadingStatus.setErrorUrl(null);
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/main.jsp#1 $$Change: 651360 $--%>
