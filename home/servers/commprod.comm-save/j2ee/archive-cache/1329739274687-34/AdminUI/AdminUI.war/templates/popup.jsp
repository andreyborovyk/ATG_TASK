<%--
Popup template. It wraps page content with header and footer sections.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/popup.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="atg.searchadmin.page" var="page"/>
  <d:getvalueof param="atg.searchadmin.skipMessages" var="skipMessages" />
  <%-- The following script shouldn't be merged with others to prevent JS errors when opening the app from BCC --%>
  <script>if (typeof dojo == "undefined") top.document.location = "${pageContext.request.contextPath}";</script>
  <d:getvalueof var="navigationState" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent" />
  <script>loadingStatus.setErrorUrl("${pageContext.request.contextPath}${navigationState.currentStateNode.link}");</script>
  <div dojoType="dijit.layout.LayoutContainer" id="popup"
       style="width: 100%; height: 100%; padding: 0; margin: 0; border: 0;">
    <div class="header" dojoType="dojox.layout.ContentPane" layoutAlign="top">
      <div class="controls">
        <a href="#" title="<fmt:message key='popup.close_button.tooltip'/>" onclick="return closePopUp()">
          <fmt:message key="popup.close_button"/>
        </a>
      </div>
      <div>
        <h3>
          <d:getvalueof var="stateNode" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent.currentNodeByTarget.popup" />
          <fmt:message key="${stateNode.title}">
            <c:forEach var="titleParam" items="${stateNode.titleParameters}">
              <fmt:param value="${titleParam}" />
            </c:forEach>
          </fmt:message>
        </h3>
      </div>
    </div>
    <div dojoType="dojox.layout.ContentPane" layoutAlign="client">
      <d:include page="${page}" />
    </div>
    <div id="popupFooterCopy" class="footer" dojoType="dojox.layout.ContentPane" layoutAlign="bottom">
    </div>
  </div>
  <script>
    <c:if test="${not skipMessages}">
      var messagesData = <admin-dojo:jsonObject><admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue></admin-dojo:jsonObject>;
    </c:if>
    loadingStatus.setErrorUrl(null);
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/popup.jsp#1 $$Change: 651360 $--%>
